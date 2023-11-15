package com.jerboa.ui.components.login

import android.util.Log
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.model.AccountViewModel
import com.jerboa.model.LoginViewModel
import com.jerboa.model.SiteViewModel

@Composable
fun LoginActivity(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "Got to login activity")

    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current

    val loginViewModel: LoginViewModel = viewModel()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LoginHeader(
                appState::popBackStack,
            )
        },
        content = { padding ->
            LoginForm(
                loading = loginViewModel.loading,
                modifier =
                    Modifier
                        .padding(padding)
                        .imePadding(),
                onClickLogin = { form, instance ->
                    loginViewModel.login(
                        form = form,
                        instance = instance.trim(),
                        ctx = ctx,
                        accountViewModel = accountViewModel,
                        siteViewModel = siteViewModel,
                        onGoHome = appState::toHome,
                    )
                },
            )
        },
    )
}
