package com.el_aouthmanie.nticapp.ui.screens.homeScreen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.el_aouthmanie.nticapp.modules.intities.Seance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random



@Composable
fun ScheduleCard(seance: Seance) {
    val circles = remember {
        List(5) {
            CircleDecoration(
                radius = Random.nextFloat() * 100f + 30f,
                alpha = Random.nextFloat() * 0.3f,
                xFactor = Random.nextFloat(),
                yFactor = Random.nextFloat()
            )
        }
    }

    val lines = remember {
        List(3) {
            LineDecoration(
                startXF = Random.nextFloat(),
                startYF = Random.nextFloat(),
                endXF = Random.nextFloat(),
                endYF = Random.nextFloat(),
                strokeWidth = Random.nextFloat() * 6f + 2f
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        colors = CardDefaults.outlinedCardColors().copy(containerColor = Color(0xFF1E88E5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Draw background decorations
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                circles.forEach {
                    drawCircle(
                        color = Color.White.copy(alpha = it.alpha),
                        radius = it.radius,
                        center = androidx.compose.ui.geometry.Offset(w * it.xFactor, h * it.yFactor)
                    )
                }
                lines.forEach {
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = androidx.compose.ui.geometry.Offset(size.width * it.startXF, size.height * it.startYF),
                        end = androidx.compose.ui.geometry.Offset(size.width * it.endXF, size.height * it.endYF),
                        strokeWidth = it.strokeWidth
                    )
                }
            }

            // Time range
            Text(
                text = "${seance.startingTime} - ${seance.endingTime}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Animated "OnGoing" text
            WaveText(
                modifier = Modifier.align(Alignment.TopEnd),
                text = "OnGoing"
            )

            // Title and subtitle
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = 32.dp)
            ) {
                val scrollState = rememberScrollState()
                Text(
                    text = seance.teacher,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = seance.moduleDetails + "\n" + seance.nomMode,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.verticalScroll(scrollState)
                )
            }

            // Room badge
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seance.classRoom,
                    color = Color.Blue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WaveText(modifier: Modifier = Modifier, text: String) {
    val offsets = remember { List(text.length) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        while (true) {
            offsets.forEachIndexed { index, anim ->
                launch {
                    anim.animateTo(-10f, tween(400))
                    anim.animateTo(0f, tween(400))
                }
                delay(200) // delay between characters
            }
        }
    }

    Row(modifier = modifier) {
        text.forEachIndexed { i, c ->
            val offsetY = offsets[i].value
            Text(
                text = c.toString(),
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    translationY = offsetY
                }
            )
        }
    }
}


data class CircleDecoration(val radius: Float, val alpha: Float, val xFactor: Float, val yFactor: Float)
data class LineDecoration(val startXF: Float, val startYF: Float, val endXF: Float, val endYF: Float, val strokeWidth: Float)
