package com.example.brickbreaker.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

/**
 * Motor principal do jogo.
 *
 * Esta classe é o CORAÇÃO da parte do Integrante 2. Ela guarda o estado do
 * jogo (bola e paddle) e roda o "game loop": a cada quadro, atualiza as
 * posições com base no tempo decorrido. É aqui que o movimento contínuo
 * acontece.
 *
 * A engine expõe a bola e o paddle como estado observável do Compose
 * (mutableStateOf), para que a tela (GameScreen, do grupo) se redesenhe
 * automaticamente sempre que algo se mover.
 *
 * A base para colisões já está preparada: existe um "gancho" (collisionHandler)
 * que o Integrante 4 pode preencher com a lógica de colisão, sem precisar
 * alterar o loop. Assim cada um mexe só na sua parte.
 *
 * @param screenWidth largura da área de jogo, em pixels.
 * @param screenHeight altura da área de jogo, em pixels.
 */
class GameEngine(
    private val screenWidth: Float,
    private val screenHeight: Float
) {

    /** A bola, como estado observável pelo Compose. */
    var ball by mutableStateOf(criarBolaInicial())
        private set

    /** O paddle, como estado observável pelo Compose. */
    var paddle by mutableStateOf(criarPaddleInicial())
        private set

    /** Indica se o jogo está rodando (o loop só atualiza quando true). */
    var running by mutableStateOf(true)
        private set

    /**
     * Gancho de colisões. O Integrante 4 atribui aqui uma função que recebe
     * a bola e o paddle atuais e aplica as regras de colisão (com paredes,
     * paddle e tijolos). Enquanto ninguém preencher, o loop apenas move os
     * objetos, sem colidir. Isso é o "deixar a base preparada para colisões".
     */
    var collisionHandler: ((Ball, Paddle) -> Unit)? = null

    /** Cria a bola no centro da tela, subindo em diagonal. */
    private fun criarBolaInicial(): Ball {
        return Ball(
            position = Offset(screenWidth / 2f, screenHeight / 2f),
            velocity = Offset(180f, -320f)
        )
    }

    /** Cria o paddle centralizado, perto do rodapé da tela. */
    private fun criarPaddleInicial(): Paddle {
        return Paddle(
            centerX = screenWidth / 2f,
            y = screenHeight - 120f
        )
    }

    /**
     * O game loop. Deve ser chamado uma vez por quadro (a tela do jogo
     * fica chamando isto continuamente).
     *
     * O que ele faz, em ordem:
     *  1. Se o jogo estiver pausado, não faz nada.
     *  2. Move a bola de acordo com o tempo decorrido.
     *  3. Chama o gancho de colisões (se o Integrante 4 tiver preenchido).
     *
     * @param deltaSeconds tempo decorrido desde o último quadro, em segundos.
     */
    fun update(deltaSeconds: Float) {
        if (!running) return

        val bolaAtual = ball
        bolaAtual.update(deltaSeconds)
        ball = bolaAtual.copy()

        collisionHandler?.invoke(ball, paddle)
    }

    /**
     * Move o paddle acompanhando o toque/arraste do jogador. A tela chama
     * isto sempre que o dedo se move sobre a área de jogo.
     *
     * @param x posição horizontal do toque, em pixels.
     */
    fun onDrag(x: Float) {
        val p = paddle
        p.moveTo(x, screenWidth)
        paddle = p.copy()
    }

    /** Pausa o loop (por exemplo, quando a bola cai ou abre um diálogo). */
    fun pause() {
        running = false
    }

    /** Retoma o loop. */
    fun resume() {
        running = true
    }

    /** Reinicia bola e paddle às posições iniciais (usado ao recomeçar). */
    fun reset() {
        ball = criarBolaInicial()
        paddle = criarPaddleInicial()
        running = true
    }
}