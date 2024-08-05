package com.jerboa.ui.components.registrationapplications

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.UnreadOrAll
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.model.AccountViewModel
import com.jerboa.model.RegistrationApplicationsViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.unreadOrAllFromBool
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationApplicationsScreen(
    appState: JerboaAppState,
    drawerState: DrawerState,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    padding: PaddingValues? = null,
) {
    Log.d("jerboa", "got to registration applications screen")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val registrationApplicationsViewModel: RegistrationApplicationsViewModel =
        viewModel(factory = RegistrationApplicationsViewModel.Companion.Factory(account, siteViewModel))

    val baseModifier = if (padding == null) {
        Modifier
    } else {
        // https://issuetracker.google.com/issues/249727298
        // Else it also applies the padding above the ime (keyboard)
        Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
            .systemBarsPadding()
    }

    Scaffold(
        modifier = baseModifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            RegistrationApplicationsHeader(
                scrollBehavior = scrollBehavior,
                unreadCount = siteViewModel.unreadAppCount,
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                selectedUnreadOrAll = unreadOrAllFromBool(registrationApplicationsViewModel.unreadOnly),
                onClickUnreadOrAll = { unreadOrAll ->
                    account.doIfReadyElseDisplayInfo(
                        appState,
                        ctx,
                        snackbarHostState,
                        scope,
                        siteViewModel,
                        accountViewModel,
                        loginAsToast = true,
                    ) {
                        registrationApplicationsViewModel.resetPage()
                        registrationApplicationsViewModel.updateUnreadOnly(unreadOrAll == UnreadOrAll.Unread)
                        registrationApplicationsViewModel.listApplications(
                            registrationApplicationsViewModel.getFormApplications(),
                        )
                    }
                },
            )
        },
        content = { innerPadding ->
            RegistrationApplications(
                padding = innerPadding,
                appState = appState,
                registrationApplicationsViewModel = registrationApplicationsViewModel,
                siteViewModel = siteViewModel,
                ctx = ctx,
                account = account,
                scope = scope,
                snackbarHostState = snackbarHostState,
            )
        },
    )
}
