package com.example.brickbreaker.game

/**
 * Responsável pelas colisões da bola com paredes, paddle e tijolos.
 */
object CollisionDetector {

    /**
     * Trata colisões com as paredes esquerda, direita e superior.
     */
    fun handleWallCollisions(
        ball: Ball,
        screenWidth: Float
    ) {
        // Parede esquerda
        if (ball.position.x - ball.radius <= 0f && ball.velocity.x < 0f) {
            ball.position = ball.position.copy(
                x = ball.radius
            )
            ball.bounceHorizontal()
        }

        // Parede direita
        if (
            ball.position.x + ball.radius >= screenWidth &&
            ball.velocity.x > 0f
        ) {
            ball.position = ball.position.copy(
                x = screenWidth - ball.radius
            )
            ball.bounceHorizontal()
        }

        // Teto
        if (ball.position.y - ball.radius <= 0f && ball.velocity.y < 0f) {
            ball.position = ball.position.copy(
                y = ball.radius
            )
            ball.bounceVertical()
        }
    }

    /**
     * Verifica se a bola saiu pela parte inferior da tela.
     */
    fun ballFell(
        ball: Ball,
        screenHeight: Float
    ): Boolean {
        return ball.position.y - ball.radius > screenHeight
    }

    /**
     * Trata a colisão da bola com o paddle.
     *
     * A colisão só é processada quando a bola está descendo.
     */
    fun handlePaddleCollision(
        ball: Ball,
        paddle: Paddle
    ): Boolean {

        if (ball.velocity.y <= 0f) {
            return false
        }

        val collided = circleIntersectsRectangle(
            ball = ball,
            rectX = paddle.left,
            rectY = paddle.y,
            rectWidth = paddle.width,
            rectHeight = paddle.height
        )

        if (!collided) {
            return false
        }

        // Retira a bola de dentro do paddle antes do rebote.
        ball.position = ball.position.copy(
            y = paddle.y - ball.radius
        )

        ball.bounceVertical()

        return true
    }

    /**
     * Trata a colisão da bola com os tijolos.
     *
     * Apenas um tijolo pode ser destruído por atualização.
     * Isso ajuda a impedir que a bola atravesse vários tijolos
     * de uma só vez.
     */
    fun handleBrickCollision(
        ball: Ball,
        bricks: List<Brick>
    ): Brick? {

        for (brick in bricks) {

            // Tijolos já destruídos não participam das colisões.
            if (brick.isDestroyed) {
                continue
            }

            val collided = circleIntersectsRectangle(
                ball = ball,
                rectX = brick.x,
                rectY = brick.y,
                rectWidth = brick.width,
                rectHeight = brick.height
            )

            if (collided) {
                brick.isDestroyed = true

                resolveBrickBounce(
                    ball = ball,
                    brick = brick
                )

                // Encerra a procura depois do primeiro tijolo atingido.
                return brick
            }
        }

        return null
    }

    /**
     * Determina o lado do tijolo atingido e rebate a bola
     * na direção adequada.
     */
    private fun resolveBrickBounce(
        ball: Ball,
        brick: Brick
    ) {
        val ballLeft = ball.position.x - ball.radius
        val ballRight = ball.position.x + ball.radius
        val ballTop = ball.position.y - ball.radius
        val ballBottom = ball.position.y + ball.radius

        val brickLeft = brick.x
        val brickRight = brick.x + brick.width
        val brickTop = brick.y
        val brickBottom = brick.y + brick.height

        val penetrationLeft = ballRight - brickLeft
        val penetrationRight = brickRight - ballLeft
        val penetrationTop = ballBottom - brickTop
        val penetrationBottom = brickBottom - ballTop

        val minHorizontal = minOf(
            penetrationLeft,
            penetrationRight
        )

        val minVertical = minOf(
            penetrationTop,
            penetrationBottom
        )

        if (minHorizontal < minVertical) {

            // Colisão pela lateral esquerda ou direita.
            if (penetrationLeft < penetrationRight) {
                ball.position = ball.position.copy(
                    x = brickLeft - ball.radius
                )
            } else {
                ball.position = ball.position.copy(
                    x = brickRight + ball.radius
                )
            }

            ball.bounceHorizontal()

        } else {

            // Colisão pela parte superior ou inferior.
            if (penetrationTop < penetrationBottom) {
                ball.position = ball.position.copy(
                    y = brickTop - ball.radius
                )
            } else {
                ball.position = ball.position.copy(
                    y = brickBottom + ball.radius
                )
            }

            ball.bounceVertical()
        }
    }

    /**
     * Verifica colisão entre uma bola circular e um retângulo.
     *
     * Essa função é utilizada tanto para o paddle quanto
     * para os tijolos.
     */
    private fun circleIntersectsRectangle(
        ball: Ball,
        rectX: Float,
        rectY: Float,
        rectWidth: Float,
        rectHeight: Float
    ): Boolean {

        val closestX = ball.position.x.coerceIn(
            rectX,
            rectX + rectWidth
        )

        val closestY = ball.position.y.coerceIn(
            rectY,
            rectY + rectHeight
        )

        val distanceX = ball.position.x - closestX
        val distanceY = ball.position.y - closestY

        val distanceSquared =
            (distanceX * distanceX) +
            (distanceY * distanceY)

        return distanceSquared <=
            (ball.radius * ball.radius)
    }
}
