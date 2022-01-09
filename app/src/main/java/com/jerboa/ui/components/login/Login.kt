package com.jerboa.ui.components.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.api.Login
import com.jerboa.db.Account

@Composable
fun MyTextField(
    label: String,
    placeholder: String? = null,
    text: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        placeholder = { placeholder?.let { Text(text = it) } },
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
        )
    )
}

@Composable
fun PasswordField(
    password: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(text = "Password") },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(imageVector = image, "")
            }
        }
    )
}

@Composable
fun LoginForm(
    loading: Boolean = false,
    onClickLogin: (form: Login, instance: String) -> Unit = { _: Login, _: String -> },
) {
    var instance by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val isValid =
        instance.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()

    val form = Login(
        username_or_email = username.trim(),
        password = password.trim()
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MyTextField(
            label = "Instance",
            placeholder = "ex: lemmy.ml",
            text = instance,
            onValueChange = { instance = it }
        )
        MyTextField(
            label = "Email or Username",
            text = username,
            onValueChange = { username = it }
        )
        PasswordField(
            password = password,
            onValueChange = { password = it },
        )
        Button(
            enabled = isValid,
            onClick = { onClickLogin(form, instance) },
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = MaterialTheme
                        .colors.onSurface
                )
            } else {
                Text("Login")
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
                text = "Login",
            )
        },
        navigationIcon = {
            IconButton(
                enabled = !accounts.isNullOrEmpty(),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
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
