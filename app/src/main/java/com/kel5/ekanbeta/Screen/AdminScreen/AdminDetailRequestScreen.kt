package com.kel5.ekanbeta.Screen.AdminScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.ViewModel.CartViewModel
import com.kel5.ekanbeta.ViewModel.StockRequestViewModel
import com.kel5.ekanbeta.ViewModel.UserViewModel
import androidx.compose.runtime.getValue
import com.kel5.ekanbeta.Screen.UserScreen.CartItemView
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun AdminConfirmStockDetailScreen(
    navController: NavHostController,
    userId: String
){
    val userViewModel: UserViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val stockRequestViewModel: StockRequestViewModel = viewModel()

    val user by userViewModel.getUserById(userId).collectAsState(initial = null)
    val userId = userId

    LaunchedEffect(userId) {
        userViewModel.loadUserById(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(PrimaryColor)
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Detail Request Pengguna",
                fontSize = 24.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Detail Keranjang - $userId")

        user?.cartItems?.forEach { (productId, qty) ->
            CartItemView(productId = productId, qty = qty, cartViewModel = cartViewModel)
            Divider(color = Color.LightGray, thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                stockRequestViewModel.confirmStock(userId)
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = Color.White)
        ) {
            Text("Konfirmasi Stok")
        }
    }
}