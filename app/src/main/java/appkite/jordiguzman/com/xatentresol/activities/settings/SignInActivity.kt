package appkite.jordiguzman.com.xatentresol.activities.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private val rcSigning = 1
    private var visible: Boolean? = false
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

        //TODO Revisar que no salga el aviso de consultar el correo la primera vez y revisar los dos campor Banned y banned creados.
        readShared()
        if (!visible!! )alertDialog()
        if (!firstTime!!)longSnackbar(constraint_layout_signin, R.string.verifica_correo)
        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setLogo(R.drawable.ic_logo)
                    .build()
            startActivityForResult(intent, rcSigning)
        }
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
                longSnackbar(constraint_layout_signin, R.string.verifica_correo)
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
