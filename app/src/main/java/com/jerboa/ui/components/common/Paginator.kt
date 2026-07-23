package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.ui.theme.XXL_PADDING

@Composable
fun Paginator(
    currentPage: Long,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onNextEnabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = XXL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrevious, enabled = currentPage > 1) {
            Text(stringResource(R.string.previous))
        }
        Text(stringResource(R.string.page_number, currentPage))
        Button(onClick = onNext, enabled = onNextEnabled) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PaginatorPreview() {
    Paginator(
        currentPage = 1,
        onNext = { },
        onPrevious = { }
    )
}