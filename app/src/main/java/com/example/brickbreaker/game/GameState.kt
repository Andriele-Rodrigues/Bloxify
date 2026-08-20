package com.example.brickbreaker.game

/**
 * Representa os possíveis estados da partida.
 */
enum class GameStatus {
    PLAYING,

    // A bola não atingiu o paddle.
    BALL_LOST,

    // Todos os tijolos do nível foram destruídos.
    LEVEL_COMPLETED,

    // Todos os níveis foram concluídos.
    GAME_COMPLETED
}

/**
 * Guarda as informações gerais do estado atual do jogo.
 */
data class GameState(
    val currentLevel: Int = 1,
    val totalLevels: Int = 5,
    val status: GameStatus = GameStatus.PLAYING
)
