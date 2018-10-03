package appkite.jordiguzman.com.xatentresol.activities.chat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.model.TextMessage
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_group_chat.*
import java.util.*

class GroupChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section
    private val firebaseMessage = FirebaseMessaging.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)


        firebaseMessage.isAutoInitEnabled
        XatUtil.getCurrentUser {
            currentUser = it
        }

        otherUserId = "EhSAF1HIsdTBHr4chS9S"

        XatUtil.getOrCreateChatChannelGroup(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration =
                    XatUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send_group.setOnClickListener {
                if (editText_message_group.text.isEmpty())return@setOnClickListener


                val messageToSend =
                        TextMessage(editText_message_group.text.toString(), Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                otherUserId, currentUser.name)
                editText_message_group.setText("")
                XatUtil.sendMessage(messageToSend, channelId)

            }
            fab_send_image_group.setOnClickListener {
                //alertDialog()

            }
        }
    }
    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recycler_view_group.apply {
                layoutManager = LinearLayoutManager(this@GroupChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()


        recycler_view_group.scrollToPosition(recycler_view_group.adapter.itemCount - 1)
    }
}
