package appkite.jordiguzman.com.xatentresol.activities.settings

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import appkite.jordiguzman.com.xatentresol.R
import kotlinx.android.synthetic.main.activity_notifications_settings.*
import org.jetbrains.anko.startActivity


class NotificationsSettingsActivity : AppCompatActivity() {

    companion object {
        var noNotifications = true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_settings)


        switch_notification_main1.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked){
                noNotifications = true
                switch_notification_main2.isEnabled = false
                switch_notification_main3.isEnabled = false
                switch_notification_main2.isChecked = false
                switch_notification_main3.isChecked = false
            }else{
                noNotifications = false
                switch_notification_main2.isEnabled = true
                switch_notification_main3.isEnabled = true
            }
        }

        switch_notification_main2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                startActivity<DialogNotificationsAudioActivity>()

            }
        }
        switch_notification_main3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorService.vibrate(500)

            }
        }
    }

}

