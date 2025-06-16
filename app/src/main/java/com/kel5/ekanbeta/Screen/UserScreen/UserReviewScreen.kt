@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.Data.ReviewData
import com.kel5.ekanbeta.ViewModel.ReviewViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.toInt

@Composable
fun UserListReview(navController: NavHostController, productId: String){
    val reviewViewModel: ReviewViewModel = viewModel()

    var selectedRating by remember { mutableStateOf("Semua") }
    val ratingOptions = listOf("Semua", "5", "4", "3", "2", "1")

    val reviews by reviewViewModel.reviews.observeAsState()

    LaunchedEffect(productId) {
        reviewViewModel.loadReviews(productId)
    }

    val filteredReviews = if (selectedRating == "Semua") {
        reviews ?: emptyList()
    } else {
        val selectedRatingInt = selectedRating.toIntOrNull() ?: 0
        reviews?.filter { it.rating.toInt() == selectedRatingInt } ?: emptyList()
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
                                text = "Review Produk",
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
                .background(BackgroundColor)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(ratingOptions) { rating ->
                    val isSelected = rating == selectedRating

                    Button(
                        onClick = { selectedRating = rating },
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor,
                                contentColor = Color.White
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = PrimaryColor
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp),
                        border = if (isSelected) null else BorderStroke(1.dp, PrimaryColor)
                    ) {
                        if (rating == "Semua") {
                            Text(text = rating, fontSize = 14.sp, fontFamily = Poppins)
                        } else {
                            Text(text = rating, fontSize = 14.sp, fontFamily = Poppins)
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = if (isSelected) Color.White else PrimaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredReviews) { review ->
                    Column{
                        ReviewItem(review = review)
                    }
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: ReviewData) {
    val dateFormatted = remember(review.timestamp) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(review.timestamp?.toDate() ?: Date())
        } catch (e: Exception) {
            "-"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = review.username ?: "Anonim",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                repeat(5) { index ->
                    if (index < review.rating.toInt()) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }

            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = dateFormatted,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = review.comment ?: "Tidak ada komentar",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}