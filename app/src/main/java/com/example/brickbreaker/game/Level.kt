package com.example.brickbreaker.game

// Essa estrutura representa uma Fase inteira, contendo o número dela e uma lista de tijolos
data class Level(
    val levelNumber: Int,
    val bricks: List<Brick>
) {
    // detecta se todos os tijolos foram destruídos
    fun isComplete(): Boolean {
        // Retorna "verdadeiro" apenas se a condição "isDestroyed" de TODOS os tijolos for verdadeira
        return bricks.all { it.isDestroyed }
    }
}