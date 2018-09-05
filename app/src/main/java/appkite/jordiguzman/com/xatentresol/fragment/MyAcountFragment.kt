package appkite.jordiguzman.com.xatentresol.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.SignInActivity
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_my_acount.*
import kotlinx.android.synthetic.main.fragment_my_acount.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import java.io.ByteArrayOutputStream


class MyAcountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    private var isEditable = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_acount, container, false)


        view.apply {
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
                    activeUserProfile(view)
                    return@setOnClickListener
                }
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        XatUtil.updateCurrentUser(editText_name.text.toString(),
                                editText_bio.text.toString(), imagePath)
                    }
                else
                    XatUtil.updateCurrentUser(editText_name.text.toString(),
                            editText_bio.text.toString(), null)
                longSnackbar(constraint_layout_fragment_my_acount, "Saved")
                deactiveUserProfile(view)
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance()
                        .signOut(this@MyAcountFragment.context!!)
                        .addOnCompleteListener {
                            startActivity(intentFor<SignInActivity>().newTask().clearTask())
                        }
            }
            if (btn_save.text == "Edit profile") {
                isEditable = true
                deactiveUserProfile(view)
            }
        }

        return view
    }

    private fun deactiveUserProfile(view: View) {
        view.editText_name.isEnabled = false
        view.editText_bio.isEnabled = false
        view.imageView_profile_picture.isEnabled = false
    }

    private fun activeUserProfile(view: View) {
        view.editText_name.isEnabled = true
        view.editText_bio.isEnabled = true
        view.imageView_profile_picture.isEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                    .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                    .load(selectedImageBytes)
                    .into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        XatUtil.getCurrentUser { user ->
            if (this@MyAcountFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if (!pictureJustChanged && user.profilePicturePath != null)
                    GlideApp.with(this)
                            .load(StorageUtil.pathToReference(user.profilePicturePath))
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(imageView_profile_picture)

            }
        }
    }

}
