package appkite.jordiguzman.com.xatentresol.activities


import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.fragment.MyAcountFragment
import appkite.jordiguzman.com.xatentresol.fragment.PeopleFragment
import appkite.jordiguzman.com.xatentresol.fragment.SettingsFragment
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(PeopleFragment())

        navigation.itemBackgroundResource = R.color.colorPrimaryDark

        chechFirstTimeUser()

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_people -> {
                        replaceFragment(PeopleFragment())
                    true
                }
                R.id.navigation_my_account -> {
                    replaceFragment(MyAcountFragment())
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


                else -> false
            }
        }


    }

    private fun alertDialog(){
        val dialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialog)
                .setTitle(R.string.close_message)
        val alertDialog = builder.show()
        alertDialog.show()

        dialog.btn_yes.setOnClickListener {
            closeApp()
        }
        dialog.btn_no.setOnClickListener {
            navigation.selectedItemId = R.id.navigation_people
            replaceFragment(PeopleFragment())
            alertDialog.dismiss()
        }
    }

    private fun chechFirstTimeUser() {
        XatUtil.getCurrentUser {
            if (it.profilePicturePath == null) {
                replaceFragment(MyAcountFragment())
            }
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
