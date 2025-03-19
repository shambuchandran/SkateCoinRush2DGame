package org.example.skatecoinrush

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.launch
import org.example.skatecoinrush.domain.Game
import org.example.skatecoinrush.domain.GameStatus
import org.example.skatecoinrush.util.GameFontFamily
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import skatecoinrush.composeapp.generated.resources.Res
import skatecoinrush.composeapp.generated.resources.coin
import skatecoinrush.composeapp.generated.resources.movebgone
import skatecoinrush.composeapp.generated.resources.skatethree
import skatecoinrush.composeapp.generated.resources.stonetwo

@Composable
@Preview
fun App() {
    var screenHeight by remember { mutableStateOf(0) }
    var screenWidth by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(Game()) }

    val spriteState = rememberSpriteState(
        totalFrames = 6,
        framesPerRow = 6
    )
    val spriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = 171,
                frameHeight = 275,
                image = Res.drawable.skatethree
            )
        )
    }
    val coinSpriteState = rememberSpriteState(
        totalFrames = 7,
        framesPerRow = 7
    )
    val coinSpriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = 76,
                frameHeight = 76,
                image = Res.drawable.coin
            )
        )
    }
    val sheetImage = spriteSpec.imageBitmap
    val currentFrame by spriteState.currentFrame.collectAsState()

    val coinSheetImage = coinSpriteSpec.imageBitmap
    val currentCoinFrame by coinSpriteState.currentFrame.collectAsState()

    val stoneImage = imageResource(Res.drawable.stonetwo)





    LaunchedEffect(game.status) {
        while (game.status == GameStatus.Started) {
            withFrameMillis {
                game.updateGameProgress()
            }
        }
        if (game.status == GameStatus.Over) {
            spriteState.stop()
            coinSpriteState.stop()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            spriteState.stop()
            spriteState.cleanup()
            coinSpriteState.stop()
            coinSpriteState.cleanup()
        }
    }
    MaterialTheme {
        val backgroundOffset = remember { androidx.compose.animation.core.Animatable(0f) }
        var imageWidth by remember { mutableStateOf(0) }

        LaunchedEffect(game.status){
            while (game.status == GameStatus.Started){
                backgroundOffset.animateTo(
                    targetValue = -imageWidth.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            5000, easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize().align(Alignment.TopStart).onSizeChanged {
                    imageWidth = it.width
                }
                    .offset {
                        IntOffset(
                            x=backgroundOffset.value.toInt(),
                            y = 0
                        )
                    },
                painter = painterResource(Res.drawable.movebgone),
                contentDescription = "moving background",
                contentScale = ContentScale.Crop
            )
            Image(
                modifier = Modifier.fillMaxSize().align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            x=backgroundOffset.value.toInt()+imageWidth,
                            y = 0
                        )
                    },
                painter = painterResource(Res.drawable.movebgone),
                contentDescription = "moving background",
                contentScale = ContentScale.Crop
            )
        }

        Canvas(modifier = Modifier.fillMaxSize().onGloballyPositioned {
            val size = it.size
            if (screenWidth != size.width || screenHeight != size.height) {
                screenWidth = size.width
                screenHeight = size.height
                game = game.copy(
                    screenWidth = size.width,
                    screenHeight = size.height,
                )
            }
        }.clickable {
            if (game.status == GameStatus.Started) {
                game.toJump()
            }
        }
        ) {
            drawSpriteView(
                spriteSpec = spriteSpec,
                spriteState = spriteState,
                currentFrame = currentFrame,
                image = sheetImage,
                offset = IntOffset(
                    x = game.skateBoy.x.toInt(),
                    y = (game.skateBoy.y - game.skateBoy.size).toInt()
                )
            )

            game.coins.forEach { coin ->
                drawSpriteView(
                    spriteSpec = coinSpriteSpec,
                    spriteState = coinSpriteState,
                    currentFrame = currentCoinFrame,
                    image = coinSheetImage,
                    offset = IntOffset(
                        x = coin.x.toInt(),
                        y = coin.y.toInt()
                    )
                )
            }

            val stoneScaleX = 0.2f
            val stoneScaleY = 0.2f
            game.stones.forEach { stone ->
                withTransform({
                    scale(stoneScaleX,stoneScaleY, pivot = Offset(stone.x,stone.y))
                }){
                    drawImage(
                        image = stoneImage,
                        topLeft = Offset(stone.x, stone.y-stone.height)
                    )

                }
            }

//            drawCircle(
//                color = Color.Blue,
//                radius = game.skateBoy.size,
//                center = Offset(
//                    x = game.skateBoy.x,
//                    y = game.skateBoy.y
//                )
//            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BEST: ${game.beatScore}",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = GameFontFamily()
            )
            Text(
                text = "${game.currentScore}",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = GameFontFamily()
            )

        }
        if (game.status == GameStatus.Idle) {
            Column (
                modifier = Modifier.fillMaxSize()
                    .background(color = Color.Black.copy(0.8f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    Text(text = "SKATE",
                        color = Color.Red, fontSize = 62.sp, fontFamily = GameFontFamily()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "COIN",
                        color = Color.Red, fontSize = 62.sp, fontFamily = GameFontFamily()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "RUSH",
                        color = Color.Red, fontSize = 62.sp, fontFamily = GameFontFamily()
                    )
                }
                Button(
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(size = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Red.copy(0.7f)
                    ),
                    onClick = {
                        game.start()
                        spriteState.start()
                        coinSpriteState.start()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        "START", fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = GameFontFamily()
                    )
                }
            }
        }
        if (game.status == GameStatus.Over) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(color = Color.Black.copy(0.5f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Over!",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.displayLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GameFontFamily()
                )
                Text(
                    text = "Score: ${game.currentScore}",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.displaySmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GameFontFamily()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(size = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Red.copy(0.7f)
                    ),
                    onClick = {
                        game.start()
                        spriteState.start()
                        scope.launch {
                            backgroundOffset.snapTo(0f)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        "RESTART", fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = GameFontFamily()
                    )
                }
            }
        }


//        val screenWidth = getScreenWidth()
//        val spriteState = rememberSpriteState(
//            totalFrames = 7,
//            framesPerRow = 7
//        )
//        val spriteSpec = remember {
//            SpriteSpec(
//                screenWidth = screenWidth.value,
//                default = SpriteSheet(
//                    frameWidth = 152,
//                    frameHeight = 152,
//                    image = Res.drawable.cointwo
//                )
//            )
//        }
//        DisposableEffect(Unit){
//            spriteState.start()
//            onDispose {
//                spriteState.stop()
//                spriteState.cleanup()
//            }
//        }
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            SpriteView(
//                spriteState = spriteState,
//                spriteSpec = spriteSpec
//            )
//        }

    }
}
