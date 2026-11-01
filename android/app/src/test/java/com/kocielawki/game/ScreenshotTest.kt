package com.kocielawki.game

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.kocielawki.game.logic.solve
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import com.kocielawki.game.ui.KocieLawkiApp
import org.junit.Rule
import org.junit.Test

/** Zrzuty ekranu bez emulatora: ./gradlew recordPaparazziDebug */
class ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, maxPercentDifference = 1.0)

    /** Tryb podglądu: koty od razu siedzą na miejscach, okna są w pełni widoczne. */
    private fun shot(content: @Composable () -> Unit) =
        paparazzi.snapshot { CompositionLocalProvider(LocalInspectionMode provides true) { content() } }

    private fun game(level: Int, tips: Boolean = false) = GameViewModel(MemoryStore().apply {
        put("max", level); put("level", level)
        if (!tips) { put("tip-rules", true); put("tip-sleepy", true) }
    })

    @Test fun level1WithTip() = shot { KocieLawkiApp(game(1, tips = true)) }

    @Test fun level7Selected() {
        val vm = game(7)
        vm.tap(0)
        shot { KocieLawkiApp(vm) }
    }

    @Test fun level12Sleepy() = shot { KocieLawkiApp(game(12)) }

    @Test fun level15() = shot { KocieLawkiApp(game(15)) }

    @Test fun winScreen() {
        val vm = game(3)
        val path = solve(vm.board.benches.map { b -> b.map { it.color } })!!
        for (m in path) { vm.tap(m.from); vm.tap(m.to) }
        shot { KocieLawkiApp(vm) }
    }
}
