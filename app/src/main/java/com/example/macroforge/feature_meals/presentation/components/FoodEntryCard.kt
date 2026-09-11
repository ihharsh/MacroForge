package com.example.macroforge.feature_meals.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.core.ui.theme.*
import com.example.macroforge.core.ui.theme.ComponentDefaults.quantityTextFieldColors
import com.example.macroforge.feature_meals.presentation.model.FoodUiItem
import com.example.macroforge.feature_meals.presentation.util.formatMacro

@Composable
fun FoodEntryCard(
    food: FoodUiItem,
    onQuantityChanged: (Float) -> Unit,
    onRemove: () -> Unit
) {
    // Keep qty text local — only update domain when float parses successfully
    var qtyText by remember(food.id) {
        mutableStateOf(
            if (food.quantity % 1f == 0f) food.quantity.toInt().toString()
            else food.quantity.toString()
        )
    }

    Card(
        shape = FoodCardShape,                      // RoundedCornerShape(20dp)
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = ElevationCard),  // 2dp
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = FoodCardSpec.borderWidth,   // 1.5dp
                color = BorderSolid,                // #ECEEF3
                shape = FoodCardShape
            )
    ) {
        Column {
            // ── Header Row: name + qty pill + trash ─────────────────────────
            // TSX: padding "11px 12px 9px" — top:11, h:12, bottom:9
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start  = FoodCardSpec.headerPaddingHorizontal,  // 12dp
                        end    = FoodCardSpec.headerPaddingHorizontal,
                        top    = Space11,   // 11dp
                        bottom = Space9     // 9dp
                    ),
                verticalAlignment = Alignment.CenterVertically,         // TSX: alignItems:center
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Food name + subtitle
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Space2)  // 2dp gap
                ) {
                    Text(
                        text = food.name,
                        style = FoodCardName,           // Outfit Bold 13sp ls:-0.26sp
                        color = TextPrimary,            // #0C0F1A
                        maxLines = 1
                    )
                    Text(
                        text = food.subtitle,
                        style = FoodCardSubtitle,       // Outfit Medium 10sp
                        color = TextSecondary,          // #6B7280
                        maxLines = 1
                    )
                }

                Spacer(Modifier.width(Space8))          // 8dp gap — TSX gap:8

                // ── Quantity Pill ────────────────────────────────────────────
                // TSX: bg:surfaceAlt, border:1dp borderColor, padding "5px 8px",
                //      borderRadius:10, input DM Mono 13sp + unit label
                Row(
                    modifier = Modifier
                        .clip(QuantityPillShape)            // 10dp corners
                        .background(SurfaceAlt)
                        .border(
                            width = QuantityPillSpec.borderWidth,   // 1dp
                            color = BorderSolid,
                            shape = QuantityPillShape
                        )
                        .padding(
                            horizontal = QuantityPillSpec.paddingHorizontal,  // 8dp
                            vertical   = QuantityPillSpec.paddingVertical     // 5dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Number input — BasicTextField gives full style control
                    // without M3 OutlinedTextField fighting our bg/border
                    androidx.compose.foundation.text.BasicTextField(
                        value = qtyText,
                        onValueChange = { value ->
                            qtyText = value
                            value.toFloatOrNull()?.let(onQuantityChanged)
                        },
                        singleLine = true,
                        textStyle = QuantityInput.copy(            // DM Mono 13sp Medium
                            textAlign = TextAlign.Start,
                            color = TextPrimary
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier.width(QuantityPillSpec.inputWidth).padding(start = 5.dp) // 44dp
                    )

                    Spacer(Modifier.width(Space4))

                    // Unit label — "g", "ml", "scoop", "egg" etc.
                    Text(
                        text = food.unit,
                        style = QuantityUnit,           // Outfit Bold 10sp ghost
                        color = TextGhost               // #B0B5C3
                    )
                }

                Spacer(Modifier.width(Space6))          // 6dp gap before trash

                // ── Trash Button ─────────────────────────────────────────────
                // TSX: width:28, height:28, borderRadius:9, bg:redBg, icon:12dp red
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .clip(RoundedCornerShape(TrashButtonSpec.cornerRadius))  // 9dp// #FEF3F2

                        .background(RedBg)
                        .size(TrashButtonSpec.size)                     // 28dp

                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove ${food.name}",
                        tint = Red,                                     // #F04438
                        modifier = Modifier.size(TrashButtonSpec.iconSize)       // 12dp
                    )
                }
            }

            // ── Macro Chips Row ──────────────────────────────────────────────
            // TSX: margin "0 12px 11px" → start:12, end:12, bottom:11
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start  = FoodCardSpec.macroRowMarginHorizontal,  // 12dp
                        end    = FoodCardSpec.macroRowMarginHorizontal,
                        bottom = FoodCardSpec.macroRowMarginBottom        // 11dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(MacroChipGridGap)  // 5dp
            ) {
                MacroPill(
                    value         = food.calories.toInt().toString(),
                    label         = "KCAL",
                    valueColor    = Orange,
                    bgColor       = OrangeBg,
                    showGramSuffix = false,
                    modifier      = Modifier.weight(1f)
                )
                MacroPill(
                    value      = formatMacro(food.protein),
                    label      = "PROTEIN",
                    valueColor = Blue,
                    bgColor    = BlueBg,
                    modifier   = Modifier.weight(1f)
                )
                MacroPill(
                    value      = formatMacro(food.carbs),
                    label      = "CARBS",
                    valueColor = Amber,
                    bgColor    = AmberBg,
                    modifier   = Modifier.weight(1f)
                )
                MacroPill(
                    value      = formatMacro(food.fats),
                    label      = "FAT",
                    valueColor = Green,
                    bgColor    = GreenBg,
                    modifier   = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MacroPill(
    value: String,
    label: String,
    valueColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    showGramSuffix: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MacroChipSpec.cornerRadius))    // 10dp
            .background(bgColor)
            .padding(vertical = MacroChipSpec.paddingVertical),      // 5dp top+bottom
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MacroChipSpec.valueToLabelGap)  // 1dp
        ) {
            // Value — DM Mono for numeric feel
            Text(
                text = if (showGramSuffix) "${value}g" else value,
                style = MacroChipValue,     // DM Mono Medium 13sp
                color = valueColor
            )
            // Label — small uppercase Outfit
            Text(
                text = label,
                style = MacroChipLabel,     // Outfit SemiBold 9sp ls:0.54sp uppercase
                color = TextGhost           // #B0B5C3
            )
        }
    }
}