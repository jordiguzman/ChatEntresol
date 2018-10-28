package appkite.jordiguzman.com.xatentresol.activities.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.support.v7.app.AppCompatActivity
import android.text.util.Linkify
import android.util.Log
import appkite.jordiguzman.com.xatentresol.R
import kotlinx.android.synthetic.main.activity_info_app.*

class InfoAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_app)


        Linkify.addLinks(tv_link, Linkify.WEB_URLS)

        tv_link.setOnClickListener {
            val uri: Uri = Uri.parse("https://pasalavida30.wordpress.com/kick-in-the-eye-2/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, this.packageName)
            try {
                startActivity(intent)
            }catch (exp: Exception){
                Log.d("Exception", exp.message)
            }
        }
    }
}
