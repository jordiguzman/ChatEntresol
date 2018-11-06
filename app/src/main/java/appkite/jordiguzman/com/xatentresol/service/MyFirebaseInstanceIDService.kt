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

            //if (NotificationsSettingsActivity.noNotifications)return
            XatUtil.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                tokens.add(newRegistrationToken)
                XatUtil.setFCMRegistrationTokens(tokens)
            }
        }
    }
}