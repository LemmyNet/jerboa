package com.jerboa.ui.components.post

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.ui.components.comment.edit.ToCommentEdit
import com.jerboa.ui.components.comment.reply.ToCommentReply
import com.jerboa.ui.components.community.ToCommunity
import com.jerboa.ui.components.person.ToProfile
import com.jerboa.ui.components.post.edit.ToPostEdit
import com.jerboa.ui.components.report.ToCommentReport
import com.jerboa.ui.components.report.ToPostReport

class PostNavController(
    override val navController: NavController,
    val toCommentEdit: ToCommentEdit,
    val toCommentReply: ToCommentReply,
    val toPostEdit: ToPostEdit,
    val toCommunity: ToCommunity,
    val toPostReport: ToPostReport,
    val toProfile: ToProfile,
    val toPost: ToPost,
    val toComment: ToComment,
    val toCommentReport: ToCommentReport,
) : NavControllerWrapper()

class ToPost(
    val navigate: (postId: Int) -> Unit,
)

class ToComment(
    val navigate: (commentId: Int) -> Unit,
)
