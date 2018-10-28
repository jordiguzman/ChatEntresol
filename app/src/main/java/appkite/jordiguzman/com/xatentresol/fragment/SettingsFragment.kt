package appkite.jordiguzman.com.xatentresol.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.legal.LegalActivity
import appkite.jordiguzman.com.xatentresol.activities.settings.ChangePasswordActivity
import appkite.jordiguzman.com.xatentresol.activities.settings.MyAccountActivity
import appkite.jordiguzman.com.xatentresol.activities.settings.NotificationsSettingsActivity
import appkite.jordiguzman.com.xatentresol.activities.settings.SignInActivity
import appkite.jordiguzman.com.xatentresol.activities.ui.InfoAppActivity
import appkite.jordiguzman.com.xatentresol.activities.users.UsersBannedActivity
import appkite.jordiguzman.com.xatentresol.adapter.SettingsAdapter
import appkite.jordiguzman.com.xatentresol.model.ItemSettings
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity


class SettingsFragment : Fragment() {


    private var listLogo = intArrayOf(
            R.drawable.ic_delete,
            R.drawable.ic_change,
            R.drawable.ic_vpn_key_black_24dp,
            R.drawable.ic_notifications,
            R.drawable.ic_info,
            R.drawable.ic_person,
            R.drawable.ic_close_black_24dp,
            R.drawable.ic_info_outline_black_24dp)



            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.fragment_settings, container, false)

        view.apply {

            val settingsAdapter: SettingsAdapter
            val arraySettings: ArrayList<ItemSettings> = populateList()

            settingsAdapter = SettingsAdapter(view.context, arraySettings)
            listview_setting.adapter = settingsAdapter

            listview_setting.setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> alertDialog()
                    1 -> changePassword()
                    2 -> toMyAccount()
                    3 -> pushNotification()
                    4 -> toLegal()
                    5 -> reportUser()
                    6 -> closeSession()
                    7 -> infoApp()


                }
            }
        }
        return view
    }

    private fun infoApp() {
        startActivity<InfoAppActivity>()
    }

    private fun closeSession() {
        AuthUI.getInstance()
                .signOut(context!!)
                .addOnCompleteListener {
                    SignInActivity.firstTime = true
                    startActivity(intentFor<SignInActivity>().newTask().clearTask())
                }
    }

    private fun toLegal() {
        startActivity<LegalActivity>()
    }

    private fun reportUser() {

        startActivity<UsersBannedActivity>()


    }

    private fun populateList(): ArrayList<ItemSettings>{
        val list = ArrayList<ItemSettings>()

        for (i in listLogo.indices){
            val itemSettings = ItemSettings()
            itemSettings.setLogo(listLogo[i])
            itemSettings.setTitles(resources.getStringArray(R.array.list_settings)[i])
            list.add(itemSettings)
        }

        return list
    }

    private fun toMyAccount() {
        startActivity<MyAccountActivity>()
    }


    private fun pushNotification() {
        startActivity<NotificationsSettingsActivity>()
    }

    private fun changePassword() {
         startActivity<ChangePasswordActivity>()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun deleteUser() {
        XatUtil.deleteCurrentUser()
        startActivity<SignInActivity>()
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun alertDialog(){
        val dialog = LayoutInflater.from(activity).inflate(R.layout.custom_dialog, null)
        val builder = activity?.let {
            AlertDialog.Builder(it)
                .setView(dialog)
                .setTitle(R.string.delete_message)
        }
        val alertDialog = builder?.show()
        alertDialog?.show()

        dialog.btn_yes.setOnClickListener {
             deleteUser()
        }
        dialog.btn_no.setOnClickListener {
            alertDialog?.dismiss()
        }
    }

}

