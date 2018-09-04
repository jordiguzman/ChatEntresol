package appkite.jordiguzman.com.xatentresol.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import appkite.jordiguzman.com.xatentresol.R
import org.jetbrains.anko.startActivity

class KickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kick)


        hideNavigation()
       val countDownTimer = object: CountDownTimer(500, 200){
           override fun onFinish() {
                toSplash()
               finish()
       }

           override fun onTick(millisUntilFinished: Long) {

           }

       }
        countDownTimer.start()

    }

    private fun toSplash(){
        startActivity<SplashActivity>()

    }

    fun hideNavigation() {
        val decorView = window.decorView

        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun onBackPressed() {

    }

}
