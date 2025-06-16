@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import com.kel5.ekanbeta.ViewModel.AddressViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserSelectAddressScreen(navController: NavHostController){
    val addressViewModel: AddressViewModel = viewModel()

    val addresses by addressViewModel.addresses.observeAsState(emptyList())

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
                                text = "Pilih Alamat",
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
        Box(
            modifier = Modifier
                .padding(padding)
                .background(BackgroundColor)
                .fillMaxSize()
                .padding(16.dp)
        ){
            LazyColumn{
                items(addresses){ address ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                addressViewModel.selectAddress(address)
                                navController.popBackStack()
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = address.nama, fontWeight = FontWeight.Bold)
                            Text(text = address.nomorHp)
                            Text(text = "${address.detailAlamat}, ${address.kota}, ${address.provinsi} ${address.kodePos}")
                        }
                    }
                }
            }
        }
    }
}