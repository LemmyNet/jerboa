package com.jerboa.ui.components.common
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.decodeUriToBitmap
import com.jerboa.pictrsImageThumbnail
import com.jerboa.ui.theme.*

@Composable
fun CircularIcon(
    icon: String,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(icon, thumbnailSize))
            .crossfade(true) //                transformations(CircleCropTransformation())
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
    )
}

@Composable
fun LargerCircularIcon(icon: String) {
    CircularIcon(
        icon = icon,
        size = LARGER_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE
    )
}

@Preview
@Composable
fun CircularIconPreview() {
    CircularIcon(icon = sampleCommunitySafe.icon!!)
}

@Composable
fun PictrsThumbnailImage(
    thumbnail: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(thumbnail, THUMBNAIL_SIZE))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(12f))
    )
}

@Composable
fun PictrsUrlImage(
    url: String,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = (configuration.screenHeightDp - 150).dp

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(url, MAX_IMAGE_SIZE))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(100.dp, screenHeight)
    )
}

@Composable
fun PictrsBannerImage(
    url: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(url, MAX_IMAGE_SIZE))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun PickImage(
    modifier: Modifier = Modifier,
    onPickedImage: (image: Uri) -> Unit,
    image: Uri? = null,
    showImage: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    val ctx = LocalContext.current
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    if (image != null) {
        LaunchedEffect(image) {
            imageUri = image
            bitmap.value = decodeUriToBitmap(ctx, imageUri!!)
            Log.d("jerboa", imageUri.toString())
            onPickedImage(image)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        bitmap.value = decodeUriToBitmap(ctx, imageUri!!)
        Log.d("jerboa", imageUri.toString())
        onPickedImage(uri!!)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        OutlinedButton(onClick = {
            launcher.launch("image/*")
        }) {
            Text(
                text = "Upload Image",
                color = MaterialTheme.colors.onBackground.muted
            )
        }

        if (showImage) {
            Spacer(modifier = Modifier.height(SMALL_PADDING))

            imageUri?.let {
                bitmap.value?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
