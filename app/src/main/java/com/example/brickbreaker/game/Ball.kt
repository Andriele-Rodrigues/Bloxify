package com.example.brickbreaker.game

import androidx.compose.ui.geometry.Offset

/**
 * Representa a bola do jogo (Imutável para melhor integração com Compose).
 */
data class Ball(
    val position: Offset,
    val velocity: Offset,
    val radius: Float = 16f
) {
    /**
     * Retorna uma nova instância da bola com a posição atualizada.
     */
    fun move(deltaSeconds: Float): Ball {
        return copy(
            position = Offset(
                x = position.x + velocity.x * deltaSeconds,
                y = position.y + velocity.y * deltaSeconds
            )
        )
    }

    fun bounceHorizontal(): Ball {
        return copy(velocity = velocity.copy(x = -velocity.x))
    }

    fun bounceVertical(): Ball {
        return copy(velocity = velocity.copy(y = -velocity.y))
    }
}
