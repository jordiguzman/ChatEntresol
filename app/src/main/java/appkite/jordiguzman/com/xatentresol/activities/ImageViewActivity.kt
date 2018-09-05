package appkite.jordiguzman.com.xatentresol.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.recyclerview.item.ImageMessageItem
import kotlinx.android.synthetic.main.activity_image_view.*

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)



            GlideApp.with(this)
                    .load(ImageMessageItem.pathImage)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(photo_view)


    }
}
