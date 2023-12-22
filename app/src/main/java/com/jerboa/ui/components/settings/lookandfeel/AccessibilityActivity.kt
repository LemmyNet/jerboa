
package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.jerboa.R
import com.jerboa.getLangPreferenceDropdownEntries
import com.jerboa.matchLocale
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityActivity(
    onBack: () -> Unit,
    onClickCrashLogs: () -> Unit
) {
    Log.d("jerboa", "Got to About activity")

    val ctx = LocalContext.current

    val localeMap =
        remember {
            getLangPreferenceDropdownEntries(ctx)
        }

    val currentAppLocale = matchLocale(localeMap)
    val langState = rememberIntSettingState(localeMap.keys.indexOf(currentAppLocale))

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = "Accessibility", onClickBack = onBack)
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
            ) {
                SettingsListDropdown(
                    title = {
                        Text(text = stringResource(R.string.lang_language))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = stringResource(R.string.lang_language),
                        )
                    },
                    state = langState,
                    items = localeMap.values.toList(),
                    onItemSelected = { i, _ ->
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.create(localeMap.keys.elementAt(i)),
                        )
                    },
                )
            }
        },
    )
}

@Preview
@Composable
fun AccessibilityPreview() {
    AccessibilityActivity(
        onBack = {},
        onClickCrashLogs = {}
    )
}
