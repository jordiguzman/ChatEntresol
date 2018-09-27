package appkite.jordiguzman.com.xatentresol.activities.chat

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
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
import kotlinx.android.synthetic.main.dialog_camera_galley.view.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


private const val RC_IMAGE_GALLERY = 2
private const val RC_IMAGE_CAMERA = 1
class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section
    val firebaseMessage = FirebaseMessaging.getInstance()
    private val MY_PHOTO = "my_photo"
    private var outputFileUri: Uri? = null
    private var mImageUri: Uri? = null




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
                alertDialog()

            }
        }
    }

    private fun alertDialog(){
        val dialog = LayoutInflater.from(this).inflate(R.layout.dialog_camera_galley, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialog)
        val alertDialog = builder.show()
        alertDialog.show()

        dialog.btn_camera.setOnClickListener {

            cameraIntent()

            alertDialog.dismiss()


        }
        dialog.btn_galeria.setOnClickListener {



            alertDialog.dismiss()
        }
    }

    private fun fromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RC_IMAGE_GALLERY)
    }

    private fun cameraIntent() {
         val filename = "" + System.currentTimeMillis() + ".jpg"
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.TITLE, filename)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

        mImageUri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        grantUriPermission(null, mImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(intent, RC_IMAGE_CAMERA)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_IMAGE_GALLERY && resultCode == Activity.RESULT_OK &&
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

        }else if (requestCode == RC_IMAGE_CAMERA && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null)  {
            if (mImageUri == null) {
                Log.d("Camera", "URI is null")
                return
            }
            val uri : Uri = mImageUri!!

            val ins : InputStream? = contentResolver?.openInputStream(uri)
            val img : Bitmap? = BitmapFactory.decodeStream(ins)
            ins?.close()

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


    private fun checkPermissionToApp(permision: String, requestPermition: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permision) != PackageManager.PERMISSION_GRANTED){
                val strings = Array(0){permision}
                ActivityCompat.requestPermissions(this, strings,requestPermition )
                return
            }
        }
        when(requestPermition){
            RC_IMAGE_GALLERY -> fromGallery()
        }

    }
}


