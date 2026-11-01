package com.kocielawki.game

import com.kocielawki.game.logic.buildLevel
import com.kocielawki.game.logic.solve
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LogicTest {
    @Test
    fun everyLevelIsSolvable() {
        for (n in 1..30) {
            val level = buildLevel(n)
            assertEquals(level.config.colors + level.config.empty, level.benches.size)
            assertNotNull("level $n", solve(level.benches.map { b -> b.map { it.color } }))
        }
    }
}
