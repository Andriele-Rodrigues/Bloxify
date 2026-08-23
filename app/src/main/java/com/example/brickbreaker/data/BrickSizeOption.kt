package com.example.brickbreaker.data

/**
 * Available brick dimension options for Bloxify.
 *
 * @property title Human-readable localized title in Portuguese.
 * @property height Height of each individual brick in pixels (Float).
 * @property columns Number of columns per row in the brick grid.
 */
enum class BrickSizeOption(
    val title: String,
    val height: Float,
    val columns: Int
) {
    PEQUENO(
        title = "Pequeno",
        height = 35f,
        columns = 10
    ),
    MEDIO(
        title = "Médio",
        height = 50f,
        columns = 8
    ),
    GRANDE(
        title = "Grande",
        height = 65f,
        columns = 6
    )
}
