package com.jerboa.feat

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.R
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

enum class BackConfirmationMode(
    @param:StringRes val resId: Int,
) {
    None(R.string.no_back_confirmation),
    Toast(R.string.press_again_confirmation),
    Dialog(R.string.back_dialog),
}

object BackConfirmation {
    private var isBackPressedOnce = false
    private var ref: OnBackPressedCallback? = null

    fun ComponentActivity.addConfirmationToast(
        navController: NavController,
        ctx: Context,
    ) {
        if (ref != null) disposeConfirmation()

        ref =
            this.onBackPressedDispatcher.addCallback(this) {
                val isRoot = navController.previousBackStackEntry == null
                if (isRoot && isBackPressedOnce) {
                    finish()
                } else if (isRoot) {
                    Toast.makeText(ctx, ctx.getText(R.string.back_confirmation), Toast.LENGTH_SHORT).show()
                    isBackPressedOnce = true
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        isBackPressedOnce = false
                    }, 2, TimeUnit.SECONDS)
                } else {
                    navController.navigateUp()
                }
            }
    }

    fun ComponentActivity.addConfirmationDialog(
        navController: NavController,
        showDialog: () -> Unit,
    ) {
        if (ref != null) disposeConfirmation()

        ref =
            this.onBackPressedDispatcher.addCallback(this) {
                val isRoot = navController.previousBackStackEntry == null
                if (isRoot) {
                    showDialog()
                } else {
                    navController.navigateUp()
                }
            }
    }

    fun disposeConfirmation() {
        ref?.remove()
        ref = null
    }
}

@Composable
fun ShowConfirmationDialog(
    close: () -> Unit,
    exit: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = close,
        confirmButton = {
            Button(onClick = exit) {
                Text(text = stringResource(id = R.string.exit))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.back_dialog_title))
        },
        dismissButton = {
            Button(onClick = close) {
                Text(text = stringResource(id = R.string.input_fields_cancel))
            }
        },
    )
}
