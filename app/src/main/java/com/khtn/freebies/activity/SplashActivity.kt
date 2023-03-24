package com.khtn.freebies.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.khtn.freebies.R
import com.khtn.freebies.helper.checkAppStart
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.schedule

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@SplashActivity)

        val str = checkAppStart(this@SplashActivity, sharedPreferences)
        Log.d("TAG_U", "onCheck: $str")

        Timer().schedule(2000){
            startOnboardActivity()
//        when (str.toString()) {
//            "NORMAL" -> startMainActivity()
//            "FIRST_TIME", "FIRST_TIME_VERSION" -> startOnboardActivity()
//        }
            finish()
        }
    }

    private fun startOnboardActivity() {
        val intent = Intent(this@SplashActivity, OnboardActivity::class.java)
        startActivity(intent)
    }

    private fun startMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
    }
}