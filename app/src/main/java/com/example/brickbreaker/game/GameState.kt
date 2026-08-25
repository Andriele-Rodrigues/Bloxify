package com.example.brickbreaker.game

enum class GameStatus {
    PLAYING,
    BALL_LOST,
    LEVEL_COMPLETED,
    GAME_COMPLETED
}

data class GameState(
    val currentLevel: Int = 1,
    val totalLevels: Int = 5,
    val score: Int = 0,
    val lives: Int = 3,
    val status: GameStatus = GameStatus.PLAYING,
    val resetKey: Int = 0 // Contador para forçar a regeneração do nível ao reiniciar tudo
) {
    fun addPoints(points: Int): GameState {
        return copy(score = score + points)
    }

    fun ballLost(): GameState {
        val newLives = lives - 1
        return if (newLives <= 0) {
            copy(lives = 0, status = GameStatus.BALL_LOST)
        } else {
            copy(lives = newLives, status = GameStatus.BALL_LOST)
        }
    }

    fun restartLevel(): GameState {
        return if (lives <= 0) {
            // Reinicia tudo: Nível 1, Score 0, 3 Vidas e incrementa resetKey
            GameState(currentLevel = 1, score = 0, lives = 3, status = GameStatus.PLAYING, resetKey = resetKey + 1)
        } else {
            // Apenas continua o nível atual (vidas > 0)
            copy(status = GameStatus.PLAYING)
        }
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
