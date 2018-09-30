package appkite.jordiguzman.com.xatentresol.activities.users

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_users_banned.*

class UsersBannedActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_banned)

        rv_banned_users.adapter = adapter

        listenForUsers()
    }

    private fun listenForUsers() {
        XatUtil.getAllUsers()
    }
}


