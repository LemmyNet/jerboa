package com.jerboa.feat

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
import com.jerboa.PostType
import com.jerboa.R
import com.jerboa.getInputStream
import com.jerboa.registerActivityResultLauncher
import com.jerboa.saveMediaP
import com.jerboa.saveMediaQ
import com.jerboa.startActivitySafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

fun storeMedia(scope: CoroutineScope, ctx: Context, url: String, mediaType: PostType) {
    if (SDK_INT < 29 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        val appCompat = (ctx as MainActivity)

        appCompat.registerActivityResultLauncher(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                actualStoreImage(scope, ctx, url, mediaType)
            } else {
                Toast.makeText(ctx, ctx.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        actualStoreImage(scope, ctx, url, mediaType)
    }
}

private fun actualStoreImage(scope: CoroutineScope, ctx: Context, url: String, mediaType: PostType) {
    scope.launch {
        saveMedia(url, ctx, mediaType)
    }
}

// Needs to check for permission before this for API 29 and below
private suspend fun saveMedia(url: String, context: Context, mediaType: PostType) {
    val toastId = if (mediaType == PostType.Image) R.string.saving_image else R.string.saving_media
    Toast.makeText(context, context.getString(toastId), Toast.LENGTH_SHORT).show()

    val fileName = Uri.parse(url).pathSegments.last()
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

    try {
        withContext(Dispatchers.IO) {
            context.getInputStream(url).use {
                if (SDK_INT < 29) {
                    saveMediaP(context, it, mimeType, fileName, mediaType)
                } else {
                    saveMediaQ(context, it, mimeType, fileName, mediaType)
                }
            }
        }
        val toastId2 = if (mediaType == PostType.Image) R.string.saved_image else R.string.saved_media
        Toast.makeText(context, context.getString(toastId2), Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        Log.d("saveMedia", "failed saving media", e)
        Toast.makeText(context, R.string.failed_saving_media, Toast.LENGTH_SHORT).show()
    } catch (e: IllegalArgumentException) {
        Log.d("saveMedia", "invalid URL", e)
        Toast.makeText(context, R.string.failed_saving_media, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Shares the actual file from the link
 */
fun shareMedia(scope: CoroutineScope, ctx: Context, url: String, mediaType: PostType) {
    scope.launch(Dispatchers.Main) {
        try {
            val fileName = Uri.parse(url).pathSegments.last()

            val file = File(ctx.cacheDir, fileName)

            withContext(Dispatchers.IO) {
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
            when (mediaType) {
                PostType.Image -> shareIntent.setType("image/*")
                PostType.Video -> shareIntent.setType("video/*")
                PostType.Link -> shareIntent.setType("text/*")
            }
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            ctx.startActivitySafe(Intent.createChooser(shareIntent, ctx.getString(R.string.share)))
        } catch (e: IOException) {
            Log.d("shareMedia", "failed", e)
            Toast.makeText(ctx, R.string.failed_sharing_media, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d("shareMedia", "invalid URL", e)
            Toast.makeText(ctx, R.string.failed_sharing_media, Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Just shares the link
 */
fun shareLink(url: String, ctx: Context) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, ctx.getString(R.string.share))
    ctx.startActivitySafe(shareIntent)
}

/**
 * Opens matrix for that user
 */
fun openMatrix(matrixId: String, ctx: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://matrix.to/#/$matrixId"))
    ctx.startActivitySafe(intent)
}
