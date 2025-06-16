package com.kel5.ekanbeta.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor

@Composable
fun StatusStepIndicator(currentStep: Int, allDisabled: Boolean = false) {
    val steps = listOf("Buat\nPesanan", "Pembayaran", "Verifikasi")
    val stepIcons = listOf(
        Icons.Default.ShoppingCart,
        Icons.Default.CreditCard,
        Icons.Default.VerifiedUser
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, title ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 12.dp) // padding horizontal
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = stepIcons[index],
                        contentDescription = title,
                        tint = if (allDisabled) Color.Gray else if (index <= currentStep) PrimaryColor else Color.Gray
                    )
                }
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontFamily = Poppins,
                    color = if (allDisabled) Color.Gray else if (index <= currentStep) PrimaryColor else Color.Gray
                )
            }

            if (index < steps.lastIndex) {
                DashedLine(
                    color = if (allDisabled) Color.Gray else if (index < currentStep) PrimaryColor else Color.Gray
                )
            }
        }
    }
}

@Composable
fun DashedLine(
    color: Color,
    dashWidth: Float = 8f,
    gapWidth: Float = 4f,
    modifier: Modifier = Modifier
        .width(30.dp)
        .height(2.dp)
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val canvasWidth = size.width
        var startX = 0f
        while (startX < canvasWidth) {
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(startX, 0f),
                end = androidx.compose.ui.geometry.Offset((startX + dashWidth).coerceAtMost(canvasWidth), 0f),
                strokeWidth = size.height
            )
            startX += dashWidth + gapWidth
        }
    }
}