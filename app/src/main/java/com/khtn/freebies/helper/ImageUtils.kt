package com.khtn.freebies.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.khtn.freebies.R
import com.khtn.freebies.custom.DialogImageResourceSheet
import com.khtn.freebies.custom.SheetListener
import java.io.File

object ImageUtils {
    private const val PERMISSION_REQ_CODE = 114
    const val FROM_GALLERY_MAIN = 116
    const val TAKE_PHOTO_HOME = 111
    const val FROM_GALLERY_PROFILE = 120
    const val FROM_GALLERY_ADDING_NEW = 121
    const val TAKE_PHOTO_PROFILE = 124
    const val TAKE_PHOTO_ADDING_NEW = 125

    const val IN_PROFILE = 10
    const val IN_ADDING_NEW = 11

    private var photoUri: Uri? = null

    fun askPermission(context: Fragment, option: Int) {
        if (checkStoragePermission(context.requireActivity())) {
            when (option) {
                ImageOptions.CHOSE_GALLERY -> chooseGallery(context.requireActivity(), true, FROM_GALLERY_MAIN)

                ImageOptions.TAKE_PHOTO -> takePhoto(context.requireActivity(), TAKE_PHOTO_HOME)

                else -> showCameraOptions(context, true, option)
            }
        }
    }

    fun askPermission(context: Activity, option: Int) {
        if (checkStoragePermission(context)) {
            if (option == ImageOptions.CHOSE_GALLERY)
                chooseGallery(context, true, FROM_GALLERY_MAIN)
        }
    }

    private fun checkStoragePermission(context: Activity): Boolean {
        return checkPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    private fun showCameraOptions(context: Fragment, isMultiple: Boolean, option: Int) {
        photoUri = null
        val builder = DialogImageResourceSheet.newInstance(Bundle())
        builder.addListener(object : SheetListener {
            override fun selectedItem(index: Int) {
                when (index) {
                    ImageOptions.TAKE_PHOTO -> takePhoto(
                        context = context.requireActivity(),
                        reqCode = if (option == IN_PROFILE) TAKE_PHOTO_PROFILE else TAKE_PHOTO_ADDING_NEW
                    )

                    ImageOptions.CHOSE_GALLERY -> chooseGallery(
                        context = context.requireActivity(),
                        isMultiple = isMultiple,
                        reqCode = if (option == IN_PROFILE) FROM_GALLERY_PROFILE else FROM_GALLERY_ADDING_NEW
                    )

                    ImageOptions.CANCEL -> return
                }
            }
        })
        builder.show(context.childFragmentManager, "")
    }

    private fun chooseGallery(context: Activity, isMultiple: Boolean, reqCode: Int) {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            context.startActivityForResult(intent, reqCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePhoto(context: Activity, reqCode: Int) {
        val fileName = "Snap_" + System.currentTimeMillis() / 1000 + ".jpg"
        openCameraIntent(context, MediaStore.ACTION_IMAGE_CAPTURE, fileName, reqCode)
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Suppress("SameParameterValue")
    private fun openCameraIntent(
        context: Activity,
        action: String,
        fileName: String,
        reqCode: Int
    ) {
        try {
            val intent = Intent(action)
            if (intent.resolveActivity(context.packageManager) != null) {
                val file = File(createImageFolder(context, ""), fileName)
                photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    FileProvider.getUriForFile(context, providerPath(context), file)
                else Uri.fromFile(file)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                context.startActivityForResult(intent, reqCode)
                context.overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            } else
                context.toast(context.getString(R.string.camera_not_available))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getPhotoUri(data: Intent?): Uri? {
        return if (data == null || data.data == null) photoUri else data.data
    }

    private fun providerPath(context: Context): String {
        return context.packageName + ".fileprovider"
    }

    private fun createImageFolder(
        context: Context,
        path: String
    ): String? {
        val folderPath = context.getExternalFilesDir("")
            ?.absolutePath + "/" + context.getString(R.string.app_name)
        try {
            val file = File("$folderPath/$path")
            if (!file.exists())
                file.mkdirs()
            return file.absolutePath
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return folderPath
    }

    @Suppress("DEPRECATION")
    private fun checkPermission(
        context: Activity,
        vararg permissions: String,
        reqCode: Int = PERMISSION_REQ_CODE
    ): Boolean {
        var allPermitted = false
        for (permission in permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED)
            if (!allPermitted) break
        }
        if (allPermitted) return true
        context.requestPermissions(
            permissions,
            reqCode
        )
        return false
    }

    fun loadImageProfile(
        shapeableImageView: ShapeableImageView,
        url: String
    ) {
        Glide.with(shapeableImageView.context)
            .load(url)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(shapeableImageView)
    }

    fun loadImage(
        imageView: ImageView,
        url: String
    ) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(imageView)
    }

    fun loadImage(
        imageView: ImageView,
        @DrawableRes src: Int
    ) {
        Glide.with(imageView.context)
            .load(src)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(imageView)
    }
}