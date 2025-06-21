package com.kel5.ekanbeta.Screen.AdminScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kel5.ekanbeta.ViewModel.ChatViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kel5.ekanbeta.Common.ChatViewModelFactory
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.ui.theme.Poppins
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kel5.ekanbeta.Data.RoomchatDisplay
import com.kel5.ekanbeta.Repository.AuthRepo
import com.kel5.ekanbeta.Room.AppDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminChatScreen(navController: NavController) {
    val context = LocalContext.current
    val chatRepo = remember {
        val db = AppDatabase.getDatabase(context)
        ChatRepo().apply { setMessageDao(db.messageDao()) }
    }
    val authRepo = remember { AuthRepo() }
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepo, authRepo)
    )

    val chatRooms by chatViewModel.chatRooms.collectAsState()
    val chatstat = chatRooms.isEmpty().toString()
    val currentUserId = chatViewModel.currentUserId

    val displayList = remember { mutableStateListOf<RoomchatDisplay>() }

    LaunchedEffect(chatRooms) {
        displayList.clear()
        chatRooms.forEach { room ->
            val otherId = room.participants.firstOrNull{ it != currentUserId } ?: return@forEach
            chatViewModel.getUserData(otherId) { name ->
                val username = name ?: "Unknown"
                displayList.add(RoomchatDisplay(room, username, otherId))
            }
        }
    }

    LaunchedEffect(Unit) {
        chatViewModel.loadAllChatRooms()
    }

    Scaffold(
        bottomBar = {
            AdminBottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Daftar Chat", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
            Text(chatstat)

            if(displayList.isEmpty()){
                Text("Tidak ada chat.", modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn {
                    items(displayList.size) { index ->
                        val chat = displayList[index]
                        ChatListItem(chat, onClick = {
                            navController.navigate("ChatScreen/${chat.otherUserId}?isAdmin=true")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: RoomchatDisplay, onClick: () -> Unit){
    val formattedTime = remember(chat.room.lastTimestamp){
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(chat.room.lastTimestamp)
        sdf.format(date)
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()){
            Text(chat.otherUsername, fontWeight = FontWeight.Bold)
            Text(chat.room.lastMessage ?: "", maxLines = 1)
            Text(formattedTime, fontSize = 12.sp)
        }
    }
}
