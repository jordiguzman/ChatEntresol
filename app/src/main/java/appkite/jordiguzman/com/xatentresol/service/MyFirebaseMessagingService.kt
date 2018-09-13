package appkite.jordiguzman.com.xatentresol.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", remoteMessage.data.toString())
        if (remoteMessage.notification != null){

            Log.d("FCM", remoteMessage.data.toString())
        }

    }
}