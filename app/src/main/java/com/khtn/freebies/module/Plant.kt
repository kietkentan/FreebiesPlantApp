package com.khtn.freebies.module

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Date

@Parcelize
@Serializable
data class Plant(
    var id: String? = "",
    val name: String = "",
    val kingdom: String? = null,
    val family: String? = null,
    val speciesId: String = "",
    val description: String = "",
    var images: List<String>? = listOf(),
    val tags: MutableList<String>? = arrayListOf(),
    val createAt: Long = 0,
    val updateAt: Long = 0,
    val createBy: String = ""
): Parcelable