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
            when (str.toString()) {
                "NORMAL" -> startActivity(SetupUserActivity::class.java)

                "FIRST_TIME", "FIRST_TIME_VERSION" -> startActivity(OnboardActivity::class.java)
            }
            finish()
        }
    }

    private fun startActivity(clazz: Class<*>) {
        val intent = Intent(this@SplashActivity, clazz)
        startActivity(intent)
    }
}