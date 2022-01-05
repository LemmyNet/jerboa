package com.jerboa.ui.components.home

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.api.Login

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
    navController: NavController = rememberNavController(),
    userViewModel: UserViewModel = viewModel()
) {
    var instance by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val ctx = LocalContext.current

    val isValid =
        instance.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()

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
            onClick = {
                val form = Login(
                    username_or_email = username,
                    password = password
                )
                userViewModel.login(
                    navController = navController,
                    form = form,
                    instance = instance,
                    ctx = ctx,
                )
                Toast.makeText(
                    ctx,
                    "Form: i is $instance and username is $username",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            if (userViewModel.loading) {
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
private fun LoginHeader(
    navController: NavController,
) {
    TopAppBar(
        title = {
            Text(
                text = "Login",
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
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
    val navController = rememberNavController()
    LoginHeader(navController = navController)
}

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                LoginHeader(navController)
            },
            content = {
                LoginForm(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
        )
    }
}
