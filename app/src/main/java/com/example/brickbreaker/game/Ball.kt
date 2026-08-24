package com.example.brickbreaker.game

import androidx.compose.ui.geometry.Offset

/**
 * Representa a bola do jogo, com movimento e aceleração vertical (gravidade).
 *
 * A bola guarda posição, velocidade e um valor de gravidade. A gravidade é
 * uma aceleração constante que aumenta a velocidade vertical a cada quadro,
 * dando à bola um comportamento de "peso", como se estivesse caindo.
 *
 * @property position centro da bola (x, y).
 * @property velocity vetor velocidade (vx, vy) em pixels por segundo.
 * @property radius raio da bola em pixels.
 * @property gravity aceleração vertical em pixels por segundo ao quadrado.
 */
data class Ball(
    var position: Offset,
    var velocity: Offset,
    val radius: Float = 16f,

    // ===================================================================
    // AJUSTE A GRAVIDADE AQUI. Rode o jogo, veja o comportamento e mude
    // este número até ficar do seu gosto. Guia aproximado:
    //   0f    -> sem gravidade (Breakout clássico, quica pra sempre)
    //   100f  -> bem leve, só um toque de peso
    //   200f  -> leve, jogável (bom ponto de partida)
    //   400f  -> moderada, dá pra sentir a queda
    //   800f+ -> forte/realista, a bola despenca (difícil)
    // ===================================================================
    val gravity: Float = 200f
) {

    /**
     * Atualiza a bola a cada quadro. Acontece em duas etapas:
     *
     *  1. A gravidade acelera a velocidade vertical:
     *         velocidade_y = velocidade_y + gravidade * tempo
     *     (a velocidade horizontal não é afetada pela gravidade)
     *
     *  2. A nova velocidade move a posição:
     *         posição = posição + velocidade * tempo
     *
     * Multiplicar pelo tempo decorrido (deltaSeconds) mantém o movimento
     * suave e igual em qualquer aparelho, independente da taxa de quadros.
     *
     * @param deltaSeconds tempo decorrido desde o último quadro, em segundos.
     */
    fun update(deltaSeconds: Float) {
        // 1) A gravidade aumenta a velocidade de queda.
        velocity = velocity.copy(
            y = velocity.y + gravity * deltaSeconds
        )

        // 2) A velocidade (já acelerada) move a bola.
        position = Offset(
            x = position.x + velocity.x * deltaSeconds,
            y = position.y + velocity.y * deltaSeconds
        )
    }

    /** Inverte a componente horizontal da velocidade (rebate na lateral). */
    fun bounceHorizontal() {
        velocity = velocity.copy(x = -velocity.x)
    }

    /** Inverte a componente vertical da velocidade (rebate no topo/paddle). */
    fun bounceVertical() {
        velocity = velocity.copy(y = -velocity.y)
    }
}