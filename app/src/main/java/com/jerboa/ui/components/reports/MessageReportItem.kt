package com.jerboa.ui.components.reports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.samplePrivateMessageReportView
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PrivateMessageReportView
import it.vercruysse.lemmyapi.datatypes.ResolvePrivateMessageReport

@Composable
fun MessageReportItem(
    messageReportView: PrivateMessageReportView,
    onResolveClick: (ResolvePrivateMessageReport) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    showAvatar: Boolean,
) {
    Column(
        modifier =
            Modifier.padding(
                vertical = MEDIUM_PADDING,
                horizontal = MEDIUM_PADDING,
            ),
        verticalArrangement = Arrangement.Absolute.spacedBy(MEDIUM_PADDING),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ReportCreatorBlock(messageReportView.creator, onPersonClick, showAvatar)
            TimeAgo(
                published = messageReportView.private_message_report.published,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.spacedBy(MEDIUM_PADDING),
        ) {
            Text(
                text = stringResource(R.string.message) + ": ",
                style = MaterialTheme.typography.labelLarge,
            )

            MyMarkdownText(
                markdown = messageReportView.private_message_report.original_pm_text,
                onClick = {},
            )
        }

        ReportReasonBlock(messageReportView.private_message_report.reason)

        messageReportView.resolver?.let { resolver ->
            ReportResolverBlock(
                resolver = resolver,
                resolved = messageReportView.private_message_report.resolved,
                onPersonClick = onPersonClick,
                showAvatar = showAvatar,
            )
        }

        ResolveButtonBlock(
            resolved = messageReportView.private_message_report.resolved,
            onResolveClick = {
                onResolveClick(
                    ResolvePrivateMessageReport(
                        report_id = messageReportView.private_message_report.id,
                        resolved = !messageReportView.private_message_report.resolved,
                    ),
                )
            },
        )
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = SMALL_PADDING))
}

@Preview
@Composable
fun MessageReportItemPreview() {
    MessageReportItem(
        messageReportView = samplePrivateMessageReportView,
        onPersonClick = {},
        onResolveClick = {},
        showAvatar = false,
    )
}
