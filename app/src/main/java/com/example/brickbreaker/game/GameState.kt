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
 * Guarda as informações gerais do estado atual do jogo
 * e controla as principais transições entre os estados.
 */
data class GameState(
    val currentLevel: Int = 1,
    val totalLevels: Int = 5,
    val status: GameStatus = GameStatus.PLAYING
) {

    /**
     * Chamado quando a bola passa pelo paddle.
     */
    fun ballLost(): GameState {
        return copy(status = GameStatus.BALL_LOST)
    }

    /**
     * Reinicia o nível atual.
     */
    fun restartLevel(): GameState {
        return copy(status = GameStatus.PLAYING)
    }

    /**
     * Marca o nível atual como concluído.
     * Se for o último nível, finaliza o jogo.
     */
    fun completeLevel(): GameState {
        return if (currentLevel >= totalLevels) {
            copy(status = GameStatus.GAME_COMPLETED)
        } else {
            copy(status = GameStatus.LEVEL_COMPLETED)
        }
    }

    /**
     * Avança para o próximo nível.
     * Se já estiver no último, finaliza o jogo.
     */
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
