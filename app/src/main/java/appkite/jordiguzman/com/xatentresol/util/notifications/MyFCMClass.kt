package appkite.jordiguzman.com.xatentresol.util.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFCMClass: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        if (p0!!.notification != null){
            Log.d("Title", "msg" + p0.notification!!.title)
            Log.d("Body", "msg" + p0.notification!!.body)
        }
        if (p0.data.isNotEmpty()){
            Log.d("Data","msg" + p0.data.toString())
        }
    }
}