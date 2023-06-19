package com.jerboa.ui.components.post.create

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.PostView
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgs
import com.jerboa.ui.components.community.list.ToCommunityList
import com.jerboa.ui.components.post.ToPost

typealias OnCreatePost = (PostView) -> Unit

class CreatePostDependencies(
    val selectedCommunity: Community?,
) : ViewModel()

typealias ToCreatePost = NavigateWithNoArgs<CreatePostDependencies>

class CreatePostNavController(
    override val navController: NavController,
    val toPost: ToPost,
    val toCommunityList: ToCommunityList,
) : NavControllerWrapper()
