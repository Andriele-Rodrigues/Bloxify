package com.example.brickbreaker.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brickbreaker.data.GamePreferences
import com.example.brickbreaker.game.GameEngine
import com.example.brickbreaker.game.GameRules
import com.example.brickbreaker.game.GameState
import com.example.brickbreaker.game.GameStatus
import com.example.brickbreaker.game.Level
import com.example.brickbreaker.game.LevelGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember { GamePreferences.getInstance(context) }
    val selectedColor by preferences.brickColorFlow.collectAsState()
    val selectedSize by preferences.brickSizeFlow.collectAsState()
    val highScore by preferences.highScoreFlow.collectAsState()

    var gameState by remember { mutableStateOf(GameState()) }
    var currentLevel by remember { mutableStateOf<Level?>(null) }
    var engine by remember { mutableStateOf<GameEngine?>(null) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // O Nível é gerado quando o número do nível muda OU quando há um reset total (resetKey)
        LaunchedEffect(widthPx, heightPx, gameState.currentLevel, gameState.resetKey) {
            if (widthPx > 0 && heightPx > 0) {
                if (engine == null) {
                    engine = GameEngine(screenWidth = widthPx, screenHeight = heightPx)
                } else {
                    engine?.reset()
                }
                currentLevel = LevelGenerator.generateLevel(
                    levelNumber = gameState.currentLevel,
                    screenWidth = widthPx,
                    screenHeight = heightPx,
                    brickHeight = selectedSize.height,
                    columns = selectedSize.columns
                )
            }
        }

        val currentEngine = engine
        val activeLevel = currentLevel

        if (currentEngine != null && activeLevel != null) {
            currentEngine.onUpdate = { ball, paddle ->
                val (updatedBall, newState) = GameRules.processFrame(
                    ball = ball,
                    paddle = paddle,
                    level = activeLevel,
                    gameState = gameState,
                    screenWidth = widthPx,
                    screenHeight = heightPx
                )

                // IMPORTANTE: Atualiza o gameState a cada frame para refletir os pontos no HUD
                gameState = newState

                if (newState.status != GameStatus.PLAYING) {
                    currentEngine.pause()
                    if (newState.lives <= 0 || newState.status == GameStatus.GAME_COMPLETED) {
                        preferences.saveGameScore(newState.score)
                    }
                }
                updatedBall to newState
            }

            LaunchedEffect(gameState.status) {
                if (gameState.status == GameStatus.PLAYING) {
                    currentEngine.resume()
                    var lastTime = withFrameNanos { it }
                    while (isActive && gameState.status == GameStatus.PLAYING) {
                        withFrameNanos { frameTime ->
                            val deltaSeconds = ((frameTime - lastTime) / 1_000_000_000f).coerceIn(0f, 0.05f)
                            lastTime = frameTime
                            currentEngine.update(deltaSeconds)
                        }
                    }
                }
            }

            LaunchedEffect(gameState.status) {
                if (gameState.status == GameStatus.LEVEL_COMPLETED) {
                    delay(1500)
                    gameState = gameState.nextLevel()
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentEngine) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            currentEngine.onDrag(change.position.x)
                        }
                    }
            ) {
                activeLevel.bricks.forEach { brick ->
                    if (!brick.isDestroyed) {
                        drawRoundRect(
                            color = Color(selectedColor.getColorForRow(brick.row)),
                            topLeft = Offset(brick.x, brick.y),
                            size = Size(brick.width, brick.height),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )
                    }
                }

                val p = currentEngine.paddle
                drawRoundRect(
                    color = Color(0xFF2196F3),
                    topLeft = Offset(p.left, p.y),
                    size = Size(p.width, p.height),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )

                val b = currentEngine.ball
                drawCircle(
                    color = Color(0xFFFFC107),
                    radius = b.radius,
                    center = b.position
                )
            }
        }

        HUD(gameState = gameState, onBack = onBack, highScore = highScore)
        
        if (gameState.status != GameStatus.PLAYING) {
            GameDialog(
                gameState = gameState,
                status = gameState.status,
                highScore = highScore,
                onRestart = {
                    gameState = gameState.restartLevel()
                    engine?.reset()
                },
                onNext = { gameState = gameState.nextLevel() },
                onMenu = onBack
            )
        }
    }
}

@Composable
fun HUD(gameState: GameState, onBack: () -> Unit, highScore: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🏠 NÍVEL ${gameState.currentLevel}/${gameState.totalLevels}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "PONTOS: ${gameState.score.toString().padStart(4, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "RECORDE: ${highScore.toString().padStart(4, '0')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { index ->
                    Text(
                        text = if (index < gameState.lives) "❤️" else "🖤",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 1.dp)
                    )
                }
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.height(38.dp)) { 
                Text("SAIR", fontSize = 12.sp) 
            }
        }
    }
}

@Composable
fun GameDialog(
    gameState: GameState, 
    status: GameStatus, 
    highScore: Int,
    onRestart: () -> Unit, 
    onNext: () -> Unit, 
    onMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                when (status) {
                    GameStatus.BALL_LOST -> {
                        val isGameOver = gameState.lives <= 0
                        Text(
                            text = if (isGameOver) "FIM DE JOGO" else "BOLA PERDIDA",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (isGameOver) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pontuação Final: ${gameState.score}")
                            Text("Recorde Anterior: $highScore")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        if (!isGameOver) {
                            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) { Text("CONTINUAR NÍVEL") }
                        } else {
                            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) { Text("REINICIAR TUDO") }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("PULAR NÍVEL") }
                    }
                    GameStatus.LEVEL_COMPLETED -> {
                        Text("VITÓRIA!", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aguarde o próximo nível...")
                    }
                    GameStatus.GAME_COMPLETED -> {
                        Text("PARABÉNS!", style = MaterialTheme.typography.headlineSmall)
                        Text("Você venceu o desafio!")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pontuação Final: ${gameState.score}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onMenu) { Text("VOLTAR AO MENU") }
                    }
                    else -> {}
                }
            }
        }
    }
}
