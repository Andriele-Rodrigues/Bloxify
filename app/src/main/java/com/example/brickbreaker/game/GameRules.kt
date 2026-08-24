package com.example.brickbreaker.game

/**
 * Centraliza as regras executadas durante a partida.
 */
object GameRules {

    fun processFrame(
        ball: Ball,
        paddle: Paddle,
        level: Level,
        gameState: GameState,
        screenWidth: Float,
        screenHeight: Float
    ): Pair<Ball, GameState> {

        var currentBall = ball
        var currentGameState = gameState

        // 1. Paredes
        currentBall = CollisionDetector.handleWallCollisions(currentBall, screenWidth)
        
        // 2. Paddle
        currentBall = CollisionDetector.handlePaddleCollision(currentBall, paddle)
        
        // 3. Tijolos (com pontuação)
        val (ballAfterBricks, points) = CollisionDetector.handleBrickCollision(currentBall, level.bricks)
        currentBall = ballAfterBricks
        if (points > 0) {
            currentGameState = currentGameState.addPoints(points)
        }

        // 4. Regras de Estado
        if (CollisionDetector.ballFell(currentBall.position.y, currentBall.radius, screenHeight)) {
            return currentBall to currentGameState.ballLost()
        }

        if (level.isComplete()) {
            return currentBall to currentGameState.completeLevel()
        }

        return currentBall to currentGameState
    }
}
