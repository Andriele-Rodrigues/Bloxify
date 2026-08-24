package com.example.brickbreaker.game

/**
 * Responsável pelos cálculos de colisão do jogo.
 */
object CollisionDetector {

    /**
     * Trata colisão com paredes e teto, retornando uma nova bola se houver impacto.
     */
    fun handleWallCollisions(
        ball: Ball,
        screenWidth: Float
    ): Ball {
        var newBall = ball
        
        // Parede esquerda
        if (newBall.position.x - newBall.radius <= 0f) {
            newBall = newBall.copy(position = newBall.position.copy(x = newBall.radius))
            newBall = newBall.bounceHorizontal()
        }
        // Parede direita
        else if (newBall.position.x + newBall.radius >= screenWidth) {
            newBall = newBall.copy(position = newBall.position.copy(x = screenWidth - newBall.radius))
            newBall = newBall.bounceHorizontal()
        }
        
        // Teto
        if (newBall.position.y - newBall.radius <= 0f) {
            newBall = newBall.copy(position = newBall.position.copy(y = newBall.radius))
            newBall = newBall.bounceVertical()
        }
        
        return newBall
    }

    /**
     * Trata colisão com o paddle.
     */
    fun handlePaddleCollision(
        ball: Ball,
        paddle: Paddle
    ): Ball {
        if (ball.velocity.y > 0) { // Só colide se estiver descendo
            if (circleIntersectsRectangle(
                    ballX = ball.position.x,
                    ballY = ball.position.y,
                    ballRadius = ball.radius,
                    rectX = paddle.left,
                    rectY = paddle.y,
                    rectWidth = paddle.width,
                    rectHeight = paddle.height
                )
            ) {
                return ball.copy(position = ball.position.copy(y = paddle.y - ball.radius))
                    .bounceVertical()
            }
        }
        return ball
    }

    /**
     * Trata colisão com os tijolos.
     */
    fun handleBrickCollision(
        ball: Ball,
        bricks: List<Brick>
    ): Ball {
        for (brick in bricks) {
            if (!brick.isDestroyed) {
                if (circleIntersectsRectangle(
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
                    return ball.bounceVertical()
                }
            }
        }
        return ball
    }

    fun ballFell(ballY: Float, ballRadius: Float, screenHeight: Float): Boolean {
        return ballY - ballRadius > screenHeight
    }

    fun circleIntersectsRectangle(
        ballX: Float,
        ballY: Float,
        ballRadius: Float,
        rectX: Float,
        rectY: Float,
        rectWidth: Float,
        rectHeight: Float
    ): Boolean {
        val closestX = ballX.coerceIn(rectX, rectX + rectWidth)
        val closestY = ballY.coerceIn(rectY, rectY + rectHeight)
        val distanceX = ballX - closestX
        val distanceY = ballY - closestY
        val distanceSquared = (distanceX * distanceX) + (distanceY * distanceY)
        return distanceSquared <= ballRadius * ballRadius
    }
}
