package org.example.skatecoinrush

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stevdza_san.sprite.util.getScreenHeight
import org.example.skatecoinrush.domain.Game
import org.example.skatecoinrush.domain.GameStatus
import org.example.skatecoinrush.util.GameFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import skatecoinrush.composeapp.generated.resources.Res
import skatecoinrush.composeapp.generated.resources.movebg

@Composable
@Preview
fun App() {
    var screenHeight by remember { mutableStateOf(0) }
    var screenWidth by remember { mutableStateOf(0) }
    var game by remember{ mutableStateOf(Game()) }

    LaunchedEffect(Unit){
        game.start()
    }
    LaunchedEffect(game.status){
        while (game.status ==GameStatus.Started){
            withFrameMillis {
                game.updateGameProgress()
            }
        }
    }
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize().align(Alignment.TopStart),
                painter = painterResource(Res.drawable.movebg),
                contentDescription = "moving background",
                contentScale = ContentScale.Crop
            )
        }

        Canvas(modifier = Modifier.fillMaxSize().onGloballyPositioned {
            val size = it.size
            if (screenWidth != size.width || screenHeight != size.height){
                screenWidth = size.width
                screenHeight = size.height
                game = game.copy(
                    screenWidth = size.width,
                    screenHeight = size.height,
                )
            }
        }.clickable {
            if (game.status == GameStatus.Started){
                game.toJump()
            }
        }
        ){
            drawCircle(
                color = Color.Blue,
                radius = game.skateBoy.size,
                center = Offset(
                    x = game.skateBoy.x,
                    y = game.skateBoy.y
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BEST: 0",
                fontWeight=FontWeight.Bold,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = GameFontFamily()
            )
            Text(
                text = "0",
                fontWeight=FontWeight.Bold,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = GameFontFamily()
            )

        }
        if (game.status == GameStatus.Over){
            Column(modifier = Modifier.fillMaxSize()
                .background(color = Color.Black.copy(0.5f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "Game Over",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GameFontFamily()
                )

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