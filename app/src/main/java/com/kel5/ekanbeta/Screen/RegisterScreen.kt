package com.kel5.ekanbeta.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.ekanbeta.ViewModel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.PrimaryColor


@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val username by authViewModel.username.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val confirmPassword by authViewModel.confirmPassword.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val usernameError by authViewModel.usernameError.collectAsState()
    val emailError by authViewModel.emailError.collectAsState()
    val passwordError by authViewModel.passwordError.collectAsState()
    val confirmPasswordError by authViewModel.confirmPasswordError.collectAsState()
    val generalError by authViewModel.generalError.collectAsState()
    val registerSuccess by authViewModel.registerSuccess.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registerSuccess) {
        if(registerSuccess == true){
            navController.navigate("UserHome"){
                popUpTo("Register"){ inclusive = true}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.Center
    ){
        Text(
            text = "Register",
            color = PrimaryColor,
            fontFamily = Poppins,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        val textFieldShape = RoundedCornerShape(16.dp)

        OutlinedTextField(
            value = username,
            onValueChange = { authViewModel.onUsernameChanged(it)},
            label = { Text("Username") },
            shape = textFieldShape,
            isError = usernameError != null,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        if(usernameError != null){
            Text(
                usernameError!!,
                color = Color.Red,
                fontFamily = Poppins,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { authViewModel.onEmailChanged(it)},
            label = { Text("Email") },
            shape = textFieldShape,
            isError = emailError != null,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        if(emailError != null){
            Text(
                emailError!!,
                color = Color.Red,
                fontFamily = Poppins,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { authViewModel.onPasswordChanged(it)},
            label = { Text("Password") },
            shape = textFieldShape,
            isError = passwordError != null,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if(passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        if(passwordError != null){
            Text(
                passwordError!!,
                color = Color.Red,
                fontFamily = Poppins,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { authViewModel.onConfirmPasswordChanged(it)},
            label = { Text("Konfirmasi Password") },
            shape = textFieldShape,
            isError = confirmPasswordError != null,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        if(confirmPasswordError != null){
            Text(
                confirmPasswordError!!,
                color = Color.Red,
                fontFamily = Poppins,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                authViewModel.clearErrors()
                authViewModel.register() },
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            enabled = !isLoading
        ) {
            if(isLoading){
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
            } else {
                Text(
                    text = "Buat Akun",
                    fontFamily = Poppins,
                    fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                append("Sudah memiliki akun?")
                pushStyle(SpanStyle(color = PrimaryColor))
                append(" Login")
                pop()
            },
            modifier = Modifier.clickable {
                navController.navigate("Login")
            }
        )

        if (generalError != null) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = generalError!!,
                color = Color.Red,
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterScreenPreview() {
//    RegisterScreen(navController = rememberNavController())
//}
