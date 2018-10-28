package appkite.jordiguzman.com.xatentresol.activities.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
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
import appkite.jordiguzman.com.xatentresol.activities.ui.MainActivity
import appkite.jordiguzman.com.xatentresol.fragment.PeopleFragment
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.ImageMessage
import appkite.jordiguzman.com.xatentresol.model.TextMessage
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.AppConstants
import appkite.jordiguzman.com.xatentresol.util.ImageUtils
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
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_IMAGE_GALLERY = 2
private const val RC_IMAGE_CAMERA = 1
private const val RP_GALLERY = 22
private const val RP_CAMERA = 11

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section
    private val firebaseMessage = FirebaseMessaging.getInstance()
    private var mImageUri: Uri? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



        if (savedInstanceState != null) {
            mImageUri = savedInstanceState.getParcelable("uriImage")
        }

        tv_user_main_activity.text = intent.getStringExtra(AppConstants.USER_NAME)

        firebaseMessage.isAutoInitEnabled

        XatUtil.getCurrentUser {
            currentUser = it
            if (PeopleFragment.pathUser != null){
                GlideApp.with(this)
                        .load(StorageUtil.pathToReference(PeopleFragment.pathUser!!))
                        .placeholder(R.drawable.ic_person_white)
                        .into(iv_chat_user)
            }else{
                iv_chat_user.background = ContextCompat.getDrawable(this, R.drawable.ic_person_white)
            }

        }

        otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        XatUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration =
                    XatUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                if (editText_message.text.isEmpty()) return@setOnClickListener


                val messageToSend =
                        TextMessage(editText_message.text.toString(), Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                otherUserId, currentUser.name)
                editText_message.setText("")
                XatUtil.sendMessage(messageToSend, channelId)

            }
            fab_send_image.setOnClickListener {
                alertDialogCameraGallery()

            }
        }
    }

    @SuppressLint("InflateParams")
    private fun alertDialogCameraGallery() {
        val dialog = LayoutInflater.from(this).inflate(R.layout.dialog_camera_galley, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialog)
        val alertDialog = builder.show()
        alertDialog.show()

        dialog.btn_camera.setOnClickListener {
           checkPermissionToAppCamera()
            alertDialog.dismiss()
        }
        dialog.btn_galeria.setOnClickListener {
            checkPermissionToAppStorage()
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
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        Log.d("imageUri", mImageUri.toString())
        startActivityForResult(intent, RC_IMAGE_CAMERA)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_IMAGE_GALLERY && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
            val selectedImagePath = data.data


            //val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)
            val selectedImageBmp = ImageUtils.handleSamplingAndRotationBitmap(this, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                        ImageMessage(imagePath, Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                otherUserId, currentUser.name)
                XatUtil.sendMessage(messageToSend, currentChannelId)

            }

        } else if (requestCode == RC_IMAGE_CAMERA && resultCode == Activity.RESULT_OK) {

            if (mImageUri == null) {
                return
            }

            val img: Bitmap? = ImageUtils.handleSamplingAndRotationBitmap(this, mImageUri!!)
            val outputStream = ByteArrayOutputStream()
            img!!.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()
            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                        ImageMessage(imagePath, Calendar.getInstance().time,
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




    private fun checkPermissionToAppStorage() {

        val permissionWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionRead = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionWrite != PackageManager.PERMISSION_GRANTED && permissionRead != PackageManager.PERMISSION_GRANTED) {
            makeRequestStorage()
        } else {
            fromGallery()
        }

    }


    private fun checkPermissionToAppCamera() {

        val permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            makeRequestCamera()
            Log.d("Entro", "makeRequest")
        } else {
            cameraIntent()
        }
    }

    private fun makeRequestStorage() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RP_GALLERY)
    }

    private fun makeRequestCamera() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RP_CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RP_GALLERY -> {
                if (!grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fromGallery()
                }
            }
            RP_CAMERA -> {
                Log.d("Entro", "result")
                if (!grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                         grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent()
                }
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)

        val uriImage = mImageUri
        outState?.putParcelable("uriImage", uriImage)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            mImageUri = savedInstanceState.getParcelable("uriImage")
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity<MainActivity>()
    }

}


