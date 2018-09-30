package appkite.jordiguzman.com.xatentresol.recyclerview.item

import android.annotation.SuppressLint
import appkite.jordiguzman.com.xatentresol.R
import com.firebase.ui.auth.data.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_banned_users.view.*


class UserItem( private val user: ArrayList<User>): Item<ViewHolder>() {



    @SuppressLint("RestrictedApi")
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val uri = user.get(position).photoUri
        viewHolder.itemView.tv_banned_users.text =  user.get(position).name
        val targetImageview = viewHolder.itemView.photo_view_banned_users
        Picasso.get().load(uri).into(targetImageview)
     }

    override fun getLayout(): Int {
        return R.layout.item_banned_users
    }

}