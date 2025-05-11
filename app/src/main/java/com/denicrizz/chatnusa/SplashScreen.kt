package com.denicrizz.chatnusa
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.app.Activity

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root layout (LinearLayout vertikal)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Icon
        val logo = ImageView(this).apply {
            setImageResource(R.mipmap.ic_launcher_round)
            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                bottomMargin = 24
            }
        }

        // App name
        val appName = TextView(this).apply {
            text = "ChatNusa"
            textSize = 24f
            setTextColor(Color.BLACK)
            setPadding(0, 0, 0, 8)
        }

        // Tagline
        val tagline = TextView(this).apply {
            text = "Teman Informasi Anda"
            textSize = 14f
            setTextColor(Color.GRAY)
        }

        // Tambahkan ke layout
        layout.addView(logo)
        layout.addView(appName)
        layout.addView(tagline)

        // Tampilkan layout
        setContentView(layout)

        // Timer 2 detik lalu ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}




