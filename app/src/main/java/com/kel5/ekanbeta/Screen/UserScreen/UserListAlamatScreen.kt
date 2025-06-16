@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ViewModel.AddressViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kel5.ekanbeta.Data.AddressData
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserListAlamatScreen(navController: NavHostController){
    val addressViewModel : AddressViewModel = viewModel()

    val addresses by addressViewModel.addresses.observeAsState(emptyList())
    val error by addressViewModel.error.observeAsState()

    LaunchedEffect(Unit) {
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
                                text = "Daftar Alamat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                fontFamily = Poppins,
                                color = PrimaryColor
                            )
                        },
                        actions = {
                            Button(
                                onClick = { navController.navigate("UserInputAlamat") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryColor,
                                    contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .size(height = 30.dp, width = 30.dp)
                            ) {
                                Text("+", fontSize = 18.sp, textAlign = TextAlign.Center)
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
    ){ padding  ->
        if(addresses.isEmpty()){
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Daftar Alamat Kosong",
                    fontFamily = Poppins,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()) // penting agar tidak tertutup AppBar
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ){
                items(addresses) { alamat ->
                    AddressItem(alamat)
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }

        error?.let {
            Text(
                text = "Error: $it",
                color = Color.Red
            )
        }
    }
}

@Composable
fun AddressItem(address: AddressData){
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Text(
            text = address.nama,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Text(
            text = "${address.detailAlamat}\n" +
                    "${address.kota}, ${address.provinsi}\n" +
                    "${address.kodePos}\n" +
                    "${address.nomorHp}",
            fontFamily = Poppins,
            color = Color.DarkGray
        )

        if(address.catatan.isNotBlank()){
            Text(
                text = "Catatan: " + address.catatan,
                fontFamily = Poppins,
                color = Color.Gray
            )
        }
    }
}