package appkite.jordiguzman.com.xatentresol.activities.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.ui.MainActivity
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_acount.*
import kotlinx.android.synthetic.main.custom_dialog_photo_name.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream


private const val RC_SELECT_IMAGE = 2


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_acount)

        initView()
        setupEdittextCount()


        nameUsers.clear()
        getAllUsers()


        fb_my_account.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
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
            editText_name.selectAll()
        }
        iv_edit_bio.setOnClickListener {
            editText_bio.isActivated = true
            editText_bio.isEnabled = true
            editText_bio.selectAll()
        }


    }

    private fun clearEditText() {
        iv_edit_name.text.text = ""
        iv_edit_bio.text.text = ""
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
                for (document in task.result) {
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




