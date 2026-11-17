package habitflow.adnoryx.eu.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiOverlay(
    trigger: Long,
    modifier: Modifier = Modifier
) {
    if (trigger == 0L) return

    val progress = remember(trigger) { Animatable(0f) }
    val colors = remember {
        listOf(
            Color(0xFF0088FF),
            Color(0xFF00E676),
            Color(0xFFFFD600),
            Color(0xFFFF4081),
            Color(0xFF7C4DFF),
            Color(0xFF00E5FF)
        )
    }

    val particles = remember(trigger) {
        List(70) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = Random.nextFloat() * 650f + 200f
            ConfettiParticle(
                startX = 0.5f + (Random.nextFloat() - 0.5f) * 0.4f,
                startY = 0.25f + (Random.nextFloat() - 0.5f) * 0.15f,
                velocityX = cos(angle) * speed,
                velocityY = sin(angle) * speed - 350f,
                color = colors.random(),
                size = Random.nextFloat() * 14f + 10f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    val t = progress.value
    if (t >= 1f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val gravity = 1400f
        val alpha = (1f - t).coerceIn(0f, 1f)

        particles.forEach { p ->
            val x = p.startX * size.width + p.velocityX * t
            val y = p.startY * size.height + p.velocityY * t + 0.5f * gravity * t * t
            if (x in -50f..(size.width + 50f) && y in -50f..(size.height + 50f)) {
                rotate(degrees = p.rotationSpeed * t, pivot = Offset(x, y)) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(x - p.size / 2f, y - p.size / 2f),
                        size = Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }
    }
}
