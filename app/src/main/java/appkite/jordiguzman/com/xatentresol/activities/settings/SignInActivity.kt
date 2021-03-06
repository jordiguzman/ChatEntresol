package appkite.jordiguzman.com.xatentresol.activities.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.ui.MainActivity
import appkite.jordiguzman.com.xatentresol.service.MyFirebaseInstanceIDService
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.custom_dialog_notice.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


private const val URL = "https://xatentresol-146fe.firebaseapp.com/"

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private val rcSigning = 1
    private var alertCorrectEmailVisible: Boolean? = false
    companion object {
        var firstTime: Boolean? = true
    }

    private val signInProviders =
            listOf(AuthUI.IdpConfig.EmailBuilder()
                    .setAllowNewAccounts(true)
                    .setRequireName(true)
                    .build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        readShared()


        initSignIn()


    }


    private fun countDownTimerEmailNotice(){
        object : CountDownTimer(2000, 2000){
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                if (!firstTime!!)alertCorrectEmailVisible = true
               alertDialog()
            }
        }.start()
    }

    private fun initSignIn() {
        if (!firstTime!!) longSnackbar(constraint_layout_signin, R.string.verifica_correo)
        if (!alertCorrectEmailVisible!!) {
            alertDialog()
        }else{
            countDownTimerEmailNotice()
        }
        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setLogo(R.drawable.ic_logo)
                    .setTosAndPrivacyPolicyUrls(
                            URL,
                            URL)
                    .setTheme(R.style.AppTheme)
                    .build()
            startActivityForResult(intent, rcSigning)
        }
    }





    private fun readShared() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("view", android.content.Context.MODE_PRIVATE)
        alertCorrectEmailVisible = sharedPreferences.getBoolean("alertCorrectEmailVisible", false)
    }
    private fun shared() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("view", android.content.Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("alertCorrectEmailVisible", alertCorrectEmailVisible!!)
        editor.apply()
    }

    @SuppressLint("InflateParams")
    private fun alertDialog() {
        if (alertCorrectEmailVisible!!)return
        val dialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog_notice, null)
        val builder = android.support.v7.app.AlertDialog.Builder(this)
                .setView(dialog)
        val alertDialog = builder.show()
        alertDialog.show()
        dialog.btn_ok.setOnClickListener {
            if (dialog.checkBox_notice.isChecked) {
                alertCorrectEmailVisible = true
                shared()
            }
            alertDialog.dismiss()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSigning) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val progressDialog = indeterminateProgressDialog("Setting up your account")

                XatUtil.initCurrentUserIfFirstTime {

                    if (!XatUtil.verifiedUserEmail()){
                        XatUtil.sendEmailVerification(this)
                    }
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    val registrationToken = FirebaseInstanceId.getInstance().token
                    MyFirebaseInstanceIDService.addTokenXat(registrationToken)

                    progressDialog.dismiss()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return
                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK ->
                        longSnackbar(constraint_layout_signin, getString(R.string.no_network))
                    ErrorCodes.UNKNOWN_ERROR ->
                        longSnackbar(constraint_layout_signin, "Unknown error")
                }
            }
        }
    }


    override fun onBackPressed() {

    }
}
