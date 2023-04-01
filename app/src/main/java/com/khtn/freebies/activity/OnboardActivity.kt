package com.khtn.freebies.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.viewpager2.widget.ViewPager2
import com.khtn.freebies.R
import com.khtn.freebies.adapter.OnboardAdapter
import com.khtn.freebies.databinding.ActivityOnboardBinding
import com.khtn.freebies.module.OnboardItems
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityOnboardBinding
    private val list : List<OnboardItems> = OnboardItems.getData()

    private val SIZE = list.size
    private var previousState =  ViewPager2.SCROLL_STATE_IDLE
    private var currentPage : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPagerOnboard.adapter = OnboardAdapter(list, supportFragmentManager, lifecycle)
        binding.circleIndicator.setViewPager(binding.viewPagerOnboard)

        binding.viewPagerOnboard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position

                binding.btnNext.text = if (position == 2) getString(R.string.login) else getString(R.string.next)
            }

            @Suppress("KotlinConstantConditions")
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if ((currentPage >= SIZE - 1)// end of list. these checks can be
                    // used individualy to detect end or start of pages
                    && previousState == ViewPager2.SCROLL_STATE_DRAGGING // from    DRAGGING
                    && state == ViewPager2.SCROLL_STATE_IDLE) {          // to      IDLE
                    //overscroll performed. work here
                    onSigIn()
                }
                previousState = state
            }
        })

        binding.btnNext.setOnClickListener(this@OnboardActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> checkIntroPosition()
        }
    }

    private fun checkIntroPosition() {
        if (currentPage == 2) onSigIn() else onNextIntro()
    }

    private fun onNextIntro() {
        binding.viewPagerOnboard.setCurrentItem(currentPage + 1, true)
    }

    private fun onSigIn() {
        val intent = Intent(this@OnboardActivity, SetupUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}
