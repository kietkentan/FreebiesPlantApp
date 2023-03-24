package com.khtn.freebies.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.khtn.freebies.R
import com.khtn.freebies.adapter.OnboardAdapter
import com.khtn.freebies.module.OnboardItems
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3

@AndroidEntryPoint
class OnboardActivity : AppCompatActivity(), OnClickListener {
    private lateinit var viewPagerIntro : ViewPager2
    private lateinit var circleIndicator : CircleIndicator3
    private lateinit var btnNext : AppCompatButton
    private val list : List<OnboardItems> = OnboardItems.getData()

    private val SIZE = list.size
    private var previousState =  ViewPager2.SCROLL_STATE_IDLE
    private var currentPage : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)

        viewPagerIntro = findViewById(R.id.view_pager_onboard)
        circleIndicator = findViewById(R.id.circle_indicator)
        btnNext = findViewById(R.id.btn_next)

        viewPagerIntro.adapter = OnboardAdapter(list, supportFragmentManager, lifecycle)
        viewPagerIntro.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position

                when (position) {
                    2 -> btnNext.text = getString(R.string.login)
                    else -> btnNext.text = getString(R.string.next)
                }
            }

            @Suppress("KotlinConstantConditions")
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if ((currentPage >= SIZE - 1)// end of list. these checks can be
                    // used individualy to detect end or start of pages
                    && previousState == ViewPager2.SCROLL_STATE_DRAGGING // from    DRAGGING
                    && state == ViewPager2.SCROLL_STATE_IDLE) {          // to      IDLE
                    //overscroll performed. do your work here
                    onSigIn()
                }
                previousState = state
            }
        })

        circleIndicator.setViewPager(viewPagerIntro)

        btnNext.setOnClickListener(this@OnboardActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> checkIntroPosition()
        }
    }

    private fun checkIntroPosition() {
        if (currentPage == 2) onSigIn() else onNextIntro(currentPage)
    }

    private fun onNextIntro(currentPage : Int) {
        viewPagerIntro.setCurrentItem(currentPage + 1, true)
    }

    private fun onSigIn() {
        val intent = Intent(this@OnboardActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
