package com.khtn.freebies.module

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    var id : String = "",
    var name : String = "",
    var email : String = "",
    var avatar : String = ""
): Parcelable