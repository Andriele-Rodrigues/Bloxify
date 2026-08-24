package com.example.brickbreaker.game

import kotlin.random.Random

object LevelGenerator {

    // Esta função gera os tijolos a partir de uma matriz matemática
    fun generateLevel(
        levelNumber: Int,
        screenWidth: Float,
        screenHeight: Float,
        brickHeight: Float = 50f,
        columns: Int = 8
    ): Level {
        val rows = 5       // 5 Linhas de tijolos
        val padding = 8f   // Espaço entre os tijolos

        // Calcula a largura perfeita para os tijolos ocuparem bem a tela
        val brickWidth = (screenWidth - (padding * (columns + 1))) / columns

        val bricks = mutableListOf<Brick>()
        var idCounter = 0

        // Aqui foi criado a "Matriz" andando por cada linha e coluna
        for (row in 0 until rows) {
            for (col in 0 until columns) {

                // Calcula onde esse tijolo específico vai ficar desenhado na tela
                val x = padding + col * (brickWidth + padding)
                val y = padding + row * (brickHeight + padding) + 150f // +150f para não colar no teto

                // Define as regras visuais
                val shouldCreateBrick = when (levelNumber) {
                    1 -> true                                         // Nível 1: Tudo preenchido
                    2 -> (row + col) % 2 == 0                         // Nível 2: Padrão "Tabuleiro de Xadrez"
                    3 -> row % 2 == 0                                 // Nível 3: Linhas alternadas sim/não
                    4 -> col == 0 || col == columns - 1 || row == 0   // Nível 4: Apenas as bordas da tela
                    else -> Random.nextBoolean()                      // Nível 5 em diante: Totalmente aleatório
                }

                // Se a regra disse "sim", nós adicionamos o tijolo na parede
                if (shouldCreateBrick) {
                    bricks.add(
                        Brick(
                            id = idCounter++,
                            x = x,
                            y = y,
                            width = brickWidth,
                            height = brickHeight,
                            row = row,
                            col = col
                        )
                    )
                }
            }
        }

        // Retorna a fase pronta com a lista de tijolos!
        return Level(levelNumber, bricks)
    }
}
