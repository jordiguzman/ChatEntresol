package appkite.jordiguzman.com.xatentresol.activities.ui

import android.app.Activity
import android.os.Bundle
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import kotlinx.android.synthetic.main.activity_no_network.*
import org.jetbrains.anko.startActivity

class NoNetworkActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network)

        btn_no_network.setOnClickListener{
            if (XatUtil.isNetworkAvailable(this)){
                startActivity<SplashActivity>()
                finish()
            }else{
                return@setOnClickListener
            }
        }
    }


    override fun onBackPressed() {

    }
}
