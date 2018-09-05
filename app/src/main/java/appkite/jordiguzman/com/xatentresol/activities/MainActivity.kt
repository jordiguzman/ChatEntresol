package appkite.jordiguzman.com.xatentresol.activities


import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.fragment.MyAcountFragment
import appkite.jordiguzman.com.xatentresol.fragment.PeopleFragment
import appkite.jordiguzman.com.xatentresol.fragment.SettingsFragment
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {




    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(PeopleFragment())

        navigation.itemBackgroundResource = R.color.colorPrimaryDark




        XatUtil.getCurrentUser {

                if (it.profilePicturePath == null) {
                    replaceFragment(MyAcountFragment())
                }

        }

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
                R.id.navigation_help ->{
                    true
                }
                R.id.navigation_exit ->{
                    closeApp()
                    true
                }


                else -> false
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun closeApp() {
         finishAndRemoveTask()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit()


    }

    override fun onBackPressed() {

    }
}
