package com.jerboa.ui.components.community.list

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.Community
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgs
import com.jerboa.ui.components.community.ToCommunity

typealias OnSelectCommunity = (Community) -> Unit

class CommunityListDependencies(
    val onSelectCommunity: OnSelectCommunity? = null,
) : ViewModel()

typealias ToCommunityList = NavigateWithNoArgs<CommunityListDependencies>

class CommunityListNavController(
    override val navController: NavController,
    val toCommunity: ToCommunity,
) : NavControllerWrapper()
