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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.brickbreaker.audio.SoundManager
import com.example.brickbreaker.data.BrickColorOption
import com.example.brickbreaker.data.BrickSizeOption
import com.example.brickbreaker.data.GamePreferences
import com.example.brickbreaker.game.CollisionDetector
import com.example.brickbreaker.game.GameEngine
import com.example.brickbreaker.game.GameState
import com.example.brickbreaker.game.GameStatus
import com.example.brickbreaker.game.Level
import com.example.brickbreaker.game.LevelGenerator
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember { GamePreferences.getInstance(context) }

    val selectedColor by preferences.brickColorFlow.collectAsState()
    val selectedSize by preferences.brickSizeFlow.collectAsState()
    val isSoundEnabled by preferences.soundEnabledFlow.collectAsState()

    // Initialize and remember SoundManager tied to Compose lifecycle
    val soundManager = remember { SoundManager(context) }
    DisposableEffect(soundManager, isSoundEnabled) {
        soundManager.isSoundEnabled = isSoundEnabled
        onDispose {
            soundManager.release()
        }
    }

    var gameState by remember { mutableStateOf(GameState()) }
    var currentLevel by remember { mutableStateOf<Level?>(null) }
    var engine by remember { mutableStateOf<GameEngine?>(null) }

    // Play stage start chime on level initiation, level changes, and restarts
    LaunchedEffect(gameState.currentLevel, gameState.status) {
        if (gameState.status == GameStatus.PLAYING) {
            soundManager.playStageStart()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Initialize engine and level when layout dimensions or size preferences are available
        LaunchedEffect(widthPx, heightPx, gameState.currentLevel, selectedSize) {
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
            // Collision handler configuration with audio feedback
            currentEngine.collisionHandler = { ball, paddle ->
                // 1. Paddle collision check
                if (ball.velocity.y > 0 && CollisionDetector.circleIntersectsRectangle(
                        ballX = ball.position.x,
                        ballY = ball.position.y,
                        ballRadius = ball.radius,
                        rectX = paddle.left,
                        rectY = paddle.y,
                        rectWidth = paddle.width,
                        rectHeight = paddle.height
                    )
                ) {
                    ball.bounceVertical()
                    ball.position = ball.position.copy(y = paddle.y - ball.radius - 1f)
                    soundManager.playPaddleHit()
                }

                // 2. Wall collisions
                if (CollisionDetector.hitLeftWall(ball.position.x, ball.radius)) {
                    ball.position = ball.position.copy(x = ball.radius + 1f)
                    ball.bounceHorizontal()
                } else if (CollisionDetector.hitRightWall(ball.position.x, ball.radius, widthPx)) {
                    ball.position = ball.position.copy(x = widthPx - ball.radius - 1f)
                    ball.bounceHorizontal()
                }

                if (CollisionDetector.hitTopWall(ball.position.y, ball.radius)) {
                    ball.position = ball.position.copy(y = ball.radius + 1f)
                    ball.bounceVertical()
                }

                // 3. Fall (Ball lost)
                if (CollisionDetector.ballFell(ball.position.y, ball.radius, heightPx)) {
                    gameState = gameState.ballLost()
                    currentEngine.pause()
                }

                // 4. Brick collisions
                var bounced = false
                for (brick in activeLevel.bricks) {
                    if (!brick.isDestroyed && CollisionDetector.circleIntersectsRectangle(
                            ballX = ball.position.x,
                            ballY = ball.position.y,
                            ballRadius = ball.radius,
                            rectX = brick.x,
                            rectY = brick.y,
                            rectWidth = brick.width,
                            rectHeight = brick.height
                        )
                    ) {
                        brick.isDestroyed = true
                        if (!bounced) {
                            ball.bounceVertical()
                            bounced = true
                        }
                    }
                }

                if (activeLevel.isComplete()) {
                    gameState = gameState.completeLevel()
                    currentEngine.pause()
                }
            }

            // Game loop driven by Compose frame clock
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

            // Interactive game canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            currentEngine.onDrag(change.position.x)
                        }
                    }
            ) {
                // Draw bricks
                for (brick in activeLevel.bricks) {
                    if (!brick.isDestroyed) {
                        val brickColor = Color(selectedColor.getColorForRow(brick.row))
                        drawRoundRect(
                            color = brickColor,
                            topLeft = Offset(brick.x, brick.y),
                            size = Size(brick.width, brick.height),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }

                // Draw paddle
                val p = currentEngine.paddle
                drawRoundRect(
                    color = Color(0xFF625B71),
                    topLeft = Offset(p.left, p.y),
                    size = Size(p.width, p.height),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Draw ball
                val b = currentEngine.ball
                drawCircle(
                    color = Color(0xFF7D5260),
                    radius = b.radius,
                    center = b.position
                )
            }
        }

        // Top HUD Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nível ${gameState.currentLevel} / ${gameState.totalLevels}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedButton(onClick = onBack) {
                Text("SAIR")
            }
        }

        // Overlay dialogs for GameStatus
        if (gameState.status != GameStatus.PLAYING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (gameState.status) {
                            GameStatus.BALL_LOST -> {
                                Text(
                                    text = "Bola Perdida!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        gameState = gameState.restartLevel()
                                        engine?.reset()
                                    }
                                ) {
                                    Text("TENTAR NOVAMENTE")
                                }
                            }
                            GameStatus.LEVEL_COMPLETED -> {
                                Text(
                                    text = "Nível Concluído!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        gameState = gameState.nextLevel()
                                    }
                                ) {
                                    Text("PRÓXIMO NÍVEL")
                                }
                            }
                            GameStatus.GAME_COMPLETED -> {
                                Text(
                                    text = "Parabéns! Jogo Concluído!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = onBack) {
                                    Text("VOLTAR AO MENU")
                                }
                            }
                            GameStatus.PLAYING -> {}
                        }
                    }
                }
            }
        }
    }
}
