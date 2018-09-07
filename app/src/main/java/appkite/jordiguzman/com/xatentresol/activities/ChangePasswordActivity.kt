package appkite.jordiguzman.com.xatentresol.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import appkite.jordiguzman.com.xatentresol.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*
import org.jetbrains.anko.toast

class ChangePasswordActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        mAuth = FirebaseAuth.getInstance()
        btnResetPassword.setOnClickListener {
            val email = edtResetEmail.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                toast("Enter your email")
            } else {
                mAuth!!.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toast("Check email to reset your password")
                                //TODO reiniciar hacia signup
                            } else {
                                toast("Fail to send reset password email!")
                            }
                        }
            }
        }



    }
}
