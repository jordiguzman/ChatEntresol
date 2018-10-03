package appkite.jordiguzman.com.xatentresol.activities.users

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import appkite.jordiguzman.com.xatentresol.R
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
        getAllUsers()

        btn_send.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {
        val progressDialog = indeterminateProgressDialog("Sending Email. Please wait.")
        val sender = Thread(Runnable {
            try {
                val sender = GMailSender("jordiguz@gmail.com", "noes0r0todoloquereluce")
                sender.sendMail("EmailSender App",
                        "Prueba cuerpo de correo",
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
                    if (!name.equals(currentUserName)){
                        userListBanned.add(User(name!!, "", profilePicturePath, mutableListOf()))
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
}







