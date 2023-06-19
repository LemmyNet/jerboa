package com.jerboa.ui.components.person

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.Route
import com.jerboa.ui.components.comment.edit.ToCommentEdit
import com.jerboa.ui.components.comment.reply.ToCommentReply
import com.jerboa.ui.components.community.ToCommunity
import com.jerboa.ui.components.post.ToComment
import com.jerboa.ui.components.post.ToPost
import com.jerboa.ui.components.post.edit.ToPostEdit
import com.jerboa.ui.components.report.ToCommentReport
import com.jerboa.ui.components.report.ToPostReport

class ToProfile(
    private val navigateToDestination: (profileId: Int, saved: Boolean) -> Unit,
) {
    fun navigate(profileId: Int, saved: Boolean = Route.ProfileFromIdArgs.SAVED_DEFAULT) {
        navigateToDestination(profileId, saved)
    }
}

class PersonProfileNavController(
    override val navController: NavController,
    val toCommentEdit: ToCommentEdit,
    val toCommentReply: ToCommentReply,
    val toPostEdit: ToPostEdit,
    val toCommentReport: ToCommentReport,
    val toPostReport: ToPostReport,
    val toCommunity: ToCommunity,
    val toPost: ToPost,
    val toProfile: ToProfile,
    val toComment: ToComment,
) : NavControllerWrapper()
