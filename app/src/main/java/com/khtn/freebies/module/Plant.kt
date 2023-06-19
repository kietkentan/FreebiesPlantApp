package com.khtn.freebies.module

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Plant(
    var id: String = "",
    val name: String = "",
    val kingdom: String = "",
    val family: String = "",
    val speciesId: String = "",
    val description: String = "",
    val images: List<String> = arrayListOf(),
    val tags: MutableList<String> = arrayListOf(),
    val createAt: Long = 0,
    val updateAt: Long = 0,
    val createBy: String = ""
): Parcelable