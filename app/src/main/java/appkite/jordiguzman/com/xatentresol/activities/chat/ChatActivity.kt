package appkite.jordiguzman.com.xatentresol.activities.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.model.ImageMessage
import appkite.jordiguzman.com.xatentresol.model.TextMessage
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.AppConstants
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*


private const val RC_SELECTED_IMAGE = 2
class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section
    val firebaseMessage = FirebaseMessaging.getInstance()



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        tv_user_main_activity.text = intent.getStringExtra(AppConstants.USER_NAME)

        firebaseMessage.isAutoInitEnabled

        XatUtil.getCurrentUser {
            currentUser = it
        }

        otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        XatUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration =
                    XatUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                if (editText_message.text.isEmpty())return@setOnClickListener


                val messageToSend =
                        TextMessage(editText_message.text.toString(), Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                otherUserId, currentUser.name)
                editText_message.setText("")
                XatUtil.sendMessage(messageToSend, channelId)

            }
            fab_send_image.setOnClickListener {
                Snackbar.make(containes_chat, "Recuerda que las fotos de cámara se tienen que hacer en panorámico", Snackbar.LENGTH_LONG).show()
                //toast("Recuerda que las fotos de cámara se tienen que hacer en panorámico")
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select image"), RC_SELECTED_IMAGE)
            }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECTED_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null){
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes){ imagePath ->
                val messageToSend =
                        ImageMessage(imagePath,  Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                otherUserId, currentUser.name)
                XatUtil.sendMessage(messageToSend, currentChannelId)

            }

        }
    }




    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter.itemCount - 1)
    }
}
