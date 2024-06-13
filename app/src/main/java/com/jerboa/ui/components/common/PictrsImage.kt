package com.jerboa.ui.components.common

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunity
import com.jerboa.pictrsImageThumbnail
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.LARGER_ICON_SIZE
import com.jerboa.ui.theme.LARGER_ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.MAX_IMAGE_SIZE
import com.jerboa.ui.theme.THUMBNAIL_SIZE
import com.jerboa.ui.theme.muted
import com.jerboa.util.BlurTransformation

@Composable
fun CircularIcon(
    modifier: Modifier = Modifier,
    icon: String,
    contentDescription: String?,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE,
    blur: Boolean = false,
) {
    val ctx = LocalContext.current
    val imageRequest =
        remember {
            getImageRequest(
                context = ctx,
                path = icon,
                size = thumbnailSize,
                blur = blur,
            )
        }

    AsyncImage(
        model = imageRequest,
        placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .getBlurredOrRounded(
                rounded = true,
                blur = blur,
            )
            .size(size),
    )
}

@Composable
fun LargerCircularIcon(
    modifier: Modifier = Modifier,
    icon: String,
    contentDescription: String? = null,
    blur: Boolean = false,
) {
    CircularIcon(
        modifier = modifier,
        icon = icon,
        contentDescription = contentDescription,
        size = LARGER_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
        blur = blur,
    )
}

@Preview
@Composable
fun CircularIconPreview() {
    CircularIcon(
        icon = sampleCommunity.icon!!,
        contentDescription = "",
    )
}

fun getImageRequest(
    context: Context,
    path: String,
    size: Int,
    blur: Boolean,
): ImageRequest {
    val builder =
        ImageRequest.Builder(context)
            .data(pictrsImageThumbnail(path, size))
            .crossfade(true)

    if (blur && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        builder.transformations(
            listOf(
                BlurTransformation(
                    scale = 0.5f,
                    radius = 100,
                ),
            ),
        )
    }

    return builder.build()
}

@Composable
fun PictrsThumbnailImage(
    thumbnail: String,
    blur: Boolean,
    roundBottomEndCorner: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val ctx = LocalContext.current
    val imageRequest =
        remember {
            getImageRequest(
                context = ctx,
                path = thumbnail,
                size = THUMBNAIL_SIZE,
                blur = blur,
            )
        }

    AsyncImage(
        model = imageRequest,
        placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .getBlurredOrRounded(
                    rounded = false,
                    blur = blur,
                )
                .clip(
                    RoundedCornerShape(
                        12f,
                        12f,
                        if (roundBottomEndCorner) 0f else 12f,
                        12f,
                    ),
                ),
    )
}

@Composable
fun PictrsUrlImage(
    url: String,
    blur: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val ctx = LocalContext.current
    val imageRequest =
        remember {
            getImageRequest(
                context = ctx,
                path = url,
                size = MAX_IMAGE_SIZE,
                blur = blur,
            )
        }

    AsyncImage(
        model = imageRequest,
        placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillWidth,
        modifier =
            modifier
                .getBlurredOrRounded(
                    rounded = false,
                    blur = blur,
                )
                .fillMaxWidth(),
    )
}

@Composable
fun PictrsBannerImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    blur: Boolean = false,
) {
    val ctx = LocalContext.current
    val imageRequest =
        remember {
            getImageRequest(
                context = ctx,
                path = url,
                size = MAX_IMAGE_SIZE,
                blur = blur,
            )
        }

    AsyncImage(
        model = imageRequest,
        placeholder = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillWidth,
        modifier =
            modifier
                .getBlurredOrRounded(
                    blur = blur,
                )
                .fillMaxWidth(),
    )
}

@Composable
fun ColumnScope.PickImage(
    modifier: Modifier = Modifier,
    onPickedImage: (image: Uri) -> Unit,
    sharedImage: Uri? = null,
    isUploadingImage: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    fun initiateUpload(uri: Uri) {
        Log.d("jerboa", "Uploading image...")
        Log.d("jerboa", uri.toString())
        onPickedImage(uri)
    }

    if (sharedImage != null) {
        LaunchedEffect(sharedImage) {
            initiateUpload(sharedImage)
        }
    }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let {
                initiateUpload(it)
            }
        }

    OutlinedButton(
        modifier = modifier.align(horizontalAlignment),
        onClick = {
            launcher.launch("image/*")
        },
    ) {
        if (isUploadingImage) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
        } else {
            Text(
                text = stringResource(R.string.pictrs_image_upload_image),
                color = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
    }
}
