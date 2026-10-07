"""Generator strony Metalove.

    python3 build.py             gotowa strona do public/
    python3 build.py --podglad   wersja do podglądu w Claude (podglad/)

Treść jest w tresc.py, zdjęcia źródłowe w zdjecia/ (JPG). Wymaga tylko Pillow.
"""
import hashlib
import html
import json
import os
import shutil
import sys
from datetime import date

from PIL import Image, ImageDraw, ImageOps

ROOT = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, ROOT)
import tresc as T  # noqa: E402

PODGLAD = '--podglad' in sys.argv
OUT = os.path.join(ROOT, 'podglad' if PODGLAD else 'public')
F = T.FIRMA
KAT = {k['slug']: k for k in T.KATEGORIE}
REAL = {r['slug']: r for r in T.REALIZACJE}
IMG = {}
SZEROKOSCI = (640, 1280)
ROK = date.today().year
WERSJA = {}


def e(s):
    return html.escape(str(s), quote=True)


def todo(nazwa):
    return f'<span class="todo">[{e(nazwa)}]</span>'


def slug(s):
    tab = str.maketrans('ąćęłńóśźżĄĆĘŁŃÓŚŹŻ ', 'acelnoszzACELNOSZZ-')
    return s.translate(tab).lower()


# ---------- adresy ----------

def href(cel, gl):
    """cel: '' (strona główna) albo ścieżka katalogu zakończona '/'."""
    pre = '../' * gl
    if PODGLAD:
        return pre + cel + 'index.html'
    return (pre + cel) or './'


def zasob(sciezka, gl):
    v = None if PODGLAD else WERSJA.get(sciezka)
    return '../' * gl + 'assets/' + sciezka + (f'?v={v}' if v else '')


def obraz_url(nazwa, w, gl):
    return '../' * gl + f'img/{nazwa}-{w}.webp'


# ---------- zdjęcia ----------

def przygotuj_zdjecia():
    src = os.path.join(ROOT, 'zdjecia')
    dst = os.path.join(OUT, 'img')
    os.makedirs(dst, exist_ok=True)
    for fn in sorted(os.listdir(src)):
        if not fn.lower().endswith(('.jpg', '.jpeg', '.png')):
            continue
        nazwa = os.path.splitext(fn)[0]
        im = ImageOps.exif_transpose(Image.open(os.path.join(src, fn))).convert('RGB')
        szer = []
        for w in SZEROKOSCI:
            ww = min(w, im.width)
            if ww in szer:
                continue
            h = round(im.height * ww / im.width)
            cel = os.path.join(dst, f'{nazwa}-{ww}.webp')
            if not os.path.exists(cel) or os.path.getmtime(cel) < os.path.getmtime(os.path.join(src, fn)):
                im.resize((ww, h), Image.LANCZOS).save(cel, 'WEBP', quality=80, method=6)
            szer.append(ww)
        IMG[nazwa] = dict(w=im.width, h=im.height, szer=szer)
    # próbki materiałów
    for m in T.MATERIALY:
        if 'wycinek' in m:
            nazwa, x, y, w, h = m['wycinek']
            im = Image.open(os.path.join(src, nazwa + '.jpg')).convert('RGB')
            im.crop((x, y, x + w, y + h)).resize((480, 160), Image.LANCZOS).save(
                os.path.join(dst, f'material-{slug(m["nazwa"])}.webp'), 'WEBP', quality=82, method=6)
    # obraz do podglądu linków (1200x630)
    im = Image.open(os.path.join(src, 'schody-na-belce.jpg')).convert('RGB')
    h = round(im.width * 630 / 1200)
    top = max(0, min(im.height - h, int(im.height * 0.33)))
    im.crop((0, top, im.width, top + h)).resize((1200, 630), Image.LANCZOS).save(
        os.path.join(dst, 'og.jpg'), 'JPEG', quality=85, optimize=True, progressive=True)


def obraz(nazwa, alt, gl, sizes, klasa='', priorytet=False):
    info = IMG[nazwa]
    srcset = ', '.join(f'{obraz_url(nazwa, w, gl)} {w}w' for w in info['szer'])
    w = info['szer'][-1]
    h = round(info['h'] * w / info['w'])
    atr = [f'src="{obraz_url(nazwa, w, gl)}"', f'srcset="{srcset}"', f'sizes="{sizes}"',
           f'width="{w}"', f'height="{h}"', f'alt="{e(alt)}"', 'decoding="async"']
    atr.append('loading="eager" fetchpriority="high"' if priorytet else 'loading="lazy"')
    if klasa:
        atr.append(f'class="{klasa}"')
    if nazwa in T.FOKUS:
        atr.append(f'style="object-position:{T.FOKUS[nazwa]}"')
    return '<img ' + ' '.join(atr) + '>'


def alt_dla(nazwa):
    for r in T.REALIZACJE:
        for n, alt in r['zdjecia']:
            if n == nazwa:
                return alt
    return ''


# ---------- wspólne elementy ----------

LOGO = '<span aria-hidden="true">METAL<span class="logo__o"></span>VE</span>'
MENU = [('realizacje/', 'Realizacje', 'realizacje'), ('oferta/', 'Oferta', 'oferta'),
        ('dla-projektantow/', 'Dla projektantów', 'projektanci')]


def telefon():
    if F['telefon']:
        return f'<a href="tel:{e(F["telefon"].replace(" ", ""))}">{e(F["telefon"])}</a>'
    return todo('telefon')


def email():
    if F['email']:
        return f'<a href="mailto:{e(F["email"])}">{e(F["email"])}</a>'
    return todo('e-mail')


def social():
    ig = f'<a href="{e(F["instagram"])}" rel="noopener">Instagram</a>' if F['instagram'] else todo('Instagram')
    fb = f'<a href="{e(F["facebook"])}" rel="noopener">Facebook</a>' if F['facebook'] else todo('Facebook')
    return f'{ig} · {fb}'


def naglowek(gl, aktywne):
    li = ''.join(
        f'<li><a href="{href(c, gl)}"{" aria-current=" + chr(34) + "page" + chr(34) if a == aktywne else ""}>{e(n)}</a></li>'
        for c, n, a in MENU)
    return f'''<a class="pomin" href="#tresc">Przejdź do treści</a>
<header class="naglowek">
  <div class="kontener naglowek__wnetrze">
    <a class="logo" href="{href("", gl)}" aria-label="Metalove, strona główna">{LOGO}</a>
    <button class="menu-przycisk" type="button" aria-expanded="false" aria-controls="menu">Menu</button>
    <nav id="menu" class="menu" aria-label="Główne menu">
      <ul>{li}</ul>
      <a class="przycisk przycisk--maly" href="{href("wycena/", gl)}">Wycena</a>
    </nav>
  </div>
</header>'''


def stopka(gl):
    oferta = ''.join(f'<li><a href="{href(k["slug"] + "/", gl)}">{e(k["nazwa"])}</a></li>' for k in T.KATEGORIE)
    return f'''<footer class="stopka">
  <div class="kontener stopka__siatka">
    <div class="stopka__marka">
      <a class="logo logo--jasne" href="{href("", gl)}" aria-label="Metalove, strona główna">{LOGO}</a>
      <p>Pracownia mebli i konstrukcji ze stali. {e(F["zasieg"])}.</p>
    </div>
    <div>
      <h2 class="stopka__tytul">Oferta</h2>
      <ul>{oferta}</ul>
    </div>
    <div>
      <h2 class="stopka__tytul">Metalove</h2>
      <ul>
        <li><a href="{href("realizacje/", gl)}">Realizacje</a></li>
        <li><a href="{href("dla-projektantow/", gl)}">Dla projektantów</a></li>
        <li><a href="{href("wycena/", gl)}">Wycena</a></li>
        <li><a href="{href("polityka-prywatnosci/", gl)}">Polityka prywatności</a></li>
      </ul>
    </div>
    <div>
      <h2 class="stopka__tytul">Kontakt</h2>
      <ul>
        <li>{telefon()}</li>
        <li>{email()}</li>
        <li>{social()}</li>
      </ul>
    </div>
  </div>
  <div class="kontener stopka__dol"><span>© {ROK} Metalove</span><span>{e(F["miasto"])}</span></div>
</footer>'''


def glowa(tytul, opis, gl, sciezka, noindex=False):
    d = (F['domena'] or '').rstrip('/')
    out = [
        '<meta charset="utf-8">',
        '<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">',
        f'<title>{e(tytul)}</title>',
        f'<meta name="description" content="{e(opis)}">',
    ]
    if noindex:
        out.append('<meta name="robots" content="noindex">')
    if d:
        out.append(f'<link rel="canonical" href="{d}/{sciezka}">')
        out.append(f'<meta property="og:url" content="{d}/{sciezka}">')
        out.append(f'<meta property="og:image" content="{d}/img/og.jpg">')
    out += [
        '<meta property="og:type" content="website">',
        '<meta property="og:locale" content="pl_PL">',
        '<meta property="og:site_name" content="Metalove">',
        f'<meta property="og:title" content="{e(tytul)}">',
        f'<meta property="og:description" content="{e(opis)}">',
        '<meta name="theme-color" content="#16181B">',
        f'<link rel="icon" href="{zasob("favicon.svg", gl)}" type="image/svg+xml">',
        f'<link rel="apple-touch-icon" href="{zasob("apple-touch-icon.png", gl)}">',
        f'<link rel="preload" href="{"../" * gl}assets/fonts/archivo-normal-500-800-latin.woff2" as="font" type="font/woff2" crossorigin>',
        f'<link rel="stylesheet" href="{zasob("style.css", gl)}">',
    ]
    return '\n'.join(out)


def strona(sciezka, tytul, opis, tresc, aktywne='', schema=None, noindex=False):
    """sciezka: '' dla strony głównej albo 'realizacje/x/'. Zapisuje index.html."""
    gl = sciezka.count('/')
    head = glowa(tytul, opis, gl, sciezka, noindex)
    if schema:
        head += f'\n<script type="application/ld+json">{json.dumps(schema, ensure_ascii=False)}</script>'
    cialo = f'{naglowek(gl, aktywne)}\n<main id="tresc">\n{tresc}\n</main>\n{stopka(gl)}'
    skrypt = f'<script src="{zasob("main.js", gl)}" defer></script>'
    if PODGLAD and sciezka == '':
        # strona startowa podglądu: szkielet dokumentu dokłada przeglądarka podglądu
        doc = f'{head}\n<div class="podglad" data-podglad="1">\n{cialo}\n</div>\n{skrypt}\n'
    else:
        flaga = ' data-podglad="1"' if PODGLAD else ''
        doc = f'<!doctype html>\n<html lang="pl">\n<head>\n{head}\n</head>\n<body{flaga}>\n{cialo}\n{skrypt}\n</body>\n</html>\n'
    cel = os.path.join(OUT, sciezka, 'index.html')
    os.makedirs(os.path.dirname(cel), exist_ok=True)
    with open(cel, 'w', encoding='utf-8') as f:
        f.write(doc)


# ---------- klocki treści ----------

def kafel(r, gl, sizes='(min-width: 900px) 33vw, (min-width: 420px) 50vw, 100vw'):
    nazwa, alt = r['zdjecia'][0]
    k = KAT[r['kategoria']]
    return f'''<li class="kafel" data-kat="{k["slug"]}">
  <a href="{href("realizacje/" + r["slug"] + "/", gl)}">
    <span class="kafel__foto">{obraz(nazwa, alt, gl, sizes)}</span>
    <span class="kafel__tytul">{e(r["tytul"])}</span>
    <span class="kafel__kat">{e(k["nazwa"])}</span>
  </a>
</li>'''


def siatka(realizacje, gl):
    return '<ul class="siatka">' + ''.join(kafel(r, gl) for r in realizacje) + '</ul>'


def karty_kategorii(gl):
    out = []
    for k in T.KATEGORIE:
        out.append(f'''<li class="karta">
  <a href="{href(k["slug"] + "/", gl)}">
    <span class="karta__foto">{obraz(k["zdjecie"], alt_dla(k["zdjecie"]), gl, "(min-width: 900px) 33vw, (min-width: 560px) 50vw, 100vw")}</span>
    <span class="karta__nazwa">{e(k["nazwa"])}<span class="karta__strzalka" aria-hidden="true">→</span></span>
    <span class="karta__opis">{e(k["krotko"])}</span>
  </a>
</li>''')
    return '<ul class="karty">' + ''.join(out) + '</ul>'


def probki(gl):
    out = []
    for m in T.MATERIALY:
        if 'wycinek' in m:
            wzor = f'<img src="{"../" * gl}img/material-{slug(m["nazwa"])}.webp" width="480" height="160" alt="" loading="lazy" decoding="async">'
        else:
            wzor = '<span class="probka__stal"></span>'
        out.append(f'<li class="probka"><span class="probka__wzor">{wzor}</span>'
                   f'<span class="probka__nazwa">{e(m["nazwa"])}</span>'
                   f'<span class="probka__uwaga">{e(m["uwaga"])}</span></li>')
    return '<ul class="probki">' + ''.join(out) + '</ul>'


def kroki():
    li = ''.join(f'<li class="krok"><span class="krok__nr">{i:02d}</span><h3>{e(t)}</h3><p>{e(d)}</p></li>'
                 for i, (t, d) in enumerate(T.KROKI, 1))
    return f'<ol class="kroki">{li}</ol>'


def cta(gl, tytul='Masz pomysł, którego nie ma w katalogu?'):
    return f'''<section class="kontener cta" aria-labelledby="cta-tytul">
  <h2 id="cta-tytul">{e(tytul)}</h2>
  <p class="lead">Opisz go w kilku zdaniach i dołącz zdjęcie miejsca. Odezwiemy się w ciągu {e(F["czas_odpowiedzi"])} z pierwszymi widełkami cenowymi.</p>
  <div class="przyciski"><a class="przycisk" href="{href("wycena/", gl)}">Wyślij zapytanie</a></div>
  <p class="kontakt-linia"><span>{telefon()}</span><span>{email()}</span><span>{e(F["zasieg"])}</span></p>
</section>'''


def faq(pary):
    return '<div class="faq">' + ''.join(
        f'<details><summary>{e(q)}</summary><p>{e(a)}</p></details>' for q, a in pary) + '</div>'


# ---------- strony ----------

def strona_glowna():
    gl = 0
    wyr = [REAL[s] for s in T.WYROZNIONE]
    pro = ''.join(f'<li><h3>{e(t)}</h3><p>{e(d)}</p></li>' for t, d in T.DLA_PROJEKTANTOW)
    tresc = f'''<section class="kontener hero">
  <div class="hero__tekst">
    <p class="etykieta">Pracownia stali · {e(F["miasto"])}</p>
    <h1>Meble i konstrukcje ze stali, których nie ma w katalogu</h1>
    <p class="lead">Schody, drzwi i ścianki loftowe, stoły, lustra i wyposażenie lokali. Projektujemy, spawamy i montujemy na wymiar w Warszawie i okolicach, łącząc stal z mosiądzem, szkłem, kamieniem i drewnem.</p>
    <div class="przyciski">
      <a class="przycisk" href="{href("wycena/", gl)}">Opisz swój pomysł</a>
      <a class="przycisk przycisk--obrys" href="{href("realizacje/", gl)}">Zobacz realizacje</a>
    </div>
    <p class="sygnatura">Jeśli da się to narysować, zrobimy to ze stali.</p>
  </div>
  <figure class="hero__zdjecia">
    <span class="hero__glowne">{obraz("schody-na-belce", alt_dla("schody-na-belce"), gl, "(min-width: 900px) 540px, 92vw", priorytet=True)}</span>
    <span class="hero__wstawka">{obraz("stolik-zielony-marmur", alt_dla("stolik-zielony-marmur"), gl, "(min-width: 900px) 230px, 38vw")}</span>
    <figcaption class="podpis">Schody na jednej belce · stal, dąb</figcaption>
  </figure>
</section>

<section class="sekcja sekcja--papier" aria-labelledby="realizacje-tytul">
  <div class="kontener">
    <div class="sekcja__naglowek">
      <div>
        <p class="etykieta">Realizacje</p>
        <h2 id="realizacje-tytul">Wybrane realizacje</h2>
        <p class="tekst-szary">Większość projektów robimy od zera, pod konkretne wnętrze.</p>
      </div>
      <a class="link-strzalka" href="{href("realizacje/", gl)}">Wszystkie realizacje →</a>
    </div>
    {siatka(wyr, gl)}
  </div>
</section>

<section class="sekcja" aria-labelledby="oferta-tytul">
  <div class="kontener">
    <div class="sekcja__naglowek">
      <div>
        <p class="etykieta">Oferta</p>
        <h2 id="oferta-tytul">Co robimy</h2>
      </div>
    </div>
    {karty_kategorii(gl)}
  </div>
</section>

<section class="sekcja sekcja--papier" aria-labelledby="materialy-tytul">
  <div class="kontener">
    <div class="sekcja__naglowek">
      <div>
        <p class="etykieta">Materiały</p>
        <h2 id="materialy-tytul">Stal w dobrym towarzystwie</h2>
        <p class="tekst-szary">Stal jest szkieletem. Resztę dobieramy do wnętrza: mosiądz, kamień, drewno albo szkło.</p>
      </div>
    </div>
    {probki(gl)}
  </div>
</section>

<section class="sekcja" aria-labelledby="kroki-tytul">
  <div class="kontener">
    <div class="sekcja__naglowek">
      <div>
        <p class="etykieta">Jak pracujemy</p>
        <h2 id="kroki-tytul">Od szkicu do montażu w czterech krokach</h2>
      </div>
    </div>
    {kroki()}
  </div>
</section>

<section class="pasmo" aria-labelledby="projektanci-tytul">
  <div class="kontener pasmo__siatka">
    <div class="pasmo__tekst">
      <p class="etykieta etykieta--jasna">Dla projektantów</p>
      <h2 id="projektanci-tytul">Projektujesz wnętrza? Zrobimy to, co narysujesz.</h2>
      <p>Pracujemy na Twoim projekcie i pilnujemy detalu. Przed wyceną konsultujemy rozwiązania techniczne, a przed produkcją pokazujemy rysunek do akceptacji.</p>
      <div class="przyciski"><a class="przycisk przycisk--mosiadz" href="{href("dla-projektantow/", gl)}">Zasady współpracy</a></div>
    </div>
    <ul class="lista-kresek">{pro}</ul>
  </div>
</section>

{cta(gl)}'''
    schema = {
        '@context': 'https://schema.org',
        '@type': 'HomeAndConstructionBusiness',
        'name': 'Metalove',
        'description': 'Pracownia mebli i konstrukcji ze stali na wymiar w Warszawie.',
        'address': {'@type': 'PostalAddress', 'addressLocality': F['miasto'], 'addressCountry': 'PL'},
        'areaServed': F['zasieg'],
    }
    if F['telefon']:
        schema['telephone'] = F['telefon']
    if F['email']:
        schema['email'] = F['email']
    if F['domena']:
        schema['url'] = F['domena']
        schema['image'] = F['domena'].rstrip('/') + '/img/og.jpg'
    same = [u for u in (F['instagram'], F['facebook']) if u]
    if same:
        schema['sameAs'] = same
    strona('', 'Metalove' if PODGLAD else 'Metalove: meble i konstrukcje ze stali na wymiar, Warszawa',
           'Pracownia Metalove: schody, ścianki i drzwi loftowe, stoły, lustra i wyposażenie lokali ze stali, mosiądzu, szkła, kamienia i drewna. Na wymiar, Warszawa i okolice.',
           tresc, schema=schema)


def strona_realizacji_lista():
    gl = 1
    obecne = [k for k in T.KATEGORIE if any(r['kategoria'] == k['slug'] for r in T.REALIZACJE)]
    przyciski = '<button type="button" data-filtr="wszystkie" aria-pressed="true">Wszystkie</button>' + ''.join(
        f'<button type="button" data-filtr="{k["slug"]}" aria-pressed="false">{e(k["etykieta"])}</button>' for k in obecne)
    tresc = f'''<section class="kontener strona-naglowek">
  <p class="etykieta">Portfolio</p>
  <h1>Realizacje</h1>
  <p class="lead">Schody, ścianki loftowe, meble, lustra i wnętrza lokali. Kliknij zdjęcie, żeby zobaczyć szczegóły.</p>
  <div class="filtr" role="group" aria-label="Pokaż realizacje z kategorii" data-filtr-grupa>{przyciski}</div>
</section>
<section class="kontener sekcja-dol">
  {siatka(T.REALIZACJE, gl)}
</section>
{cta(gl)}'''
    strona('realizacje/', 'Realizacje | Metalove',
           'Zdjęcia realizacji pracowni Metalove: schody metalowe, drzwi i ścianki loftowe, meble ze stali, lustra i wyposażenie salonów.',
           tresc, aktywne='realizacje')


def tabliczka(r):
    k = KAT[r['kategoria']]
    pola = [('Kategoria', e(k['nazwa']))]
    for etykieta, klucz in (('Lokalizacja', 'lokalizacja'), ('Rok', 'rok'), ('Projekt', 'projektant')):
        if r.get(klucz):
            pola.append((etykieta, e(r[klucz])))
    kom = []
    for i, (et, wart) in enumerate(pola):
        szer = ' class="szeroka"' if (i == len(pola) - 1 and len(pola) % 2 == 1) else ''
        kom.append(f'<div{szer}><dt>{et}</dt><dd>{wart}</dd></div>')
    kom.append(f'<div class="szeroka"><dt>Materiały</dt><dd>{e(", ".join(r["materialy"]))}</dd></div>')
    return '<dl class="tabliczka">' + ''.join(kom) + '</dl>'


def strona_realizacji(r):
    gl = 2
    k = KAT[r['kategoria']]
    zdj = ''.join(f'<figure>{obraz(n, alt, gl, "(min-width: 900px) 640px, 100vw", priorytet=(i == 0))}</figure>'
                  for i, (n, alt) in enumerate(r['zdjecia']))
    inne = [x for x in T.REALIZACJE if x['kategoria'] == r['kategoria'] and x['slug'] != r['slug']]
    inne += [x for x in T.REALIZACJE if x['kategoria'] != r['kategoria']]
    tresc = f'''<article class="kontener realizacja">
  <div class="realizacja__info">
    <nav class="okruszki" aria-label="Ścieżka"><a href="{href("realizacje/", gl)}">Realizacje</a> / <a href="{href(k["slug"] + "/", gl)}">{e(k["nazwa"])}</a></nav>
    <h1>{e(r["tytul"])}</h1>
    {tabliczka(r)}
    <p class="lead">{e(r["opis"])}</p>
    <div class="przyciski"><a class="przycisk" href="{href("wycena/", gl)}">Chcę coś podobnego</a></div>
  </div>
  <div class="realizacja__zdjecia">{zdj}</div>
</article>
<section class="sekcja sekcja--papier" aria-labelledby="inne-tytul">
  <div class="kontener">
    <div class="sekcja__naglowek"><div><h2 id="inne-tytul">Inne realizacje</h2></div>
      <a class="link-strzalka" href="{href("realizacje/", gl)}">Wszystkie realizacje →</a></div>
    {siatka(inne[:3], gl)}
  </div>
</section>'''
    strona(f'realizacje/{r["slug"]}/', f'{r["tytul"]} | Realizacje Metalove', r['opis'], tresc, aktywne='realizacje')


def strona_kategorii(k):
    gl = 1
    realizacje = [r for r in T.REALIZACJE if r['kategoria'] == k['slug']]
    zakres = ''.join(f'<li>{e(z)}</li>' for z in k['zakres'])
    tresc = f'''<section class="kontener kat-hero">
  <div class="kat-hero__tekst">
    <nav class="okruszki" aria-label="Ścieżka"><a href="{href("oferta/", gl)}">Oferta</a> / {e(k["nazwa"])}</nav>
    <h1>{e(k["h1"])}</h1>
    <p class="lead">{e(k["lead"])}</p>
    <div class="przyciski"><a class="przycisk" href="{href("wycena/", gl)}">Opisz swój projekt</a></div>
  </div>
  <figure>{obraz(k["zdjecie"], alt_dla(k["zdjecie"]), gl, "(min-width: 860px) 50vw, 100vw", priorytet=True)}</figure>
</section>
<section class="kontener sekcja-mala" aria-labelledby="zakres-tytul">
  <h2 id="zakres-tytul">Co robimy</h2>
  <ul class="zakres">{zakres}</ul>
</section>
<section class="sekcja sekcja--papier" aria-labelledby="kat-real-tytul">
  <div class="kontener">
    <div class="sekcja__naglowek"><div><p class="etykieta">Realizacje</p><h2 id="kat-real-tytul">Zrobiliśmy już między innymi</h2></div>
      <a class="link-strzalka" href="{href("realizacje/", gl)}">Wszystkie realizacje →</a></div>
    {siatka(realizacje, gl)}
  </div>
</section>
<section class="kontener sekcja-mala" aria-labelledby="faq-tytul">
  <h2 id="faq-tytul">Częste pytania</h2>
  {faq(k["faq"])}
</section>
{cta(gl)}'''
    strona(f'{k["slug"]}/', k['tytul_seo'], k['opis_seo'], tresc, aktywne='oferta')


def strona_oferty():
    gl = 1
    tresc = f'''<section class="kontener strona-naglowek">
  <p class="etykieta">Oferta</p>
  <h1>Co robimy</h1>
  <p class="lead">Wszystko na wymiar, ze stali i materiałów, które do niej pasują. Wybierz kategorię, żeby zobaczyć realizacje i odpowiedzi na częste pytania.</p>
</section>
<section class="kontener sekcja-dol">{karty_kategorii(gl)}</section>
{cta(gl)}'''
    strona('oferta/', 'Oferta | Metalove',
           'Schody metalowe, ścianki i drzwi loftowe, meble ze stali, lustra, wyposażenie salonów i kolekcja stolików. Na wymiar, Warszawa i okolice.',
           tresc, aktywne='oferta')


def zgody(prefix, newsletter_tekst):
    return f'''<div class="zgody">
  <label class="zgoda" for="{prefix}-zgoda"><input type="checkbox" id="{prefix}-zgoda" name="zgoda-kontakt" value="tak" required>
    <span>Zgadzam się, żeby Metalove skontaktowało się ze mną w sprawie tego zapytania. Szczegóły w <a href="../{"polityka-prywatnosci/index.html" if PODGLAD else "polityka-prywatnosci/"}">polityce prywatności</a>.</span></label>
  <label class="zgoda" for="{prefix}-newsletter"><input type="checkbox" id="{prefix}-newsletter" name="zgoda-newsletter" value="tak">
    <span>{e(newsletter_tekst)}</span></label>
</div>'''


def wybor(nazwa, prefix, opcje, wymagane=False, typ='radio'):
    out = []
    for i, o in enumerate(opcje):
        req = ' required' if (wymagane and i == 0) else ''
        out.append(f'<label for="{prefix}-{nazwa}-{i}"><input type="{typ}" id="{prefix}-{nazwa}-{i}" name="{nazwa}" value="{e(o)}"{req}><span>{e(o)}</span></label>')
    return '<div class="wybor">' + ''.join(out) + '</div>'


def formularz(nazwa, pola, dziekujemy, gl):
    akcja = '' if PODGLAD else f'{"../" * gl}dziekujemy/'
    return f'''<form class="formularz" name="{nazwa}" method="POST" action="{akcja}" enctype="multipart/form-data"
      data-netlify="true" netlify-honeypot="pole-x" data-formularz data-dziekujemy="{e(dziekujemy)}">
  <input type="hidden" name="form-name" value="{nazwa}">
  <p class="ukryte-pole"><label>Nie wypełniaj tego pola: <input name="pole-x" tabindex="-1" autocomplete="off"></label></p>
  {pola}
  <div class="formularz__wyslij">
    <button class="przycisk" type="submit">Wyślij</button>
    <p class="status" role="status" aria-live="polite"></p>
  </div>
</form>'''


def strona_wyceny():
    gl = 1
    p = 'w'
    pola = f'''<fieldset class="pole">
    <legend>Co chcesz zrobić?</legend>
    {wybor("rodzaj", p, ["Schody lub balustrada", "Ścianka lub drzwi loftowe", "Mebel", "Lustro", "Wnętrze lokalu", "Coś nietypowego"], wymagane=True)}
  </fieldset>
  <div class="pole">
    <label for="w-opis">Opisz pomysł</label>
    <textarea id="w-opis" name="opis" rows="5" required placeholder="Np. schody na belce z dębowymi stopniami, 14 stopni, zmiana kierunku na górze."></textarea>
  </div>
  <div class="pole">
    <label for="w-zdjecie1">Zdjęcie miejsca, inspiracji albo szkic</label>
    <input type="file" id="w-zdjecie1" name="zdjecie1" accept="image/*,.pdf">
    <input type="file" id="w-zdjecie2" name="zdjecie2" accept="image/*,.pdf" aria-label="Drugie zdjęcie (opcjonalnie)">
    <p class="podpowiedz">JPG, PNG albo PDF, do 8 MB łącznie.</p>
  </div>
  <div class="dwie-kolumny">
    <div class="pole"><label for="w-wymiary">Orientacyjne wymiary</label><input type="text" id="w-wymiary" name="wymiary" placeholder="Np. 120 × 240 cm"></div>
    <div class="pole"><label for="w-miejscowosc">Miejscowość</label><input type="text" id="w-miejscowosc" name="miejscowosc" autocomplete="address-level2" required></div>
  </div>
  <div class="dwie-kolumny">
    <div class="pole"><label for="w-budzet">Budżet</label>
      <select id="w-budzet" name="budzet"><option value="">Wybierz</option><option>do 3 tys. zł</option><option>3–10 tys. zł</option><option>10–30 tys. zł</option><option>powyżej 30 tys. zł</option><option>Nie wiem, doradźcie</option></select></div>
    <div class="pole"><label for="w-termin">Kiedy?</label>
      <select id="w-termin" name="termin"><option value="">Wybierz</option><option>Jak najszybciej</option><option>W ciągu 1–3 miesięcy</option><option>Za 3–6 miesięcy</option><option>Bez pośpiechu</option></select></div>
  </div>
  <fieldset class="pole">
    <legend>Jestem</legend>
    {wybor("kto", p, ["Klientem prywatnym", "Projektantem wnętrz", "Firmą"])}
  </fieldset>
  <div class="dwie-kolumny">
    <div class="pole"><label for="w-imie">Imię</label><input type="text" id="w-imie" name="imie" autocomplete="given-name" required></div>
    <div class="pole"><label for="w-telefon">Telefon</label><input type="tel" id="w-telefon" name="telefon" autocomplete="tel"></div>
  </div>
  <div class="pole"><label for="w-email">E-mail</label><input type="email" id="w-email" name="email" autocomplete="email" required></div>
  {zgody(p, "Chcę raz w miesiącu dostawać e-mail z nowymi realizacjami.")}'''
    dz = f'Dostaliśmy Twoje zapytanie. Odezwiemy się w ciągu {F["czas_odpowiedzi"]}.'
    tresc = f'''<section class="kontener strona-naglowek">
  <p class="etykieta">Wycena</p>
  <h1>Wycena projektu</h1>
  <p class="lead">Opisz, co chcesz zrobić, i dołącz zdjęcie miejsca albo inspiracji. Im więcej szczegółów, tym dokładniejsze widełki podamy przy pierwszej rozmowie.</p>
</section>
<section class="kontener wycena">
  {formularz("wycena", pola, dz, gl)}
  <aside class="wycena__bok" aria-label="Co dalej">
    <div>
      <h2 class="bok-tytul">Co dalej?</h2>
      <ol class="bok-kroki">
        <li><span>01</span><span>Czytamy zapytanie i oglądamy zdjęcia.</span></li>
        <li><span>02</span><span>W ciągu {e(F["czas_odpowiedzi"])} odzywamy się z pierwszymi widełkami.</span></li>
        <li><span>03</span><span>Umawiamy pomiar w Warszawie albo okolicy.</span></li>
      </ol>
    </div>
    <div>
      <h2 class="bok-tytul">Wolisz zadzwonić?</h2>
      <ul class="bok-kontakt"><li>{telefon()}</li><li>{email()}</li><li>{social()}</li></ul>
      <p class="tekst-szary bok-uwaga">Pracujemy w Warszawie i okolicach, do ok. 50 km.</p>
    </div>
  </aside>
</section>'''
    strona('wycena/', 'Wycena projektu | Metalove',
           'Opisz swój pomysł i dołącz zdjęcie miejsca. Pracownia Metalove odezwie się z pierwszą wyceną. Warszawa i okolice.',
           tresc)


def strona_projektantow():
    gl = 1
    p = 'p'
    zalety = ''.join(f'<li><h3>{e(t)}</h3><p>{e(d)}</p></li>' for t, d in T.DLA_PROJEKTANTOW)
    pola = f'''<div class="dwie-kolumny">
    <div class="pole"><label for="p-imie">Imię i nazwisko</label><input type="text" id="p-imie" name="imie" autocomplete="name" required></div>
    <div class="pole"><label for="p-pracownia">Pracownia</label><input type="text" id="p-pracownia" name="pracownia" autocomplete="organization"></div>
  </div>
  <div class="dwie-kolumny">
    <div class="pole"><label for="p-email">E-mail</label><input type="email" id="p-email" name="email" autocomplete="email" required></div>
    <div class="pole"><label for="p-telefon">Telefon</label><input type="tel" id="p-telefon" name="telefon" autocomplete="tel"></div>
  </div>
  <div class="pole"><label for="p-wiadomosc">Nad czym pracujesz?</label><textarea id="p-wiadomosc" name="wiadomosc" rows="4" placeholder="Opcjonalnie: projekt, termin, co chcesz zrobić ze stali."></textarea></div>
  {zgody(p, "Chcę raz w miesiącu dostawać e-mail z nowymi realizacjami i wykończeniami.")}'''
    tresc = f'''<section class="kontener strona-naglowek">
  <p class="etykieta">Dla projektantów</p>
  <h1>Współpraca z projektantami wnętrz</h1>
  <p class="lead">Rysujesz rzeczy, których nie ma w katalogu? My je robimy. Pracujemy na Twoim projekcie, pilnujemy detalu, a Twój klient dostaje to, co narysowałeś.</p>
</section>
<section class="kontener sekcja-mala" aria-labelledby="zalety-tytul">
  <h2 id="zalety-tytul">Co dostajesz</h2>
  <ul class="zalety">{zalety}</ul>
</section>
<section class="sekcja sekcja--papier" aria-labelledby="start-tytul">
  <div class="kontener wycena">
    <div class="pasmo-tekst">
      <h2 id="start-tytul">Jak zacząć</h2>
      <p class="lead">Zostaw kontakt. Wyślemy próbki wykończeń i zaproponujemy krótkie spotkanie w pracowni albo online.</p>
      <ul class="bok-kontakt"><li>{telefon()}</li><li>{email()}</li></ul>
    </div>
    {formularz("projektanci", pola, "Dziękujemy. Odezwiemy się, żeby umówić próbki i spotkanie.", gl)}
  </div>
</section>'''
    strona('dla-projektantow/', 'Współpraca z projektantami wnętrz | Metalove',
           'Wykonujemy projekty architektów i projektantów wnętrz: konsultacja techniczna, rysunek do akceptacji, próbki wykończeń. Stal, mosiądz, szkło. Warszawa.',
           tresc, aktywne='projektanci')


def strona_polityki():
    wzor = '' if F['pelna_nazwa'] else '<p class="uwaga">To wzór. Przed publikacją uzupełnij dane firmy i sprawdź treść z prawnikiem.</p>'
    admin = e(F['pelna_nazwa']) if F['pelna_nazwa'] else todo('pełna nazwa firmy')
    adres = e(F['adres']) if F['adres'] else todo('adres')
    nip = e(F['nip']) if F['nip'] else todo('NIP')
    tresc = f'''<section class="kontener strona-naglowek">
  <h1>Polityka prywatności</h1>
</section>
<section class="kontener tekst-strony">
  {wzor}
  <h2>Kto jest administratorem danych</h2>
  <p>Administratorem danych osobowych jest {admin}, {adres}, NIP {nip}. Kontakt w sprawie danych: {email()}, {telefon()}.</p>
  <h2>Jakie dane zbieramy</h2>
  <p>Dane, które wpiszesz w formularzu: imię, telefon, adres e-mail, nazwę pracowni, opis projektu, miejscowość i przesłane pliki.</p>
  <h2>Po co i na jakiej podstawie</h2>
  <ul>
    <li>Żeby odpowiedzieć na zapytanie i przygotować wycenę, czyli podjąć działania przed zawarciem umowy (art. 6 ust. 1 lit. b RODO).</li>
    <li>Żeby wysyłać e-mail z nowymi realizacjami, jeśli zaznaczysz tę zgodę (art. 6 ust. 1 lit. a RODO). Zgodę możesz wycofać w każdej chwili.</li>
    <li>Żeby ustalić, dochodzić roszczeń albo bronić się przed nimi (art. 6 ust. 1 lit. f RODO).</li>
  </ul>
  <h2>Komu przekazujemy dane</h2>
  <p>Firmom, które obsługują dla nas stronę, formularze i pocztę e-mail, na przykład dostawcy hostingu. Część z nich może przetwarzać dane poza Europejskim Obszarem Gospodarczym na podstawie standardowych klauzul umownych albo decyzji Komisji Europejskiej. Nie sprzedajemy danych.</p>
  <h2>Jak długo przechowujemy dane</h2>
  <p>Zapytania, które nie zakończyły się umową, przechowujemy do roku od ostatniego kontaktu. Dane klientów przechowujemy tak długo, jak wymagają tego przepisy podatkowe. Adres do newslettera do czasu wycofania zgody.</p>
  <h2>Twoje prawa</h2>
  <p>Masz prawo dostępu do swoich danych, ich sprostowania, usunięcia, ograniczenia przetwarzania i przeniesienia, prawo sprzeciwu oraz prawo wniesienia skargi do Prezesa Urzędu Ochrony Danych Osobowych.</p>
  <h2>Pliki cookie</h2>
  <p>Strona nie używa plików cookie analitycznych ani marketingowych. Jeśli to się zmieni, najpierw poprosimy o zgodę.</p>
</section>'''
    strona('polityka-prywatnosci/', 'Polityka prywatności | Metalove',
           'Jak Metalove przetwarza dane osobowe z formularzy na stronie.', tresc)


def strona_dziekujemy():
    gl = 1
    tresc = f'''<section class="kontener strona-naglowek">
  <p class="etykieta">Dziękujemy</p>
  <h1>Mamy Twoje zapytanie</h1>
  <p class="lead">Odezwiemy się w ciągu {e(F["czas_odpowiedzi"])}. W międzyczasie możesz obejrzeć nasze realizacje.</p>
  <div class="przyciski"><a class="przycisk" href="{href("realizacje/", gl)}">Zobacz realizacje</a><a class="przycisk przycisk--obrys" href="{href("", gl)}">Strona główna</a></div>
</section>'''
    strona('dziekujemy/', 'Dziękujemy | Metalove', 'Potwierdzenie wysłania zapytania.', tresc, noindex=True)


def strona_404():
    # 404.html leży w katalogu głównym, więc linki liczymy od niego
    tresc = f'''<section class="kontener strona-naglowek">
  <p class="etykieta">Błąd 404</p>
  <h1>Nie ma takiej strony</h1>
  <p class="lead">Może została przeniesiona. Zacznij od strony głównej albo zobacz realizacje.</p>
  <div class="przyciski"><a class="przycisk" href="/">Strona główna</a><a class="przycisk przycisk--obrys" href="/realizacje/">Realizacje</a></div>
</section>'''
    head = glowa('Nie ma takiej strony | Metalove', 'Strona nie istnieje.', 0, '', noindex=True)
    head = head.replace('href="assets/', 'href="/assets/').replace('href="./', 'href="/')
    cialo = f'{naglowek(0, "")}\n<main id="tresc">\n{tresc}\n</main>\n{stopka(0)}'
    cialo = cialo.replace('href="./"', 'href="/"')
    for c, _, _ in MENU + [('wycena/', '', ''), ('polityka-prywatnosci/', '', '')]:
        cialo = cialo.replace(f'href="{c}"', f'href="/{c}"')
    for k in T.KATEGORIE:
        cialo = cialo.replace(f'href="{k["slug"]}/"', f'href="/{k["slug"]}/"')
    doc = f'<!doctype html>\n<html lang="pl">\n<head>\n{head}\n</head>\n<body>\n{cialo}\n<script src="/assets/main.js" defer></script>\n</body>\n</html>\n'
    with open(os.path.join(OUT, '404.html'), 'w', encoding='utf-8') as f:
        f.write(doc)


def mapa_strony():
    d = (F['domena'] or '').rstrip('/')
    robots = 'User-agent: *\nAllow: /\n'
    if d:
        sciezki = ['', 'realizacje/', 'oferta/', 'wycena/', 'dla-projektantow/', 'polityka-prywatnosci/']
        sciezki += [k['slug'] + '/' for k in T.KATEGORIE]
        sciezki += ['realizacje/' + r['slug'] + '/' for r in T.REALIZACJE]
        urls = ''.join(f'<url><loc>{d}/{s}</loc></url>' for s in sciezki)
        with open(os.path.join(OUT, 'sitemap.xml'), 'w', encoding='utf-8') as f:
            f.write(f'<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">{urls}</urlset>\n')
        robots += f'Sitemap: {d}/sitemap.xml\n'
    with open(os.path.join(OUT, 'robots.txt'), 'w', encoding='utf-8') as f:
        f.write(robots)


def zasoby():
    src = os.path.join(ROOT, 'assets')
    dst = os.path.join(OUT, 'assets')
    os.makedirs(os.path.join(dst, 'fonts'), exist_ok=True)
    for fn in os.listdir(os.path.join(src, 'fonts')):
        shutil.copy2(os.path.join(src, 'fonts', fn), os.path.join(dst, 'fonts', fn))
    css = open(os.path.join(src, 'fonts.css'), encoding='utf-8').read() + '\n' + \
        open(os.path.join(src, 'style.css'), encoding='utf-8').read()
    with open(os.path.join(dst, 'style.css'), 'w', encoding='utf-8') as f:
        f.write(css)
    shutil.copy2(os.path.join(src, 'main.js'), os.path.join(dst, 'main.js'))
    shutil.copy2(os.path.join(src, 'favicon.svg'), os.path.join(dst, 'favicon.svg'))
    for nazwa in ('style.css', 'main.js'):
        WERSJA[nazwa] = hashlib.sha1(open(os.path.join(dst, nazwa), 'rb').read()).hexdigest()[:8]
    # ikona dla iPhone'a: mosiężny pierścień na grafitowym tle
    ik = Image.new('RGB', (720, 720), (22, 24, 27))
    ImageDraw.Draw(ik).ellipse((180, 180, 540, 540), outline=(201, 162, 74), width=78)
    ik.resize((180, 180), Image.LANCZOS).save(os.path.join(dst, 'apple-touch-icon.png'))
    with open(os.path.join(OUT, '_headers'), 'w', encoding='utf-8') as f:
        f.write('/assets/fonts/*\n  Cache-Control: public, max-age=31536000, immutable\n'
                '/img/*\n  Cache-Control: public, max-age=2592000\n')


def main():
    if os.path.isdir(OUT):
        for fn in os.listdir(OUT):
            p = os.path.join(OUT, fn)
            if fn == 'img':
                continue
            shutil.rmtree(p) if os.path.isdir(p) else os.remove(p)
    os.makedirs(OUT, exist_ok=True)
    zasoby()
    przygotuj_zdjecia()
    strona_glowna()
    strona_realizacji_lista()
    for r in T.REALIZACJE:
        strona_realizacji(r)
    strona_oferty()
    for k in T.KATEGORIE:
        strona_kategorii(k)
    strona_wyceny()
    strona_projektantow()
    strona_polityki()
    strona_dziekujemy()
    if not PODGLAD:
        strona_404()
    mapa_strony()
    stron = sum(1 for _, _, pliki in os.walk(OUT) for p in pliki if p.endswith('.html'))
    print(f'Gotowe: {stron} stron w {os.path.relpath(OUT, ROOT)}/')


if __name__ == '__main__':
    main()
