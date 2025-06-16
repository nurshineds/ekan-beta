@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.Data.AddressData
import com.kel5.ekanbeta.ViewModel.AddressViewModel
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun UserInputAlamatScreen(navController: NavHostController, onSuccess: () -> Unit){
    val addressViewModel: AddressViewModel = viewModel()

    var nama by remember { mutableStateOf("") }
    var nomorHp by remember { mutableStateOf("") }
    var kota by remember { mutableStateOf("") }
    var provinsi by remember { mutableStateOf("") }
    var kodePos by remember { mutableStateOf("") }
    var detailAlamat by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }

    val context = LocalContext.current

    val isFormValid = nama.isNotBlank() &&
            nomorHp.isNotBlank() &&
            kota.isNotBlank() &&
            provinsi.isNotBlank() &&
            kodePos.isNotBlank() &&
            detailAlamat.isNotBlank()

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
                                text = "Tambah Alamat",
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
                .padding(16.dp)
                .fillMaxSize()
        ) {
            InputField(value = nama, onValueChange = { nama = it }, label = "Nama")
            InputField(value = nomorHp, onValueChange = { nomorHp = it }, label = "Nomor Telepon")
            InputField(value = kota, onValueChange = { kota = it }, label = "Kota")
            InputField(value = provinsi, onValueChange = { provinsi = it }, label = "Provinsi")
            InputField(value = kodePos, onValueChange = { kodePos = it }, label = "Kode Pos")
            InputField(value = detailAlamat, onValueChange = { detailAlamat = it }, label = "Detail Alamat", maxLines = 3)
            InputField(value = catatan, onValueChange = { catatan = it }, label = "Catatan (Opsional)", maxLines = 2)

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = {
                    val newAddress = AddressData(
                        nama = nama,
                        nomorHp = nomorHp,
                        kota = kota,
                        provinsi = provinsi,
                        kodePos = kodePos,
                        detailAlamat = detailAlamat,
                        catatan = catatan
                    )
                    addressViewModel.addAddress(
                        newAddress,
                        onSuccess = {
                            onSuccess()
                        },
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = Color.White),
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = isFormValid,
            ) {
                Text(
                    text = "Simpan",
                    fontFamily = Poppins,
                    fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLines: Int = 1
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        maxLines = maxLines
    )
}