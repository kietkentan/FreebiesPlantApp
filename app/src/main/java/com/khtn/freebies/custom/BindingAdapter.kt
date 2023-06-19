package com.khtn.freebies.custom

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.khtn.freebies.R
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.addChip

@BindingAdapter(
    value = ["loadImageProfile"],
    requireAll = false
)
fun setValue(
    shapeableImageView: ShapeableImageView,
    url: String?
) {
    url?.let { ImageUtils.loadImageProfile(shapeableImageView, it) }
}

@BindingAdapter(
    value = ["loadImage"],
    requireAll = false
)
fun setValue(
    imageView: ImageView,
    url: String?
) {
    url?.let { ImageUtils.loadImage(imageView, it) }
}

@BindingAdapter(
    value = ["loadSrc"],
    requireAll = false
)
fun setValue(
    imageView: ImageView,
    @DrawableRes src: Int?
) {
    src?.let { imageView.setImageResource(src) }
}

@BindingAdapter(
    value = ["checkNull"],
    requireAll = false
)
fun setValue(
    view: View,
    str: String?
) {
    if (str.isNullOrEmpty())
        view.visibility = View.GONE
    else view.visibility = View.VISIBLE
}

@BindingAdapter(
    value = ["addTags"],
    requireAll = false
)
fun setValue(
    chipGroup: ChipGroup,
    tags: MutableList<String>?
) {
    tags?.let {
        chipGroup.apply {
            removeAllViews()
            tags.forEachIndexed { _, tag -> addChip(tag, true) }
        }
    }
}