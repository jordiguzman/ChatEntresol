@file:Suppress("DEPRECATION")

package appkite.jordiguzman.com.xatentresol.service

import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService



@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class MyFirebaseInstanceIDService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
         val newRegistrationToken = FirebaseInstanceId.getInstance().token

        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenXat(newRegistrationToken)
    }

    companion object {
        fun addTokenXat(newRegistrationToken: String?){
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null")

            //TODO anular cuando el usuario lo desee
            XatUtil.getFCMRegistrtionTokens { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrtionTokens

                tokens.add(newRegistrationToken)
                XatUtil.setFCMRegistrtionTokens(tokens)
            }
        }
    }
}