package appkite.jordiguzman.com.xatentresol.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.chat.ChatActivity
import appkite.jordiguzman.com.xatentresol.recyclerview.item.PersonItem
import appkite.jordiguzman.com.xatentresol.util.AppConstants
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.support.v4.startActivity


class PeopleFragment : Fragment() {


    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true
    private lateinit var peopleSection: Section

    companion object {
        var personIdBanned: String? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        userListenerRegistration = XatUtil.addUsersListener(this.activity!!, this::updateRecyclerView)

        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        XatUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                    setOnItemLongClickListener(onItemLongClick)

                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

    }
    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {
            startActivity<ChatActivity>(
                    AppConstants.USER_NAME to item.person.name,
                    AppConstants.USER_ID to item.userId,
                    AppConstants.USER_PATH_PHOTO to item.person.profilePicturePath!!



            )
        }
    }

    private val onItemLongClick = OnItemLongClickListener{ item, _ ->

        if (item is PersonItem){

        }

        return@OnItemLongClickListener true

    }

        
       
          
    

}
