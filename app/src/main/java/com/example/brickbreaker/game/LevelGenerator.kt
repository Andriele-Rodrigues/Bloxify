package com.example.brickbreaker.game

import kotlin.random.Random

object LevelGenerator {

    fun generateLevel(
        levelNumber: Int,
        screenWidth: Float,
        screenHeight: Float,
        brickHeight: Float = 50f,
        columns: Int = 8
    ): Level {
        val rows = 5
        val padding = 8f
        val brickWidth = (screenWidth - (padding * (columns + 1))) / columns

        val bricks = mutableListOf<Brick>()
        var idCounter = 0

        for (row in 0 until rows) {
            // Pontuação baseada na linha (conforme documentação)
            val rowPoints = (5 - row) * 20 

            for (col in 0 until columns) {
                val x = padding + col * (brickWidth + padding)
                val y = padding + row * (brickHeight + padding) + 150f

                val shouldCreateBrick = when (levelNumber) {
                    1 -> true
                    2 -> (row + col) % 2 == 0
                    3 -> row % 2 == 0
                    4 -> col == 0 || col == columns - 1 || row == 0
                    else -> Random.nextBoolean()
                }

                if (shouldCreateBrick) {
                    bricks.add(
                        Brick(
                            id = idCounter++,
                            x = x,
                            y = y,
                            width = brickWidth,
                            height = brickHeight,
                            row = row,
                            col = col,
                            points = rowPoints
                        )
                    )
                }
            }
        }
        return Level(levelNumber, bricks)
    }
}
