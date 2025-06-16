package com.kel5.ekanbeta.Screen.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import com.kel5.ekanbeta.ui.theme.Poppins
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ViewModel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import androidx.compose.runtime.remember
import com.kel5.ekanbeta.Common.ChatViewModelFactory
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.ViewModel.ChatViewModel
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun ProfileUserScreen(
    navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    val chatRepo = remember { ChatRepo() }
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepo)
    )

    val username by authViewModel.currentUsername.collectAsState()
    val email by authViewModel.currentEmail.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val adminUid = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit){
        authViewModel.fetchCurrentUserData()
        chatViewModel.getAdminUID { uid ->
            adminUid.value = uid
        }
    }

    Scaffold(
        bottomBar = {
            UserBottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(PrimaryColor)
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profil",
                    fontSize = 24.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = username ?: "Pengguna",
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = email ?: "",
                fontFamily = Poppins,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 36.dp)){
//                ProfileActionButton(
//                    text = "Ubah Profil",
//                    icon = Icons.Default.Edit,
//                    onClick = { /* navigate to edit screen */ }
//                )

//                Spacer(modifier = Modifier.height(16.dp))

                ProfileActionButton(
                    text = "Alamat Pengiriman",
                    icon = Icons.Default.LocationOn,
                    onClick = { navController.navigate("UserListAlamat") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileActionButton(
                    text = "Chat Admin",
                    icon = Icons.Default.Chat,
                    onClick = { adminUid.value?.let { uid ->
                        navController.navigate("ChatScreen/$uid")
                        }
                    }
                )

//                Spacer(modifier = Modifier.height(16.dp))
//
//                ProfileActionButton(
//                    text = "About Us",
//                    icon = Icons.Default.Info,
//                    onClick = { /* navigate to edit screen */ }
//                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("Login"){
                        popUpTo("UserProfile") { inclusive = true }
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
                        text = "Logout",
                        fontFamily = Poppins,
                        fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(elevation = 5.dp, shape = buttonShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$text Icon",
                tint = PrimaryColor
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = Poppins
            )
        }
    }
}
