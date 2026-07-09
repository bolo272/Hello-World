# THE HOLE — Game Design Document (v0.3, "a quiet digging toy")

A PC game about slowly, contentedly digging a hole.

**One-line pitch:** Tiny Glade, but downward.

---

## 1. Direction (pivoted in v0.3)

Earlier prototypes (v0.1–v0.2, see §8) were mobile tap-loops with an
economy, upgrades and hazard pressure. Playtesting verdict: however the
presentation was dressed, the *structure* still felt like a freemium
game. v0.3 discards that structure entirely and rebuilds on the design
values of Tiny Glade:

- **No fail states. No economy. No numbers to grow.** Nothing is won,
  nothing is lost, nothing asks to be optimised.
- **The craft itself is the game.** You carve — smoothly, with the
  mouse, like sculpting — and the result is a cross-section diorama
  that is yours.
- **The world answers with charm, not punishment.** Everything you do
  makes the scene *more alive*, never less.

## 2. The player verb

Hold the left mouse button and the earth softly erodes under a round
brush — a continuous, organic carve, not tile-clicking. Strata have
*texture* rather than gates: loam melts away, clay drags pleasantly,
sand pours, stone takes patient grinding (and rewards it with pebbles
that tumble and clatter). Right-click hangs a little lantern. Scroll
descends. That's the whole control surface.

## 3. The world answers

| You do | The world does |
|---|---|
| carve near the surface | roots emerge from the ceiling and sway; grass keeps waving at the rim |
| undermine sand | it trickles down in soft golden threads |
| grind through stone | pebbles pop out, bounce, settle, and become part of the floor |
| breach a spring | water seeps out gently and gathers into a still pool |
| leave a pool alone | mushrooms grow in the damp |
| dig below a pool | it drains downward, quietly finding its new level |
| reach the deep (12 m+) | glow-worms dot the walls; crystals glint mint-green in the dark |
| hang a lantern | warm light pools on the walls; moths orbit it at night |
| move the cursor near a bird | it startles and flies away (it comes back) |

## 4. Time and light

A slow day/night cycle (~5 minutes) drifts through dawn, day, dusk and
night — the palette, hills, sun/moon and ambient sounds follow. Darkness
grows with depth; light is the deep game's texture: lantern pools,
crystal glow, fireflies at night, glow-worm constellations. The hole at
20 m under moonlight, strung with lanterns above a still pool, is the
screenshot players share.

## 5. Presentation

- Soft painterly 2D: terrain built from overlapping soft-edged blobs
  (no visible grid), pastel palette — sage hills, warm loam, clay rose,
  mauve stone, deep indigo caves, lantern amber, crystal mint.
- UI is nearly absent: a quiet depth figure bottom-left, a sound toggle,
  whispered one-line hints in Quicksand. No banners, no meters, no icons.
- Depth markers exist *in the world* — small etched stones on the bank
  wall every 10 m.
- Audio: soft dig scuffs per material, pebble clicks, water drips,
  birdsong by day, wind pad underneath. Everything at a murmur.

## 6. Session shape

There is no loop to close. A session is: carve for a while, watch the
water find its level, hang a lantern, notice it got dark, keep going or
don't. Progression is only *place*: the strata slowly change character
with depth (crystals below ~21 m), so descending always shows something
new — at the pace the player chooses.

## 7. Prototype (in this repo)

`index.html` — self-contained, no dependencies. PC / mouse:

```
cd hole-game && python3 -m http.server 8000   # then open localhost:8000
```

Hold LMB to carve · right-click hangs a lantern · scroll or arrow keys
to descend.

Implemented: continuous soft-brush carving with per-material feel,
procedural strata (loam/clay/sand/stone/crystal/springs), sand trickle,
pebble physics, springs → settling pools → drainage, mushrooms,
dangling roots, glow-worms, crystal glow, fireflies, birds that flee
the cursor, butterflies, moths, lanterns, full day/night cycle with
sun/moon/stars/hills, depth darkness with light punching through,
etched depth stones, gentle synthesized ambience, and whisper-quiet UI.

Not yet: saving your hole (important for a toy — you want to keep it),
more strata personalities, fossils/curios to uncover, photo mode,
gamepad, performance polish for very deep holes.

## 8. History

- **v0.1** — mobile tap-to-dig prototype with tools, money, cave-in
  hazards, tourists. Verdict: mechanically sound, felt freemium.
- **v0.2** — pixel-art reskin with animated strikes and full feedback
  layer. Better craft, same freemium skeleton.
- **v0.3** — this document: structure replaced, not the skin.

## 9. Open questions

1. Persistence — localStorage snapshots of the carved world (grid is
   easy; the painted canvases can be re-derived).
2. Is there a place for very light "finds" (fossils, buried curios)
   that stay purely decorative — museum shelf, not inventory?
3. Wider worlds: side-scrolling as well as depth? Multiple biomes as
   you descend (peat, chalk, sea caves)?
4. A photo mode with depth-of-field would make sharing effortless.
