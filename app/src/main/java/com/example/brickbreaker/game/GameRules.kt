package com.example.brickbreaker.game

/**
 * Centraliza as regras executadas durante a partida.
 */
object GameRules {

    /**
     * Processa todas as colisões de um frame e retorna o novo estado da bola e do jogo.
     */
    fun processFrame(
        ball: Ball,
        paddle: Paddle,
        level: Level,
        gameState: GameState,
        screenWidth: Float,
        screenHeight: Float
    ): Pair<Ball, GameState> {

        var currentBall = ball

        // 1. Colisões Físicas
        currentBall = CollisionDetector.handleWallCollisions(currentBall, screenWidth)
        currentBall = CollisionDetector.handlePaddleCollision(currentBall, paddle)
        currentBall = CollisionDetector.handleBrickCollision(currentBall, level.bricks)

        // 2. Regras de Estado
        if (CollisionDetector.ballFell(currentBall.position.y, currentBall.radius, screenHeight)) {
            return currentBall to gameState.ballLost()
        }

        if (level.isComplete()) {
            return currentBall to gameState.completeLevel()
        }

        return currentBall to gameState
    }
}
