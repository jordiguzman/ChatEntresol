package appkite.jordiguzman.com.xatentresol.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.fragment_settings, container, false)

        view.apply {
            btn_delete_user.setOnClickListener {
                deleteUser()
            }


        }




        return view
    }

    private fun deleteUser() {

        XatUtil.deleteCurrentUser()

    }


}


