package appkite.jordiguzman.com.xatentresol.fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.ChangePasswordActivity
import appkite.jordiguzman.com.xatentresol.activities.MainActivity
import appkite.jordiguzman.com.xatentresol.activities.SignInActivity
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.support.v4.startActivity


class SettingsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.fragment_settings, container, false)

        view.apply {
             val list = ArrayList<String>()
            val adapter: ArrayAdapter<String>?
            list.add("Delete user")
            list.add("Change password")
            list.add("Push notification")

            adapter = ArrayAdapter(activity,android.R.layout.simple_list_item_1, list )
            listview_setting.adapter = adapter

            listview_setting.setOnItemClickListener{
                _, _, position, _ ->
                  when(position){
                      0 -> alertDialog()
                      1 -> changePassword()
                      3 -> pushNotification()
                  }

            }

        }

        return view
    }

    private fun pushNotification() {

    }

    private fun changePassword() {
         startActivity<ChangePasswordActivity>()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun deleteUser() {
        XatUtil.deleteCurrentUser()
        startActivity<SignInActivity>()
    }

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
            startActivity<MainActivity>()
            alertDialog?.dismiss()
        }
    }

}


