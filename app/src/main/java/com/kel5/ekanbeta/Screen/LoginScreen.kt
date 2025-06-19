package com.kel5.ekanbeta.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kel5.ekanbeta.ViewModel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun LoginScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = viewModel()

    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()

    val emailError by authViewModel.emailError.collectAsState()
    val passwordError by authViewModel.passwordError.collectAsState()
    val generalError by authViewModel.generalError.collectAsState()

    val userRole by authViewModel.userRole.collectAsState()

    val isLoading by authViewModel.isLoading.collectAsState()
    val loginSuccess by authViewModel.loginSuccess.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.Center
    ){
        Text(
            text = "Login",
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

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                authViewModel.clearErrors()
                authViewModel.login()
                if(loginSuccess == true){
                    when(userRole){
                        "admin" -> navController.navigate("AdminHome"){
                            popUpTo("Login") { inclusive = true }
                        }
                        "user" -> navController.navigate("UserHome"){
                            popUpTo("Login") { inclusive = true }
                        }
                    }
                }
                      },
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
                    text = "Login",
                    fontFamily = Poppins,
                    fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                append("Belum memiliki akun?")
                pushStyle(SpanStyle(color = PrimaryColor))
                append(" Register")
                pop()
            },
            modifier = Modifier.clickable {
                navController.navigate("Register")
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