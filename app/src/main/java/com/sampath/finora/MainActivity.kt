package com.sampath.finora

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.ImageViewTarget

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoGif = findViewById<ImageView>(R.id.logoGif)

        // Load and play GIF splash once
        Glide.with(this)
            .asGif()
            .load(R.drawable.logoo) // "logoo.gif" in res/drawable
            .into(object : ImageViewTarget<GifDrawable>(logoGif) {
                override fun setResource(resource: GifDrawable?) {
                    resource?.let {
                        it.setLoopCount(1) // Play once
                        logoGif.setImageDrawable(it)
                        it.start()

                        // Fixed delay (adjust to your GIF length, e.g., 3 seconds)
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@MainActivity, Signup::class.java)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }, 3000) // 3 seconds delay
                    }
                }
            })
    }
}
