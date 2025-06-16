package com.kel5.ekanbeta

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.Screen.AdminScreen.AdminChatScreen
import com.kel5.ekanbeta.Screen.AdminScreen.AdminConfirmStockDetailScreen
import com.kel5.ekanbeta.Screen.AdminScreen.AdminConfirmStockScreen
import com.kel5.ekanbeta.Screen.AdminScreen.AdminHomeScreen
import com.kel5.ekanbeta.Screen.AdminScreen.AdminProfileScreen
import com.kel5.ekanbeta.Screen.ChatScreen
import com.kel5.ekanbeta.Screen.LoginScreen
import com.kel5.ekanbeta.Screen.OnboardingScreen
import com.kel5.ekanbeta.Screen.RegisterScreen
import com.kel5.ekanbeta.Screen.UserScreen.HomeUserScreen
import com.kel5.ekanbeta.Screen.SplashScreen
import com.kel5.ekanbeta.Screen.UserScreen.CartUserScreen
import com.kel5.ekanbeta.Screen.UserScreen.ProductUserScreen
import com.kel5.ekanbeta.Screen.UserScreen.ProfileUserScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserListAlamatScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserProductDetailScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserInputAlamatScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserListReview
import com.kel5.ekanbeta.Screen.UserScreen.UserMakeOrderScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserOrderHistoryScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserOrderSuccessScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserPayPage
import com.kel5.ekanbeta.Screen.UserScreen.UserPaymentDetailScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserPaymentMethodScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserReviewingScreen
import com.kel5.ekanbeta.Screen.UserScreen.UserSelectAddressScreen

@Composable
fun AppNav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Splash") {
        composable("Splash") { SplashScreen(navController) }
        composable("OnBoarding") { OnboardingScreen(navController) }
        composable("Register") { RegisterScreen(navController) }
        composable("Login") { LoginScreen(navController) }

        // USER SCREEN
        composable("UserHome") { HomeUserScreen(navController) }
        composable("UserProduct") { ProductUserScreen(navController) }
        composable("UserCart") { CartUserScreen(navController) }
        composable("UserProfile") { ProfileUserScreen(navController) }
        composable("UserListAlamat") { UserListAlamatScreen(navController) }
        composable("UserInputAlamat") { UserInputAlamatScreen(navController, onSuccess = { navController.navigate("UserListAlamat") }) }
        composable("UserMakeOrder") { UserMakeOrderScreen(navController) }
        composable("UserSelectAddress") { UserSelectAddressScreen(navController) }
        composable("OrderSuccess") { UserOrderSuccessScreen(navController) }
        composable("UserHistory") { UserOrderHistoryScreen(navController, uid = Firebase.auth.currentUser?.uid ?: "") }

        //PEMBAYARAN
        composable(
            route = "UserBayar/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType})
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            UserPayPage(navController, orderId)
        }

        //METODE PEMBAYARAN
        composable(
            route = "UserPayMethod/{totalAmount}/{orderId}",
            arguments = listOf(
                navArgument("totalAmount") { type = NavType.FloatType },
                navArgument("orderId") { type = NavType.StringType }            )
        ) {
                backStackEntry ->
            val totalAmount = backStackEntry.arguments?.getFloat("totalAmount") ?: 0f
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            UserPaymentMethodScreen(navController, totalAmount, orderId)
        }

        //DETAIL PEMBAYARAN
        composable(
            route = "UserPayDetail/{bankName}/{accountNumber}/{amount}/{orderId}",
            arguments = listOf(
                navArgument("bankName") { type = NavType.StringType },
                navArgument("accountNumber") { type = NavType.StringType },
                navArgument("amount") { type = NavType.FloatType },
                navArgument("orderId") { type = NavType.StringType }
            )
        ) {  backStackEntry ->
            val bankName = backStackEntry.arguments?.getString("bankName") ?: ""
            val accountNumber = backStackEntry.arguments?.getString("accountNumber") ?: ""
            val amount = backStackEntry.arguments?.getFloat("amount") ?: 0f
            val orderId = backStackEntry.arguments?.getString("orderID") ?: ""

            UserPaymentDetailScreen(
                navController = navController,
                bankName = bankName,
                accountNumber = accountNumber,
                amount = amount,
                orderId = orderId
            )
        }

        // DETAIL PRODUK
        composable(
            route = "ProductDetail/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            UserProductDetailScreen(navController, productId)
        }

        //REVIEW
        composable(
            route = "UserReview/{uid}/{orderId}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            UserReviewingScreen(navController, uid, orderId)
        }

        //USER LIST REVIEW
        composable("reviewList/{productId}") {
            val productId = it.arguments?.getString("productId") ?: ""
            UserListReview(navController = navController, productId)
        }

        // ADMIN SCREEN
        composable("AdminHome") { AdminHomeScreen(navController) }
        composable("AdminConfirm") { AdminConfirmStockScreen(navController) }
        composable("AdminChatList") { AdminChatScreen(navController) }
        composable("AdminProfile") { AdminProfileScreen(navController) }

        //DETAIL REQUEST CEK STOK UNTUK ADMIN
        composable("requestStockDetail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            AdminConfirmStockDetailScreen(navController = navController, userId = userId)
        }

        // CHAT SCREEN UNTUK ADMIN
        composable(
            route = "ChatScreen/{toUserId}?isAdmin={isAdmin}",
            arguments = listOf(
                navArgument("toUserId") { type = NavType.StringType },
                navArgument("isAdmin") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val toUserId = backStackEntry.arguments?.getString("toUserId") ?: ""
            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false

            ChatScreen(
                toUserId = toUserId,
                chatRepo = ChatRepo(),
                isAdmin = isAdmin,
                navController = navController
            )
        }
    }
}
