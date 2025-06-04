package com.jerboa.ui.components.settings.crashlogs

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.crazylegend.crashyreporter.CrashyReporter
import com.jerboa.R
import com.jerboa.feat.copyTextToClipboard
import com.jerboa.formatDuration
import com.jerboa.showSnackbar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import java.time.Instant
import java.util.Date

@Composable
fun CrashLogsScreen(onClickBack: () -> Unit) {
    Log.d("jerboa", "Got to Crash log screen")

    CrashLogs(onClickBack = onClickBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashLogs(onClickBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val logs =
        remember {
            (CrashyReporter.getLogFiles() ?: listOf())
                .sortedByDescending { it.lastModified() }
                .toMutableStateList()
        }

    val snackbarHostState = remember { SnackbarHostState() }
    val deleteMessage = stringResource(R.string.crash_logs_all_deleted)

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.crash_logs),
                onClickBack = onClickBack,
                actions = {
                    IconButton(
                        onClick = {
                            CrashyReporter.purgeLogs()
                            logs.clear()
                            showSnackbar(
                                scope,
                                snackbarHostState,
                                deleteMessage,
                                null,
                                true,
                                SnackbarDuration.Short,
                            )
                        },
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.crash_logs_delete),
                        )
                    }
                },
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
            ) {
                logs.map {
                    CrashLog(crash = it.readText(), modified = it.lastModified())
                }
            }
        },
    )
}

@Composable
fun CrashLog(
    crash: String,
    modified: Long,
) {
    var expanded by remember { mutableStateOf(false) }
    val textModifier = Modifier.clickable(onClick = { expanded = !expanded })
    val ctx = LocalContext.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(MEDIUM_PADDING),
    ) {
        Column(
            modifier = Modifier.padding(MEDIUM_PADDING),
        ) {
            IconButton(onClick = {
                copyTextToClipboard(ctx, crash, "crash", R.string.copied)
            }) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = stringResource(R.string.crash_logs_copy),
                )
            }
            Text(text = formatDuration(Date(modified)))
        }
        if (expanded) {
            Text(
                text = crash,
                modifier = textModifier,
            )
        } else {
            Text(
                text = crash,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = textModifier,
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = SMALL_PADDING))
}

@Preview
@Composable
fun CrashLogPreview() {
    CrashLog(
        crash = "A really bad one\nlots\nof\ntrace\nlines\nhere",
        Instant.now().toEpochMilli() - 1000 * 60 * 60 * 24 * 10,
    )
}
