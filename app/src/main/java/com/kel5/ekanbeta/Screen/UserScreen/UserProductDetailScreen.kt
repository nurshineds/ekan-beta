@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kel5.ekanbeta.Common.ChatViewModelFactory
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.ViewModel.CartViewModel
import com.kel5.ekanbeta.ViewModel.ChatViewModel
import com.kel5.ekanbeta.ViewModel.ProductViewModel
import com.kel5.ekanbeta.ViewModel.UserViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserProductDetailScreen(navController: NavHostController, productId: String) {
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val chatRepo = remember { ChatRepo() }
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepo)
    )

    val product = productViewModel.selectedProduct.collectAsState()
    val adminUid = remember { mutableStateOf<String?>(null) }
    val user = userViewModel.user.value
    val context = LocalContext.current

    LaunchedEffect(productId) {
        productViewModel.getProductById(productId)
    }

    LaunchedEffect(Unit) {
        chatViewModel.getAdminUID { uid ->
            adminUid.value = uid
        }
    }

    LaunchedEffect(Unit) {
        cartViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                        modifier = Modifier.background(Color.White),
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
                                text = "Detail Produk",
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
            Surface(
                color = Color.White,
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Button(
                        onClick = {
                            adminUid.value?.let { uid -> navController.navigate("ChatScreen/$uid") }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "Chat Admin tentang Produk",
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Button(
                        onClick = {
                            user?.uid?.let { cartViewModel.addToCart(productId)}
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Tambah Produk ke Keranjang",
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Tambah Keranjang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    ) { padding ->
        product.value?.let { selectedProduct ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .background(BackgroundColor)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                AsyncImage(
                    model = selectedProduct.imageUrl,
                    contentDescription = "Gambar Produk",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = selectedProduct.nama,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )

                    if (selectedProduct.diskon > 0) {
                        Text(
                            text = formatRupiah(selectedProduct.harga),
                            fontSize = 12.sp,
                            color = Color.Red,
                            textDecoration = TextDecoration.LineThrough,
                            fontFamily = Poppins
                        )
                    }

                    val hargaDisplay = if (selectedProduct.diskon > 0)
                        selectedProduct.hargaDiskon
                    else
                        selectedProduct.harga

                    Text(
                        text = formatRupiah(hargaDisplay) + "/kg",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("reviewList/$productId")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = selectedProduct.rating.toString(),
                                color = Color.Black,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color.Yellow
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "Lihat Penilaian Produk",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = Poppins
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Navigate",
                                tint = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Deskripsi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )

                    Text(
                        text = selectedProduct.deskripsi,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    detailColumn("Origin Produk", selectedProduct.origin, Modifier.weight(1f))
                    detailColumn("Cara Penyajian", selectedProduct.penyajian, Modifier.weight(1f))
                    detailColumn("Kondisi Produk", selectedProduct.kondisi, Modifier.weight(1f))
                    detailColumn("Jenis Potongan", selectedProduct.potongan, Modifier.weight(1f))
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        }
    }
}

@Composable
fun detailColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 14.sp,
                fontFamily = Poppins,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value.ifEmpty { "-" },
            color = Color.Black,
            fontSize = 14.sp,
            fontFamily = Poppins,
            textAlign = TextAlign.Center
        )
    }
}
