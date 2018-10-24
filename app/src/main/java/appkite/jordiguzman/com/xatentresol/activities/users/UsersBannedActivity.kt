package appkite.jordiguzman.com.xatentresol.activities.users

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.settings.MyAccountActivity
import appkite.jordiguzman.com.xatentresol.adapter.UserBannedAdapter
import appkite.jordiguzman.com.xatentresol.email.GMailSender
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_users_banned.*
import org.jetbrains.anko.indeterminateProgressDialog

class UsersBannedActivity : AppCompatActivity() {

    private var userListBanned = ArrayList<User>()
    private var currentUserName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_banned)

        getCurrentUserName()


        //TODO aqui se produce el error de cambio de nombre de usuario MUY IMPORTANTE!!!



    }

    private fun sendMessage() {
        val progressDialog = indeterminateProgressDialog("Sending Email. Please wait.")
        val sender = Thread(Runnable {
            try {
                val sender = GMailSender("jordiguz@gmail.com", "noes0r0todoloquereluce")
                sender.sendMail("EmailSender App",
                        "Cuerpo Correo",
                        "jordiguz@gmail.com",
                        "jordiguz@gmail.com")
                progressDialog.dismiss()
            } catch (e: Exception) {
                Log.e("mylog", "Error: " + e.message)
            }
        })
        sender.start()
    }
    private fun getCurrentUserName() {
        XatUtil.getCurrentUser { user ->
            currentUserName = user.name
            getAllUsers()
        }
    }

    private fun getAllUsers(){
        if (!userListBanned.isEmpty()){
            userListBanned.clear()
        }
        val pathUser = "users"
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(pathUser)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                for (document in task.result){
                    val name = document.getString("name")
                    val profilePicturePath = document.getString("profilePicturePath")
                    val email = document.getString("emailUser")
                    val uid = document.getString("uidUser")
                    if (!name.equals(currentUserName)){
                        userListBanned.add(User(name!!, "", profilePicturePath, mutableListOf(), email!!, false, uid!!))
                        Log.d("banned", userListBanned.size.toString())
                    }
                }

                setupRecyclerview()
            }
        }
    }

    private fun setupRecyclerview() {
        rv_users_banned.setHasFixedSize(true)
        rv_users_banned.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val adapter = UserBannedAdapter(userListBanned, this)
        rv_users_banned.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                MyAccountActivity.fromMyAccount = true
                NavUtils.navigateUpFromSameTask(this)

            }
        }
        return super.onOptionsItemSelected(item)
    }
}







