package com.kocielawki.game.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class Shape { HEART, STAR, DOT, CONFETTI }

/** Cząsteczka liczona analitycznie z czasu, więc nie trzeba jej aktualizować co klatkę. */
class Particle(
    val x: Float, val y: Float, val vx: Float, val vy: Float, val gravity: Float,
    val born: Long, val life: Long, val color: Color, val size: Float, val shape: Shape,
    val spin: Float, val drag: Float = 0f,
)

val HEART_PATH: Path = svgPath("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z")
val STAR_PATH: Path = Path().apply {
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) 12f else 5f
        val a = -PI / 2 + i * PI / 5
        val px = 12f + r * cos(a).toFloat()
        val py = 12f + r * sin(a).toFloat()
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
    close()
}

val PARTY = listOf(Color(0xFFFF5C9A), Color(0xFFFFD23A), Color(0xFF5ACD63), Color(0xFF4A7BF0), Color(0xFFA56AEA), Color(0xFF28C2C6), Color(0xFFFF8A2A))

class ParticleSystem {
    val items = mutableStateListOf<Particle>()

    /** Serduszka i gwiazdki nad skompletowaną ławką. */
    fun celebrate(center: Offset, width: Float, now: Long, unit: Float) {
        repeat(16) {
            val a = (-PI / 2 + (Random.nextFloat() - .5f) * 2.2f).toFloat()
            val sp = unit * (5f + Random.nextFloat() * 6f)
            items += Particle(
                center.x + (Random.nextFloat() - .5f) * width, center.y,
                cos(a) * sp, sin(a) * sp, unit * 9f, now, 900L + Random.nextLong(500),
                if (it % 3 == 0) Color(0xFFFFD23A) else Color(0xFFFF5C9A), unit * (1.6f + Random.nextFloat()),
                if (it % 3 == 0) Shape.STAR else Shape.HEART, (Random.nextFloat() - .5f) * 200f,
            )
        }
    }

    /** Konfetti z góry ekranu po wygranej. */
    fun confetti(width: Float, now: Long, unit: Float) {
        repeat(90) {
            items += Particle(
                Random.nextFloat() * width, -unit * (2f + Random.nextFloat() * 30f),
                (Random.nextFloat() - .5f) * unit * 4f, unit * (2f + Random.nextFloat() * 4f), unit * 3.2f,
                now + Random.nextLong(600), 3200L + Random.nextLong(1400), PARTY[it % PARTY.size],
                unit * (1.1f + Random.nextFloat() * .9f), if (it % 7 == 0) Shape.STAR else Shape.CONFETTI,
                (Random.nextFloat() - .5f) * 720f, drag = .35f,
            )
        }
    }

    fun prune(now: Long) { items.removeAll { now - it.born > it.life } }

    fun draw(scope: DrawScope, now: Long) = with(scope) {
        for (p in items) {
            val age = (now - p.born)
            if (age < 0 || age > p.life) continue
            val t = age / 1000f
            val dragF = if (p.drag > 0f) (1f - kotlin.math.exp(-p.drag * t * 3f)) / (p.drag * 3f) else t
            val x = p.x + p.vx * 60f * dragF + if (p.shape == Shape.CONFETTI) sin(t * 5f + p.spin) * p.size * 3f else 0f
            val y = p.y + p.vy * 60f * dragF + .5f * p.gravity * 60f * t * t
            val alpha = (1f - (age.toFloat() / p.life)).coerceIn(0f, 1f).let { if (it > .3f) 1f else it / .3f }
            translate(x, y) {
                rotate(p.spin * t, pivot = Offset.Zero) {
                    when (p.shape) {
                        Shape.HEART, Shape.STAR -> scale(p.size / 12f, p.size / 12f, pivot = Offset.Zero) {
                            translate(-12f, -12f) { drawPath(if (p.shape == Shape.HEART) HEART_PATH else STAR_PATH, p.color, alpha) }
                        }
                        Shape.DOT -> drawCircle(p.color, p.size, Offset.Zero, alpha)
                        Shape.CONFETTI -> {
                            val h = p.size * kotlin.math.abs(cos(t * 9f + p.spin)) + .5f
                            drawRect(p.color, Offset(-p.size, -h / 2f), Size(p.size * 2f, h), alpha)
                        }
                    }
                }
            }
        }
    }
}
