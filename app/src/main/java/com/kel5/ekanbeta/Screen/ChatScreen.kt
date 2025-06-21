@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.Common.ChatViewModelFactory
import com.kel5.ekanbeta.Repository.AuthRepo
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.ViewModel.ChatViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    toUserId: String,
    chatRepo: ChatRepo,
    authRepo: AuthRepo,
    isAdmin: Boolean,
    navController: NavHostController
) {
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepo, authRepo)
    )

    val messages by chatViewModel.messages.collectAsState()
    val isOnline by chatViewModel.isOnline.collectAsState()
    val isTyping by chatViewModel.isTyping.collectAsState()
    val lastSeen by chatViewModel.lastSeen.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val currentUserId = chatViewModel.currentUserId
    var title by remember { mutableStateOf("E-Kan Customer Service") }

    LaunchedEffect(toUserId, messages.size) {
        chatViewModel.startListening(toUserId)
        if (isAdmin) {
            chatViewModel.getUserData(toUserId) {
                title = it ?: "Pengguna"
            }
        }

        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.stopListening()
        }
    }

    val lastSeenText = when {
        isOnline == true -> "Online"
        isTyping -> "Sedang mengetik..."
        lastSeen != null -> {
            val diff = System.currentTimeMillis() - lastSeen!!
            val minutes = diff / 60000
            when {
                minutes < 1 -> "Baru saja aktif"
                minutes < 60 -> "Aktif $minutes menit lalu"
                else -> {
                    val date = Date(lastSeen!!)
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Aktif terakhir ${sdf.format(date)}"
                }
            }
        }
        else -> ""
    }

    Scaffold(
        topBar = {
            Surface(tonalElevation = 4.dp, shadowElevation = 4.dp) {
                Column {
                    TopAppBar(
                        modifier = Modifier.background(Color.White),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    tint = PrimaryColor
                                )
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isAdmin) Color.LightGray else PrimaryColor,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isAdmin) Icons.Default.Person else Icons.Default.HeadsetMic,
                                        contentDescription = "Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        fontFamily = Poppins,
                                        lineHeight = 18.sp
                                    )
                                    if (lastSeenText.isNotEmpty()) {
                                        Text(
                                            text = lastSeenText,
                                            fontSize = 12.sp,
                                            fontFamily = Poppins,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.White
                        )
                    )
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(BackgroundColor)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    val isMe = message.senderId == currentUserId
                    val alignment = if (isMe) Arrangement.End else Arrangement.Start
                    val bubbleColor = if (isMe) PrimaryColor else Color.LightGray
                    val textColor = if (isMe) Color.White else Color.Black
                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = alignment
                    ) {
                        Surface(
                            color = bubbleColor,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .padding(4.dp)
                                .widthIn(max = screenWidth * 0.8f)
                        ) {
                            Text(
                                text = message.text,
                                color = textColor,
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Gray, Color.Transparent)
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(65.dp)
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = {
                                messageText = it
                                chatViewModel.setTyping(toUserId, it.isNotEmpty())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            placeholder = {
                                Text(
                                    text = "Ketik pesan...",
                                    color = Color.LightGray,
                                    fontFamily = Poppins
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = Color.Gray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(25.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(onClick = {
                            if (messageText.isNotBlank()) {
                                chatViewModel.sendMessage(toUserId, messageText.trim())
                                messageText = ""
                            }
                        }) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Kirim pesan",
                                tint = PrimaryColor,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
