@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.Common.hitungHargaPromo
import com.kel5.ekanbeta.ViewModel.ReviewViewModel
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserReviewingScreen(
    navController: NavHostController,
    uid: String,
    orderId: String
) {
    val reviewViewModel: ReviewViewModel = viewModel()
    val products by reviewViewModel.products.collectAsState()
    var orderLoaded by remember { mutableStateOf(false) }

    val ratings = remember { mutableStateOf(mutableMapOf<String, Float>()) }
    val comments = remember { mutableStateOf(mutableMapOf<String, String>()) }

    LaunchedEffect(orderId) {
        if (!orderLoaded) {
            reviewViewModel.loadProductsForOrder(uid, orderId)
            orderLoaded = true
        }
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
                                text = "Review",
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
        },
        bottomBar = {
            Button(
                onClick = {
                    if (ratings.value.values.any { it <= 0f }) {
                        Toast.makeText(navController.context, "Semua produk harus diberi rating", Toast.LENGTH_SHORT).show()
                    } else {
                        reviewViewModel.submitAllReview(
                            ratings = ratings.value,
                            comments = comments.value,
                            products = products,
                            orderId = orderId,
                            onSuccess = {
                                Toast.makeText(navController.context, "Review berhasil dikirim", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = {
                                Toast.makeText(navController.context, "Gagal mengirim review", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2EADC9),
                    contentColor = Color.White
                )
            ) {
                Text("Review", fontFamily = Poppins)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) {
            items(products) { product ->
                val rating = ratings.value[product.id] ?: 0f
                val comment = comments.value[product.id] ?: ""

                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text(text = product.nama, fontSize = 14.sp, color = Color.Black, fontFamily = Poppins)
                            Text(text = "1 Kg", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                        }

                        Column(modifier = Modifier.padding(8.dp)) {
                            val hargaDisplay = hitungHargaPromo(product.harga, product.diskon)
                            if (product.diskon > 0) {
                                Column {
                                    Text(
                                        text = formatRupiah(product.harga.toInt()),
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        style = LocalTextStyle.current.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    )
                                    Text(
                                        text = formatRupiah(hargaDisplay.toInt()),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryColor
                                    )
                                }
                            } else {
                                Text(
                                    text = formatRupiah(product.harga.toInt()),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RatingBar(rating = rating) { selected ->
                        ratings.value = ratings.value.toMutableMap().apply {
                            this[product.id] = selected
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = {
                            val updated = comments.value.toMutableMap()
                            updated[product.id] = it
                            comments.value = updated
                        },
                        label = { Text("Komentar", fontFamily = Poppins) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row {
        for (i in 1..5) {
            val icon = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star
            val tint = if (i <= rating) Color.Yellow else Color.Gray

            Icon(
                imageVector = icon,
                contentDescription = "Rating Star $i",
                tint = tint,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
            )
        }
    }
}
