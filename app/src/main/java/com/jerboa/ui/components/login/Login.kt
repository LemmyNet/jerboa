@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.jerboa.ui.components.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.R
import com.jerboa.ui.components.common.ifNotNull
import it.vercruysse.lemmyapi.datatypes.Login

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    text: String,
    onValueChange: (String) -> Unit,
    autofillContentType: ContentType? = null,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        placeholder = { placeholder?.let { Text(text = it) } },
        keyboardOptions =
            KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                autoCorrectEnabled = false,
            ),
        modifier = modifier
            .width(OutlinedTextFieldDefaults.MinWidth)
            .ifNotNull(autofillContentType) {
                this.semantics { contentType = it }
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
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier =
            modifier
                .width(OutlinedTextFieldDefaults.MinWidth)
                .semantics { contentType = ContentType.Password },
        value = password,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(text = stringResource(R.string.login_password)) },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image =
                if (passwordVisibility) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstancePicker(
    expanded: Boolean,
    setExpanded: ((Boolean) -> Unit),
    instance: String,
    setInstance: ((String) -> Unit),
) {
    val filteringOptions = DEFAULT_LEMMY_INSTANCES.filter { it.contains(instance, ignoreCase = true) }
    val expand = filteringOptions.isNotEmpty() && expanded

    ExposedDropdownMenuBox(
        expanded = expand,
        onExpandedChange = setExpanded,
    ) {
        OutlinedTextField(
            modifier =
                Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .width(OutlinedTextFieldDefaults.MinWidth),
            label = { Text(stringResource(R.string.login_instance)) },
            placeholder = { Text(stringResource(R.string.login_instance_placeholder)) },
            value = instance,
            singleLine = true,
            onValueChange = {
                setExpanded(true)
                setInstance(it)
            },
            trailingIcon = {
                TrailingIcon(expanded = expand)
            },
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = false, keyboardType = KeyboardType.Uri),
        )

        DropdownMenu(
            expanded = expand,
            onDismissRequest = {
                setExpanded(false)
            },
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onClickLogin: (form: Login, instance: String) -> Unit,
) {
    var instance by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var totp by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val isValid =
        instance.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()

    val form =
        Login(
            username_or_email = username.trim(),
            password = password.take(60),
            totp_2fa_token = totp.ifBlank { null },
        )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InstancePicker(expanded = expanded, { expanded = it }, instance, { instance = it })

        MyTextField(
            label = stringResource(R.string.login_email_or_username),
            text = username,
            onValueChange = { username = it },
            autofillContentType = ContentType.Username + ContentType.EmailAddress,
        )
        PasswordField(
            password = password,
            onValueChange = { password = it },
        )
        MyTextField(
            label = stringResource(R.string.login_totp),
            text = totp,
            onValueChange = { totp = it },
            autofillContentType = ContentType.SmsOtpCode,
        )
        Button(
            enabled = isValid && !loading,
            onClick = { onClickLogin(form, instance.lowercase()) },
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
    LoginForm(
        onClickLogin = { _: Login, _: String -> },
    )
}
