@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ViewModel.OrderViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.Screen.StatusStepIndicator
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserPayPage(navController: NavHostController, orderId: String){
    val orderViewModel: OrderViewModel = viewModel()

    val order by orderViewModel.selectedOrder.observeAsState()

    LaunchedEffect(orderId) {
        orderViewModel.loadOrderById(orderId)
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 4.dp
            ) {
                Column {
                    TopAppBar(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(end = 16.dp),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    tint = PrimaryColor
                                )
                            }
                        },
                        title = {
                            Text(
                                text = "Detail Pesanan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                fontFamily = Poppins,
                                color = PrimaryColor
                            )
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
                .padding(24.dp)
                .fillMaxSize(),
        ) {
            StatusStepIndicator(currentStep = 1)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Pengiriman",
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Alamat Pengiriman :",
                        fontFamily = Poppins,
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                }
                Column(modifier = Modifier
                    .padding(start = 16.dp, bottom = 8.dp)
                    .padding(vertical = 4.dp)) {
                    order?.address?.let { Text(text = it.nama, fontFamily = Poppins) }
                    order?.address?.let { Text(text = it.nomorHp, fontFamily = Poppins) }
                    Text(
                        text = "${order?.address?.detailAlamat}, " +
                                "${order?.address?.kota}, " +
                                "${order?.address?.provinsi} " +
                                "${order?.address?.kodePos}",
                        fontFamily = Poppins
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Rincian Pesanan",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )

                Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                    order?.let {
                        Text("Status: ${it.status.replaceFirstChar { it.uppercase() }}", fontFamily = Poppins)
                        Text("Total Produk: ${it.items.values.sum()}", fontFamily = Poppins)
                        Text("Total Harga: " + formatRupiah(it.total.toInt()), fontFamily = Poppins)
                    }

                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            order?.let {
                Button(
                    onClick = {
                        val total = it.total
                        navController.navigate("UserPayMethod/$total/$orderId")
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2EADC9),
                        contentColor = Color.White)
                ) {
                    Text(text = "Bayar", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}