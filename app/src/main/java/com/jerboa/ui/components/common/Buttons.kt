package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun PaginationButton(
    currentPage: Long,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = XXL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrevious) {
            Text(stringResource(R.string.previous))
        }
        Text(stringResource(R.string.page_number, currentPage))
        Button(onClick = onNext) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PaginationButtonPreview() {
    PaginationButton(
        currentPage = 1,
        onNext = { },
        onPrevious = { }
    )
}

@Composable
@Preview(showBackground = true)
private fun RetryLoadingPostsPreview() {
    RetryLoadingPosts {  }
}