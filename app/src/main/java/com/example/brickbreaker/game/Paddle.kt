package com.example.brickbreaker.game

import androidx.compose.ui.geometry.Offset

/**
 * Representa a plataforma (paddle) que o jogador controla para rebater a bola.
 *
 * O paddle se move apenas na horizontal, acompanhando o toque/arraste do dedo,
 * e nunca pode sair das bordas da tela. A responsabilidade do motor aqui é:
 *  - mover o paddle pelo toque;
 *  - impedir que ele saia da tela.
 * O rebote da bola no paddle é tratado pelo Integrante 4 (colisões).
 *
 * A posição centerX representa o CENTRO do paddle no eixo horizontal.
 * O y é fixo (o paddle fica sempre na mesma altura, perto do rodapé).
 *
 * @property centerX posição horizontal do centro do paddle, em pixels.
 * @property y posição vertical do topo do paddle, em pixels.
 * @property width largura do paddle, em pixels.
 * @property height altura do paddle, em pixels.
 */
data class Paddle(
    var centerX: Float,
    val y: Float,
    val width: Float = 220f,
    val height: Float = 28f
) {

    /** Metade da largura, útil para os cálculos de borda. */
    private val halfWidth: Float
        get() = width / 2f

    /** Canto esquerdo do paddle (usado para desenhar e para colisões). */
    val left: Float
        get() = centerX - halfWidth

    /** Canto direito do paddle. */
    val right: Float
        get() = centerX + halfWidth

    /**
     * Move o paddle para uma nova posição horizontal, mantendo-o dentro da
     * tela. Se o dedo do jogador sair da área visível, o paddle "gruda" na
     * borda em vez de sumir.
     *
     * O coerceIn limita o valor ao intervalo permitido: nunca menor que
     * halfWidth (senão a metade esquerda sairia da tela) nem maior que
     * (screenWidth - halfWidth) (senão a metade direita sairia).
     *
     * @param targetX posição horizontal desejada (onde o dedo tocou/arrastou).
     * @param screenWidth largura total da tela, em pixels.
     */
    fun moveTo(targetX: Float, screenWidth: Float) {
        centerX = targetX.coerceIn(halfWidth, screenWidth - halfWidth)
    }
}