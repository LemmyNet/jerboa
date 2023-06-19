package com.jerboa.ui.components.comment.edit

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.CommentView
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgs

typealias OnCommentEdit = (CommentView) -> Unit

class CommentEditDependencies(
    val commentView: CommentView,
    val onCommentEdit: OnCommentEdit?,
) : ViewModel()

typealias ToCommentEdit = NavigateWithNoArgs<CommentEditDependencies>

class CommentEditNavController(
    override val navController: NavController,
) : NavControllerWrapper()
