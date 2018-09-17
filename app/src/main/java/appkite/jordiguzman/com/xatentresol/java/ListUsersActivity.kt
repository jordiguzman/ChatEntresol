package appkite.jordiguzman.com.xatentresol.java

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import appkite.jordiguzman.com.xatentresol.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list_users.*

class ListUsersActivity : AppCompatActivity() {




    private var userList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        getData()

    }



    fun getData(){
        val pathUser = "users"
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(pathUser)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                for (document in task.result){
                    userList.add(document.id)
                    tv_users_java.append(document.id.plus("\n"))

                }

            }
        }
    }


}

