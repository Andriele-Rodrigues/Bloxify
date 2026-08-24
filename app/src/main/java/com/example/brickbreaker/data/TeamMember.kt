package com.example.brickbreaker.data

/**
 * Represents a team member contributing to the Bloxify Android Brick Breaker project.
 *
 * @property id Integer identifier corresponding to roster index.
 * @property name Full legal / project name of the member.
 * @property initials 2-letter uppercase initials used for circular avatar icon.
 * @property memberNumber Project role number designation (e.g. "Integrante 1", "Integrante 5").
 * @property role Short summary of project responsibilities.
 * @property detailedResponsibilities Comprehensive description of deliverables and modules owned.
 * @property avatarColor Hex/Long ARGB color value for avatar badge background styling.
 */
data class TeamMember(
    val id: Int,
    val name: String,
    val initials: String,
    val memberNumber: String,
    val role: String,
    val detailedResponsibilities: String = role,
    val avatarColor: Long = 0xFF6650A4L
) {
    companion object {
        /**
         * Roster of the 5 project members in standard presentation order.
         */
        val MEMBERS: List<TeamMember> = listOf(
            TeamMember(
                id = 1,
                name = "Andriele Rodrigues",
                initials = "AR",
                memberNumber = "Integrante 1",
                role = "Estrutura Inicial, Navegação Compose, Telas Base e Tema",
                detailedResponsibilities = "Arquitetura base Compose, Scaffold, AppNavigation e Material 3 Theme.",
                avatarColor = 0xFF9C27B0L // Purple
            ),
            TeamMember(
                id = 2,
                name = "Bruno Kunzler Borges",
                initials = "BK",
                memberNumber = "Integrante 2",
                role = "Motor do Jogo, Física da Bola e Controle do Paddle",
                detailedResponsibilities = "GameEngine com loop 60fps, física vetorial da bola e movimentação do paddle.",
                avatarColor = 0xFF2196F3L // Blue
            ),
            TeamMember(
                id = 3,
                name = "Felipe Vicentini",
                initials = "FV",
                memberNumber = "Integrante 5",
                role = "Configurações, Áudio SoundPool, Integrantes e Documentação",
                detailedResponsibilities = "SettingsScreen, SoundManager com SoundPool, MembersScreen e README com wireframes.",
                avatarColor = 0xFF00B0FFL // Light Blue / Cyan
            ),
            TeamMember(
                id = 4,
                name = "Jeferson Duarte",
                initials = "JD",
                memberNumber = "Integrante 4",
                role = "Detecção de Colisões, Estados da Partida e Regras do Jogo",
                detailedResponsibilities = "Colisões AABB/círculo-retângulo, GameState (jogando, vitória, derrota) e pontuação.",
                avatarColor = 0xFF00E676L // Emerald Green
            ),
            TeamMember(
                id = 5,
                name = "Matheus Feijó Barp",
                initials = "MB",
                memberNumber = "Integrante 3",
                role = "Lógica dos Tijolos, Matrizes dos 5 Níveis e Gerador Procedural",
                detailedResponsibilities = "LevelGenerator com 5 matrizes de blocos matemáticas e gerador procedural caótico.",
                avatarColor = 0xFFFF9100L // Amber / Orange
            )
        )
    }
}
