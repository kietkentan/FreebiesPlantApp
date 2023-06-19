package com.khtn.freebies.module

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Articles(
    val id: String = "",
    val content: String = "",
    val title: String = "",
    val sortTitle: String = "",
    val thumbnail: String = "",
    val images: List<String> = arrayListOf(),
    val tags: MutableList<String> = arrayListOf(),
    val createAt: Long = 0,
    val updateAt: Long = 0,
    val createBy: String = "",
): Parcelable