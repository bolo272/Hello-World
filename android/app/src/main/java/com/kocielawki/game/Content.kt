package com.kocielawki.game

import androidx.compose.ui.graphics.Color

enum class Mark { STRIPES, NONE, BLAZE, MASK, PATCHES, TABBY, WRINKLES, SPOTS }

data class Breed(
    val name: String,
    val fur: Color, val shade: Color, val belly: Color, val ear: Color,
    val eyeL: Color, val eyeR: Color, val nose: Color, val line: Color, val whisker: Color,
    val collar: Color, val mark: Mark,
    val patchA: Color = Color.Transparent, val patchB: Color = Color.Transparent,
)

private fun c(hex: Long) = Color(0xFF000000 or hex)

/** Dziewięć kolorowych kotów. Każdy ma też własny wzór i obrożę, żeby nie rozróżniać ich tylko po kolorze. */
val BREEDS = listOf(
    Breed("Zielony", c(0x5ACD63), c(0x2A9A43), c(0xD2F7CC), c(0xF7A8B8), c(0x2E7DE0), c(0x2E7DE0), c(0xE8708A), c(0x1F4D26), c(0x1F4D26), c(0xFF5A8A), Mark.STRIPES),
    Breed("Czarny", c(0x2A2A36), c(0x16161E), c(0x3E3E50), c(0x8A5A72), c(0xFFD23A), c(0xFFD23A), c(0x7A4A62), c(0xC9C2D8), c(0xD8D2E6), c(0xFF4D4D), Mark.NONE),
    Breed("Żółty", c(0xFFD03A), c(0xE39A00), c(0xFFF3BC), c(0xFFA9A0), c(0x3B8BEB), c(0x3B8BEB), c(0xF07A6A), c(0x6B4700), c(0x6B4700), c(0x7A4FE0), Mark.NONE),
    Breed("Niebieski", c(0x4A7BF0), c(0x2A4FB8), c(0xD4E2FF), c(0xF2A6C8), c(0xFFD23A), c(0xFFD23A), c(0xF07A9A), c(0x152A66), c(0x152A66), c(0xFFB020), Mark.BLAZE),
    Breed("Fioletowy", c(0xA56AEA), c(0x6A36B4), c(0xEEDDFF), c(0xF4A6D0), c(0x3FD0B0), c(0x3FD0B0), c(0x3A1D66), c(0x2F1658), c(0x2F1658), c(0x2EC4A0), Mark.MASK),
    Breed("Różowy", c(0xFF7EB8), c(0xD2468A), c(0xFFE3F0), c(0xFFC2DC), c(0x7B4DE0), c(0x7B4DE0), c(0xB0306A), c(0x6A1640), c(0x6A1640), c(0x2E9BF0), Mark.PATCHES, c(0xFFC4DE), c(0xC2367A)),
    Breed("Czerwony", c(0xF0443A), c(0xAE2017), c(0xFFCDC6), c(0xFFB0A8), c(0xFFD23A), c(0xFFD23A), c(0x7A1410), c(0x5A0E0A), c(0x5A0E0A), c(0x30C46A), Mark.TABBY),
    Breed("Brązowy", c(0x9C6440), c(0x5C381E), c(0xE6C6A6), c(0xE8A08C), c(0x7CC63E), c(0x7CC63E), c(0x5A2E1A), c(0x3A2010), c(0x3A2010), c(0xFF8A2A), Mark.WRINKLES),
    Breed("Turkusowy", c(0x28C2C6), c(0x12858C), c(0xC8F4F2), c(0xF7A8C0), c(0xFF8A2A), c(0xFF8A2A), c(0xE06A8A), c(0x0B4448), c(0x0B4448), c(0xE8457A), Mark.SPOTS),
)

val FACTS = listOf(
    "Prawie wszystkie koty szylkretowe to kotki. Geny rudego i czarnego futra leżą na chromosomie X.",
    "Koty nie czują smaku słodkiego. Brakuje im działającego receptora, który go wykrywa.",
    "Mruczenie ma częstotliwość mniej więcej 25–150 Hz.",
    "Kocięta syjamskie rodzą się prawie białe. Ciemne uszy i pyszczek pojawiają się z wiekiem.",
    "W każdym kocim uchu pracują 32 mięśnie, więc kot obraca uszy niezależnie od siebie.",
    "Dorosłe koty miauczą głównie do ludzi. Między sobą częściej porozumiewają się zapachem i mową ciała.",
    "Kot zwykle przesypia 12–16 godzin na dobę.",
    "Wzór na kocim nosie jest niepowtarzalny, trochę jak odcisk palca u człowieka.",
    "Wśród rudych kotów zdecydowaną większość stanowią kocury.",
    "Kot widzi w półmroku dużo lepiej od nas. Za oczami ma warstwę, która odbija światło.",
    "Wąsy kota są mniej więcej tak szerokie jak jego ciało, więc działają jak miarka przy przeciskaniu się przez szpary.",
    "Sfinksy nie są całkiem łyse. Pokrywa je delikatny meszek, w dotyku przypominający zamsz.",
)
