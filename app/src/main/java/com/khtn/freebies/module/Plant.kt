package com.khtn.freebies.module

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Plant(
    var id: String = "",
    var uploaderId: String = "",
    val name: String = "",
    val kingdom: String = "",
    val family: String = "",
    val speciesId: String = "",
    val description: String = "",
    val images: List<String> = arrayListOf(),
    val tags: List<String> = arrayListOf(),
    @ServerTimestamp
    val date: Date = Date()
): Parcelable