package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyActivity
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

private const val commentReplyRoutePattern = "commentReply"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.commentReplyScreen(
    commentReplyViewModel: CommentReplyViewModel,
    accountViewModel: AccountViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    navController: NavController,
    siteViewModel: SiteViewModel,
) {
    composable(
        route = commentReplyRoutePattern,
    ) {
        CommentReplyActivity(
            commentReplyViewModel = commentReplyViewModel,
            postViewModel = postViewModel,
            accountViewModel = accountViewModel,
            personProfileViewModel = personProfileViewModel,
            navController = navController,
            siteViewModel = siteViewModel,
        )
    }
}
