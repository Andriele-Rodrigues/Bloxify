package com.example.brickbreaker.game

/**
 * Centraliza as regras executadas durante a partida.
 *
 * Essa classe conecta o sistema de colisões ao estado geral do jogo.
 */
object GameRules {

    /**
     * Processa todas as colisões de um frame.
     *
     * Retorna o estado atualizado da partida caso a bola seja perdida
     * ou todos os tijolos do nível sejam destruídos.
     */
    fun processCollisions(
        ball: Ball,
        paddle: Paddle,
        level: Level,
        gameState: GameState,
        screenWidth: Float,
        screenHeight: Float
    ): GameState {

        // Colisão com as paredes e o teto.
        CollisionDetector.handleWallCollisions(
            ball = ball,
            screenWidth = screenWidth
        )

        // Colisão com o paddle.
        CollisionDetector.handlePaddleCollision(
            ball = ball,
            paddle = paddle
        )

        // Colisão com os tijolos.
        CollisionDetector.handleBrickCollision(
            ball = ball,
            bricks = level.bricks
        )

        // A bola passou pelo paddle e saiu da tela.
        if (
            CollisionDetector.ballFell(
                ball = ball,
                screenHeight = screenHeight
            )
        ) {
            return gameState.ballLost()
        }

        // Todos os tijolos foram destruídos.
        if (level.isComplete()) {
            return gameState.completeLevel()
        }

        // Nenhuma regra alterou o estado da partida.
        return gameState
    }
}
