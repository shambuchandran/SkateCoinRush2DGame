package org.example.skatecoinrush.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Game(
    val screenWidth:Int = 0,
    val screenHeight:Int = 0,
    val gravity:Float = 0.8f,
    val jump:Float= -27f,
    val jumpMaxSpeed :Float = 15f

) {
    var status by mutableStateOf(GameStatus.Idle)
        private set
    var skateJumpVelocity by mutableStateOf(0f)
        private set
    var isJumping by mutableStateOf(false)
        private set
    var skateBoy by mutableStateOf(
        SkateBoy(x= (screenWidth/8).toFloat(), y = (screenHeight/10).toFloat())
    )
        private set

    fun start(){
        status = GameStatus.Started
    }
    fun gameOver(){
        status = GameStatus.Over
    }
    fun toJump(){
        if (!isJumping){
            skateJumpVelocity = jump
            isJumping = true
        }

    }
    fun updateGameProgress(){
        val falseBottom = screenHeight*0.85f
        if (skateBoy.y < 0){
//            stopJump()
//            return
            skateBoy = skateBoy.copy(y = 0f)
            skateJumpVelocity = 0f
        }else if (skateBoy.y > falseBottom){
            //gameOver()
            //return
            skateBoy = skateBoy.copy(y = falseBottom)
            skateJumpVelocity = -2f
            isJumping = false
            return
        }
        skateJumpVelocity = (skateJumpVelocity+gravity).coerceIn(-jumpMaxSpeed * 2F,jumpMaxSpeed)
        skateBoy=skateBoy.copy(y= skateBoy.y+ skateJumpVelocity)
    }
//    fun stopJump(){
//        skateJumpVelocity = 0f
//        skateBoy = skateBoy.copy(y = 0f)
//    }
}