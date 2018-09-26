package appkite.jordiguzman.com.xatentresol.util


import android.content.Context
import android.util.Log
import android.widget.Toast
import appkite.jordiguzman.com.xatentresol.model.*
import appkite.jordiguzman.com.xatentresol.recyclerview.item.ImageMessageItem
import appkite.jordiguzman.com.xatentresol.recyclerview.item.PersonItem
import appkite.jordiguzman.com.xatentresol.recyclerview.item.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item


object XatUtil {

    private val chatInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = chatInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    private var mAuth: FirebaseAuth? = null
    private var userList = mutableListOf<String>()

    private val chatChannelsCollectionRef = chatInstance.collection("chatChannels")


    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                        "", null,  mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    fun sendEmailVerification(context: Context){
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth!!.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        //Toast.makeText(context, "Verification email sent to " + user.email!!, Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
    }
    fun verifiedUserEmail(context: Context): Boolean{
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth!!.currentUser
        if (!user!!.isEmailVerified){
            Toast.makeText(context, "Verifica tu cuenta en el correo.", Toast.LENGTH_SHORT).show()
            return false
        }
        return  true
    }


    fun deleteCurrentUser(){
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
         db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                 .delete()
        db.collection("chatChannels").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .delete()
        chatChannelsCollectionRef.document(FirebaseAuth.getInstance().currentUser!!.uid)
                .delete()
        /**
         * The profile photos not delete. If the amount of these is great delete manually.
         */

        val auth = FirebaseAuth.getInstance().currentUser
        auth?.delete()
        currentUserDocRef.delete()
    }

    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePicturePath != null){
            userFieldMap["profilePicturePath"] = profilePicturePath

        }


        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
         try {
             currentUserDocRef.get()
                     .addOnSuccessListener {
                         onComplete(it.toObject(User::class.java)!!)
                     }
         }catch (e: Exception){
             Log.d("Error", e.message)

         }


    }
    fun getAllUsers(){
        val pathUser = "users"
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(pathUser)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                for (document in task.result){
                    userList.add(document.id)

                }
                showUsers()
            }
        }

    }

    private fun showUsers() {
        for (i: Int in userList.indices){
            Log.d("Users", userList[i].plus("\n"))
        }

        }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatInstance.collection("users")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("XatEntresol", "Users listener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                            items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                    }
                    onListen(items)
                }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()



    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }

                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                    val newChannel = chatChannelsCollectionRef.document()
                    newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                    currentUserDocRef
                            .collection("engagedChatChannels")
                            .document(otherUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    chatInstance.collection("users").document(otherUserId)
                            .collection("engagedChatChannels")
                            .document(currentUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
    }


    fun deleteChatChannel(otherUserId: String,
                          onComplete: (channelId: String) -> Unit){
        currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId).get().addOnSuccessListener {
                    if (it.exists()){
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }
                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                    val channelToDelete = chatChannelsCollectionRef.document()
                    channelToDelete.delete()
                    currentUserDocRef
                            .collection("engagedChatChannels")
                            .document(otherUserId)
                            .delete()
                    chatInstance.collection("users").document(otherUserId)
                            .collection("engagedChatChannels")
                            .document(currentUserId)
                            .delete()

                    onComplete(channelToDelete.id)


                }

    }
    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
                .orderBy("time")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("XatEntresol", "ChatMessagesListener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        if (it["type"] == MessageType.TEXT)
                            items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                        else
                            items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                        return@forEach
                    }
                    onListen(items)
                }
    }
    fun sendMessage(message: Message, channelId: String){
        chatChannelsCollectionRef.document(channelId)
                .collection("messages")
                .add(message)

    }





    //Region FCM
    fun getFCMRegistrtionTokens(onComplete: (tokens: MutableList<String>) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            val user= it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrtionTokens(registrationTokens: MutableList<String>){
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }
    //endRegion FCM


}



