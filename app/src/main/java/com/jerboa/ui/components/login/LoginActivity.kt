package com.jerboa.ui.components.login

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.db.AccountViewModel

@Composable
fun LoginActivity(
    navController: NavController,
    loginViewModel: LoginViewModel,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "Got to login activity")

    val scaffoldState = rememberScaffoldState()
    val accounts by accountViewModel.allAccounts.observeAsState()
    val ctx = LocalContext.current

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                LoginHeader(
                    navController = navController,
                    accounts = accounts
                )
            },
            content = {
                LoginForm(
                    loading = loginViewModel.loading,
                    onClickLogin = { form, instance ->
                        loginViewModel.login(
                            navController = navController,
                            form = form,
                            instance = instance.trim(),
                            ctx = ctx,
                            accountViewModel = accountViewModel,
                        )
                    }
                )
            }
        )
    }
}
