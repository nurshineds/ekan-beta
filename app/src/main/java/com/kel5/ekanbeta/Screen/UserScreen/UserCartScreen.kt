package com.kel5.ekanbeta.Screen.UserScreen

import android.R.attr.enabled
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ViewModel.CartViewModel
import com.kel5.ekanbeta.ViewModel.UserViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.Data.ProductData

@Composable
fun CartUserScreen(navController: NavHostController) {
    val cartViewModel: CartViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val user by userViewModel.user
    val context = LocalContext.current
    val status by cartViewModel.stockReqStatus.collectAsState()

    LaunchedEffect(Unit) {
        cartViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(user) {
        user?.uid?.let{
            cartViewModel.loadStockReqStatus(it)
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
                    text = "Keranjang",
                    fontSize = 24.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if(user != null){
                val cartItems = user!!.cartItems.toList()
                val uid = user!!.uid

                if(!cartItems.isEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        items(cartItems, key = { it.first }){ (productId, qty) ->
                            CartItemView(
                                productId = productId,
                                qty = qty,
                                cartViewModel = cartViewModel
                            )
                            Divider(color = Color.LightGray, thickness = 1.dp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp)
                            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp, bottomEnd = 0.dp, bottomStart = 0.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ){
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ){
                            Button(
                                onClick = {
                                    if(status == null){
                                        cartViewModel.sendStockRequest(uid)
                                    } else if (status == "pending") {
                                        cartViewModel.cancelStockRequest(uid)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (status){
                                        null -> PrimaryColor
                                        "pending" -> Color.Red
                                        "confirmed" -> Color.Gray
                                        else -> PrimaryColor
                                    },
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = (status == null || status == "pending")
                            ){
                                Text(
                                    text = when (status){
                                        null -> "Cek Stok"
                                        "pending" -> "Batalkan Cek Stok"
                                        "confirmed" -> "Stok Dikonfirmasi"
                                        else -> "Cek Stok"
                                    },
                                    fontFamily = Poppins
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    navController.navigate("UserMakeOrder")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (status == "confirmed") PrimaryColor else Color.Gray,
                                    contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = status == "confirmed"
                            ){
                                Text(
                                    text = "Buat Pesanan",
                                    fontFamily = Poppins
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keranjang kosong",
                            fontFamily = Poppins,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemView(productId: String, qty: Long, cartViewModel: CartViewModel){
    val products by cartViewModel.products

    LaunchedEffect(key1 = productId) {
        cartViewModel.loadProduct(productId)
    }

    val product = products[productId]

    if(product != null){
        ItemCard(
            product = product,
            qty = qty,
            onIncrease = {
                cartViewModel.addToCart(productId)
            },
            onDecrease = {
                cartViewModel.removeFromCart(productId)
            },
            onRemove = {
                cartViewModel.removeFromCart(productId, removeAll = true)
            }
        )
    }
}

@Composable
fun ItemCard(
    product: ProductData,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    qty: Long
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color.White,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Apakah Anda yakin ingin menghapus produk ini dari keranjang?",
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onRemove()
                            showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Hapus", fontFamily = Poppins, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Batal", fontFamily = Poppins, color = Color.White)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { showDialog = true }) {
            Icon(
                imageVector = Icons.Filled.DeleteForever,
                contentDescription = "Hapus Item",
                tint = PrimaryColor
            )
        }

        AsyncImage(
            model = product.imageUrl,
            contentDescription = "Gambar Produk",
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            Text(
                text = product.nama,
                fontSize = 16.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold
            )

            val hargaDisplay = if (product.diskon > 0) product.hargaDiskon else product.harga
            Text(
                text = formatRupiah(hargaDisplay) + "/kg",
                fontSize = 14.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )

            Text(text = "$qty Kg", fontSize = 14.sp, color = Color.Gray)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Button(
                onClick = onDecrease,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(height = 30.dp, width = 30.dp),
            ) {
                Text("-", fontSize = 18.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = "$qty", fontSize = 18.sp, color = Color.Black)

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onIncrease,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(height = 30.dp, width = 30.dp)
            ) {
                Text("+", fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
