package com.jerboa.ui.components.blocks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.api.ApiState
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.SMALL_ICON_SIZE
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun BlockedElement(
    apiState: ApiState<*>,
    icon: String?,
    name: String,
    onUnblock: () -> Unit,
    onSuccessfulUnblock: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            CircularIcon(
                icon = it,
                contentDescription = null,
                size = ICON_SIZE,
            )
            Spacer(modifier = Modifier.padding(horizontal = SMALL_PADDING))
        }
        Text(name, modifier = Modifier.weight(1f))
        TextButton(onClick = onUnblock, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
            when (apiState) {
                ApiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(SMALL_ICON_SIZE),
                    color = MaterialTheme.colorScheme.secondary,
                )

                is ApiState.Success -> onSuccessfulUnblock()
                else -> Icon(
                    imageVector = Icons.Rounded.Close,
                    modifier = Modifier.size(SMALL_ICON_SIZE),
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun BlockedElementPreview() {
    BlockedElement(
        apiState = ApiState.Empty,
        icon = "",
        name = "Element name",
        onUnblock = { },
        onSuccessfulUnblock = { },
    )
}
