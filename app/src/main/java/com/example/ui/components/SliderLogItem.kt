package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftTeal

@Composable
fun SliderLogItem(title: String, description: String, value: Float, onValueChange: (Float) -> Unit) {
    val score = value.toInt()
    
    val color = when(score) {
        1 -> Color(0xFFD32F2F)
        2 -> Color(0xFFE64A19)
        3 -> Color(0xFFFFA000)
        4 -> Color(0xFF388E3C)
        5 -> Color(0xFF00796B)
        else -> SoftTeal
    }

    val displayText = when(score) {
        1 -> "🔴 Severe Challenge"
        2 -> "🟠 Moderate Challenge"
        3 -> "🟡 Stable / Normal"
        4 -> "🟢 Good Response"
        5 -> "🌟 Excellent Day"
        else -> "Stable"
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .background(color.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$displayText ($score/5)",
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 11.sp
                )
            }
        }
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(bottom = 6.dp, top = 2.dp)
        )
        Slider(
            value = value,
            onValueChange = { onValueChange(it) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        )
    }
}
