package com.example.brickbreaker.game

// Essa estrutura guarda as informações de 1 tijolo
data class Brick(
    val id: Int,             // Um número de identificação único
    val x: Float,            // Posição na horizontal (esquerda/direita)
    val y: Float,            // Posição na vertical (cima/baixo)
    val width: Float,        // Largura do tijolo
    val height: Float,       // Altura do tijolo
    var isDestroyed: Boolean = false // O tijolo começa inteiro (não destruído)
)