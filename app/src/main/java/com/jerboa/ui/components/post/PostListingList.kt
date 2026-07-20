package com.jerboa.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.datatypes.sampleInstantScores
import com.jerboa.datatypes.sampleLocalSite
import com.jerboa.datatypes.sampleMyUserInfo
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.InstantScores
import com.jerboa.feat.PostOrCommentType
import com.jerboa.feat.needBlur
import com.jerboa.hostNameCleaned
import com.jerboa.isSameInstance
import com.jerboa.nsfwCheck
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.NsfwBadge
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.common.scoreColor
import com.jerboa.ui.components.common.scoreOrPctStr
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALLER_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.LocalSite
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.enums.VoteAction

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostListingList(
    postView: PostView,
    instantScores: InstantScores,
    myUserInfo: MyUserInfo?,
    localSite: LocalSite,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    showCommunityName: Boolean = true,
    account: Account,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: BlurNSFW,
    appState: JerboaAppState,
    showIfRead: Boolean,
    lowBandwidthMode: Boolean,
) {
    Column(
        modifier =
            Modifier
                .padding(
                    horizontal = MEDIUM_PADDING,
                    vertical = MEDIUM_PADDING,
                ).testTag("jerboa:post"),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(
                    SMALL_PADDING,
                ),
        ) {
            if (showVotingArrowsInListView) {
                PostVotingTile(
                    instantScores = instantScores,
                    myUserInfo = myUserInfo,
                    localSite = localSite,
                    creatorId = postView.post.creator_id,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    account = account,
                )
            }
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable { onPostClick(postView) },
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            ) {
                PostName(
                    post = postView.post, read = postView.post_actions?.read_at != null, showIfRead = showIfRead)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(SMALLER_PADDING, Alignment.Start),
                ) {
                    // You must use a center align modifier for each of these
                    val centerMod = Modifier.align(Alignment.CenterVertically)
                    if (showCommunityName) {
                        CommunityLink(
                            community = postView.community,
                            onClick = {},
                            clickable = false,
                            showDefaultIcon = false,
                            showAvatar = false,
                            blurNSFW = blurNSFW,
                            modifier = centerMod,
                        )
                        DotSpacer(modifier = centerMod)
                    }
                    PersonProfileLink(
                        person = postView.creator,
                        onClick = {},
                        clickable = false,
                        color = MaterialTheme.colorScheme.outline,
                        showAvatar = false,
                        modifier = centerMod,
                    )
                    DotSpacer(modifier = centerMod)
                    postView.post.url?.also { postUrl ->
                        if (!isSameInstance(postUrl, account.instance)) {
                            val hostName = hostNameCleaned(postUrl)
                            hostName?.also {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = centerMod,
                                )
                                DotSpacer(modifier = centerMod)
                            }
                        }
                    }
                    TimeAgo(
                        published = postView.post.published_at,
                        updated = postView.post.updated_at,
                        modifier = centerMod,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SMALLER_PADDING),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!showVotingArrowsInListView) {
                        Text(
                            text = instantScores.score.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scoreColor(myVote = instantScores.myVote),
                        )
                        DotSpacer()
                    }
                    Text(
                        text =
                            stringResource(
                                R.string.post_listing_comments_count,
                                postView.post.comments,
                            ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    CommentNewCount(
                        comments = postView.post.comments,
                        readComments = postView.post_actions?.read_comments_amount,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    NsfwBadge(visible = nsfwCheck(postView))
                }
            }
            ThumbnailTile(
                post = postView.post,
                imageDetails = postView.image_details,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurEnabled = blurNSFW.needBlur(postView),
                appState = appState,
                lowBandwidthMode = lowBandwidthMode,
            )
        }
    }
}

@Preview
@Composable
fun PostListingListPreview() {
    PostListingList(
        postView = samplePostView,
        myUserInfo = sampleMyUserInfo,
        localSite = sampleLocalSite,
        instantScores = sampleInstantScores,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        account = AnonAccount,
        showVotingArrowsInListView = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showIfRead = true,
        lowBandwidthMode = false,
    )
}

@Preview
@Composable
fun PostListingListWithThumbPreview() {
    PostListingList(
        postView = samplePostView,
        instantScores = sampleInstantScores,
        myUserInfo = sampleMyUserInfo,
        localSite = sampleLocalSite,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        account = AnonAccount,
        showVotingArrowsInListView = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = BlurNSFW.NSFW,
        appState = rememberJerboaAppState(),
        showIfRead = true,
        lowBandwidthMode = false,
    )
}

@Composable
fun CommentNewCount(
    comments: Long,
    readComments: Long?,
    style: TextStyle = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
    spacing: Dp = 0.dp,
) {
    val read = readComments ?: 0
    val unread = if (read >= comments || read == 0L) {
        null
    } else {
        comments - read
    }

    if (unread != null) {
        Spacer(Modifier.padding(horizontal = spacing))

        Text(
            text = stringResource(R.string.post_listing_new, unread),
            style = style,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
fun PostVotingTile(
    instantScores: InstantScores,
    myUserInfo: MyUserInfo?,
    localSite: LocalSite,
    creatorId: PersonId,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    account: Account,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier =
            Modifier
                .fillMaxHeight()
                .padding(end = MEDIUM_PADDING),
    ) {
        VoteGeneric(
            instantScores = instantScores,
            myUserInfo = myUserInfo,
            localSite = localSite,
            voteAction = VoteAction.UpVote,
            voteContentType = PostOrCommentType.Post,
            creatorId = creatorId,
            onVoteClick = onUpvoteClick,
            account = account,
            // TODO what is hide scores for?
//            hideScores = true,
        )

        val scoreOrPctStr = instantScores.scoreOrPctStr(
            myUserInfo = myUserInfo,
            localSite = localSite,
            type = PostOrCommentType.Post,
        )

        Text(
            text = scoreOrPctStr ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = scoreColor(myVote = instantScores.myVote),
            // Hide the vote number if its
            modifier = Modifier.alpha(if (scoreOrPctStr != null) 1f else 0f),
        )

            // invisible Text below aligns width of PostVotingTiles
            Text(
                text = "00000",
                modifier = Modifier.height(0.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            VoteGeneric(
                instantScores = instantScores,
                myUserInfo = myUserInfo,
                localSite = localSite,
                voteAction = VoteAction.DownVote,
                voteContentType = PostOrCommentType.Post,
                creatorId = creatorId,
                onVoteClick = onDownvoteClick,
                account = account,
                // TODO
//                hideScores = true,
            )
    }
}
