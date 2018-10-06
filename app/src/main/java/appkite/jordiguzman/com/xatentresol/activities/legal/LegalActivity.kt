package appkite.jordiguzman.com.xatentresol.activities.legal

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import appkite.jordiguzman.com.xatentresol.R
import kotlinx.android.synthetic.main.activity_legal.*

class LegalActivity : AppCompatActivity() {

    private val URL = "https://xatentresol-146fe.firebaseapp.com/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal)

       if (web_view != null){
           web_view.loadUrl(URL)
           web_view.settings.javaScriptEnabled
           web_view.settings.mediaPlaybackRequiresUserGesture= false
       }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
