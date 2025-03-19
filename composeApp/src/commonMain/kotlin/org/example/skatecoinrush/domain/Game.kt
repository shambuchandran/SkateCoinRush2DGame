package org.example.skatecoinrush.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

const val SCORE_KEY ="score"
data class Game(
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val gravity: Float = 0.8f,
    val jump: Float = -27f,
    val jumpMaxSpeed: Float = 15f,
    val stoneSpeed: Float = 5f,
    val coinSpeed: Float = 5f
    ):KoinComponent {

        private val settings :ObservableSettings by inject()
    var status by mutableStateOf(GameStatus.Idle)
        private set
    var skateJumpVelocity by mutableStateOf(0f)
        private set
    var isJumping by mutableStateOf(false)
        private set
    var skateBoy by mutableStateOf(
        SkateBoy(x = (screenWidth / 20).toFloat(), y = (screenHeight / 10).toFloat())
    )
        private set
    var stones by mutableStateOf(listOf<Stone>())
        private set
    var coins by mutableStateOf(listOf<Coin>())
        private set
    var currentScore by mutableStateOf(0)
        private set
    var beatScore by mutableStateOf(0)
        private set

    init {
        beatScore = settings.getInt(
            key = SCORE_KEY,
            defaultValue = 0
        )
        settings.addIntListener(
            key = SCORE_KEY,
            defaultValue = 0
        ){
            beatScore = it
        }
    }


    fun start() {
        status = GameStatus.Started
        stones = listOf(Stone(screenWidth.toFloat(), screenHeight * 0.75f + 150))
        coins = listOf(Coin(screenWidth.toFloat(), screenHeight * 0.4f))
        currentScore = 0
    }

    fun gameOver() {
        status = GameStatus.Over
        saveScore()
    }

    fun toJump() {
        if (!isJumping) {
            skateJumpVelocity = jump
            isJumping = true
        }
    }
    private fun saveScore(){
        if (beatScore<currentScore){
            settings.putInt(key = SCORE_KEY, value = currentScore)
            beatScore = currentScore
        }
    }

    fun updateGameProgress() {
        val falseBottom = screenHeight * 0.75f
        if (skateBoy.y < 0) {
//            stopJump()
//            return
            skateBoy = skateBoy.copy(y = 0f)
            skateJumpVelocity = 0f
        } else if (skateBoy.y > falseBottom) {
            //gameOver()
            //return
            skateBoy = skateBoy.copy(y = falseBottom)
            skateJumpVelocity = 0F
            isJumping = false
            return
        }
        skateJumpVelocity = (skateJumpVelocity + gravity).coerceIn(-jumpMaxSpeed * 2F, jumpMaxSpeed)
        skateBoy = skateBoy.copy(y = skateBoy.y + skateJumpVelocity)

        // Move stones and remove them off-screen
        stones = stones.map { it.copy(x = it.x - stoneSpeed) }
            .filter { it.x + it.width > -screenWidth * 0.2f }

        if (stones.isEmpty() || stones.last().x < screenWidth - Random.nextInt(450, 900)) {
            stones = stones + Stone(screenWidth.toFloat(), screenHeight * 0.75f + 150)
        }

        // Collision Detection
        stones.forEach { stone ->
            val stoneScale = 0.2f // Match rendering scale
            val stoneWidth = stone.width * stoneScale
            val stoneHeight = stone.height * stoneScale
            val stoneY = stone.y - stoneHeight - 150// Scaled top position

            // Stone hitbox
            val stoneLeft = stone.x
            val stoneRight = stone.x + stoneWidth
            val stoneTop = stoneY
            val stoneBottom = stone.y

            // SkateBoy hitbox (square based on size)
            val skateLeft = skateBoy.x
            val skateRight = skateBoy.x + skateBoy.size
            val skateTop = skateBoy.y - skateBoy.size
            val skateBottom = skateBoy.y

            // Check overlap
            if (skateLeft < stoneRight &&
                skateRight > stoneLeft &&
                skateTop < stoneBottom &&
                skateBottom > stoneTop
            ) {
                gameOver()
                return
            }

            // COLLISION DETECTION: SkateBoy vs Coins
            coins = coins.map { coin ->
                // Coin hitbox
                val coinLeft = coin.x
                val coinRight = coin.x + coin.width
                val coinTop = coin.y - coin.height
                val coinBottom = coin.y

                // SkateBoy hitbox
                val skateLeftCoin = skateBoy.x
                val skateRightCoin = skateBoy.x + skateBoy.size
                val skateTopCoin = skateBoy.y - skateBoy.size
                val skateBottomCoin = skateBoy.y

                // Check for collision
                if (skateLeftCoin < coinRight &&
                    skateRightCoin > coinLeft &&
                    skateTopCoin < coinBottom &&
                    skateBottomCoin > coinTop
                ) {
                    // Collision detected: Collect the coin
                    currentScore += 1 // Increase score
                    null // Remove the coin from the list
                } else {
                    coin // Keep the coin in the list
                }
            }.filterNotNull() // Filter out null values (collected coins)

            // Move coins and remove them off-screen
            coins = coins.map { it.copy(x = it.x - coinSpeed) }
                .filter { it.x + it.width > -screenWidth * 0.2f }

            if (coins.isEmpty() || coins.last().x < screenWidth - Random.nextInt(250, 1000)) {
                coins = coins + Coin(screenWidth.toFloat(), (screenHeight * 0.4f))
            }


        }
    }


//    fun stopJump(){
//        skateJumpVelocity = 0f
//        skateBoy = skateBoy.copy(y = 0f)
//    }
}