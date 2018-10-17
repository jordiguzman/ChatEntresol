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
import android.webkit.WebView
import android.webkit.WebViewClient
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
    private var visible: Boolean? = false
    private var legal: Boolean? = false
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
        readSharedLegal()


        if (!legal!!){
            avisoLegal()
        }else{
            initSignIn()
        }

    }

    private fun countDownTimer() {
        object : CountDownTimer(3000, 3000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                alertDialog()
            }
        }.start()
    }

    private fun initSignIn() {
        if (!firstTime!!) longSnackbar(constraint_layout_signin, R.string.verifica_correo)
        if (!visible!!) countDownTimer()
        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setLogo(R.drawable.ic_logo)
                    .build()
            startActivityForResult(intent, rcSigning)
        }
    }

    private fun avisoLegal() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.aviso_legal))
        alert.setIcon(R.drawable.ic_logo)

        val wv = WebView(this)
        wv.loadUrl(URL)
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        alert.setView(wv)
        alert.setPositiveButton(getString(R.string.aceptar)){ dialog, _ ->
            dialog.dismiss()
            legal = true
            sharedLegal()
            initSignIn()
        }
        alert.setNegativeButton(getString(R.string.rechazar)) { dialog, _ ->
            dialog.dismiss()
            longSnackbar(constraint_layout_signin, getString(R.string.aviso_rechazo_legal))}
        alert.show()

    }

    private fun readSharedLegal() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("legal", android.content.Context.MODE_PRIVATE)
        legal = sharedPreferences.getBoolean("accepted", false)
    }

    private fun sharedLegal(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("legal", android.content.Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("accepted", legal!!)
        editor.apply()
    }

    private fun readShared() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("view", android.content.Context.MODE_PRIVATE)
        visible = sharedPreferences.getBoolean("visible", false)
    }
    private fun shared() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("view", android.content.Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("visible", visible!!)
        editor.apply()
    }

    @SuppressLint("InflateParams")
    private fun alertDialog() {
        val dialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog_notice, null)
        val builder = android.support.v7.app.AlertDialog.Builder(this)
                .setView(dialog)
        val alertDialog = builder.show()
        alertDialog.show()
        dialog.btn_ok.setOnClickListener {
            if (dialog.checkBox_notice.isChecked) {
                visible = true
                shared()
                if (!firstTime!!){
                    longSnackbar(constraint_layout_signin, R.string.verifica_correo)

                }

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
