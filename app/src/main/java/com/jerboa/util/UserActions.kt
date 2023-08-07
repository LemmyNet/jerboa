package com.jerboa.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.jerboa.MainActivity
import com.jerboa.R
import com.jerboa.getInputStream
import com.jerboa.registerActivityResultLauncher
import com.jerboa.saveBitmap
import com.jerboa.saveBitmapP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

fun storeImage(scope: CoroutineScope, ctx: Context, url: String) {
    if (SDK_INT < 29 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        val appCompat = (ctx as MainActivity)

        appCompat.registerActivityResultLauncher(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                actualStoreImage(scope, ctx, url)
            } else {
                Toast.makeText(ctx, ctx.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        actualStoreImage(scope, ctx, url)
    }
}

private fun actualStoreImage(scope: CoroutineScope, ctx: Context, url: String) {
    scope.launch {
        saveImage(url, ctx)
    }
}

// Needs to check for permission before this for API 29 and below
private suspend fun saveImage(url: String, context: Context) {
    Toast.makeText(context, context.getString(R.string.saving_image), Toast.LENGTH_SHORT).show()

    val fileName = Uri.parse(url).pathSegments.last()
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

    try {
        withContext(Dispatchers.IO) {
            context.getInputStream(url).use {
                if (SDK_INT < 29) {
                    saveBitmapP(context, it, mimeType, fileName)
                } else {
                    saveBitmap(context, it, mimeType, fileName)
                }
            }
        }
        Toast.makeText(context, context.getString(R.string.saved_image), Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        Log.d("image", "failed saving image", e)
        Toast.makeText(context, R.string.failed_saving_image, Toast.LENGTH_SHORT).show()
    } catch (e: IllegalArgumentException) {
        Log.d("image", "invalid URL", e)
        Toast.makeText(context, R.string.failed_saving_image, Toast.LENGTH_SHORT).show()
    }
}

fun shareImage(scope: CoroutineScope, ctx: Context, url: String) {
    try {
        val fileName = Uri.parse(url).pathSegments.last()

        val file = File(ctx.cacheDir, fileName)

        scope.launch(Dispatchers.IO) {
            ctx.getInputStream(url).use { input ->
                file.outputStream().use {
                    input.copyTo(it)
                }
            }
        }

        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".provider", file)
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.setType("image/*")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(shareIntent, ctx.getString(R.string.share)))
    } catch (e: IOException) {
        Log.d("share", "failed", e)
        Toast.makeText(ctx, R.string.failed_saving_image, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.d("image", "invalid URL", e)
        Toast.makeText(ctx, R.string.failed_saving_image, Toast.LENGTH_SHORT).show()
    }
}
