package appkite.jordiguzman.com.xatentresol.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import appkite.jordiguzman.com.xatentresol.R
import com.google.firebase.firestore.ListenerRegistration


class PeopleFragment : Fragment() {


    private lateinit var userListenerRegistration: ListenerRegistration

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

}// Required empty public constructor
