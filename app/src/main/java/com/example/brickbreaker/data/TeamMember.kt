package com.example.brickbreaker.data

/**
 * Representa um integrante do grupo Bloxify.
 */
data class TeamMember(
    val id: Int,
    val name: String,
    val initials: String,
    val memberNumber: String,
    val role: String,
    val detailedResponsibilities: String,
    val avatarColor: Long
) {
    companion object {
        val MEMBERS: List<TeamMember> = listOf(
            TeamMember(
                id = 1,
                name = "Andriele Rodrigues",
                initials = "AR",
                memberNumber = "Integrante 1",
                role = "Estrutura Inicial e Navegação",
                detailedResponsibilities = "Estrutura inicial, MainActivity, rotas e navegação.",
                avatarColor = 0xFF9C27B0L
            ),
            TeamMember(
                id = 2,
                name = "Bruno Kunzler Borges",
                initials = "BK",
                memberNumber = "Integrante 2",
                role = "Motor do Jogo e Física",
                detailedResponsibilities = "Motor do jogo (Ball, Paddle, GameEngine, loop de física).",
                avatarColor = 0xFF2196F3L
            ),
            TeamMember(
                id = 3,
                name = "Felipe Vicentini",
                initials = "FV",
                memberNumber = "Integrante 3",
                role = "Configurações e Áudio",
                detailedResponsibilities = "Configurações, persistência, sons (SoundPool), integrantes e docs.",
                avatarColor = 0xFF00B0FFL
            ),
            TeamMember(
                id = 4,
                name = "Jeferson Duarte",
                initials = "JD",
                memberNumber = "Integrante 4",
                role = "Tijolos e Níveis",
                detailedResponsibilities = "Matrizes de tijolos, 5 níveis e geração procedural (LevelGenerator).",
                avatarColor = 0xFF00E676L
            ),
            TeamMember(
                id = 5,
                name = "Matheus Feijó Barp",
                initials = "MB",
                memberNumber = "Integrante 5",
                role = "Colisões e Regras",
                detailedResponsibilities = "Sistema de colisões (CollisionDetector), pontuação e estados do jogo.",
                avatarColor = 0xFFFF9100L
            )
        )
    }
}
