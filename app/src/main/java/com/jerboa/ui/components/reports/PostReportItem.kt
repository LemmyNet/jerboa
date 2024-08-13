package com.jerboa.ui.components.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.samplePostReportView
import com.jerboa.feat.BlurNSFW
import com.jerboa.ui.components.post.PostCommunityAndCreatorBlock
import com.jerboa.ui.components.post.PostName
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.VERTICAL_SPACING
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostReportView
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.ResolvePostReport
import it.vercruysse.lemmyapi.dto.SubscribedType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostReportItem(
    postReportView: PostReportView,
    onResolveClick: (ResolvePostReport) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onPostClick: (PostView) -> Unit,
    onCommunityClick: (Community) -> Unit,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
) {
    // Build a post-view using the content at the time it was reported,
    // not the current state.
    val origPost = postReportView.post.copy(
        name = postReportView.post_report.original_post_name,
        url = postReportView.post_report.original_post_url,
        body = postReportView.post_report.original_post_body,
        published = postReportView.post_report.published,
    )

    val postView = PostView(
        post = origPost,
        creator = postReportView.post_creator,
        creator_banned_from_community = postReportView.creator_banned_from_community,
        subscribed = SubscribedType.NotSubscribed,
        community = postReportView.community,
        my_vote = postReportView.my_vote,
        counts = postReportView.counts,
        creator_blocked = false,
        creator_is_admin = false,
        creator_is_moderator = false,
        read = false,
        saved = false,
        unread_comments = 0,
        banned_from_community = false,
        hidden = false,
    )

    Column(
        modifier =
            Modifier.padding(
                vertical = MEDIUM_PADDING,
                horizontal = MEDIUM_PADDING,
            ),
        verticalArrangement = Arrangement.Absolute.spacedBy(MEDIUM_PADDING),
    ) {
        // These are taken from Post.Card . Don't use the full PostListing, as you don't
        // need any of the actions there

        // Need to make this clickable
        Column(
            verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING),
            modifier = Modifier
                .clickable { onPostClick(postView) },
        ) {
            PostCommunityAndCreatorBlock(
                postView = postView,
                onCommunityClick = onCommunityClick,
                onPersonClick = onPersonClick,
                showCommunityName = true,
                showAvatar = showAvatar,
                blurNSFW = blurNSFW,
                fullBody = false,
            )

            PostName(
                post = postView.post,
                read = postView.read,
                showIfRead = false,
            )
        }

        ReportCreatorBlock(postReportView.creator, onPersonClick, showAvatar)

        ReportReasonBlock(postReportView.post_report.reason)

        postReportView.resolver?.let { resolver ->
            ReportResolverBlock(
                resolver = resolver,
                resolved = postReportView.post_report.resolved,
                onPersonClick = onPersonClick,
                showAvatar = showAvatar,
            )
        }

        ResolveButtonBlock(
            resolved = postReportView.post_report.resolved,
            onResolveClick = {
                onResolveClick(
                    ResolvePostReport(
                        report_id = postReportView.post_report.id,
                        resolved = !postReportView.post_report.resolved,
                    ),
                )
            },
        )
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = SMALL_PADDING))
}

@Preview
@Composable
fun PostReportItemPreview() {
    PostReportItem(
        postReportView = samplePostReportView,
        onPersonClick = {},
        onPostClick = {},
        onCommunityClick = {},
        onResolveClick = {},
        showAvatar = false,
        blurNSFW = BlurNSFW.NSFW,
    )
}
