package com.jerboa.ui.components.comment.reply

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.CommentView
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgs
import com.jerboa.ui.components.person.ToProfile

typealias OnCommentReply = (CommentView) -> Unit

class CommentReplyDependencies(
    val replyItem: ReplyItem,
    val isModerator: Boolean,
    val onCommentReply: OnCommentReply?,
) : ViewModel()

typealias ToCommentReply = NavigateWithNoArgs<CommentReplyDependencies>

class CommentReplyNavController(
    override val navController: NavController,
    val toProfile: ToProfile,
) : NavControllerWrapper()
