package com.example.brickbreaker.game

// Essa estrutura guarda as informações de 1 tijolo
data class Brick(
    val id: Int,             
    val x: Float,            
    val y: Float,            
    val width: Float,        
    val height: Float,       
    var isDestroyed: Boolean = false, 
    val row: Int = 0,        
    val col: Int = 0,
    val points: Int = 10     // Valor do tijolo para a pontuação
)
