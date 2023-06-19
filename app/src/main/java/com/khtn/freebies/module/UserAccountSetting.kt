package com.khtn.freebies.module

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class UserAccountSetting(
    val id: String = "",
    var display_name: String = "",
    var profile_photo: String? = null,
    var location: String? = null,
    var post_liked: Int = 0,
    var plant_liked: Int = 0,
    var posts: Int = 0,
    var plants: Int = 0
): Parcelable