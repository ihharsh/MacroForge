package com.example.macroforge.feature_meals.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.feature_meals.presentation.util.formatMacro
import com.yourname.macroforge.core.ui.theme.Amber
import com.yourname.macroforge.core.ui.theme.Background
import com.yourname.macroforge.core.ui.theme.Blue
import com.yourname.macroforge.core.ui.theme.Green
import com.yourname.macroforge.core.ui.theme.Orange
import com.yourname.macroforge.core.ui.theme.Surface
import com.yourname.macroforge.core.ui.theme.TextGhost

@Composable
fun MealTotalFooter(
    totalCalories: Float,
    totalProtein: Float,
    totalCarbs: Float,
    totalFats: Float,
    onSaveMeal: () -> Unit
) {
    Surface(
        color = Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("MEAL TOTAL", color = TextGhost, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Nutrition detail", color = Orange, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                MacroDonut(
                    protein = totalProtein,
                    carbs = totalCarbs,
                    fats = totalFats,
                    centerValue = totalCalories.toInt().toString(),
                    modifier = Modifier.size(84.dp)
                )
                Spacer(Modifier.width(12.dp))
                MacroStat(formatMacro(totalProtein), "G PROT", Blue, Color(0xFFE3E9FF), Icons.Default.Bolt, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                MacroStat(formatMacro(totalCarbs), "G CARB", Amber, Color(0xFFFCEFD8), Icons.Default.Grain, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                MacroStat(formatMacro(totalFats), "G FAT", Green, Color(0xFFDFF3E4), Icons.Default.WaterDrop, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            val total = (totalProtein * 4 + totalCarbs * 4 + totalFats * 9).coerceAtLeast(1f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                Box(Modifier.weight((totalProtein * 4 / total).coerceAtLeast(0.001f)).fillMaxHeight().background(Blue))
                Box(Modifier.weight((totalCarbs * 4 / total).coerceAtLeast(0.001f)).fillMaxHeight().background(Amber))
                Box(Modifier.weight((totalFats * 9 / total).coerceAtLeast(0.001f)).fillMaxHeight().background(Green))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onSaveMeal,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Meal", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MacroStat(
    value: String,
    label: String,
    color: Color,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(bgColor).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 9.sp, color = color, letterSpacing = 0.4.sp)
    }
}

@Composable
private fun MacroDonut(
    protein: Float,
    carbs: Float,
    fats: Float,
    centerValue: String,
    modifier: Modifier = Modifier
) {
    val total = (protein + carbs + fats).coerceAtLeast(0.001f)
    val proteinSweep = 360f * (protein / total)
    val carbSweep = 360f * (carbs / total)
    val fatSweep = 360f * (fats / total)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * 0.16f
            var startAngle = -90f
            drawArc(Blue, startAngle, proteinSweep, false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            startAngle += proteinSweep
            drawArc(Amber, startAngle, carbSweep, false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            startAngle += carbSweep
            drawArc(Green, startAngle, fatSweep, false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerValue, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Orange)
            Text("KCAL", fontSize = 9.sp, color = TextGhost)
        }
    }
}