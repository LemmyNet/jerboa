package com.jerboa.ui.components.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.JerboaAppState
import com.jerboa.datatypes.VoteDisplayMode
import com.jerboa.datatypes.samplePostReportView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.BlurNSFW
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.post.PostBody
import com.jerboa.ui.components.post.PostHeaderLine
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.dto.SubscribedType
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import it.vercruysse.lemmyapi.v0x19.datatypes.PostReportView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import it.vercruysse.lemmyapi.v0x19.datatypes.ResolvePostReport

@Composable
fun PostReportItem(
    appState: JerboaAppState,
    postReportView: PostReportView,
    onResolveClick: (ResolvePostReport) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onPostClick: (PostView) -> Unit,
    onCommunityClick: (Community) -> Unit,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
    voteDisplayMode: VoteDisplayMode,
    account: Account,
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
        Box(
            modifier = Modifier
                .clickable { onPostClick(postView) },
        ) {
            PostHeaderLine(
                postView = postView,
                myVote = postView.my_vote,
                score = postView.counts.score,
                upvotes = postView.counts.upvotes,
                downvotes = postView.counts.downvotes,
                onCommunityClick = onCommunityClick,
                onPersonClick = onPersonClick,
                showCommunityName = true,
                showAvatar = showAvatar,
                blurNSFW = blurNSFW,
                voteDisplayMode = voteDisplayMode,
                fullBody = false,
            )
        }

        //  Title + metadata
        PostBody(
            postView = postView,
            fullBody = false,
            viewSource = false,
            expandedImage = false,
            account = account,
            useCustomTabs = false,
            usePrivateTabs = false,
            blurNSFW = blurNSFW,
            showPostLinkPreview = true,
            appState = appState,
            clickBody = { onPostClick(postView) },
            showIfRead = true,
        )

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
        voteDisplayMode = VoteDisplayMode.Full,
        account = AnonAccount,
        appState = rememberJerboaAppState(),
    )
}
