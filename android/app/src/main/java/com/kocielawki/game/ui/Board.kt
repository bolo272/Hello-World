package com.kocielawki.game.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.kocielawki.game.BREEDS
import com.kocielawki.game.Breed
import com.kocielawki.game.GameEvent
import com.kocielawki.game.GameViewModel
import com.kocielawki.game.logic.CAP
import com.kocielawki.game.logic.Cat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private val WoodLight = Color(0xFFF4B678)
private val Wood = Color(0xFFDA8E4C)
private val WoodDark = Color(0xFFA5622C)
private val WoodLine = Color(0xFF7A4520)
private val Iron = Color(0xFF232750)

fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t

/** Geometria jednej ławki w pikselach planszy. catsLeft/catTop to lewy górny róg miejsca na 4 koty. */
class BenchGeom(val plank: Rect, val catsLeft: Float, val catTop: Float, val mirrored: Boolean, val hit: Rect)

class BoardLayout(val s: Float, val cols: Int, val benches: List<BenchGeom>, val width: Float) {
    /** Lewy górny róg kota na miejscu k (0 = przy brzegu ekranu, 3 = od strony środka). */
    fun slot(bench: Int, k: Int): Offset {
        val g = benches[bench]
        val i = if (g.mirrored) CAP - 1 - k else k
        return Offset(g.catsLeft + i * s, g.catTop)
    }
}

/**
 * Do dwóch kolumn ławek. Ławki dochodzą do krawędzi ekranu, a pośrodku zostaje przejście;
 * prawa kolumna jest lustrzana, więc ruchome koty zawsze siedzą przy przejściu.
 */
fun computeLayout(n: Int, w: Float, h: Float, dp: Float): BoardLayout {
    val edge = 12 * dp
    var best = 0f
    var bestCols = 1
    for (cols in 1..min(2, n)) {
        val rows = ceil(n / cols.toFloat()).toInt()
        val sW = if (cols == 1) (w - 2 * edge - 24 * dp) / 4.5f else (w - 2 * edge - 30 * dp) / 8.8f
        val sH = (h - rows * 4 * dp - (rows - 1) * 12 * dp) / (rows * 1.5f + (rows - 1) * .2f)
        val s = min(min(sW, sH), 92 * dp)
        if (s > best + .5f) { best = s; bestCols = cols }
    }
    val s = best
    val rows = ceil(n / bestCols.toFloat()).toInt()
    val benchH = 1.5f * s + 3 * dp
    val gap = .2f * s + 12 * dp
    val total = rows * benchH + (rows - 1) * gap
    val y0 = ((h - total) / 2f).coerceAtLeast(0f)
    val benches = (0 until n).map { i ->
        val col = if (bestCols == 2) i % 2 else 0
        val row = if (bestCols == 2) i / 2 else i
        val top = y0 + row * (benchH + gap)
        val catTop = top + .3f * s
        val plankTop = catTop + .84f * s - 3 * dp
        val plankH = .16f * s + 6 * dp
        val mirrored = bestCols == 2 && col == 1
        val catsLeft: Float
        val plank: Rect
        when {
            bestCols == 1 -> {
                catsLeft = (w - 4 * s) / 2f
                plank = Rect(catsLeft - .3f * s, plankTop, catsLeft + 4.3f * s, plankTop + plankH)
            }
            !mirrored -> {
                catsLeft = edge
                plank = Rect(-s, plankTop, edge + 4.3f * s, plankTop + plankH)
            }
            else -> {
                catsLeft = w - edge - 4 * s
                plank = Rect(catsLeft - .3f * s, plankTop, w + s, plankTop + plankH)
            }
        }
        val hit = Rect(maxOf(0f, plank.left), top - 4 * dp, min(w, plank.right), top + benchH + 6 * dp)
        BenchGeom(plank, catsLeft, catTop, mirrored, hit)
    }
    return BoardLayout(s, bestCols, benches, w)
}

private fun DrawScope.drawBench(g: BenchGeom, s: Float, dp: Float, glow: Color?, glowAlpha: Float, label: String, tm: TextMeasurer, w: Float) {
    val p = g.plank
    val visL = maxOf(p.left, 0f)
    val visR = min(p.right, w)
    val legH = .2f * s
    val legW = .09f * s + 3 * dp
    // cień na podłodze
    drawOval(Color.Black.copy(alpha = .2f), Offset(visL + .2f * s, p.bottom + legH - 4 * dp), Size(visR - visL - .4f * s, 9 * dp))
    // żeliwne nogi
    for (x in listOf(visL + .45f * s, visR - .45f * s - legW)) {
        drawRoundRect(Iron, Offset(x, p.bottom - 2 * dp), Size(legW, legH + 2 * dp), CornerRadius(legW / 2))
        drawRoundRect(Iron, Offset(x - legW * .4f, p.bottom + legH - 2.5f * dp), Size(legW * 1.8f, 3 * dp), CornerRadius(2 * dp))
    }
    // poświata (zaznaczenie, podpowiedź, komplet)
    if (glow != null && glowAlpha > 0f) {
        for (k in 3 downTo 1) {
            drawRoundRect(glow.copy(alpha = glowAlpha * .16f * k), Offset(p.left - k * 3 * dp, p.top - k * 3 * dp),
                Size(p.width + k * 6 * dp, p.height + k * 6 * dp), CornerRadius(6 * dp + k * 3 * dp))
        }
    }
    // deska: wierzch, front i słoje
    val r = CornerRadius(5 * dp)
    drawRoundRect(Brush.verticalGradient(listOf(WoodLight, Wood, WoodDark), p.top, p.bottom), p.topLeft, p.size, r)
    drawRoundRect(WoodLight.lighten(.35f).copy(alpha = .9f), Offset(p.left + 2 * dp, p.top + 1.5f * dp), Size(p.width - 4 * dp, p.height * .2f), CornerRadius(3 * dp))
    for (k in 0..2) {
        val y = p.top + p.height * (.45f + k * .17f)
        drawLine(WoodLine.copy(alpha = .18f), Offset(p.left + s * (.3f + k * .7f), y), Offset(p.right - s * (.5f + k * .4f), y), 1.2f * dp)
    }
    drawRoundRect(WoodDark.darken(.15f), Offset(p.left, p.bottom - 3 * dp), Size(p.width, 3 * dp), r)
    if (glow != null && glowAlpha > 0f) drawRoundRect(glow.copy(alpha = glowAlpha), p.topLeft, p.size, r, style = Stroke(2.5f * dp))
    // numer na froncie deski, pod środkiem miejsca na koty
    val style = TextStyle(color = WoodLine.copy(alpha = .75f), fontSize = (p.height * .55f / density).sp, fontFamily = Display)
    val m = tm.measure(label, style)
    drawText(m, topLeft = Offset(g.catsLeft + 2 * s - m.size.width / 2f, p.top + p.height * .55f - m.size.height / 2f))
}

private class Placed(val cat: Cat, val bench: Int, val slot: Int)

@Composable
fun BoardView(vm: GameViewModel, time: State<Long>, modifier: Modifier = Modifier) {
    val level = vm.level ?: return
    val board = vm.board
    val haptic = LocalHapticFeedback.current
    val tm = rememberTextMeasurer()
    val particles = remember { ParticleSystem() }
    val shakes = remember { mutableStateMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val hops = remember { mutableStateMapOf<Int, Long>() }
    var hint by remember { mutableStateOf<Triple<Int, Int, Long>?>(null) }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier) {
        val dp = LocalDensity.current.density
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val layout = remember(board.benches.size, w, h) { computeLayout(board.benches.size, w, h, dp) }
        val layoutNow by rememberUpdatedState(layout)
        val s = layout.s

        LaunchedEffect(vm) {
            vm.events.collect { e ->
                when (e) {
                    is GameEvent.Shake -> {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        scope.launch {
                            val a = shakes.getOrPut(e.bench) { Animatable(0f) }
                            a.snapTo(0f)
                            a.animateTo(0f, keyframes {
                                durationMillis = 380
                                -12f * dp at 50
                                10f * dp at 130
                                -7f * dp at 210
                                4f * dp at 290
                            })
                        }
                    }
                    is GameEvent.Hint -> hint = Triple(e.from, e.to, time.value)
                    is GameEvent.BenchDone -> {
                        hops[e.bench] = time.value + 420
                        scope.launch {
                            delay(420)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val g = layoutNow.benches.getOrNull(e.bench) ?: return@launch
                            particles.celebrate(Offset(g.catsLeft + 2 * layoutNow.s, g.catTop + .2f * layoutNow.s), 3.4f * layoutNow.s, time.value, dp)
                        }
                    }
                    GameEvent.Picked -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    else -> {}
                }
            }
        }
        LaunchedEffect(Unit) { while (true) { delay(1000); particles.prune(time.value) } }

        val tapModifier = Modifier.fillMaxSize().pointerInput(vm) {
            detectTapGestures { pos ->
                val i = layoutNow.benches.indexOfFirst { it.hit.contains(pos) }
                if (i >= 0) vm.tap(i)
            }
        }

        Canvas(tapModifier) {
            val now = time.value
            val pulse = .5f + .5f * sin(now / 170f)
            val hnt = hint?.takeIf { now - it.third < 2000 }
            layout.benches.forEachIndexed { i, g ->
                val bench = board.benches.getOrNull(i) ?: return@forEachIndexed
                val (glow, a) = when {
                    vm.selected == i -> Accent to (.75f + .25f * pulse)
                    hnt != null && (hnt.first == i || hnt.second == i) -> Gold to pulse
                    vm.isDone(bench) -> Good to .85f
                    else -> null to 0f
                }
                translate(shakes[i]?.value ?: 0f, 0f) { drawBench(g, s, dp, glow, a, "${i + 1}", tm, w) }
            }
        }

        val placed = buildList {
            board.benches.forEachIndexed { i, t -> t.forEachIndexed { k, c -> add(Placed(c, i, k)) } }
        }.sortedBy { it.cat.id }
        val hntNow = hint
        key(level.number) {
            for (p in placed) {
                key(p.cat.id) {
                    val bench = board.benches[p.bench]
                    val lifted = vm.selected == p.bench && p.slot >= bench.size - vm.groupSize(bench)
                    val sleeping = vm.isHidden(p.cat)
                    val hinted = hntNow != null && hntNow.first == p.bench && p.slot == bench.lastIndex
                    CatSprite(
                        breed = BREEDS[level.breeds[p.cat.color]],
                        target = layout.slot(p.bench, p.slot),
                        s = s,
                        lifted = lifted,
                        happy = vm.isDone(bench),
                        sleeping = sleeping,
                        mirrored = layout.benches[p.bench].mirrored,
                        entryIndex = p.cat.id,
                        moveIndex = vm.lastMoved.indexOf(p.cat.id),
                        slot = p.slot,
                        phase = ((p.cat.id * 7919) % 1000) / 1000f,
                        time = time,
                        hintStart = if (hinted) hntNow!!.third else -1L,
                        shake = { shakes[p.bench]?.value ?: 0f },
                        hopStart = { hops[p.bench] ?: -1L },
                        onLand = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                    )
                }
            }
        }

        Canvas(Modifier.fillMaxSize()) { particles.draw(this, time.value) }
    }
}

@Composable
private fun CatSprite(
    breed: Breed, target: Offset, s: Float, lifted: Boolean, happy: Boolean, sleeping: Boolean, mirrored: Boolean,
    entryIndex: Int, moveIndex: Int, slot: Int, phase: Float, time: State<Long>, hintStart: Long,
    shake: () -> Float, hopStart: () -> Long, onLand: () -> Unit,
) {
    val density = LocalDensity.current
    val wDp = with(density) { s.toDp() }
    val hDp = with(density) { (s * CAT_H / CAT_W).toDp() }
    // W podglądzie (Paparazzi, Android Studio) koty od razu siedzą na miejscach.
    val preview = LocalInspectionMode.current
    var pos by remember { mutableStateOf(if (preview) target else Offset(target.x, target.y - s * 10f)) }
    var entered by remember { mutableStateOf(preview) }
    val squash = remember { Animatable(1f) }
    val wake = remember { Animatable(1f) }
    var wasSleeping by remember { mutableStateOf(sleeping) }
    val land by rememberUpdatedState(onLand)

    LaunchedEffect(target) {
        val from = pos
        if ((target - from).getDistance() < .5f) { entered = true; return@LaunchedEffect }
        if (!entered) {
            // wejście na start poziomu: koty spadają z góry kaskadą i odbijają się od deski
            delay(150L + entryIndex * 30L)
            val a = Animatable(0f)
            a.animateTo(1f, spring(dampingRatio = .55f, stiffness = 240f)) { pos = Offset(lerpF(from.x, target.x, value), lerpF(from.y, target.y, value)) }
            entered = true
            squash.animateTo(.82f, tween(60)); squash.animateTo(1f, spring(.32f, 650f))
            return@LaunchedEffect
        }
        val d = (target - from).getDistance()
        val arc = if (d > s * .8f) min(s * 2.8f, s * .8f + d * .32f) else 0f
        if (arc > 0f) delay(moveIndex.coerceAtLeast(0) * 65L)
        val a = Animatable(0f)
        a.animateTo(1f, tween(if (arc > 0f) 500 else 220, easing = FastOutSlowInEasing)) {
            val v = value
            pos = Offset(lerpF(from.x, target.x, v), lerpF(from.y, target.y, v) - arc * 4f * v * (1f - v))
        }
        pos = target
        if (arc > 0f) {
            land()
            squash.snapTo(1f)
            squash.animateTo(.74f, tween(70))
            squash.animateTo(1f, spring(.28f, 700f))
        }
    }
    LaunchedEffect(sleeping) {
        if (wasSleeping && !sleeping) { wake.snapTo(.35f); wake.animateTo(1f, spring(.38f, 420f)) }
        wasSleeping = sleeping
    }
    val lift by animateFloatAsState(if (lifted) -s * .38f else 0f, spring(.42f, 480f), label = "lift")

    Canvas(
        Modifier
            .offset { IntOffset((pos.x + shake()).roundToInt(), (pos.y + lift).roundToInt()) }
            .size(wDp, hDp)
            .graphicsLayer {
                val now = time.value
                val t = now / 1000f
                transformOrigin = TransformOrigin(.5f, 1f)
                val breathe = 1f + .02f * sin(t * 2.4f + phase * 6.28f)
                val sq = squash.value
                scaleY = sq * breathe * wake.value
                scaleX = (2f - sq) * wake.value * (if (mirrored) -1f else 1f)
                var ty = 0f
                var rot = 0f
                if (lifted) { ty += sin(t * 5f + phase * 6f) * s * .035f; rot = sin(t * 6f + phase * 5f) * 3.5f }
                val hs = hopStart()
                if (happy && hs > 0) {
                    val age = now - hs - slot * 80
                    if (age in 0..560) ty -= sin(age / 560f * PI).toFloat() * s * .4f
                }
                if (hintStart > 0) {
                    val age = now - hintStart
                    if (age in 0..1500) ty -= abs(sin(age / 250f * PI)).toFloat() * s * .16f * (1f - age / 1500f)
                }
                translationY = ty
                rotationZ = rot
            }
    ) {
        val t = time.value / 1000f
        if (sleeping) {
            drawSleeper(t)
        } else {
            val period = 3.4f + phase * 3.2f
            val bt = (t + phase * 11f) % period
            val blink = if (bt < .16f) 1f - abs(bt - .08f) / .08f else 0f
            drawCat(breed, happy, blink, sin(t * 1.8f + phase * 9f) * 9f)
        }
    }
}
