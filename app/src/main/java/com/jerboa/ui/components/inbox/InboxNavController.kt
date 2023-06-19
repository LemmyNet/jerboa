package com.jerboa.ui.components.inbox

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgsAndDependencies
import com.jerboa.ui.components.comment.reply.ToCommentReply
import com.jerboa.ui.components.community.ToCommunity
import com.jerboa.ui.components.person.ToProfile
import com.jerboa.ui.components.post.ToComment
import com.jerboa.ui.components.post.ToPost
import com.jerboa.ui.components.privatemessage.ToPrivateMessageReply
import com.jerboa.ui.components.report.ToCommentReport

typealias ToInbox = NavigateWithNoArgsAndDependencies

class InboxNavController(
    override val navController: NavController,
    val toCommentReply: ToCommentReply,
    val toPrivateMessageReply: ToPrivateMessageReply,
    val toProfile: ToProfile,
    val toCommentReport: ToCommentReport,
    val toComment: ToComment,
    val toPost: ToPost,
    val toCommunity: ToCommunity,
) : NavControllerWrapper()
