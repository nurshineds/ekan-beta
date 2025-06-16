package com.kel5.ekanbeta.Screen.AdminScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kel5.ekanbeta.ViewModel.StockRequestViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun AdminConfirmStockScreen(navController: NavController) {
    val stockRequestViewModel: StockRequestViewModel = viewModel()
    val stockRequests by stockRequestViewModel.stockRequests.collectAsState()

    LaunchedEffect(Unit) {
        stockRequestViewModel.loadStockRequests()
    }
    Scaffold(
        bottomBar = { AdminBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(bottom = innerPadding.calculateBottomPadding())
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
                    text = "Konfirmasi Stok Pengguna",
                    fontSize = 24.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if(stockRequests.isEmpty()){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada permintaan cek stok",
                        fontFamily = Poppins,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(24.dp)
                ){
                    items(stockRequests){ request ->
                        val userId = request["uid"] as? String ?: return@items
                        val status = request["status"] as? String ?: "unknown"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable{
                                    navController.navigate("requestStockDetail/$userId")
                                }
                        ){
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Column {
                                    Text("User ID: $userId")
                                    Text("Status: $status")
                                }
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Lihat Detail Request")
                            }
                        }
                    }
                }
            }
        }
    }
}
