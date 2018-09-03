package appkite.jordiguzman.com.xatentresol.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_image_view.*

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        if (FirebaseAuth.getInstance().currentUser != null){
            Log.d("User", FirebaseAuth.getInstance().currentUser.toString())
            val bundle: Bundle = intent.extras
            val path = bundle.getString("path")
            GlideApp.with(this)
                    .load(path)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(photo_view)
        }else{

        }

    }
}
