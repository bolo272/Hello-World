package com.kocielawki.game.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kocielawki.game.BREEDS
import com.kocielawki.game.GameEvent
import com.kocielawki.game.GameViewModel
import com.kocielawki.game.R
import kotlinx.coroutines.delay
import kotlin.math.sin

val Display = FontFamily(Font(R.font.bagel_fat_one))
val Body = FontFamily(
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black),
)
val Accent = Color(0xFFFF5C9A)
val Gold = Color(0xFFFFD23A)
val Good = Color(0xFF3EE08F)
private val Ink = Color(0xFF23264A)
private val SkyTop = Color(0xFF2A3FAE)
private val SkyBottom = Color(0xFF4B79EE)
private val TextShadow = Shadow(Color(0x66000000), Offset(0f, 4f), 6f)

private fun strokeIcon(name: String, vararg paths: String) =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        for (p in paths) addPath(addPathNodes(p), stroke = SolidColor(Color.White), strokeLineWidth = 2.6f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
    }.build()

private val IconUndo = strokeIcon("undo", "M9 14 4 9l5-5", "M4 9h10.5a5.5 5.5 0 0 1 0 11H11")
private val IconRestart = strokeIcon("restart", "M3 12a9 9 0 1 0 3-6.7L3 8", "M3 3v5h5")
private val IconBench = strokeIcon("bench", "M2 11h20", "M5 11v8M19 11v8", "M12 3v5M9.5 5.5h5")
private val IconHint = strokeIcon("hint", "M9 18h6M10 21.5h4", "M12 2.5a6.5 6.5 0 0 0-3.8 11.8c.6.5.8 1.2.8 1.9V16h6v-.2c0-.7.3-1.4.8-1.9A6.5 6.5 0 0 0 12 2.5z")
private val IconPrev = strokeIcon("prev", "m15 18-6-6 6-6")
private val IconNext = strokeIcon("next", "m9 18 6-6-6-6")

/** Czas animacji w ms; czytany tylko w fazie rysowania, więc nie wymusza rekompozycji. */
@Composable
fun rememberFrameTime(): State<Long> = produceState(0L) {
    val start = withFrameMillis { it }
    while (true) withFrameMillis { value = it - start }
}

@Composable
fun KocieLawkiApp(vm: GameViewModel) {
    val time = rememberFrameTime()
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(SkyTop, SkyBottom)))) {
        Backdrop(time)
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.statusBarsPadding()) { TopBar(vm) }
            AnimatedVisibility(vm.tip != null) { TipCard(vm.tip) { vm.dismissTip() } }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                BoardView(vm, time, Modifier.fillMaxSize().padding(vertical = 8.dp))
                ToastHost(vm, Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp))
            }
            BottomBar(vm)
        }
        if (vm.result != null) WinOverlay(vm, time)
    }
}

@Composable
private fun Backdrop(time: State<Long>) {
    Canvas(Modifier.fillMaxSize()) {
        val t = time.value / 1000f
        // miękkie, powoli dryfujące światła w tle
        for (i in 0 until 9) {
            val x = size.width * ((i * .37f + .1f + .03f * sin(t * .2f + i)) % 1f)
            val y = size.height * ((i * .23f + .05f) % .8f) + 18f * sin(t * .35f + i * 1.7f)
            val r = size.minDimension * (.08f + (i % 3) * .05f)
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = .09f), Color.Transparent), Offset(x, y), r), r, Offset(x, y))
        }
        // drobne gwiazdki
        for (i in 0 until 26) {
            val x = size.width * ((i * .618f) % 1f)
            val y = size.height * .7f * ((i * .371f) % 1f)
            val a = .25f + .25f * sin(t * 1.3f + i * 2.1f)
            drawCircle(Color.White.copy(alpha = a), 1.6f * density, Offset(x, y))
        }
    }
}

@Composable
private fun Pill(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .shadow(6.dp, RoundedCornerShape(50), ambientColor = Color(0x55000000))
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8EC), Color(0xFFF3E3C8))), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun TopBar(vm: GameViewModel) {
    val level = vm.level
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Kocie Ławki", fontFamily = Display, fontSize = 30.sp, color = Color.White, style = TextStyle(shadow = TextShadow))
            Spacer(Modifier.weight(1f))
            Pill {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("RUCHY ", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFF8A6A3A), letterSpacing = 1.sp)
                    AnimatedContent(vm.board.moves, transitionSpec = {
                        (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                    }, label = "moves") { m ->
                        Text("$m", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Ink)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Pill {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val n = level?.number ?: 1
                    Icon(IconPrev, "Poprzedni poziom", tint = if (n > 1) Ink else Ink.copy(alpha = .25f),
                        modifier = Modifier.size(28.dp).clickable(enabled = n > 1) { vm.prev() })
                    Text("  POZIOM ", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF8A6A3A), letterSpacing = 1.sp)
                    Text("$n  ", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 22.sp, color = Ink)
                    Icon(IconNext, "Następny poziom", tint = if (n < vm.maxLevel) Ink else Ink.copy(alpha = .25f),
                        modifier = Modifier.size(28.dp).clickable(enabled = n < vm.maxLevel) { vm.goNext() })
                }
            }
            if (level?.config?.sleepy == true) {
                Spacer(Modifier.size(8.dp))
                Box(Modifier.background(Color(0xFF8F7FD0), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("Śpiochy", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TipCard(tip: GameViewModel.Tip?, onOk: () -> Unit) {
    val text = buildAnnotatedString {
        val b = SpanStyle(fontWeight = FontWeight.Black, color = Ink)
        if (tip == GameViewModel.Tip.SLEEPY) {
            withStyle(b) { append("Śpiochy! ") }
            append("Część kotów drzemie pod kocykami. Budzą się, gdy siedzą na wewnętrznym końcu ławki.")
        } else {
            withStyle(b) { append("Stuknij ławkę") }
            append(", żeby podnieść koty od strony środka, potem ")
            withStyle(b) { append("stuknij drugą") }
            append(". Kot przysiądzie się tylko do kota tej samej rasy albo na pustą ławkę. Cel: ")
            withStyle(b) { append("cztery takie same koty na każdej ławce") }
            append(".")
        }
    }
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 6.dp).fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(18.dp)).background(Color(0xFFFFFBF3), RoundedCornerShape(18.dp))
            .padding(start = 14.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, Modifier.weight(1f), fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF5D6293), lineHeight = 18.sp)
        Spacer(Modifier.size(8.dp))
        Box(Modifier.background(Color(0xFFFFE7B0), RoundedCornerShape(12.dp)).clickable(onClick = onOk).padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text("Jasne", fontFamily = Body, fontWeight = FontWeight.Black, color = Ink)
        }
    }
}

@Composable
private fun ChunkyButton(icon: ImageVector, label: String, enabled: Boolean, badge: String? = null, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .88f else 1f, spring(.4f, 700f), label = "press")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; alpha = if (enabled) 1f else .45f }
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
    ) {
        Box(Modifier.size(66.dp, 64.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val edge = 6.dp.toPx()
                val r = CornerRadius(20.dp.toPx())
                val faceY = if (pressed) edge * .7f else 0f
                drawRoundRect(Color(0x33000000), Offset(0f, edge + 3.dp.toPx()), Size(size.width, size.height - edge), r)
                drawRoundRect(Color(0xFF4A23A8), Offset(0f, edge), Size(size.width, size.height - edge), r)
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFB48CFF), Color(0xFF7A48EA)), faceY, faceY + size.height - edge),
                    Offset(0f, faceY), Size(size.width, size.height - edge), r)
                drawRoundRect(Color.White.copy(alpha = .28f), Offset(7.dp.toPx(), faceY + 4.dp.toPx()),
                    Size(size.width - 14.dp.toPx(), (size.height - edge) * .36f), CornerRadius(14.dp.toPx()))
            }
            Icon(icon, null, tint = Color.White, modifier = Modifier.align(Alignment.Center).offset(y = if (pressed) 0.dp else (-3).dp).size(30.dp))
            if (badge != null) {
                Box(
                    Modifier.align(Alignment.TopEnd).offset(6.dp, (-6).dp).size(24.dp)
                        .shadow(3.dp, CircleShape).background(Accent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text(badge, fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White) }
            }
        }
        Text(label, fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.White, style = TextStyle(shadow = TextShadow))
    }
}

@Composable
private fun BottomBar(vm: GameViewModel) {
    val busy = vm.won || vm.level == null
    Box(
        Modifier.fillMaxWidth().drawBehind {
            // drewniana podłoga z deskami
            drawRect(Brush.verticalGradient(listOf(Color(0xFFC98A4E), Color(0xFF94592C))))
            drawRect(Color(0xFF6E3F1C).copy(alpha = .5f), Offset.Zero, Size(size.width, 4.dp.toPx()))
            var x = -20f
            var row = 0
            while (row < 4) {
                val y = size.height * row / 4f
                drawLine(Color(0x33000000), Offset(0f, y), Offset(size.width, y), 1.5f)
                x = (row * 97f) % 160f
                while (x < size.width) { drawLine(Color(0x26000000), Offset(x, y), Offset(x, y + size.height / 4f), 1.5f); x += 160f }
                row++
            }
        }.navigationBarsPadding().padding(top = 14.dp, bottom = 10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ChunkyButton(IconUndo, "Cofnij", vm.historySize > 0 && !busy) { vm.undo() }
            ChunkyButton(IconRestart, "Od nowa", !busy) { vm.restart() }
            ChunkyButton(IconBench, "Ławka +1", !vm.board.extraUsed && !busy, if (vm.board.extraUsed) "0" else "1") { vm.addBench() }
            ChunkyButton(IconHint, "Podpowiedź", !busy) { vm.hint() }
        }
    }
}

@Composable
private fun ToastHost(vm: GameViewModel, modifier: Modifier) {
    var text by remember { mutableStateOf("") }
    var id by remember { mutableIntStateOf(0) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(vm) {
        vm.events.collect { e -> if (e is GameEvent.Toast) { text = e.text; id++ } }
    }
    LaunchedEffect(id) {
        if (id == 0) return@LaunchedEffect
        visible = true
        delay(2600)
        visible = false
    }
    AnimatedVisibility(visible, modifier, enter = slideInVertically { it } + fadeIn(), exit = fadeOut()) {
        Box(
            Modifier.padding(horizontal = 24.dp).widthIn(max = 420.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp)).background(Ink, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 11.dp),
        ) { Text(text, fontFamily = Body, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center) }
    }
}

@Composable
private fun WinOverlay(vm: GameViewModel, time: State<Long>) {
    val res = vm.result ?: return
    val level = vm.level ?: return
    val preview = LocalInspectionMode.current
    val appear = remember(res) { Animatable(if (preview) 1f else 0f) }
    val confetti = remember(res) { ParticleSystem() }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = constraints.maxWidth.toFloat()
        val dp = LocalDensity.current.density
        LaunchedEffect(res) {
            delay(650)
            confetti.confetti(w, time.value, dp)
            appear.animateTo(1f, spring(.6f, 240f))
        }
        Box(
            Modifier.fillMaxSize()
                .graphicsLayer { alpha = appear.value.coerceIn(0f, 1f) }
                .background(Color(0xCC121638))
                .clickable(remember { MutableInteractionSource() }, indication = null) {},
        )
        Column(
            Modifier.align(Alignment.Center).padding(24.dp).widthIn(max = 380.dp)
                .graphicsLayer {
                    val a = appear.value
                    alpha = a.coerceIn(0f, 1f)
                    scaleX = .6f + .4f * a; scaleY = .6f + .4f * a
                    rotationZ = (1f - a) * -8f
                }
                .shadow(24.dp, RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFFFFBF3), Color(0xFFFFEFD2))), RoundedCornerShape(28.dp))
                .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val breed = BREEDS[level.breeds[res.level % level.breeds.size]]
            Canvas(Modifier.size(150.dp, 126.dp).graphicsLayer {
                val t = time.value / 1000f
                translationY = -kotlin.math.abs(sin(t * 3.2f)) * 14.dp.toPx()
                rotationZ = sin(t * 3.2f) * 4f
            }) { drawCat(breed, happy = true, tail = sin(time.value / 300f) * 14f) }
            Text("Wszystkie koty na swoich ławkach!", fontFamily = Display, fontSize = 25.sp, color = Ink, textAlign = TextAlign.Center, lineHeight = 29.sp)
            Text("Poziom ${res.level} ukończony", fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF6A6F9E), modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Stat("RUCHY", "${res.moves}", Ink)
                if (res.newRecord) Stat("NOWY REKORD", "★", Good) else Stat("REKORD", "${res.best}", Ink)
            }
            Column(Modifier.padding(top = 14.dp, bottom = 16.dp).fillMaxWidth().background(Color(0x14FF5C9A), RoundedCornerShape(16.dp)).padding(12.dp)) {
                Text("KOCIA CIEKAWOSTKA", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 11.sp, color = Accent, letterSpacing = 1.sp)
                Text(res.fact, fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink, lineHeight = 19.sp)
            }
            BigButton("Następny poziom") { vm.next() }
        }
        Canvas(Modifier.fillMaxSize()) { confetti.draw(this, time.value) }
    }
}

@Composable
private fun Stat(label: String, value: String, color: Color) {
    Row(
        Modifier.background(Color.White, RoundedCornerShape(50)).padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$label ", fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 11.sp, color = color.copy(alpha = .7f), letterSpacing = 1.sp)
        Text(value, fontFamily = Body, fontWeight = FontWeight.Black, fontSize = 19.sp, color = color)
    }
}

@Composable
private fun BigButton(label: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .94f else 1f, spring(.45f, 700f), label = "big")
    Box(
        Modifier.fillMaxWidth().height(58.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interaction, indication = null, onClick = onClick)
            .drawBehind {
                val edge = 6.dp.toPx()
                val r = CornerRadius(20.dp.toPx())
                val y = if (pressed) edge * .7f else 0f
                drawRoundRect(Color(0xFFB8295F), Offset(0f, edge), Size(size.width, size.height - edge), r)
                drawRoundRect(Brush.horizontalGradient(listOf(Color(0xFFFF6FA8), Color(0xFFFF8A4C))), Offset(0f, y), Size(size.width, size.height - edge), r)
                drawRoundRect(Color.White.copy(alpha = .25f), Offset(10.dp.toPx(), y + 4.dp.toPx()), Size(size.width - 20.dp.toPx(), (size.height - edge) * .38f), CornerRadius(14.dp.toPx()))
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontFamily = Display, fontSize = 21.sp, color = Color.White, style = TextStyle(shadow = TextShadow), modifier = Modifier.offset(y = if (pressed) 0.dp else (-3).dp))
    }
}
