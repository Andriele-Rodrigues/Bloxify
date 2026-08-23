package com.example.brickbreaker.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brickbreaker.data.BrickColorOption
import com.example.brickbreaker.data.BrickSizeOption
import com.example.brickbreaker.data.GamePreferences

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    preferences: GamePreferences = GamePreferences.getInstance(LocalContext.current)
) {
    val selectedColor by preferences.brickColorFlow.collectAsState()
    val selectedSize by preferences.brickSizeFlow.collectAsState()
    val isSoundEnabled by preferences.soundEnabledFlow.collectAsState()

    var statusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Text(
            text = "CONFIGURAÇÕES",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Personalize cores, dimensões dos blocos e áudio",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Section 1: Color Palette
        SettingsSectionCard(
            title = "COR DOS TIJOLOS",
            subtitle = "Selecione o esquema de cores dos blocos"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val colorOptions = BrickColorOption.values()
                // 2 rows of 3 colors
                for (chunk in colorOptions.toList().chunked(3)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (option in chunk) {
                            ColorSwatchItem(
                                option = option,
                                isSelected = option == selectedColor,
                                modifier = Modifier.weight(1f),
                                onSelect = {
                                    preferences.setBrickColor(option)
                                    statusMessage = "Cor alterada para ${option.title}"
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Brick Size
        SettingsSectionCard(
            title = "TAMANHO DOS TIJOLOS",
            subtitle = "Altere a altura e a quantidade de colunas na grade"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (sizeOption in BrickSizeOption.values()) {
                    BrickSizeItem(
                        option = sizeOption,
                        isSelected = sizeOption == selectedSize,
                        modifier = Modifier.weight(1f),
                        onSelect = {
                            preferences.setBrickSize(sizeOption)
                            statusMessage = "Tamanho alterado para ${sizeOption.title}"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Live Preview
        SettingsSectionCard(
            title = "PRÉ-VISUALIZAÇÃO EM TEMPO REAL",
            subtitle = "Visualização instantânea do bloco no jogo"
        ) {
            LiveBrickPreview(
                colorOption = selectedColor,
                sizeOption = selectedSize
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 4: Sound Effects Toggle
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Efeitos Sonoros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sons de início de fase e colisão no paddle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = { enabled ->
                        preferences.setSoundEnabled(enabled)
                        statusMessage = if (enabled) "Sons ativados" else "Sons desativados"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status auto-save notification
        AnimatedVisibility(
            visible = statusMessage != null,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(400))
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CanvasCheckIcon(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusMessage ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 5: Bottom Action Buttons
        OutlinedButton(
            onClick = {
                preferences.resetToDefaults()
                statusMessage = "Configurações restauradas para o padrão"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            CanvasResetIcon(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RESTAURAR PADRÕES",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "VOLTAR AO MENU",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
private fun ColorSwatchItem(
    option: BrickColorOption,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 1.dp,
        animationSpec = tween(200),
        label = "borderWidth"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(200),
        label = "borderColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "containerColor"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                BorderStroke(animatedBorderWidth, animatedBorderColor),
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() },
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Color Circle Swatch
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .then(
                        if (option == BrickColorOption.MULTICOLOR) {
                            Modifier.background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFFF1744),
                                        Color(0xFFFF9100),
                                        Color(0xFFFFD600),
                                        Color(0xFF00E676),
                                        Color(0xFF00B0FF),
                                        Color(0xFF6650A4),
                                        Color(0xFFFF1744)
                                    )
                                )
                            )
                        } else {
                            Modifier.background(Color(option.primaryColor))
                        }
                    )
                    .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    CanvasCheckIcon(
                        color = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = option.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BrickSizeItem(
    option: BrickSizeOption,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 1.dp,
        animationSpec = tween(200),
        label = "borderWidth"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(200),
        label = "borderColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "containerColor"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                BorderStroke(animatedBorderWidth, animatedBorderColor),
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() },
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Miniature Brick Shape representation
            val previewHeight = when (option) {
                BrickSizeOption.PEQUENO -> 10.dp
                BrickSizeOption.MEDIO -> 16.dp
                BrickSizeOption.GRANDE -> 22.dp
            }

            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(previewHeight)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = option.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${option.height.toInt()}px • ${option.columns} col",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LiveBrickPreview(
    colorOption: BrickColorOption,
    sizeOption: BrickSizeOption
) {
    val animatedHeight by animateDpAsState(
        targetValue = when (sizeOption) {
            BrickSizeOption.PEQUENO -> 28.dp
            BrickSizeOption.MEDIO -> 42.dp
            BrickSizeOption.GRANDE -> 56.dp
        },
        animationSpec = tween(300),
        label = "previewHeight"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brick Rendering Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(animatedHeight)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (colorOption == BrickColorOption.MULTICOLOR) {
                            Modifier.background(
                                Brush.horizontalGradient(
                                    colorOption.colorList.map { Color(it) }
                                )
                            )
                        } else {
                            Modifier.background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(colorOption.primaryColor).copy(alpha = 0.9f),
                                        Color(colorOption.primaryColor)
                                    )
                                )
                            )
                        }
                    )
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        RoundedCornerShape(8.dp)
                    )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${colorOption.title} • Altura: ${sizeOption.height.toInt()}px • ${sizeOption.columns} colunas",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CanvasCheckIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()
        val p1 = Offset(size.width * 0.22f, size.height * 0.52f)
        val p2 = Offset(size.width * 0.42f, size.height * 0.72f)
        val p3 = Offset(size.width * 0.78f, size.height * 0.30f)
        drawLine(color = color, start = p1, end = p2, strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color = color, start = p2, end = p3, strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

@Composable
private fun CanvasResetIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.minDimension / 2f) - strokeWidth
        drawArc(
            color = color,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Arrow head at top
        val arrowX = center.x + radius * 0.7f
        val arrowY = center.y - radius * 0.7f
        drawLine(
            color = color,
            start = Offset(arrowX - 4.dp.toPx(), arrowY - 2.dp.toPx()),
            end = Offset(arrowX, arrowY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(arrowX - 2.dp.toPx(), arrowY + 4.dp.toPx()),
            end = Offset(arrowX, arrowY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
