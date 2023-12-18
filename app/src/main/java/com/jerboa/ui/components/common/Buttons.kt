package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.ui.theme.XXL_PADDING

@Composable
fun RetryLoadingPosts(onClick: () -> Unit) {
    Button(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = XXL_PADDING),
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
    ) {
        Text(stringResource(R.string.posts_failed_loading))
    }
}
