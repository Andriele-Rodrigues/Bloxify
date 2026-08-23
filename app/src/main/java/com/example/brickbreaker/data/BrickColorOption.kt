package com.example.brickbreaker.data

/**
 * Available color presets for the game bricks in Bloxify.
 *
 * @property title Human-readable localized title in Portuguese.
 * @property primaryColor Primary ARGB color value (as Long).
 * @property colorList List of color values for multi-colored rows or gradient variations.
 */
enum class BrickColorOption(
    val title: String,
    val primaryColor: Long,
    val colorList: List<Long>
) {
    ROXO_BLOXIFY(
        title = "Roxo Bloxify",
        primaryColor = 0xFF6650A4L,
        colorList = listOf(0xFF6650A4L, 0xFF7C67B8L, 0xFF927ECCL, 0xFFA995E0L, 0xFFBFACF4L)
    ),
    AZUL_NEON(
        title = "Azul Neon",
        primaryColor = 0xFF00B0FFL,
        colorList = listOf(0xFF00B0FFL, 0xFF33C0FFL, 0xFF66D0FFL, 0xFF99E0FFL, 0xFFCCF0FFL)
    ),
    VERDE_ESMERALDA(
        title = "Verde Esmeralda",
        primaryColor = 0xFF00E676L,
        colorList = listOf(0xFF00E676L, 0xFF33EB91L, 0xFF66F0ACL, 0xFF99F5C7L, 0xFFCCFAE3L)
    ),
    VERMELHO_RUBI(
        title = "Vermelho Rubi",
        primaryColor = 0xFFFF1744L,
        colorList = listOf(0xFFFF1744L, 0xFFFF4569L, 0xFFFF738FL, 0xFFFFA2B4L, 0xFFFFD1D9L)
    ),
    AMBAR_SOLAR(
        title = "Âmbar Solar",
        primaryColor = 0xFFFF9100L,
        colorList = listOf(0xFFFF9100L, 0xFFFFA733L, 0xFFFFBD66L, 0xFFFFD399L, 0xFFFFE9CCL)
    ),
    MULTICOLOR(
        title = "Multicolor",
        primaryColor = 0xFFE040FBL,
        colorList = listOf(
            0xFFFF1744L, // Linha 0: Vermelho Rubi
            0xFFFF9100L, // Linha 1: Âmbar Solar
            0xFFFFD600L, // Linha 2: Amarelo Sol
            0xFF00E676L, // Linha 3: Verde Esmeralda
            0xFF00B0FFL  // Linha 4: Azul Neon
        )
    );

    /**
     * Resolves the color for a specific row index.
     * Single-color presets return [primaryColor], while [MULTICOLOR] cycles through [colorList].
     */
    fun getColorForRow(rowIndex: Int): Long {
        return if (this == MULTICOLOR && colorList.isNotEmpty()) {
            val safeIndex = (rowIndex % colorList.size + colorList.size) % colorList.size
            colorList[safeIndex]
        } else {
            primaryColor
        }
    }
}
