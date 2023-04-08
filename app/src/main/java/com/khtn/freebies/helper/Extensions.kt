package com.khtn.freebies.helper

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.khtn.freebies.R

fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.disable(){
    isEnabled = false
}

fun View.enabled(){
    isEnabled = true
}

fun Fragment.toast(msg: String?){
    Toast.makeText(requireContext(),msg, Toast.LENGTH_LONG).show()
}

@SuppressLint("InflateParams")
fun ChipGroup.addChip(
    text: String,
    isTouchTargeSize: Boolean = false
) {
    val chip: Chip = LayoutInflater.from(context).inflate(R.layout.item_chip,null,false) as Chip
    chip.text = text
    chip.setTypeface(chip.typeface, Typeface.BOLD)
    chip.setEnsureMinTouchTargetSize(isTouchTargeSize)
    addView(chip)
}

fun Context.createDialog(layout: Int, cancelable: Boolean): Dialog {
    val dialog = Dialog(this, android.R.style.Theme_Dialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(layout)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(cancelable)
    return dialog
}

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val View.keyboardIsVisible: Boolean
    get() = WindowInsetsCompat
        .toWindowInsetsCompat(rootWindowInsets)
        .isVisible(WindowInsetsCompat.Type.ime())

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword() =
    isNotEmpty() && matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%!\\-_?&./])(?=\\S+\$).{11,}".toRegex())