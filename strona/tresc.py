"""Treść strony Metalove. Tu zmieniasz teksty, dane kontaktowe i realizacje.

Po zmianie uruchom:  python3 build.py
Pola ustawione na None nie trafiają na stronę (w podglądzie widać w ich miejscu [do uzupełnienia]).
"""

FIRMA = dict(
    nazwa='Metalove',
    miasto='Warszawa',
    zasieg='Warszawa i okolice, do ok. 50 km',
    telefon=None,          # np. '+48 600 000 000'
    email=None,            # np. 'kontakt@metalove.pl'
    instagram=None,        # pełny link do profilu
    facebook=None,         # pełny link do strony
    domena=None,           # np. 'https://metalove-warszawa.pl'; potrzebna do mapy strony i podglądów linków
    forma='nierejestrowana',  # 'nierejestrowana' (start bez firmy) albo 'firma' (po rejestracji w CEIDG)
    imie_nazwisko=None,    # do polityki prywatności przy działalności nierejestrowanej
    pelna_nazwa=None,      # przy formie 'firma': pełna nazwa z CEIDG
    adres=None,            # przy formie 'firma': adres firmy
    nip=None,              # przy formie 'firma'
    czas_odpowiedzi='48 godzin',  # obietnica z formularza wyceny; zmień, jeśli nie dacie rady
)

# Kategorie = podstrony oferty. Kolejność jak w menu i na stronie głównej.
KATEGORIE = [
    dict(
        slug='schody-metalowe',
        etykieta='Schody',
        nazwa='Schody i balustrady',
        h1='Schody metalowe na wymiar',
        tytul_seo='Schody metalowe na wymiar, Warszawa | Metalove',
        opis_seo='Schody na stalowej belce z dębowymi stopniami, balustrady i pochwyty. Projekt, wykonanie i montaż w Warszawie i okolicach.',
        krotko='Schody na stalowej belce, balustrady i pochwyty.',
        lead='Schody to największy mebel w domu. Projektujemy je pod konkretne wnętrze: kształt biegu, wysokość kondygnacji, drewno na stopnie i kolor stali dobieramy razem z Tobą albo z Twoim architektem.',
        zakres=[
            'Schody na stalowej belce z drewnianymi stopniami',
            'Schody proste i ze zmianą kierunku',
            'Balustrady z prętów i stalowe pochwyty',
            'Malowanie proszkowe w wybranym kolorze RAL',
        ],
        faq=[
            ('Od czego zależy cena schodów metalowych?',
             'Od liczby stopni, kształtu biegu, rodzaju drewna, balustrady i warunków montażu. Orientacyjne widełki podajemy po pierwszej rozmowie, dokładną cenę po pomiarze.'),
            ('Czy przyjeżdżacie na pomiar?',
             'Tak. Pomiar robimy w Warszawie i okolicach, do ok. 50 km.'),
            ('Czy wykonacie schody według projektu architekta?',
             'Tak. Pracujemy na rysunkach projektanta, a przed produkcją pokazujemy rysunek warsztatowy do akceptacji.'),
            ('Jak długo trwa realizacja?',
             'To zależy od projektu. Termin wykonania i montażu podajemy w ofercie.'),
        ],
        zdjecie='schody-na-belce',
    ),
    dict(
        slug='scianki-i-drzwi-loftowe',
        etykieta='Loft',
        nazwa='Ścianki i drzwi loftowe',
        h1='Ścianki i drzwi loftowe na wymiar',
        tytul_seo='Ścianki i drzwi loftowe na wymiar, Warszawa | Metalove',
        opis_seo='Drzwi i ścianki loftowe ze stali, ramy nad wyspą kuchenną, szkło przejrzyste lub ryflowane. Na wymiar, z montażem w Warszawie i okolicach.',
        krotko='Drzwi, ścianki i ramy z czarnej stali i szkła.',
        lead='Czarna rama ze stali dzieli przestrzeń, ale nie zabiera światła. Robimy drzwi, ścianki i ramy loftowe na wymiar, od zabudowy łazienki po ramę nad wyspą kuchenną.',
        zakres=[
            'Drzwi loftowe do łazienki, garderoby i salonu',
            'Ścianki działowe z przeszkleniem',
            'Ramy nad wyspą kuchenną i podziały przestrzeni',
            'Szkło przejrzyste albo ryflowane',
        ],
        faq=[
            ('Ile kosztuje ścianka loftowa?',
             'Cena zależy od wymiarów, liczby podziałów, rodzaju szkła i tego, czy w ściance są drzwi. Orientacyjne widełki podamy po rozmowie i zdjęciach miejsca.'),
            ('Czy drzwi loftowe nadają się do łazienki?',
             'Tak. Jedną z naszych realizacji są drzwi ze szkłem ryflowanym, które zamykają wnękę w łazience.'),
            ('Czy rama musi mieć szkło?',
             'Nie. Nad wyspą kuchenną zrobiliśmy otwartą stalową ramę bez szkła, która wydziela kuchnię z salonu.'),
        ],
        zdjecie='lazienka-drzwi-loft',
    ),
    dict(
        slug='meble-ze-stali',
        etykieta='Meble',
        nazwa='Meble ze stali',
        h1='Meble ze stali na wymiar',
        tytul_seo='Meble ze stali na wymiar: stoły, regały, półki | Metalove',
        opis_seo='Stoły na stalowych podstawach, konstrukcje od podłogi do sufitu i półki z giętej blachy. Stal łączymy z drewnem, kamieniem i mosiądzem.',
        krotko='Stoły, regały, półki i konstrukcje od podłogi do sufitu.',
        lead='Robimy meble, których nie znajdziesz w sklepie: od stołu z rzeźbionym blatem po półki z giętej blachy. Stal łączymy z drewnem, kamieniem i mosiądzem.',
        zakres=[
            'Stoły i podstawy do stołów',
            'Regały i konstrukcje od podłogi do sufitu',
            'Półki z giętej blachy',
            'Ławki, konsole i stoliki',
        ],
        faq=[
            ('Czy mogę przynieść własny projekt albo zdjęcie inspiracji?',
             'Tak. Wystarczy zdjęcie, szkic albo link. Pomożemy dopracować wymiary, konstrukcję i materiały.'),
            ('Jakie wykończenia stali są możliwe?',
             'Najczęściej malowanie proszkowe w wybranym kolorze RAL. Do stali dobieramy drewno, kamień, szkło albo mosiądz.'),
        ],
        zdjecie='polki-zygzak',
    ),
    dict(
        slug='lustra-na-wymiar',
        etykieta='Lustra',
        nazwa='Lustra',
        h1='Lustra na wymiar',
        tytul_seo='Lustra na wymiar w metalowej ramie i z LED | Metalove',
        opis_seo='Lustra okrągłe, owalne i prostokątne w cienkiej stalowej ramie albo z podświetleniem LED. Na wymiar, z montażem w Warszawie i okolicach.',
        krotko='Lustra w stalowej ramie i z podświetleniem LED.',
        lead='Lustro w cienkiej stalowej ramie potrafi zmienić cały przedpokój. Robimy lustra okrągłe, owalne i prostokątne z zaokrąglonymi narożnikami, w ramie albo z podświetleniem LED.',
        zakres=[
            'Lustra w ramie ze stali',
            'Lustra z podświetleniem LED',
            'Duże lustra do przedpokoju i salonu',
            'Lustra do salonów fryzjerskich i kosmetycznych',
        ],
        faq=[
            ('Jakie kształty i wymiary są możliwe?',
             'Okrągłe, owalne i prostokątne, także z zaokrąglonymi narożnikami. Wymiar dobieramy do ściany.'),
            ('Czy lustro z LED trzeba podłączyć do prądu?',
             'Tak, podświetlenie potrzebuje zasilania. Najlepiej zaplanować przewód przed wykończeniem ściany; podpowiemy, jak to przygotować.'),
        ],
        zdjecie='lustro-led',
    ),
    dict(
        slug='wnetrza-komercyjne',
        etykieta='Lokale',
        nazwa='Salony i lokale',
        h1='Meble do salonów i lokali na wymiar',
        tytul_seo='Meble do salonów i lokali na wymiar, Warszawa | Metalove',
        opis_seo='Stanowiska do salonów fryzjerskich i kosmetycznych, konstrukcje i regały do lokali, lustra z LED. Stal w wybranym kolorze, montaż w Warszawie.',
        krotko='Stanowiska, regały i lustra do salonów i lokali.',
        lead='Wnętrze lokalu to wizytówka firmy. Robimy stanowiska, regały i lustra, które pasują do marki i dobrze wyglądają na zdjęciach klientów.',
        zakres=[
            'Stanowiska do salonów fryzjerskich i kosmetycznych',
            'Regały i konstrukcje ekspozycyjne',
            'Lustra z podświetleniem LED',
            'Stal w kolorze marki, na przykład złotym',
        ],
        faq=[
            ('Czy pracujecie na projekcie architekta wnętrz?',
             'Tak. Wykonujemy elementy ze stali, szkła i luster według projektu, a przed produkcją pokazujemy rysunek do akceptacji.'),
            ('Jak szybko możecie zrobić wyposażenie lokalu?',
             'Termin zależy od zakresu. Podajemy go w ofercie, żeby można było zaplanować otwarcie.'),
        ],
        zdjecie='salon-stanowiska',
    ),
    dict(
        slug='kolekcja',
        etykieta='Kolekcja',
        nazwa='Kolekcja',
        h1='Stoliki z mosiądzu, marmuru i dębu',
        tytul_seo='Stoliki z mosiądzu i marmuru, kolekcja | Metalove',
        opis_seo='Stoliki kawowe i pomocnicze: mosiężna obręcz, blat z marmuru, półka z dębu. Robimy je w pracowni w Warszawie, także w innych wymiarach.',
        krotko='Stoliki z mosiądzu, marmuru i dębu.',
        lead='Mosiądz, marmur i dąb. Stoliki z naszej kolekcji robimy w pracowni, a wymiar i kamień możesz dobrać do swojego wnętrza.',
        zakres=[
            'Stolik kawowy z zielonym marmurem',
            'Stolik pomocniczy z białym marmurem',
            'Inne wymiary i rodzaje kamienia na zamówienie',
        ],
        faq=[
            ('Czy mogę zamówić stolik w innym wymiarze?',
             'Tak. Średnicę, wysokość i rodzaj kamienia dopasujemy do Twojego wnętrza.'),
            ('Ile kosztuje stolik?',
             'Cena zależy od wymiaru i kamienia. Napisz, który model Cię interesuje, a podamy cenę.'),
        ],
        zdjecie='stolik-zielony-marmur',
    ),
]

# Realizacje. Pola lokalizacja, rok i projektant pokażą się dopiero po wpisaniu.
REALIZACJE = [
    dict(
        slug='salon-zlote-stanowiska',
        tytul='Złote stanowiska z owalnymi lustrami LED',
        kategoria='wnetrza-komercyjne',
        zdjecia=[('salon-stanowiska', 'Dwa złote stanowiska fryzjerskie z owalnymi lustrami podświetlanymi LED')],
        materialy=['stal w złotym kolorze', 'lustra z podświetleniem LED', 'półki z białym blatem'],
        opis='Dwa stanowiska do salonu fryzjerskiego. Wysokie ramy z kwadratowych profili, owalne lustra w pierścieniu LED i półki na kosmetyki tworzą ścianę, która od razu przyciąga wzrok.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='schody-z-balustrada',
        tytul='Schody na belce z balustradą z prętów',
        kategoria='schody-metalowe',
        zdjecia=[('schody-balustrada', 'Schody na stalowej belce z dębowymi stopniami i balustradą z pionowych prętów')],
        materialy=['stalowa belka nośna', 'stopnie z dębu', 'balustrada z prętów', 'stalowy pochwyt'],
        opis='Schody ze zmianą kierunku na stalowej belce, z dębowymi stopniami. Balustrada z cienkich pionowych prętów biegnie wzdłuż schodów aż na piętro.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='schody-na-jednej-belce',
        tytul='Schody na jednej belce z dębowymi stopniami',
        kategoria='schody-metalowe',
        zdjecia=[('schody-na-belce', 'Schody na jednej stalowej belce z dębowymi stopniami i cieniem na ścianie')],
        materialy=['stalowa belka nośna', 'stopnie z dębu'],
        opis='Jedna stalowa belka niesie cały bieg, a stopnie wyglądają, jakby unosiły się w powietrzu. Przy bocznym świetle schody rysują na ścianie własny cień.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='drzwi-loftowe-w-lazience',
        tytul='Drzwi loftowe ze szkłem ryflowanym w łazience',
        kategoria='scianki-i-drzwi-loftowe',
        zdjecia=[
            ('lazienka-drzwi-loft', 'Łazienka z czarnymi drzwiami loftowymi, umywalką i czarno-białą podłogą'),
            ('drzwi-loft-zblizenie', 'Drzwi loftowe z czarnej stali i szkła ryflowanego'),
        ],
        materialy=['czarna rama stalowa', 'szkło ryflowane'],
        opis='Drzwi z czarnej stali i ryflowanego szkła zamykają wnękę w łazience. Szkło wpuszcza światło, ale zasłania to, co za nim, a czarna rama pasuje do czarno-białej podłogi.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='rama-nad-wyspa-kuchenna',
        tytul='Stalowa rama nad wyspą kuchenną',
        kategoria='scianki-i-drzwi-loftowe',
        zdjecia=[('przeszklenie-kuchnia', 'Czarna stalowa rama nad wyspą kuchenną z trzema lampami wiszącymi')],
        materialy=['czarne profile stalowe'],
        opis='Otwarta rama z czarnych profili wydziela kuchnię z salonu, a w jej wnętrzu wiszą lampy nad wyspą. Lekka konstrukcja nie zasłania światła.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='rama-art-deco',
        tytul='Geometryczna rama od podłogi do sufitu',
        kategoria='meble-ze-stali',
        zdjecia=[('regal-art-deco', 'Wysoka rama z czarnych profili stalowych z geometrycznym wzorem w stylu art déco')],
        materialy=['czarne profile stalowe'],
        opis='Wysoka rama z czarnych profili stalowych sięga od podłogi do sufitu. Geometryczny wzór w duchu art déco sprawia, że konstrukcja jest ozdobą sama w sobie.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='polki-zygzaki',
        tytul='Półki-zygzaki z giętej blachy',
        kategoria='meble-ze-stali',
        zdjecia=[('polki-zygzak', 'Cztery półki z giętej blachy w kształcie zygzaka na białej ścianie')],
        materialy=['gięta blacha stalowa'],
        opis='Cztery pasy giętej blachy układają się na ścianie w zygzaki, które są jednocześnie półkami i dekoracją.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='stol-na-stalowej-podstawie',
        tytul='Stół z rzeźbionym blatem na stalowej podstawie',
        kategoria='meble-ze-stali',
        zdjecia=[('stol', 'Stół z białym rzeźbionym blatem na czarnej stalowej podstawie ze skośnymi nogami')],
        materialy=['stalowa podstawa ze skośnymi nogami', 'biały blat'],
        opis='Biały blat z rzeźbionym spodem stoi na stalowej podstawie ze skośnymi nogami. Lekka konstrukcja zostawia dużo miejsca na krzesła.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='okragle-lustro-i-lawka',
        tytul='Okrągłe lustro i dębowa ławka',
        kategoria='lustra-na-wymiar',
        zdjecia=[('lustro-lawka', 'Duże okrągłe lustro w cienkiej ramie i niska dębowa ławka na skrzyżowanych stalowych nogach')],
        materialy=['lustro w cienkiej stalowej ramie', 'ławka z dębu', 'stalowe nogi w kształcie X'],
        opis='Duże okrągłe lustro w cienkiej ramie i niska ławka na skrzyżowanych stalowych nogach. Komplet do przedpokoju na ścianie wyłożonej dębem.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='owalne-lustro-led',
        tytul='Owalne lustro z podświetleniem LED',
        kategoria='lustra-na-wymiar',
        zdjecia=[('lustro-led', 'Owalne lustro z fazowaną krawędzią podświetlone od tyłu ciepłym światłem LED')],
        materialy=['lustro z fazowaną krawędzią', 'podświetlenie LED'],
        opis='Owalne lustro z fazowaną krawędzią, podświetlone od tyłu. Światło odbija się od ściany i tworzy wokół tafli miękką poświatę.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='wysokie-lustro-w-ramie',
        tytul='Wysokie lustro w cienkiej czarnej ramie',
        kategoria='lustra-na-wymiar',
        zdjecia=[('lustro-korytarz', 'Wysokie lustro z zaokrąglonymi narożnikami w cienkiej czarnej ramie w korytarzu')],
        materialy=['lustro', 'stalowa rama malowana na czarno'],
        opis='Lustro w pełnej wysokości z zaokrąglonymi narożnikami. Cienka czarna rama obrysowuje taflę i nie przytłacza wąskiego korytarza.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='stolik-mosiadz-zielony-marmur',
        tytul='Stolik kawowy: mosiądz, zielony marmur, dąb',
        kategoria='kolekcja',
        zdjecia=[('stolik-zielony-marmur', 'Okrągły stolik kawowy z blatem z zielonego marmuru, mosiężną obręczą i dębową półką')],
        materialy=['mosiądz', 'zielony marmur', 'dąb'],
        opis='Niski stolik kawowy z blatem z ciemnozielonego marmuru w mosiężnej obręczy i dębową półką na książki.',
        lokalizacja=None, rok=None, projektant=None,
    ),
    dict(
        slug='stolik-mosiadz-bialy-marmur',
        tytul='Stolik pomocniczy: mosiądz, biały marmur, dąb',
        kategoria='kolekcja',
        zdjecia=[('stolik-bialy-marmur', 'Wysoki okrągły stolik z blatem z białego marmuru, mosiężną obręczą i dębową półką')],
        materialy=['mosiądz', 'biały marmur', 'dąb'],
        opis='Wysoki stolik z mosiężną obręczą, blatem z białego marmuru i dębową półką. Pasuje obok fotela albo łóżka.',
        lokalizacja=None, rok=None, projektant=None,
    ),
]

# Realizacje na stronie głównej (kolejność ma znaczenie).
WYROZNIONE = [
    'salon-zlote-stanowiska', 'schody-z-balustrada', 'drzwi-loftowe-w-lazience',
    'rama-nad-wyspa-kuchenna', 'owalne-lustro-led', 'stolik-mosiadz-zielony-marmur',
]

# Punkt ostrości przy kadrowaniu miniatur (domyślnie środek zdjęcia).
FOKUS = {
    'salon-stanowiska': '50% 40%',
    'schody-balustrada': '45% 55%',
    'lazienka-drzwi-loft': '50% 45%',
    'przeszklenie-kuchnia': '50% 50%',
    'lustro-led': '50% 50%',
    'regal-art-deco': '50% 40%',
    'stol': '45% 60%',
}

# Próbki materiałów: wycinek ze zdjęcia (plik, x, y, szerokość, wysokość) albo kolor.
MATERIALY = [
    dict(nazwa='Stal malowana proszkowo', uwaga='czarny mat albo wybrany kolor RAL', kolor='steel'),
    dict(nazwa='Mosiądz', uwaga='obręcze, nogi, detale', wycinek=('stolik-bialy-marmur', 700, 432, 180, 60)),
    dict(nazwa='Biały marmur', uwaga='blaty stolików', wycinek=('stolik-bialy-marmur', 480, 200, 360, 120)),
    dict(nazwa='Zielony marmur', uwaga='blaty stolików', wycinek=('stolik-zielony-marmur', 600, 260, 420, 140)),
    dict(nazwa='Dąb', uwaga='stopnie, półki, blaty', wycinek=('stolik-zielony-marmur', 520, 820, 480, 160)),
    dict(nazwa='Szkło ryflowane', uwaga='drzwi i ścianki loftowe', wycinek=('drzwi-loft-zblizenie', 315, 640, 240, 80)),
]

KROKI = [
    ('Rozmowa', 'Opisz pomysł i dołącz zdjęcie miejsca albo inspiracji. Oddzwonimy z pierwszymi widełkami cenowymi.'),
    ('Pomiar i projekt', 'Przyjeżdżamy na pomiar. Ustalamy wymiary, materiały i wykończenie, a przy większych projektach pokazujemy rysunek do akceptacji.'),
    ('Wykonanie', 'Tniemy, spawamy i wykańczamy w pracowni. Stal łączymy z drewnem, kamieniem, szkłem albo mosiądzem.'),
    ('Montaż', 'Przywozimy gotowy element i montujemy go na miejscu, w Warszawie i okolicach.'),
]

DLA_PROJEKTANTOW = [
    ('Konsultacja przed wyceną', 'Podpowiemy, co da się zrobić ze stali, jak to zamocować i co wpływa na cenę.'),
    ('Rysunek do akceptacji', 'Zanim zaczniemy spawać, dostajesz rysunek warsztatowy z wymiarami i detalami.'),
    ('Próbki wykończeń', 'Kolory proszkowe, mosiądz, szkło ryflowane. Pokażesz klientowi materiał, a nie tylko zdjęcie.'),
    ('Zdjęcia do portfolio', 'Po montażu przekażemy zdjęcia realizacji, które możesz pokazać u siebie.'),
]
