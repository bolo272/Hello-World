# THE HOLE — Game Design Document (v0.1)

A smartphone game about digging the deepest hole on Earth.

**One-line pitch:** The earth fights back — you dig anyway.

---

## 1. Core fantasy & hook

The player digs a hole. That's it — and that's the point. The depth counter is
the score, the bragging right, the reason to keep going.

The **hook is the struggle against the elements**, not the digging itself.
Digging is the *reward*; the earth constantly tries to take it away from you:

- **Cave-ins** — unsupported walls crack, crumble, and dump rubble back into
  your hole. Hours of progress can literally fall on your head. Bracing walls
  in time (or watching them collapse because you got greedy) is the core
  tension.
- **Water pockets** — crack the wrong tile and your hole floods from the
  bottom. Now you can't dig until you pump it out.
- **Hard layers** — rock and granite are literal walls of progression. Your
  shovel bounces off. You *need* that pickaxe, that jackhammer, that drill.

Every one of these should follow the same emotional curve:
**setback → tool/tactic → triumphant breakthrough**. Overcoming an obstacle
must always feel louder and juicier than plain digging (screen shake,
particles, sound, a depth-milestone banner).

## 2. Core loop

```
DIG  →  fill your bag with material  →  SELL at the surface  →  money
 ↑                                                               ↓
 └────────  buy better tools / braces / pumps / capacity  ───────┘
```

Secondary loop: **depth record → tourists arrive → passive income** —
the hole itself becomes an attraction and a money printer, which funds
riskier, deeper digging.

## 3. Materials (the terrain is the enemy roster)

| Material | Depth | Toughness | Value | Threat / personality |
|---|---|---|---|---|
| Topsoil / dirt | 0 m+ | trivial | low | tutorial; crumbles slowly |
| Clay | 3 m+ | slow to dig | decent | stable walls — a *good* neighbour |
| Gravel / sand | 4 m+ | easy | low | **collapses fast** — the trap: easy to dig, dangerous to leave exposed |
| Rock | 6 m+ | needs Pickaxe | good | first hard progression wall |
| Coal / iron | 8 m+ | medium | good | first "jackpot" moments |
| Water pocket | 8 m+ | one hit | none | floods the hole from the bottom |
| Gold / gems | 20 m+ | hard | high | risk-reward: often embedded near hazards |
| Granite | 25 m+ | needs Drill | very high | endgame wall; also *never collapses* — safe corridor material |

Design rule: every material is either an **obstacle**, a **payout**, or a
**structural property** (stable vs. crumbly) — ideally two of the three.

## 4. Hazard systems

### Cave-ins (the star of the show)
- Any solid tile exposed to open air (side or ceiling) gains *instability*
  over time; crumbly materials (gravel, dirt) destabilise fast, clay slowly,
  rock/granite never.
- Warning phase: cracks appear, tile trembles → player has a window to react.
- Collapse: tile bursts into **rubble that falls back into the hole** and
  must be re-dug (rubble is near-worthless — the punishment is lost time).
- Collapses cascade: a falling wall destabilises its neighbours. A neglected
  hole can eat itself in seconds. This is spectacular and should be *fun to
  watch* even when it hurts.
- Counter-play: **braces** (cheap consumable) freeze a wall tile forever.
  Deciding *which* walls deserve a brace is the ongoing strategic decision.

### Flooding
- Breached water pockets release water that fills the deepest open tiles.
- Water blocks digging entirely until pumped.
- Counter-play: **pump** (buy once, upgrade for speed). Later: sell the
  water? (nice synergy with the "sell everything" fantasy).

### Hard layers
- Tool-tier gates, pure and simple. Hitting rock with a shovel gives a
  loud *CLANK* and zero progress — frustration by design, so the moment the
  pickaxe first bites through is a release.

## 5. Tools & economy

| Tool | Breaks | Feel |
|---|---|---|
| Rusty trowel | soft ground | pathetic on purpose |
| Shovel | soft ground, faster | first upgrade dopamine |
| Pickaxe | rock, coal | unlocks depth 6 m+ layer |
| Jackhammer | everything but granite | power fantasy begins |
| Plasma drill | granite | endgame; melts through anything |

Other purchases: **bag capacity** (fewer trips = smoother loop),
**braces** (consumable), **pump levels**.

Money sources: selling dug material, tourist income (scales with depth
record), later: rare artifact finds, "sponsorships" at milestone depths.

## 6. Tourists (the hole as a place)

At 10 m the hole gets noticed; visitors gather at the rim with cameras.
Deeper records → more visitors → more passive income. Later versions:
build a gift shop, viewing platform, elevator rides — all surface-side
buildings that make the *hole itself* the character that grows.

## 7. Presentation — 2.5D

- Side-on cutaway of the hole, tiles rendered as chunky blocks with a lit
  top face and shaded side face (oblique/"2.5D" look) — readable on a small
  screen, cheap to produce, and cracks/braces/water read instantly.
- Juice budget goes to the hook: collapses get screen shake, dust plumes and
  rumble; breakthroughs get flashes and fanfares; plain digging gets modest
  crumb particles so the contrast stays big.
- Portrait orientation, one-thumb play: tap to dig, drag to look up/down.

## 8. Session & progression shape

- 30-second loop: dig → bag full → sell.
- 5-minute loop: afford next upgrade, survive one hazard.
- Long arc: depth milestones (10/25/50/100 m), each introducing a new
  material or hazard so the earth keeps escalating.

## 9. Prototype (in this repo)

`index.html` is a self-contained playable prototype of the core loop —
no build step, no dependencies. Open it in any browser (best on a phone,
works with a mouse too):

```
cd hole-game && python3 -m http.server 8000   # or just open index.html
```

Implemented: tap-to-dig, drag-to-scroll, 10 materials, tool tiers with
hard-rock gating, bag/sell economy, wall instability with warning cracks,
cascading collapses and rubble, braces, water pockets + flooding + pump,
tourists with passive income, depth milestones, 2.5D block rendering,
particles, screen shake, and synthesized sound effects.

Not yet: saving, meta-buildings, artifacts/events, balancing beyond
"feels okay for a first playtest".

## 10. Open design questions

1. Should there be a digger character in the hole (can be buried by
   collapses — higher stakes, more emergent stories) or stay "god-finger"
   digging (current prototype — simpler, more zen)?
2. Offline progress (idle-game DNA) or purely active play?
3. Is rubble ever valuable (crushing plant upgrade?) so collapses become
   partially self-compensating late-game?
