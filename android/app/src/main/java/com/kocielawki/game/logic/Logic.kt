package com.kocielawki.game.logic

/** Ile kotów mieści się na jednej ławce. */
const val CAP = 4

data class Cat(val id: Int, val color: Int, val sleepy: Boolean)

data class LevelConfig(val colors: Int, val empty: Int, val sleepy: Boolean)

data class Move(val from: Int, val to: Int, val n: Int)

class Level(val number: Int, val config: LevelConfig, val breeds: List<Int>, val benches: List<List<Cat>>)

/** mulberry32 — ten sam generator co w wersji przeglądarkowej, więc poziomy są identyczne. */
class Rng(seed: Int) {
    private var a = seed

    fun next(): Double {
        a += 0x6D2B79F5
        var t = a
        t = (t xor (t ushr 15)) * (t or 1)
        t = t xor (t + (t xor (t ushr 7)) * (t or 61))
        return ((t xor (t ushr 14)).toLong() and 0xFFFFFFFFL).toDouble() / 4294967296.0
    }
}

fun <T> MutableList<T>.shuffleWith(rng: Rng): MutableList<T> {
    for (i in lastIndex downTo 1) {
        val j = (rng.next() * (i + 1)).toInt()
        val t = this[i]; this[i] = this[j]; this[j] = t
    }
    return this
}

fun levelConfig(n: Int) = LevelConfig(
    colors = minOf(9, 3 + (n - 1) / 2),
    empty = 2,
    sleepy = n >= 6 && n % 3 == 0,
)

/** DFS z zapamiętywaniem stanów. Zwraca listę ruchów albo null, gdy nie znajdzie rozwiązania w limicie. */
fun solve(start: List<List<Int>>, cap: Int = CAP, limit: Int = 60_000): List<Move>? {
    val tubes = start.map { it.toMutableList() }
    val seen = HashSet<String>()
    val path = ArrayList<Move>()
    var nodes = 0
    fun full(t: List<Int>) = t.size == cap && t.all { it == t[0] }
    fun solved() = tubes.all { it.isEmpty() || full(it) }
    fun key() = tubes.map { it.joinToString("") }.sorted().joinToString("|")

    fun moves(): List<Move> {
        val res = ArrayList<Pair<Move, Int>>()
        for (i in tubes.indices) {
            val s = tubes[i]
            if (s.isEmpty() || full(s)) continue
            val c = s.last()
            var run = 1
            while (run < s.size && s[s.size - 1 - run] == c) run++
            val uniform = run == s.size
            var firstEmpty = -1
            for (j in tubes.indices) {
                if (i == j) continue
                val d = tubes[j]
                if (d.isEmpty()) {
                    if (!uniform && firstEmpty < 0) firstEmpty = j
                    continue
                }
                if (d.size < cap && d.last() == c) {
                    val n = minOf(run, cap - d.size)
                    val dUniform = d.all { it == c }
                    if (uniform && dUniform && s.size > d.size) continue
                    val score = (if (n == run) 2 else 0) + (if (dUniform) 3 else 0) + (if (uniform) 0 else 1)
                    res += Move(i, j, n) to score
                }
            }
            if (firstEmpty >= 0) res += Move(i, firstEmpty, run) to -1
        }
        return res.sortedByDescending { it.second }.map { it.first }
    }

    fun dfs(): Boolean {
        if (solved()) return true
        if (++nodes > limit) return false
        if (!seen.add(key())) return false
        for (m in moves()) {
            val s = tubes[m.from]
            val d = tubes[m.to]
            repeat(m.n) { d.add(s.removeAt(s.lastIndex)) }
            path.add(m)
            if (dfs()) return true
            path.removeAt(path.lastIndex)
            repeat(m.n) { s.add(d.removeAt(d.lastIndex)) }
        }
        return false
    }
    return if (dfs()) path.toList() else null
}

fun buildLevel(n: Int): Level {
    val cfg = levelConfig(n)
    val rng = Rng(n * 7919 + 104729)
    val breeds = (0 until BREED_COUNT).toMutableList().shuffleWith(rng).take(cfg.colors)
    var deal: List<List<Int>> = emptyList()
    for (attempt in 0 until 40) {
        val pool = MutableList(cfg.colors * CAP) { it / CAP }.shuffleWith(rng)
        val tubes = (0 until cfg.colors).map { pool.subList(it * CAP, it * CAP + CAP).toList() }
        if (tubes.any { t -> t.all { it == t[0] } }) continue
        deal = tubes + List(cfg.empty) { emptyList() }
        if (solve(deal, CAP, 40_000) != null) break
    }
    var nextId = 0
    val benches = deal.map { t -> t.mapIndexed { k, c -> Cat(nextId++, c, cfg.sleepy && k < t.size - 1) } }
    return Level(n, cfg, breeds, benches)
}

const val BREED_COUNT = 9
