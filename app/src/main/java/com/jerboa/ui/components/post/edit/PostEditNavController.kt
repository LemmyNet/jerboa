package com.jerboa.ui.components.post.edit

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.PostView
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgs

typealias OnPostEdit = (PostView) -> Unit

class PostEditDependencies(
    val postView: PostView,
    val onPostEdit: OnPostEdit?,
) : ViewModel()

typealias ToPostEdit = NavigateWithNoArgs<PostEditDependencies>

class PostEditNavController(
    override val navController: NavController,
) : NavControllerWrapper()
