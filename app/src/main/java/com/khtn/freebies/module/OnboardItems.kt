package com.khtn.freebies.module

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.khtn.freebies.R

class OnboardItems(
    @DrawableRes
    val image : Int,
    @StringRes
    val title : Int,
    @StringRes
    val desc : Int
) {
    companion object {
        fun getData() : List<OnboardItems> {
            return listOf(
                OnboardItems(R.drawable.intro_1, R.string.title_intro_1, R.string.text_intro_1),
                OnboardItems(R.drawable.intro_2, R.string.title_intro_2, R.string.text_intro_2),
                OnboardItems(R.drawable.intro_3, R.string.title_intro_3, R.string.text_intro_3)
            )
        }
    }
}