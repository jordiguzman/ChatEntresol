package appkite.jordiguzman.com.xatentresol.activities.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.ui.MainActivity
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.ImageUtils
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_acount.*
import kotlinx.android.synthetic.main.custom_dialog_photo_name.view.*
import kotlinx.android.synthetic.main.dialog_camera_galley.view.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream


private const val RC_IMAGE_GALLERY = 2
private const val RC_IMAGE_CAMERA = 1
private const val RP_GALLERY = 22
private const val RP_CAMERA = 11


class MyAccountActivity : AppCompatActivity() {

    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    private var maxLength = 25

    companion object {
        var fromMyAccount = false
    }

    private var nameRepeat = false
    private var alertRepeat = false
    private var nameUsers = ArrayList<User>()
    private var currentUser = ""
    private var currentUserUid = ""
    private var mImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_acount)

        initView()
        setupEdittextCount()


        nameUsers.clear()
        getAllUsers()


        fb_my_account.setOnClickListener {
            alertDialogCameraGallery()
        }
        btn_save.setOnClickListener {
            if (::selectedImageBytes.isInitialized)
                StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                    XatUtil.updateCurrentUser(editText_name.text.toString(),
                            editText_bio.text.toString(), imagePath)
                    longSnackbar(constraint_layout_fragment_my_acount, "Saved")
                    delayGoToMain()

                }
            else{
                XatUtil.updateCurrentUser(editText_name.text.toString(),
                        editText_bio.text.toString(), null)
                longSnackbar(constraint_layout_fragment_my_acount, "Saved")

                getAllUsers()
                delayGoToMain()
            }

        }



        iv_edit_name.setOnClickListener {
            editText_name.isActivated = true
            editText_name.isEnabled = true
            showSoftKeyboard(editText_name)
            editText_name.selectAll()
        }
        iv_edit_bio.setOnClickListener {
            editText_bio.isActivated = true
            editText_bio.isEnabled = true
            showSoftKeyboard(editText_bio)
            editText_bio.selectAll()
        }


    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
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
        startActivityForResult(intent, RC_IMAGE_CAMERA)


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
                if (!grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent()
                }
            }
        }
    }

    private fun delayGoToMain() {
        object : CountDownTimer(2000, 2000){
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                fromMyAccount = true
                startActivity<MainActivity>()
            }
        }.start()
    }


    private fun getAllUsers() {
        if (!nameUsers.isEmpty()) nameUsers.clear()
        val pathUser = "users"
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(pathUser)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    nameUsers.add(User(document.getString("name")!!, "",
                            "", mutableListOf(), "", false,
                            document.getString("uidUser")!!))

                }
            }

            checkRepeatUserName()
        }
    }


    private fun checkRepeatUserName() {
        XatUtil.getCurrentUser { User ->
            currentUser = User.name
            currentUserUid = User.uidUser
            for (i: Int in nameUsers.indices) {
                val name = nameUsers[i].name
                val uid = nameUsers[i].uidUser
                if (name == currentUser && uid != currentUserUid) {
                    alertRepeat = true
                    alertChangeName()
                }
            }
        }
    }


    @SuppressLint("InflateParams")
    private fun alertChangeName() {
        val dialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog_photo_name, null)
        val builder = android.support.v7.app.AlertDialog.Builder(this)
                .setView(dialog)
        val alertDialog = builder.show()
        alertDialog.show()
        dialog.btn_ok_name_photo.setOnClickListener {
            fromMyAccount = true
            nameRepeat = true
            nameUsers.clear()
            checkRepeatUserName()
            alertDialog.dismiss()
        }
    }

    private fun setupEdittextCount() {
        editText_bio.filters += InputFilter.LengthFilter(maxLength)
        editText_name.filters += InputFilter.LengthFilter(maxLength)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_IMAGE_GALLERY && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                    .getBitmap(this.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                    .load(selectedImageBytes)
                    .into(imageView_profile_picture)

            pictureJustChanged = true
        }else if (requestCode == RC_IMAGE_CAMERA && resultCode == Activity.RESULT_OK) {

            if (mImageUri == null) {
                return
            }
            val img: Bitmap? = ImageUtils.handleSamplingAndRotationBitmap(this, mImageUri!!)
            val outputStream = ByteArrayOutputStream()
            img!!.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                    .load(selectedImageBytes)
                    .into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }


    private fun initView() {
        XatUtil.getCurrentUser { user ->
            editText_name.setText(user.name)
            editText_bio.setText(user.bio)
            if (!pictureJustChanged && user.profilePicturePath != null) {
                GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.profilePicturePath))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(imageView_profile_picture)

            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                return if (!pictureJustChanged) {
                    return false
                } else {
                    fromMyAccount = true
                    NavUtils.navigateUpFromSameTask(this)
                    true
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

}




