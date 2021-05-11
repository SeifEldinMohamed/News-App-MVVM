package com.seif.newsappmvvm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.seif.newsappmvvm.R
import kotlinx.android.synthetic.main.activity_splash.*

class Splash : AppCompatActivity() {
    private val splashTime: Long = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        txt_companyName.alpha = 0f
        txt_companyName.animate().setDuration(2000).alpha(1.5f)
        Handler().postDelayed({
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        }, splashTime)
    }
}
