package com.kocielawki.game

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kocielawki.game.logic.CAP
import com.kocielawki.game.logic.Cat
import com.kocielawki.game.logic.Level
import com.kocielawki.game.logic.buildLevel
import com.kocielawki.game.logic.solve
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Board(val benches: List<List<Cat>>, val moves: Int, val extraUsed: Boolean)

data class WinResult(val level: Int, val moves: Int, val best: Int?, val newRecord: Boolean, val fact: String)

sealed interface GameEvent {
    data class Shake(val bench: Int) : GameEvent
    data class Hint(val from: Int, val to: Int) : GameEvent
    data class BenchDone(val bench: Int) : GameEvent
    data class Toast(val text: String) : GameEvent
    data object Picked : GameEvent
    data object Landed : GameEvent
    data object Won : GameEvent
}

/** Zapis postępu. Na telefonie SharedPreferences, w testach pamięć. */
interface Store {
    fun int(key: String, def: Int): Int
    fun bool(key: String): Boolean
    fun put(key: String, value: Int)
    fun put(key: String, value: Boolean)
}

class PrefsStore(context: Context) : Store {
    private val p = context.getSharedPreferences("kocie-lawki", 0)
    override fun int(key: String, def: Int) = p.getInt(key, def)
    override fun bool(key: String) = p.getBoolean(key, false)
    override fun put(key: String, value: Int) = p.edit().putInt(key, value).apply()
    override fun put(key: String, value: Boolean) = p.edit().putBoolean(key, value).apply()
}

class MemoryStore : Store {
    private val m = HashMap<String, Any>()
    override fun int(key: String, def: Int) = m[key] as? Int ?: def
    override fun bool(key: String) = m[key] as? Boolean ?: false
    override fun put(key: String, value: Int) { m[key] = value }
    override fun put(key: String, value: Boolean) { m[key] = value }
}

class GameViewModel(private val prefs: Store) : ViewModel() {

    var maxLevel by mutableIntStateOf(prefs.int("max", 1)); private set
    var level by mutableStateOf<Level?>(null); private set
    var board by mutableStateOf(Board(emptyList(), 0, false)); private set
    var revealed by mutableStateOf(emptySet<Int>()); private set
    var selected by mutableStateOf<Int?>(null); private set
    var lastMoved by mutableStateOf(emptyList<Int>()); private set
    var result by mutableStateOf<WinResult?>(null); private set
    var tip by mutableStateOf<Tip?>(null); private set
    var historySize by mutableIntStateOf(0); private set

    private val history = ArrayDeque<Board>()
    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<GameEvent> = _events
    private var mismatchToasts = 0

    enum class Tip { RULES, SLEEPY }

    init {
        start(prefs.int("level", 1).coerceIn(1, maxLevel))
    }

    val won get() = result != null

    fun isHidden(cat: Cat) = cat.sleepy && cat.id !in revealed
    fun isDone(t: List<Cat>) = t.size == CAP && t.all { it.color == t[0].color }
    fun breedOf(cat: Cat) = BREEDS[level!!.breeds[cat.color]]

    fun groupSize(t: List<Cat>): Int {
        if (t.isEmpty()) return 0
        val c = t.last().color
        var n = 0
        for (k in t.indices.reversed()) {
            if (t[k].color != c || isHidden(t[k])) break
            n++
        }
        return n
    }

    /** Budowa poziomu trwa kilka milisekund, więc robimy ją od razu. */
    fun start(n: Int) {
        run {
            val lvl = buildLevel(n)
            level = lvl
            history.clear(); historySize = 0
            board = Board(lvl.benches, 0, false)
            revealed = emptySet()
            selected = null
            lastMoved = emptyList()
            result = null
            revealTops()
            prefs.put("level", n)
            tip = when {
                n <= 2 && !prefs.bool("tip-rules") -> Tip.RULES
                lvl.config.sleepy && !prefs.bool("tip-sleepy") -> Tip.SLEEPY
                else -> null
            }
        }
    }

    fun dismissTip() {
        when (tip) {
            Tip.RULES -> prefs.put("tip-rules", true)
            Tip.SLEEPY -> prefs.put("tip-sleepy", true)
            null -> {}
        }
        tip = null
    }

    private fun revealTops() {
        val r = revealed.toMutableSet()
        for (t in board.benches) {
            if (t.isEmpty()) continue
            r += t.last().id
            if (isDone(t)) t.forEach { r += it.id }
        }
        revealed = r
    }

    private fun canMove(from: Int, to: Int): String {
        val s = board.benches[from]
        val d = board.benches[to]
        return when {
            s.isEmpty() -> "empty"
            d.size >= CAP -> "full"
            d.isNotEmpty() && d.last().color != s.last().color -> "mismatch"
            else -> "ok"
        }
    }

    fun tap(i: Int) {
        if (won || level == null || i !in board.benches.indices) return
        val t = board.benches[i]
        val sel = selected
        if (sel == null) {
            when {
                t.isEmpty() -> emit(GameEvent.Shake(i))
                isDone(t) -> { emit(GameEvent.Shake(i)); emit(GameEvent.Toast("Te koty są już w komplecie.")) }
                else -> { selected = i; emit(GameEvent.Picked) }
            }
            return
        }
        if (sel == i) { selected = null; return }
        when (canMove(sel, i)) {
            "ok" -> move(sel, i)
            "full" -> { selected = null; emit(GameEvent.Shake(i)); emit(GameEvent.Toast("Na tej ławce nie ma już miejsca.")) }
            else -> {
                selected = null
                emit(GameEvent.Shake(i))
                if (mismatchToasts++ < 3) emit(GameEvent.Toast("Kot przysiada się tylko do kota swojej rasy albo na pustą ławkę."))
            }
        }
    }

    private fun move(from: Int, to: Int) {
        val benches = board.benches.map { it.toMutableList() }
        val s = benches[from]
        val d = benches[to]
        val n = minOf(groupSize(s), CAP - d.size)
        history.addLast(board); historySize = history.size
        val moved = s.subList(s.size - n, s.size).toList()
        repeat(n) { s.removeAt(s.lastIndex) }
        d.addAll(moved)
        lastMoved = moved.map { it.id }
        board = Board(benches, board.moves + 1, board.extraUsed)
        selected = null
        revealTops()
        if (isDone(d)) emit(GameEvent.BenchDone(to))
        if (board.benches.all { it.isEmpty() || isDone(it) }) {
            win()
        } else if (!anyMove()) {
            emit(GameEvent.Toast("Brak ruchów. Cofnij albo dostaw ławkę."))
        }
    }

    private fun anyMove(): Boolean {
        for (i in board.benches.indices) {
            if (isDone(board.benches[i])) continue
            for (j in board.benches.indices) if (i != j && canMove(i, j) == "ok") return true
        }
        return false
    }

    private fun win() {
        val lvl = level!!.number
        val prev = prefs.int("best-$lvl", 0).takeIf { it > 0 }
        val record = prev == null || board.moves < prev
        if (record) prefs.put("best-$lvl", board.moves)
        maxLevel = maxOf(maxLevel, lvl + 1)
        prefs.put("max", maxLevel)
        result = WinResult(lvl, board.moves, prev, record, FACTS[(lvl - 1) % FACTS.size])
        emit(GameEvent.Won)
    }

    fun undo() {
        if (history.isEmpty() || won) return
        board = history.removeLast(); historySize = history.size
        selected = null
        lastMoved = emptyList()
    }

    fun restart() {
        val lvl = level ?: return
        if (won) return
        history.clear(); historySize = 0
        board = Board(lvl.benches, 0, false)
        revealed = emptySet()
        selected = null
        lastMoved = emptyList()
        revealTops()
    }

    fun addBench() {
        if (won || level == null) return
        if (board.extraUsed) { emit(GameEvent.Toast("Dodatkowa ławka już stoi.")); return }
        history.addLast(board); historySize = history.size
        board = board.copy(benches = board.benches + listOf(emptyList()), extraUsed = true)
        selected = null
    }

    fun hint() {
        if (won || level == null) return
        val colors = board.benches.map { t -> t.map { it.color } }
        viewModelScope.launch {
            val path = withContext(Dispatchers.Default) { solve(colors) }
            when {
                path == null -> emit(GameEvent.Toast("Stąd nie widać wyjścia. Cofnij kilka ruchów albo dostaw ławkę."))
                path.isNotEmpty() -> { selected = null; emit(GameEvent.Hint(path[0].from, path[0].to)) }
            }
        }
    }

    fun next() { level?.let { start(it.number + 1) } }
    fun prev() { level?.let { if (it.number > 1) start(it.number - 1) } }
    fun goNext() { level?.let { if (it.number < maxLevel) start(it.number + 1) } }

    private fun emit(e: GameEvent) { _events.tryEmit(e) }
}
