package appkite.jordiguzman.com.xatentresol.activities.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.view.View
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.recyclerview.item.ImageMessageItem
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_image_view.*
import java.util.concurrent.ExecutionException


class ImageViewActivity : AppCompatActivity() {

    private var mMutedColor: Int = 0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)


        hideNavigation()

            GlideApp.with(this)
                    .load(ImageMessageItem.pathImage)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(photo_view)

        //setBackground(ImageMessageItem.pathImage)
        constraint_layout_imageview.setBackgroundColor(R.color.color_background_chat)

    }

    private fun setBackground(url: StorageReference) {
        Thread(Runnable {
            try {
                val bitmap = Glide.with(applicationContext)
                        .asBitmap()
                        .load(url)
                        .submit(500, 500)
                        .get()
                if (bitmap != null) {

                    val p = Palette.from(bitmap).generate()
                    mMutedColor = p.getDarkMutedColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                    constraint_layout_imageview.setBackgroundColor(mMutedColor)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        })
    }

    fun hideNavigation() {
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
