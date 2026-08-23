package com.example.brickbreaker.game

/**
 * Responsável pelos cálculos de colisão do jogo.
 *
 * As funções utilizam apenas coordenadas e dimensões para não depender
 * diretamente das classes Ball, Paddle e Brick.
 *
 * Quando essas classes estiverem disponíveis, o GameEngine poderá usar
 * estas funções passando suas respectivas posições e dimensões.
 */
object CollisionDetector {

    /**
     * Verifica colisão da bola com a parede esquerda.
     */
    fun hitLeftWall(
        ballX: Float,
        ballRadius: Float
    ): Boolean {
        return ballX - ballRadius <= 0f
    }

    /**
     * Verifica colisão da bola com a parede direita.
     */
    fun hitRightWall(
        ballX: Float,
        ballRadius: Float,
        screenWidth: Float
    ): Boolean {
        return ballX + ballRadius >= screenWidth
    }

    /**
     * Verifica colisão da bola com o teto.
     */
    fun hitTopWall(
        ballY: Float,
        ballRadius: Float
    ): Boolean {
        return ballY - ballRadius <= 0f
    }

    /**
     * Verifica se a bola saiu pela parte inferior da tela.
     */
    fun ballFell(
        ballY: Float,
        ballRadius: Float,
        screenHeight: Float
    ): Boolean {
        return ballY - ballRadius > screenHeight
    }

    /**
     * Verifica a colisão entre uma bola circular e uma área retangular.
     *
     * Essa função poderá ser usada tanto para o paddle quanto para
     * os tijolos.
     */
    fun circleIntersectsRectangle(
        ballX: Float,
        ballY: Float,
        ballRadius: Float,
        rectX: Float,
        rectY: Float,
        rectWidth: Float,
        rectHeight: Float
    ): Boolean {

        val closestX = ballX.coerceIn(
            rectX,
            rectX + rectWidth
        )

        val closestY = ballY.coerceIn(
            rectY,
            rectY + rectHeight
        )

        val distanceX = ballX - closestX
        val distanceY = ballY - closestY

        val distanceSquared =
            (distanceX * distanceX) + (distanceY * distanceY)

        return distanceSquared <= ballRadius * ballRadius
    }
}
