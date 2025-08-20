package com.jerboa.feat

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import com.jerboa.MainActivity
import com.jerboa.PostLinkType
import com.jerboa.R
import com.jerboa.getInputStream
import com.jerboa.registerActivityResultLauncher
import com.jerboa.startActivitySafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream

fun storeMedia(
    scope: CoroutineScope,
    ctx: Context,
    url: String,
    mediaType: PostLinkType,
) {
    if (SDK_INT < 29 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        val appCompat = (ctx as MainActivity)

        appCompat
            .registerActivityResultLauncher(ActivityResultContracts.RequestPermission()) { granted ->
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

private fun actualStoreImage(
    scope: CoroutineScope,
    ctx: Context,
    url: String,
    mediaType: PostLinkType,
) {
    scope.launch {
        saveMedia(url, ctx, mediaType)
    }
}

// Needs to check for permission before this for API 29 and below
private suspend fun saveMedia(
    rawUrl: String,
    context: Context,
    mediaType: PostLinkType,
) {
    val toastId = if (mediaType == PostLinkType.Image) R.string.saving_image else R.string.saving_media
    Toast.makeText(context, context.getString(toastId), Toast.LENGTH_SHORT).show()

    val uri = rawUrl.parseUriWithProxyImageSupport()
    val url = uri.toString()
    val fileName = uri.pathSegments.last()
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

    try {
        withContext(Dispatchers.IO) {
            context.getInputStream(rawUrl).use {
                if (SDK_INT < 29) {
                    saveMediaP(context, it, mimeType, fileName, mediaType)
                } else {
                    saveMediaQ(context, it, mimeType, fileName, mediaType)
                }
            }
        }
        val toastId2 = if (mediaType == PostLinkType.Image) R.string.saved_image else R.string.saved_media
        Toast.makeText(context, context.getString(toastId2), Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        Log.d("saveMedia", "failed saving media", e)
        Toast.makeText(context, R.string.failed_saving_media, Toast.LENGTH_SHORT).show()
    } catch (e: IllegalArgumentException) {
        Log.d("saveMedia", "invalid URL", e)
        Toast.makeText(context, R.string.failed_saving_media, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.d("saveMedia", "unexpected error", e)
        Toast.makeText(context, R.string.failed_saving_media, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Shares the actual file from the link
 */
fun shareMedia(
    scope: CoroutineScope,
    ctx: Context,
    rawUrl: String,
    mediaType: PostLinkType,
) {
    if (mediaType == PostLinkType.Link) {
        shareLink(rawUrl, ctx)
        return
    }

    try {
        val uri = rawUrl.parseUriWithProxyImageSupport()
        val fileName = uri.pathSegments.last()
        val file = File(ctx.cacheDir, fileName)

        scope.launch(Dispatchers.IO) {
            ctx.getInputStream(rawUrl).use { input ->
                file.outputStream().use {
                    input.copyTo(it)
                }
            }
        }

        val fileUri = FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = when (mediaType) {
                PostLinkType.Image -> "image/*"
                PostLinkType.Video -> "video/*"
                PostLinkType.Link -> throw IllegalStateException("Should be impossible")
            }
            clipData = ClipData.newUri(ctx.contentResolver, fileName, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        ctx.startActivitySafe(Intent.createChooser(shareIntent, ctx.getString(R.string.share)))
    } catch (e: IOException) {
        Log.d("shareMedia", "io failed", e)
        Toast.makeText(ctx, R.string.failed_sharing_media, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.d("shareMedia", "failed", e)
        Toast.makeText(ctx, R.string.failed_sharing_media, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Just shares the link
 */
fun shareLink(
    url: String,
    ctx: Context,
) {
    val intent =
        Intent().apply {
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
fun openMatrix(
    matrixId: String,
    ctx: Context,
) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://matrix.to/#/$matrixId"))
    ctx.startActivitySafe(intent)
}

/**
 * Copy a given text to the clipboard, using the Kotlin context
 *
 * @param context The app context
 * @param textToCopy Text to copy to the clipboard
 * @param clipLabel Label
 * @param resId Optional string resource ID, if included will be shown as a toast message
 *
 */
fun copyTextToClipboard(
    context: Context,
    textToCopy: CharSequence,
    clipLabel: CharSequence,
    @StringRes resId: Int?,
) {
    val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(clipLabel, textToCopy)
    clipboard.setPrimaryClip(clip)
    if (resId != null) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
    }
}

fun copyImageToClipboard(
    scope: CoroutineScope,
    ctx: Context,
    rawUrl: String,
) {
    try {
        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val uri = rawUrl.parseUriWithProxyImageSupport()
        val fileName = uri.pathSegments.last()
        val file = File(ctx.cacheDir, fileName)

        scope.launch(Dispatchers.IO) {
            ctx.getInputStream(rawUrl).use { input ->
                file.outputStream().use {
                    input.copyTo(it)
                }
            }
        }

        val fileUri = FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)
        clipboard.setPrimaryClip(ClipData.newUri(ctx.contentResolver, fileName, fileUri))

        // Android 13+ should show a system message already
        // see https://developer.android.com/develop/ui/views/touch-and-input/copy-paste#duplicate-notifications
        if (SDK_INT <= 32) {
            Toast.makeText(ctx, ctx.getString(R.string.media_copied), Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        Log.d("copyMedia", "io failed", e)
        Toast.makeText(ctx, R.string.failed_copy_media, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.d("copyMedia", "failed", e)
        Toast.makeText(ctx, R.string.failed_copy_media, Toast.LENGTH_SHORT).show()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Throws(IOException::class)
private fun saveMediaQ(
    ctx: Context,
    inputStream: InputStream,
    mimeType: String?,
    displayName: String,
    mediaType: PostLinkType,
): Uri {
    val mimeTypeWithFallback = mimeType ?: when (mediaType) {
        PostLinkType.Image -> "image/jpeg"
        PostLinkType.Video -> "video/mpeg"
        PostLinkType.Link -> null
    }

    val values =
        ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeTypeWithFallback)
            put(MediaStore.MediaColumns.RELATIVE_PATH, mediaType.toMediaDir() + "/Jerboa")
        }

    val resolver = ctx.contentResolver
    var uri: Uri? = null

    try {
        val insert =
            when (mediaType) {
                PostLinkType.Image -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                PostLinkType.Video -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                PostLinkType.Link -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }

        uri = resolver.insert(insert, values)
            ?: throw IOException("Failed to create new MediaStore record.")

        resolver.openOutputStream(uri)?.use {
            inputStream.copyTo(it)
        } ?: throw IOException("Failed to open output stream.")

        return uri
    } catch (e: IOException) {
        uri?.let { orphanUri ->
            // Don't leave an orphan entry in the MediaStore
            resolver.delete(orphanUri, null, null)
        }

        throw e
    }
}

// saveMedia that works for Android 9 and below
private fun saveMediaP(
    context: Context,
    inputStream: InputStream,
    mimeType: String?,
    displayName: String,
    // Link is here more like other media (think of PDF, doc, txt)
    mediaType: PostLinkType,
) {
    val dir = Environment.getExternalStoragePublicDirectory(mediaType.toMediaDir())
    val mediaDir = File(dir, "Jerboa")
    val dest = File(mediaDir, displayName)

    mediaDir.mkdirs() // make if not exist

    inputStream.use { input ->
        dest.outputStream().use {
            input.copyTo(it)
        }
    }
    // Makes it show up in gallery
    val mimeTypes = if (mimeType == null) null else arrayOf(mimeType)
    MediaScannerConnection.scanFile(context, arrayOf(dest.absolutePath), mimeTypes, null)
}
