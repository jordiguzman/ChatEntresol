package appkite.jordiguzman.com.xatentresol.adapter

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
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.custom_dialog.view.*

class UserBannedAdapter(private val userBanned: ArrayList<User>, val context: Context) :
        RecyclerView.Adapter<UserBannedAdapter.AdapterUserBannedViewHolder>(){

    private var cardViewClicked = false

    override fun onBindViewHolder(holder: AdapterUserBannedViewHolder, position: Int) {
         holder.tvBannedUsers.text = userBanned[position].name

        GlideApp.with(context)
                .load(StorageUtil.pathToReference(userBanned[position].profilePicturePath!!))
                .into(holder.photoBannedUsers)


        holder.cardViewBannedUser.setOnClickListener {
            if (cardViewClicked){
                holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
                cardViewClicked = false

                return@setOnClickListener
            }
            Log.d("User", userBanned[position].name)
            alertDialog(holder)
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary_text))

            cardViewClicked = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUserBannedViewHolder {
         val v = LayoutInflater.from(parent.context).inflate(R.layout.item_banned_users, parent, false)
        return AdapterUserBannedViewHolder(v)
    }

    override fun getItemCount(): Int {
       return userBanned.size
    }
    private fun alertDialog(holder: AdapterUserBannedViewHolder){
        val dialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_banned_users, null)
        val builder = AlertDialog.Builder(context)
                .setView(dialog)
                .setTitle(R.string.report_user_message)
        val alertDialog = builder.show()
        alertDialog.show()

        dialog.btn_yes.setOnClickListener {
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
            alertDialog.dismiss()


        }
        dialog.btn_no.setOnClickListener {
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
            alertDialog.dismiss()

        }
    }



    class AdapterUserBannedViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val photoBannedUsers = view.findViewById(R.id.photo_banned_users) as CircleImageView
        val tvBannedUsers = view.findViewById(R.id.tv_banned_users) as TextView
        val cardViewBannedUser = view.findViewById(R.id.cardView_banned_user) as CardView
    }

}
