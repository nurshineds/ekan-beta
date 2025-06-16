package com.kel5.ekanbeta.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kel5.ekanbeta.Preference.OnboardingPreference
import com.kel5.ekanbeta.R
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val imageRes: Int
)

@Composable
fun OnboardingScreen(navController: NavHostController) {
    val onboardingPages = listOf(
        OnboardingPage(
            title = "Memberikan kemudahan untuk menjangkau ikan segar kualitas tinggi",
            imageRes = R.drawable.onboard1
        ),
        OnboardingPage(
            title = "Melalui sumber tangkapan yang terjamin kualitasnya",
            imageRes = R.drawable.onboard2
        ),
        OnboardingPage(
            title = "Dapatkan ikan berkualitas sekarang!",
            imageRes = R.drawable.onboard3
        )
    )

    var currentPage by remember { mutableStateOf(0) }
    val isLastPage = currentPage == onboardingPages.lastIndex

    val context = LocalContext.current
    val onboardingPref = remember { OnboardingPreference(context) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = onboardingPages[currentPage].imageRes),
            contentDescription = "On Boarding Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(bottomStart = 72.dp))
                .background(Color.White)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo E-Kan",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topEnd = 72.dp))
                .background(Color.White)
                .padding(36.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = onboardingPages[currentPage].title,
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isLastPage) {
                        coroutineScope.launch {
                            onboardingPref.setOnboardingCompleted(true)
                            navController.navigate("Register") {
                                popUpTo("OnBoarding") { inclusive = true }
                            }
                        }
                    } else {
                        currentPage++
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (isLastPage) "Mulai" else "Lanjut",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), // Tambah jarak dari atas
                horizontalArrangement = Arrangement.Center
            ) {
                val blue = PrimaryColor
                val grey = Color.Gray
                onboardingPages.forEachIndexed { index, _ ->
                    val color = if (index <= currentPage) blue else grey
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color)
                    )
                    if (index != onboardingPages.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(navController = rememberNavController())
}