package com.jerboa.ui.components.privatemessage

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.PrivateMessageView
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.nav.NavigateWithNoArgs
import com.jerboa.ui.components.person.ToProfile

class PrivateMessageReplyDependencies(
    val privateMessageView: PrivateMessageView,
) : ViewModel()

typealias ToPrivateMessageReply = NavigateWithNoArgs<PrivateMessageReplyDependencies>

class PrivateMessageReplyNavController(
    override val navController: NavController,
    val toProfile: ToProfile,
) : NavControllerWrapper()
