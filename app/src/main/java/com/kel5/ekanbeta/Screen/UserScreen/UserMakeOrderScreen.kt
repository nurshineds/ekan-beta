@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.Common.hitungHargaPromo
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.Screen.StatusStepIndicator
import com.kel5.ekanbeta.ViewModel.AddressViewModel
import com.kel5.ekanbeta.ViewModel.OrderViewModel
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserMakeOrderScreen(navController: NavHostController){
    val orderViewModel: OrderViewModel = viewModel()
    val addressViewModel: AddressViewModel = viewModel()

    val scrollState = rememberScrollState()
    val products = orderViewModel.productList
    val total = orderViewModel.total.value
    val ongkir = orderViewModel.ongkir.value
    val totalFinal = orderViewModel.totalFinal.value

    val selectedAddress by addressViewModel.selectedAddress.observeAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        orderViewModel.fetchCheckoutData()
        addressViewModel.fetchAddresses()
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
    ){ padding  ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
        ){
            StatusStepIndicator(currentStep = 0, allDisabled = true)

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
                Column {
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
                    ) {
                        Text(
                            text = "Alamat Pengiriman",
                            fontFamily = Poppins,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        )

                        Text(
                            text = "Ganti",
                            fontFamily = Poppins,
                            modifier = Modifier.padding(end = 16.dp)
                                .clickable {
                                    navController.navigate("UserSelectAddress")
                                },
                            color = Color(0xFF2EADC9)
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {

                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedAddress != null) {
                            Text(text = selectedAddress!!.nama, fontFamily = Poppins)
                            Text(text = selectedAddress!!.nomorHp, fontFamily = Poppins)
                            Text(
                                text = "${selectedAddress!!.detailAlamat}, " +
                                        "${selectedAddress!!.kota}, " +
                                        "${selectedAddress!!.provinsi} " +
                                        "${selectedAddress!!.kodePos}",
                                fontFamily = Poppins
                            )
                        } else {
                            Text(text = "Pilih alamat pengiriman", fontFamily = Poppins, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ){
                Column {
                    Text(
                        text = "Keranjang",
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

                    products.forEach { product ->
                        productItem(product = product)
                    }

                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )

                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            Text("Total Harga", modifier = Modifier.weight(1f), fontFamily = Poppins)
                            Text(formatRupiah(total.toInt()), fontFamily = Poppins)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ){
                Column {
                    Text(
                        text = "Rincian Pembayaran",
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

                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            Text("Total Pembelian", modifier = Modifier.weight(1f), fontFamily = Poppins)
                            Text(formatRupiah(total.toInt()), fontFamily = Poppins)
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(Modifier.fillMaxWidth()) {
                            Text("Biaya Pengiriman", modifier = Modifier.weight(1f), fontFamily = Poppins)
                            Text(formatRupiah(ongkir.toInt()), fontFamily = Poppins)
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(Modifier.fillMaxWidth()) {
                            Text("Tagihan Saat Ini", modifier = Modifier.weight(1f), fontFamily = Poppins, fontWeight = FontWeight.Bold)
                            Text(formatRupiah(totalFinal.toInt()), fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Button(
                    onClick = {
                        if (selectedAddress != null) {
                            orderViewModel.placeOrder(context, selectedAddress)
                            navController.navigate("OrderSuccess")
                        } else {
                            Toast.makeText(context, "Alamat belum dipilih", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor
                    )
                ) {
                    Text("Buat Order", color = Color.White, fontFamily = Poppins)
                }
            }
        }
    }
}

@Composable
fun productItem(product: ProductData) {
    val orderViewModel: OrderViewModel = viewModel()
    val qty = orderViewModel.user.value.cartItems[product.id] ?: 0

    val hargaPerItem = hitungHargaPromo(product.harga.toInt(), product.diskon)
    val totalHarga = hargaPerItem * qty

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = " ",
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = product.nama, fontFamily = Poppins)
            Text(text = "1 kg", fontFamily = Poppins)
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 16.dp, top = 16.dp)
        ) {
            Text(text = "$qty x", fontFamily = Poppins)
            Text(text = formatRupiah(totalHarga.toInt()), fontFamily = Poppins)
        }
    }
}
