package appkite.jordiguzman.com.xatentresol.activities.ui

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.recyclerview.item.ImageMessageItem
import kotlinx.android.synthetic.main.activity_image_view.*


class ImageViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)



        hideNavigation()

            GlideApp.with(this)
                    .load(ImageMessageItem.pathImage)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(photo_view)


        constraint_layout_imageview.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_text))


        photo_view.setOnLongClickListener{
            onBackPressed()
            return@setOnLongClickListener true
        }


    }

    private fun hideNavigation() {
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
        }
    }

}


