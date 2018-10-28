package appkite.jordiguzman.com.xatentresol.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.email.GMailSender
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.custom_dialog_banned_users.view.*
import org.jetbrains.anko.indeterminateProgressDialog

class UserBannedAdapter(private val userBanned: ArrayList<User>, val context: Context) :
        RecyclerView.Adapter<UserBannedAdapter.AdapterUserBannedViewHolder>(){

    private var cardViewClicked = false
    private var reportEmailBody = ""
    private  var comment = ""
    private var emailUserBanned = ""
    private val messageToBannedUser = context.getString(R.string.message_to_banned_user)
    private var isBannedUser = false


    override fun onBindViewHolder(holder: AdapterUserBannedViewHolder, position: Int) {
         holder.tvBannedUsers.text = userBanned[position].name

        if (userBanned[position].profilePicturePath != null){
            GlideApp.with(context)
                    .load(StorageUtil.pathToReference(userBanned[position].profilePicturePath!!))
                    .into(holder.photoBannedUsers)
        }else{
            holder.photoBannedUsers.background = ContextCompat.getDrawable(context, R.drawable.ic_person)
        }



        holder.cardViewBannedUser.setOnClickListener {
            if (cardViewClicked){
                holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
                cardViewClicked = false
                return@setOnClickListener
            }

            alertDialog(holder, position)
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary_text))

            cardViewClicked = true
        }
    }

    private fun updateBannedUser(position: Int) {
        if (userBanned[position].isBanned){
            alertDialogDelete()
            return
        }
        reportEmailBody = userBanned[position].name
        val bio = userBanned[position].bio
        val profilePicturePath = userBanned[position].profilePicturePath
        emailUserBanned = userBanned[position].emailUser
        userBanned[position].isBanned = true
        isBannedUser = userBanned[position].isBanned
        //TODO Mal lo de abajo
        XatUtil.updateCurrentUser(reportEmailBody, bio, profilePicturePath, true)

    }

    //TODO hacer el alert dialog para borra el usuario con su llamada a XatUtil
    @SuppressLint("InflateParams")
    private fun alertDialogDelete() {
        val dialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_banned_users, null)
        val builder = AlertDialog.Builder(context)
                .setView(dialog)
                .setTitle(R.string.report_user_message)
        val alertDialog = builder.show()
        XatUtil.deleteBannedUser() //!!!
        alertDialog.show()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUserBannedViewHolder {
         val v = LayoutInflater.from(parent.context).inflate(R.layout.item_banned_users, parent, false)
        return AdapterUserBannedViewHolder(v)
    }

    override fun getItemCount() = userBanned.size

    @SuppressLint("InflateParams")
    private fun alertDialog(holder: AdapterUserBannedViewHolder, position: Int){
        val dialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_banned_users, null)
        val builder = AlertDialog.Builder(context)
                .setView(dialog)
                .setTitle(R.string.report_user_message)
        val alertDialog = builder.show()
        alertDialog.show()


        dialog.btn_report.setOnClickListener {
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
            cardViewClicked = false
            comment = dialog.et_user_report.text.toString()
            if (!userBanned[holder.adapterPosition].isBanned){
                sendMessageToAdmin()
                sendMessageToUserBanned()
                updateBannedUser(position)
                alertDialog.dismiss()
            }else{

            }



        }
        dialog.btn_close.setOnClickListener {
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
            cardViewClicked = false
            alertDialog.dismiss()

        }
    }
    private fun sendMessageToUserBanned() {
        val sender = Thread(Runnable {
            try {
                val sender = GMailSender("xatentresol.report@gmail.com", "noes0r0todoloquereluce")
                sender.sendMail("XatEntresól",
                        messageToBannedUser,
                        "xatentresol.report@gmail.com",
                        emailUserBanned)
            } catch (e: Exception) {
                Log.e("mylog", "Error: " + e.message)
            }
        })
        sender.start()
    }

    private fun sendMessageToAdmin() {
        val progressDialog = context.indeterminateProgressDialog(context.getString(R.string.enviando_reporte))
        val idBanedUser = StorageUtil.getIdOfBannedUser()
        val sender = Thread(Runnable {
            try {
                val sender = GMailSender("xatentresol.report@gmail.com", "noes0r0todoloquereluce")
                sender.sendMail("XatEntresól",
                        reportEmailBody.plus("\n")
                                .plus(comment)
                                .plus("\n")
                                .plus(emailUserBanned)
                                .plus("\n")
                                .plus(idBanedUser),
                        "xatentresol.report@gmail.com",
                        "xatentresol.report@gmail.com")
                progressDialog.dismiss()
            } catch (e: Exception) {
                Log.e("mylog", "Error: " + e.message)
            }
        })
        sender.start()
    }




    class AdapterUserBannedViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val photoBannedUsers = view.findViewById(R.id.photo_banned_users) as CircleImageView
        val tvBannedUsers = view.findViewById(R.id.tv_banned_users) as TextView
        val cardViewBannedUser = view.findViewById(R.id.cardView_banned_user) as CardView
    }

}
