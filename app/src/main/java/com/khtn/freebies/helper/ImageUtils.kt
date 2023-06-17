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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.khtn.freebies.R
import com.khtn.freebies.custom.DialogImageResourceSheet
import com.khtn.freebies.custom.SheetListener
import java.io.File

object ImageUtils {
    private const val PERMISSION_REQ_CODE = 114
    const val FROM_GALLERY = 116
    const val TAKE_PHOTO = 111

    private var photoUri: Uri? = null

    fun askPermission(context: Fragment, option: Int) {
        if (checkStoragePermission(context.requireActivity())) {
            when (option) {
                ImageOptions.CHOSE_GALLERY -> chooseGallery(context.requireActivity())

                ImageOptions.TAKE_PHOTO -> takePhoto(context.requireActivity())
            }
        }
    }

    fun askPermission(context: Activity, option: Int) {
        if (checkStoragePermission(context)) {
            when (option) {
                ImageOptions.CHOSE_GALLERY -> chooseGallery(context)

                ImageOptions.TAKE_PHOTO -> takePhoto(context)
            }
        }
    }

    fun askPermission(context: Fragment) {
        if (checkStoragePermission(context.requireActivity()))
            showCameraOptions(context)
    }

    private fun checkStoragePermission(context: Activity): Boolean {
        return checkPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    private fun showCameraOptions(context: Fragment) {
        photoUri = null
        val builder = DialogImageResourceSheet.newInstance(Bundle())
        builder.addListener(object : SheetListener {
            override fun selectedItem(index: Int) {
                when (index) {
                    ImageOptions.TAKE_PHOTO -> takePhoto(context.requireActivity())
                    ImageOptions.CHOSE_GALLERY -> chooseGallery(context.requireActivity())
                    ImageOptions.CANCEL -> return
                }
            }
        })
        builder.show(context.childFragmentManager, "")
    }

    private fun chooseGallery(context: Activity) {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            context.startActivityForResult(intent, FROM_GALLERY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePhoto(context: Activity) {
        val fileName = "Snap_" + System.currentTimeMillis() / 1000 + ".jpg"
        openCameraIntent(context, MediaStore.ACTION_IMAGE_CAPTURE, fileName, TAKE_PHOTO)
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
}