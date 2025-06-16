package com.kel5.ekanbeta.Screen.UserScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ViewModel.OrderViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.kel5.ekanbeta.Common.formatRupiah

@Composable
fun UserOrderHistoryScreen(navController: NavHostController, uid: String) {
    val orderViewModel: OrderViewModel = viewModel()

    LaunchedEffect(uid) {
        orderViewModel.setUser(uid)
    }

    val orders by orderViewModel.orders.observeAsState(emptyList())
    val error by orderViewModel.error.observeAsState("")

    var selectedStatus by remember { mutableStateOf("Semua") }
    val statusOptions = listOf("Semua", "Belum Bayar", "Diproses", "Dikirim", "Selesai", "Reviewed")

    val statusPriority = mapOf(
        "Belum Bayar" to 0,
        "Diproses" to 1,
        "Dikirim" to 2,
        "Selesai" to 3,
        "Reviewed" to 4
    )

    val filteredOrders = if (selectedStatus == "Semua") {
        orders.sortedBy { statusPriority[it.status] ?: 99 }
    } else {
        orders.filter { it.status.equals(selectedStatus, ignoreCase = true) }
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
                    text = "Pesanan Saya",
                    fontSize = 24.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(statusOptions) { status ->
                    val isSelected = status == selectedStatus
                    Button(
                        onClick = { selectedStatus = status },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PrimaryColor else Color.Transparent,
                            contentColor = if (isSelected) Color.White else PrimaryColor
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, PrimaryColor),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = status, fontSize = 14.sp, fontFamily = Poppins)
                    }
                }
            }

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )

            if (error.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = Color.Red, fontFamily = Poppins)
                }
            } else if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tidak ada pesanan dengan status \"$selectedStatus\".",
                        fontFamily = Poppins,
                        color = Color.Gray
                    )
                }
            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(filteredOrders) { order ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {

                                Text("Order ID: ${order.id}", fontWeight = FontWeight.Bold, fontFamily = Poppins)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Status: ${order.status.replaceFirstChar { it.uppercase() }}", fontFamily = Poppins)
                                Divider(
                                    color = Color.LightGray,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth()
                                )
                                Text("Alamat: ${order.address.detailAlamat}", fontFamily = Poppins)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (order.address.catatan.isNotEmpty()) {
                                    Text("Catatan: ${order.address.catatan}", fontFamily = Poppins)
                                }
                                Divider(
                                    color = Color.LightGray,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth()
                                )
                                Text("Total: ${order.items.values.sum()} Produk = " + formatRupiah(order.total.toInt()), fontFamily = Poppins)

                                Spacer(modifier = Modifier.height(8.dp))

                                when (order.status.lowercase()) {
                                    "belum bayar" -> {
                                        Button(
                                            onClick = {
                                                navController.navigate("UserBayar/${order.id}")
                                            },
                                            modifier = Modifier.align(Alignment.Start)
                                                .height(36.dp)
                                                .fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2EADC9),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(20.dp),
                                        ) {
                                            Text("Bayar")
                                        }
                                    }

                                    "selesai" -> {
                                        Button(
                                            onClick = {
                                                navController.navigate("UserReview/$uid/${order.id}")
                                            },
                                            modifier = Modifier.align(Alignment.Start)
                                                .height(36.dp)
                                                .fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2EADC9),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(20.dp),
                                        ) {
                                            Text("Review")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}