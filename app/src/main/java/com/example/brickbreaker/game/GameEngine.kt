package com.example.brickbreaker.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class GameEngine(
    private val screenWidth: Float,
    private val screenHeight: Float
) {
    var ball by mutableStateOf(criarBolaInicial())
    var paddle by mutableStateOf(criarPaddleInicial())
    var running by mutableStateOf(true)

    // Callback que será chamado pela GameScreen a cada frame
    var onUpdate: ((Ball, Paddle) -> Pair<Ball, GameState>)? = null

    private fun criarBolaInicial(): Ball {
        return Ball(
            position = Offset(screenWidth / 2f, screenHeight / 2f),
            velocity = Offset(400f, -600f) // Aumentei a velocidade para ser mais perceptível
        )
    }

    private fun criarPaddleInicial(): Paddle {
        return Paddle(
            centerX = screenWidth / 2f,
            y = screenHeight - 200f
        )
    }

    fun update(deltaSeconds: Float) {
        if (!running) return

        // 1. Apenas move a bola baseado no tempo
        val movedBall = ball.move(deltaSeconds)
        
        // 2. Processa física e regras (via callback para manter GameState sincronizado)
        val (finalBall, newState) = onUpdate?.invoke(movedBall, paddle) ?: (movedBall to null)
        
        // 3. Atualiza os estados observáveis
        ball = finalBall
        // O paddle só muda via onDrag, mas forçamos re-referência se necessário
    }

    fun onDrag(x: Float) {
        // Cria um novo paddle para disparar recomposição
        val newPaddle = paddle.copy()
        newPaddle.moveTo(x, screenWidth)
        paddle = newPaddle
    }

    fun pause() { running = false }
    fun resume() { running = true }
    fun reset() {
        ball = criarBolaInicial()
        paddle = criarPaddleInicial()
        running = true
    }
}
