package appkite.jordiguzman.com.xatentresol.fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.SignInActivity
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


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

            adapter = ArrayAdapter(activity,android.R.layout.simple_list_item_1, list )
            listview_setting.adapter = adapter

            listview_setting.setOnItemClickListener{
                _, _, position, _ ->
                  when(position){
                      0 -> deleteUser()
                      1 -> toast("Change password")
                  }

            }

        }


        return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun deleteUser() {
        XatUtil.deleteCurrentUser()
        startActivity<SignInActivity>()
    }


}


