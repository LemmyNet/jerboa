@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.jerboa.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.R
import com.jerboa.datatypes.types.Login
import com.jerboa.db.entity.Account
import com.jerboa.onAutofill
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    text: String,
    onValueChange: (String) -> Unit,
    autofillTypes: ImmutableList<AutofillType> = persistentListOf(),
) {
    var wasAutofilled by remember { mutableStateOf(false) }

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
        modifier = modifier
            .width(OutlinedTextFieldDefaults.MinWidth)
            .background(if (wasAutofilled) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .onAutofill(LocalAutofillTree.current, LocalAutofill.current, autofillTypes) {
                onValueChange(it)
                wasAutofilled = true
            },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    password: String,
    onValueChange: (String) -> Unit,
) {
    var wasAutofilled by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .width(OutlinedTextFieldDefaults.MinWidth)
            .background(if (wasAutofilled) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .onAutofill(
                LocalAutofillTree.current,
                LocalAutofill.current,
                persistentListOf(AutofillType.Password),
            ) {
                onValueChange(it)
                wasAutofilled = true
            },
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

@Composable
fun InstancePicker(expanded: Boolean, setExpanded: ((Boolean) -> Unit), instance: String, setInstance: ((String) -> Unit)) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            setExpanded(!expanded)
        },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .width(OutlinedTextFieldDefaults.MinWidth),
            label = { Text(stringResource(R.string.login_instance)) },
            placeholder = { Text(stringResource(R.string.login_instance_placeholder)) },
            value = instance,
            singleLine = true,
            onValueChange = setInstance,
            trailingIcon = {
                TrailingIcon(expanded = expanded)
            },
            keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Uri),
        )
        val filteringOptions = DEFAULT_LEMMY_INSTANCES.filter { it.contains(instance, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                properties = PopupProperties(focusable = false),
                modifier = Modifier.exposedDropdownSize(true),
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        modifier = Modifier.exposedDropdownSize(),
                        text = {
                            Text(text = selectionOption)
                        },
                        onClick = {
                            setInstance(selectionOption)
                            setExpanded(false)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
    var expanded by remember { mutableStateOf(false) }

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
        InstancePicker(expanded = expanded, { expanded = it }, instance, { instance = it })

        MyTextField(
            label = stringResource(R.string.login_email_or_username),
            text = username,
            onValueChange = { username = it },
            autofillTypes = persistentListOf(AutofillType.Username, AutofillType.EmailAddress),
        )
        PasswordField(
            password = password,
            onValueChange = { password = it },
        )
        MyTextField(
            label = stringResource(R.string.login_totp),
            text = totp,
            onValueChange = { totp = it },
            autofillTypes = persistentListOf(AutofillType.SmsOtpCode),
        )
        Button(
            enabled = isValid && !loading,
            onClick = { onClickLogin(form, instance) },
            modifier = Modifier.padding(top = 10.dp),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(LocalTextStyle.current.fontSize.value.dp),
                    strokeWidth = 2.dp,
                )
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
