package appkite.jordiguzman.com.xatentresol.recyclerview.item

import android.content.Context
import android.content.Intent
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.ImageViewActivity
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.ImageMessage
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_image_message.*
import kotlinx.android.synthetic.main.item_image_message.view.*


class ImageMessageItem (val message: ImageMessage,
                        val context: Context)
    :MessageItem(message){

     companion object {
         lateinit  var pathImage: StorageReference
     }



    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)



        pathImage = StorageUtil.pathToReference(message.imagePath)
        //TODO Comprobar path con toString() y sin. Tienen que ser diferentes.
        GlideApp.with(context)
                .load(pathImage)
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(viewHolder.imageView_message_image)
        viewHolder.itemView.imageView_message_image.setOnClickListener {

            toImageActivity()


        }
    }
    override fun getLayout() = R.layout.item_image_message


    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ImageMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ImageMessageItem)
    }
    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

    private fun toImageActivity(){
        pathImage = StorageUtil.pathToReference(message.imagePath)
        val intent = Intent(context, ImageViewActivity::class.java)

        context.startActivity(intent)
    }

}







