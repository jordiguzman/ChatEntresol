package appkite.jordiguzman.com.xatentresol.activities.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_acount.*
import kotlinx.android.synthetic.main.custom_dialog_photo_name.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.ByteArrayOutputStream

class MyAccountActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    private var isEditable = false
    private var maxLength = 25
    companion object {
        var fromMyAcount = false

    }
    private var nameRepeat = false
    var nameUserRepeat = ArrayList<String>()
    var nameUsers = ArrayList<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_acount)

        initView()
        setupEdittextCount()

        nameUserRepeat.clear()
        nameUsers.clear()

        getAllUsers()
        if (!nameUserRepeat.isEmpty()){
            alertChangeName()
        }





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
                        SignInActivity.firstTime = true

                        startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
        }
        if (btn_save.text == "Edit profile" || btn_save.text == "Editar perfil") {
            isEditable = true
            deactiveUserProfile()
        }




    }

    private fun getAllUsers(){
        if (!nameUsers.isEmpty()) nameUsers.clear()
        val pathUser = "users"
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(pathUser)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                for (document in task.result){
                    nameUsers.add(User(document.getString("name")!!, "", "", mutableListOf(), "", false))

                }
            }
            checkRepeatUserName()
        }
    }
    private fun checkRepeatUserName() {
        for (i: Int in nameUsers.indices-1) {
            if (nameUsers[i].name == FirebaseAuth.getInstance().currentUser?.displayName) {
                val name = nameUsers[i].name
                nameUserRepeat.add(name)
                if (!nameUserRepeat.isEmpty()){
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
            fromMyAcount =true
            nameUsers.clear()
            nameUserRepeat.clear()

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
                if (!pictureJustChanged || user.profilePicturePath == null)
                    if (!nameRepeat){
                        longSnackbar(constraint_layout_fragment_my_acount, getString(R.string.photo_user_message))
                    }
            }
        }
    }

    //TODO Resolver lo de más abajo

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                return if (!pictureJustChanged){
                   /* XatUtil.nameUserRepeat.clear()
                    XatUtil.getAllUsers()
                    Log.d("repeatUsers", XatUtil.nameUserRepeat.size.toString())
                    if (!XatUtil.nameUserRepeat.isEmpty()){
                        alertChangeName()
                        return true
                    }*/
                    return false
                } else {
                    fromMyAcount = true
                    NavUtils.navigateUpFromSameTask(this)
                    true
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
       /* XatUtil.nameUserRepeat.clear()
        XatUtil.getAllUsers()
        Log.d("repeatUsers", XatUtil.nameUserRepeat.size.toString())
        if (!XatUtil.nameUserRepeat.isEmpty()){
            alertChangeName()
        }*/

    }

}




