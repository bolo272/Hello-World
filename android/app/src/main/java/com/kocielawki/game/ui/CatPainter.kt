package com.kocielawki.game.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import com.kocielawki.game.Breed
import com.kocielawki.game.Mark

/** Kot jest rysowany w układzie 100 × 84 (jak viewBox "0 2 100 84" w wersji webowej). */
const val CAT_W = 100f
const val CAT_H = 84f

private val pathCache = HashMap<String, Path>()
fun svgPath(d: String): Path = pathCache.getOrPut(d) { PathParser().parsePathString(d).toPath() }

fun Color.lighten(t: Float) = lerp(this, Color.White, t)
fun Color.darken(t: Float) = lerp(this, Color.Black, t)

private fun DrawScope.oval(color: Color, cx: Float, cy: Float, rx: Float, ry: Float) =
    drawOval(color, Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2))

private fun DrawScope.oval(brush: Brush, cx: Float, cy: Float, rx: Float, ry: Float) =
    drawOval(brush, Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2))

private fun DrawScope.ovalLine(color: Color, cx: Float, cy: Float, rx: Float, ry: Float, w: Float) =
    drawOval(color, Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2), style = Stroke(w))

private fun DrawScope.line(d: String, color: Color, w: Float, alpha: Float = 1f) =
    drawPath(svgPath(d), color, alpha, style = Stroke(w, cap = StrokeCap.Round, join = StrokeJoin.Round))

private fun DrawScope.fill(d: String, color: Color) = drawPath(svgPath(d), color)

private const val EAR = "M19 34 L23 6 L45 21 Z"
private const val EAR_BIG = "M13 37 L14 3 L44 20 Z"
private const val EAR_IN = "M25 28 L27 13 L39 22 Z"
private const val EAR_IN_BIG = "M20 31 L20 11 L37 21 Z"
private const val TUFT = "M41 19 L44 7 L49 15 L53 5 L57 15 L61 9 L60 20 Z"

/**
 * Rysuje kota w bieżącym DrawScope (rozmiar = cały obszar).
 * blink: 0 = otwarte oczy, 1 = zamknięte. tail: wychylenie ogona w stopniach.
 */
fun DrawScope.drawCat(b: Breed, happy: Boolean, blink: Float = 0f, tail: Float = 0f) {
    val sc = size.width / CAT_W
    withTransform({
        scale(sc, sc, pivot = Offset.Zero)
        translate(0f, -2f)
    }) {
        val outline = b.fur.darken(.35f).copy(alpha = .45f)
        val big = b.mark == Mark.WRINKLES
        val earL = when (b.mark) { Mark.MASK -> b.shade; Mark.PATCHES -> b.patchA; else -> b.fur }
        val earR = when (b.mark) { Mark.MASK -> b.shade; Mark.PATCHES -> b.patchB; else -> b.fur }
        val tailColor = when (b.mark) { Mark.MASK -> b.shade; Mark.PATCHES -> b.patchB; else -> b.fur }
        val paw = if (b.mark == Mark.MASK) b.shade else b.belly

        // cień pod kotem
        oval(Color.Black.copy(alpha = .16f), 50f, 83f, 30f, 3.6f)

        // ogon
        rotate(tail, pivot = Offset(80f, 76f)) {
            line("M78 76 C96 76 99 58 89 50", outline, 10f)
            line("M78 76 C96 76 99 58 89 50", tailColor, 7.6f)
        }

        // tułów
        val bodyBrush = Brush.radialGradient(listOf(b.fur.lighten(.22f), b.fur, b.fur.darken(.14f)), Offset(42f, 58f), 42f)
        oval(bodyBrush, 50f, 68f, 33f, 15f)
        ovalLine(outline, 50f, 68f, 33f, 15f, 1.4f)
        when (b.mark) {
            Mark.STRIPES -> line("M26 60 q4 6 1 12 M74 60 q-4 6 -1 12", b.shade, 3f)
            Mark.TABBY -> line("M23 62 q5 5 3 11 M32 56 q4 7 2 15 M77 62 q-5 5 -3 11 M68 56 q-4 7 -2 15", b.shade, 2.8f)
            Mark.PATCHES -> { oval(b.patchA, 70f, 64f, 11f, 7f); oval(b.patchB, 31f, 66f, 8f, 5.5f) }
            Mark.SPOTS -> listOf(25f to 64f, 33f to 58f, 68f to 58f, 76f to 66f, 29f to 72f, 72f to 73f)
                .forEach { (x, y) -> drawCircle(b.shade.copy(alpha = .7f), 2.4f, Offset(x, y)) }
            else -> {}
        }
        oval(b.belly, 50f, 71f, 15f, 9f)

        // uszy
        val ear = if (big) EAR_BIG else EAR
        val earIn = if (big) EAR_IN_BIG else EAR_IN
        fill(ear, earL); line(ear, outline, 1.4f); fill(earIn, b.ear)
        scale(-1f, 1f, pivot = Offset(50f, 0f)) {
            fill(ear, earR); line(ear, outline, 1.4f); fill(earIn, b.ear)
        }

        // grzywka i głowa
        fill(TUFT, b.fur); line(TUFT, outline, 1.2f)
        val headBrush = Brush.radialGradient(listOf(b.fur.lighten(.26f), b.fur, b.fur.darken(.12f)), Offset(40f, 28f), 40f)
        oval(headBrush, 50f, 40f, 31f, 25f)
        ovalLine(outline, 50f, 40f, 31f, 25f, 1.4f)
        drawHeadMarks(b)

        // oczy
        if (happy) {
            line("M32 41 q6 -6.5 12 0 M56 41 q6 -6.5 12 0", b.line, 2.8f)
            oval(Color(0xFFFF8FA8).copy(alpha = .6f), 29f, 48f, 4.6f, 2.7f)
            oval(Color(0xFFFF8FA8).copy(alpha = .6f), 71f, 48f, 4.6f, 2.7f)
        } else {
            scale(1f, 1f - .92f * blink, pivot = Offset(50f, 39f)) {
                for ((x, col) in listOf(38f to b.eyeL, 62f to b.eyeR)) {
                    oval(Color.White, x, 39f, 6.6f, 7.6f)
                    oval(col, x, 39.6f, 5.4f, 6.6f)
                    oval(col.darken(.3f), x, 41.5f, 5.4f, 3.6f)
                    oval(Color(0xFF15121A), x, 40f, 2.2f, 4.8f)
                    drawCircle(Color.White, 1.9f, Offset(x + 2f, 36.4f))
                    drawCircle(Color.White.copy(alpha = .8f), .9f, Offset(x - 2f, 43f))
                }
            }
            if (blink > .5f) line("M32 39.5 q6 3 12 0 M56 39.5 q6 3 12 0", b.line, 2.2f)
        }

        // nos, pyszczek, wąsy
        fill("M46.5 46.5 h7 l-3.5 4 z", b.nose)
        line("M50 50.5 q-2.5 4 -6 2.2 M50 50.5 q2.5 4 6 2.2", b.line, 1.6f)
        line("M29 48 L12 45 M29 51.5 L13 53 M71 48 L88 45 M71 51.5 L87 53", b.whisker, 1.2f, .7f)

        // obroża z dzwoneczkiem
        line("M27 58.5 Q50 70 73 58.5", b.collar.darken(.25f), 6f)
        line("M27 58.5 Q50 70 73 58.5", b.collar, 4.4f)
        drawCircle(Color(0xFFF6C945), 3.6f, Offset(50f, 65f))
        drawCircle(Color(0xFFB98A1C), 3.6f, Offset(50f, 65f), style = Stroke(1f))
        drawCircle(Color.White.copy(alpha = .7f), 1.1f, Offset(48.8f, 63.8f))

        // łapki
        oval(paw, 39f, 80f, 8f, 4.2f); ovalLine(outline, 39f, 80f, 8f, 4.2f, 1f)
        oval(paw, 61f, 80f, 8f, 4.2f); ovalLine(outline, 61f, 80f, 8f, 4.2f, 1f)
    }
}

private fun DrawScope.drawHeadMarks(b: Breed) {
    val sh = b.shade
    when (b.mark) {
        Mark.STRIPES -> line("M44 20 l2 7 M50 18 v8 M56 20 l-2 7 M20 39 h8 M21 45 h7 M80 39 h-8 M79 45 h-7", sh, 2.6f)
        Mark.TABBY -> line("M38 28 l3 -8 l4 6 l5 -7 l5 7 l4 -6 l3 8 M20 39 h8 M21 45 h7 M80 39 h-8 M79 45 h-7", sh, 2.4f)
        Mark.MASK -> oval(sh.copy(alpha = .88f), 50f, 46f, 15f, 12.5f)
        Mark.PATCHES -> { oval(b.patchA, 36f, 31f, 12f, 10f); oval(b.patchB, 65f, 34f, 11f, 9f) }
        Mark.SPOTS -> listOf(Triple(43f, 24f, 2.3f), Triple(51f, 21f, 2f), Triple(58f, 25f, 2.2f), Triple(24f, 35f, 1.8f), Triple(76f, 35f, 1.8f))
            .forEach { (x, y, r) -> drawCircle(sh.copy(alpha = .75f), r, Offset(x, y)) }
        Mark.WRINKLES -> line("M37 24 q13 -6 26 0 M40 29.5 q10 -4 20 0", sh, 1.8f)
        Mark.BLAZE -> {
            fill("M50 20 L45.5 40 H54.5 Z", b.belly)
            oval(b.belly, 44f, 49f, 8f, 6f); oval(b.belly, 56f, 49f, 8f, 6f)
        }
        Mark.NONE -> {}
    }
}

private const val BLANKET = "M9 84 C7 52 17 37 27 31 L32 13 L44 26 Q50 24.5 56 26 L68 13 L73 31 C83 37 93 52 91 84 Z"

/** Śpiący kot pod kocykiem w kratkę; nie zdradza koloru. */
fun DrawScope.drawSleeper(t: Float) {
    val sc = size.width / CAT_W
    withTransform({
        scale(sc, sc, pivot = Offset.Zero)
        translate(0f, -2f)
    }) {
        val p = svgPath(BLANKET)
        oval(Color.Black.copy(alpha = .16f), 50f, 84f, 38f, 3.4f)
        val breathe = 1f + .025f * kotlin.math.sin(t * 2.2f)
        scale(1f, breathe, pivot = Offset(50f, 84f)) {
            clipPath(p) {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFA897EA), Color(0xFF7F6CC8)), 10f, 86f), Offset(0f, 0f), Size(100f, 90f))
                for (x in 6..96 step 14) drawRect(Color(0xFFB9ACF0).copy(alpha = .55f), Offset(x.toFloat(), 0f), Size(5f, 90f))
                for (y in 18..90 step 14) drawRect(Color(0xFFB9ACF0).copy(alpha = .4f), Offset(0f, y.toFloat()), Size(100f, 5f))
                drawRect(Color.White.copy(alpha = .14f), Offset(0f, 0f), Size(100f, 40f))
            }
            drawPath(p, Color(0xFF5E4DA8), style = Stroke(2f, join = StrokeJoin.Round))
            line("M12 79 H88", Color(0xFF5E4DA8), 1.6f, .7f)
        }
        // "z z" unoszące się nad kocykiem
        for (k in 0..1) {
            val ph = ((t * .5f + k * .5f) % 1f)
            val x = 70f + k * 9f + ph * 6f
            val y = 20f - k * 8f - ph * 10f
            val s = 5f - k * 1.4f
            val z = Path().apply { moveTo(x, y); relativeLineTo(s, 0f); relativeLineTo(-s, s * 1.2f); relativeLineTo(s, 0f) }
            drawPath(z, Color.White, 1f - ph, style = Stroke(1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}
