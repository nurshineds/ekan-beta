@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.ViewModel.OrderViewModel
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

@Composable
fun UserPaymentMethodScreen(
    navController: NavHostController,
    totalAmount: Float,
    orderId: String
){
    val banks = listOf(
        "BCA" to "123456789",
        "Mandiri" to "987654321",
        "BNI" to "112233445566",
        "BRI" to "665544332211"
    )

    val bankList = banks.toList()

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
                                text = "Metode Pembayaran",
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
            Text(text = "Transfer Bank",
                fontSize = 18.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            bankList.chunked(2).forEach { rowBanks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowBanks.forEach { (bankName, accountNumber) ->
                        Card(
                            shape = RoundedCornerShape(size = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("UserPayDetail/$bankName/$accountNumber/${totalAmount}/$orderId")
                                },
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("$bankName", fontSize = 14.sp, fontFamily = Poppins)
                            }
                        }
                    }

                    if (rowBanks.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun UserPaymentDetailScreen(
    navController: NavHostController,
    bankName: String,
    accountNumber: String,
    amount: Float,
    orderId: String
) {
    val orderViewModel: OrderViewModel = viewModel()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    orderViewModel.setUser(Firebase.auth.currentUser?.uid ?: "")

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        LaunchedEffect(Unit) {
            delay(2000)
            showErrorDialog = false
            navController.navigate("UserHistory") {
                popUpTo("UserPaymentDetailScreen") { inclusive = true }
            }
        }

        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", fontFamily = Poppins)
                }
            },
            title = {
                Text("Terjadi Kesalahan", fontFamily = Poppins, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(errorMessage, fontFamily = Poppins)
            }
        )
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
                                text = "Detail Pembayaran",
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
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Metode Pembayaran",
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = bankName,
                        fontFamily = Poppins,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nomor Rekening",
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                )

                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = accountNumber,
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(accountNumber))
                            Toast.makeText(context, "Nomor rekening disalin", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin nomor rekening"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Jumlah Transfer",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                )

                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatRupiah(amount.toInt()),
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f),
                        color = Color.Red
                    )

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(amount.toString()))
                            Toast.makeText(context, "Jumlah Transfer disalin", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin jumlah transfer"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    Log.d("PaymentDetailScreen", "Konfirmasi pembayaran untuk orderId: $orderId")
                    orderViewModel.updateOrderStatus(orderId, "diproses", {
                        navController.navigate("UserHistory")
                    }, { error ->
                        Log.e("PaymentDetailScreen", "Update status gagal: ${error.message}", error)
                        errorMessage = "Gagal mengkonfirmasi pembayaran. Silakan cek riwayat pesanan Anda."
                        showErrorDialog = true
                    })
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2EADC9),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Konfirmasi Pembayaran", fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }
        }
    }
}