package com.example.brickbreaker.game

/**
 * Representa os possíveis estados da partida.
 */
enum class GameStatus {
    PLAYING,
    BALL_LOST,
    LEVEL_COMPLETED,
    GAME_COMPLETED
}

/**
 * Guarda as informações gerais do estado atual do jogo.
 */
data class GameState(
    val currentLevel: Int = 1,
    val totalLevels: Int = 5,
    val score: Int = 0,
    val status: GameStatus = GameStatus.PLAYING
) {

    fun addPoints(points: Int): GameState {
        return copy(score = score + points)
    }

    fun ballLost(): GameState {
        return copy(status = GameStatus.BALL_LOST)
    }

    fun restartLevel(): GameState {
        return copy(status = GameStatus.PLAYING)
    }

    fun completeLevel(): GameState {
        return if (currentLevel >= totalLevels) {
            copy(status = GameStatus.GAME_COMPLETED)
        } else {
            copy(status = GameStatus.LEVEL_COMPLETED)
        }
    }

    fun nextLevel(): GameState {
        return if (currentLevel >= totalLevels) {
            copy(status = GameStatus.GAME_COMPLETED)
        } else {
            copy(
                currentLevel = currentLevel + 1,
                status = GameStatus.PLAYING
            )
        }
    }
}
