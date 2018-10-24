package appkite.jordiguzman.com.xatentresol.activities.ui


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.chat.GroupChatActivity
import appkite.jordiguzman.com.xatentresol.activities.settings.MyAccountActivity
import appkite.jordiguzman.com.xatentresol.activities.settings.SignInActivity
import appkite.jordiguzman.com.xatentresol.fragment.PeopleFragment
import appkite.jordiguzman.com.xatentresol.fragment.SettingsFragment
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(PeopleFragment())
        navigation.selectedItemId = R.id.navigation_people

        setSupportActionBar(toolbar_main)
        if (!XatUtil.isNetworkAvailable(this)){
            longSnackbar(constraint_layout_main, getString(R.string.no_network)).show()
            return
        }


        if (PeopleFragment.personIdBanned != null){
            replaceFragment(PeopleFragment())
            navigation.selectedItemId = R.id.navigation_people
        }


        if (MyAccountActivity.fromMyAccount){
            replaceFragment(SettingsFragment())
            navigation.selectedItemId = R.id.navigation_settings
        }
        
        navigation.itemBackgroundResource = R.color.colorPrimaryDark
        //addBadge(0)

        checkFirstTimeUser()

        navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.navigation_people -> {
                        replaceFragment(PeopleFragment())
                    true
                }

                R.id.navigation_settings ->{
                    replaceFragment(SettingsFragment())
                    true

                }
                R.id.navigation_exit ->{
                    alertDialog()

                    true
                }
                R.id.navigation_group ->{
                    startActivity<GroupChatActivity>()
                    true
                }


                else -> false
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId!!){
            R.id.action_subscribe -> subscribeTopic()

        }
        return super.onOptionsItemSelected(item)

    }

    private fun subscribeTopic() {
        longSnackbar(constraint_layout_main, "Te has suscrito a las noticias de L'Entresòl")
         FirebaseMessaging.getInstance().subscribeToTopic("globalMessages")
    }




    @SuppressLint("InflateParams")
    private fun alertDialog(){
        val dialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialog)
                .setTitle(R.string.close_message)
        val alertDialog = builder.show()
        alertDialog.show()

        dialog.btn_yes.setOnClickListener {
            alertDialog.dismiss()
            closeApp()
        }
        dialog.btn_no.setOnClickListener {
            navigation.selectedItemId = R.id.navigation_people
            replaceFragment(PeopleFragment())
            alertDialog.dismiss()
        }
    }

    private fun checkFirstTimeUser() {
        if (!XatUtil.verifiedUserEmail()){
            SignInActivity.firstTime = false
            startActivity<SignInActivity>()
        }

    }


    private fun closeApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            finishAndRemoveTask()
        }else{
            finish()
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit()
    }

    override fun onBackPressed() {}

}
