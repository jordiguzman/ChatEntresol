package appkite.jordiguzman.com.xatentresol.activities.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.MenuItem
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_my_acount.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.ByteArrayOutputStream

class MyAcountActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    private var isEditable = false
    private var maxLength = 25
    companion object {
        var fromMyAcount = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_acount)

        initView()
        setupEdittextCount()

        imageView_profile_picture.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
        }
        btn_save.setOnClickListener {
            if (isEditable) {
                isEditable = false
                activeUserProfile()
                return@setOnClickListener
            }
            if (::selectedImageBytes.isInitialized)
                StorageUtil.uploadProfilePhoto(selectedImageBytes){imagePath->
                    XatUtil.updateCurrentUser(editText_name.text.toString(),
                            editText_bio.text.toString(), imagePath)
                }

            else
                XatUtil.updateCurrentUser(editText_name.text.toString(),
                        editText_bio.text.toString(), null)
            longSnackbar(constraint_layout_fragment_my_acount, "Saved")
            deactiveUserProfile()
        }

        btn_sign_out.setOnClickListener {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
        }
        if (btn_save.text == "Edit profile" || btn_save.text == "Editar perfil") {
            isEditable = true
            deactiveUserProfile()
        }




    }

    private fun setupEdittextCount() {
        editText_bio.filters += InputFilter.LengthFilter(maxLength)
        editText_name.filters += InputFilter.LengthFilter(maxLength)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
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
        }
    }

    private fun deactiveUserProfile() {
       editText_name.isEnabled = false
        editText_bio.isEnabled = false
        imageView_profile_picture.isEnabled = false
    }

    private fun activeUserProfile() {
        editText_name.isEnabled = true
        editText_bio.isEnabled = true
        imageView_profile_picture.isEnabled = true
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

            } else {
                if (!pictureJustChanged)
                    longSnackbar(constraint_layout_fragment_my_acount, getString(R.string.photo_user_message))
                    //toast("Es indispensable que añadas una foto a tu perfil")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                fromMyAcount = true
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}




