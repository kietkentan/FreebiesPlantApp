package com.khtn.freebies.module

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Species(
    var name: String = "",
    var id: String = ""
): Parcelable