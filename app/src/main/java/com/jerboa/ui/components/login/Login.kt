@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.R
import com.jerboa.datatypes.types.Login
import com.jerboa.db.Account
import com.jerboa.onAutofill

@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    text: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        placeholder = { placeholder?.let { Text(text = it) } },
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Text,
            autoCorrect = false,
        ),
        modifier = modifier,
    )
}

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    password: String,
    onValueChange: (String) -> Unit,
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(text = stringResource(R.string.login_password)) },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisibility) {
                Icons.Outlined.Visibility
            } else {
                Icons.Outlined.VisibilityOff
            }

            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(imageVector = image, "")
            }
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onClickLogin: (form: Login, instance: String) -> Unit = { _: Login, _: String -> },
) {
    var instance by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var totp by rememberSaveable { mutableStateOf("") }
    val instanceOptions = DEFAULT_LEMMY_INSTANCES
    var expanded by remember { mutableStateOf(false) }
    var wasAutofilled by remember { mutableStateOf(false) }

    val isValid =
        instance.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()

    val form = Login(
        username_or_email = username.trim(),
        password = password.take(60),
        totp_2fa_token = totp.ifBlank { null },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                label = { Text(stringResource(R.string.login_instance)) },
                placeholder = { Text(stringResource(R.string.login_instance_placeholder)) },
                value = instance,
                singleLine = true,
                onValueChange = { instance = it },
                trailingIcon = {
                    TrailingIcon(expanded = expanded)
                },
                keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Uri),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                instanceOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        modifier = Modifier.exposedDropdownSize(),
                        text = {
                            Text(text = selectionOption)
                        },
                        onClick = {
                            instance = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        MyTextField(
            modifier = Modifier
                .background(if (wasAutofilled) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                .onAutofill(AutofillType.Username, AutofillType.EmailAddress) {
                    username = it
                    wasAutofilled = true
                },
            label = stringResource(R.string.login_email_or_username),
            text = username,
            onValueChange = { username = it },
        )
        PasswordField(
            modifier = Modifier
                .background(if (wasAutofilled) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                .onAutofill(AutofillType.Password) {
                    password = it
                    wasAutofilled = true
                },
            password = password,
            onValueChange = { password = it },
        )
        MyTextField(
            modifier = Modifier
                .background(if (wasAutofilled) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                .onAutofill(AutofillType.SmsOtpCode) {
                    totp = it
                    wasAutofilled = true
                },
            label = stringResource(R.string.login_totp),
            text = totp,
            onValueChange = { totp = it },
        )
        Button(
            enabled = isValid && !loading,
            onClick = { onClickLogin(form, instance) },
            modifier = Modifier.padding(top = 10.dp),
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.login_login))
            }
        }
    }
}

@Preview
@Composable
fun LoginFormPreview() {
    LoginForm()
}

@Composable
fun LoginHeader(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.login_login),
            )
        },
        navigationIcon = {
            IconButton(
                enabled = !accounts.isNullOrEmpty(),
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.login_back),
                )
            }
        },
    )
}

@Preview
@Composable
fun LoginHeaderPreview() {
    LoginHeader()
}
