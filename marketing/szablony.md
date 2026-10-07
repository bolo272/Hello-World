# Szablony i gotowce

Do planu: [`plan-marketingowy.md`](plan-marketingowy.md). W nawiasach kwadratowych wpisz własne dane.

---

## A. Lista ujęć po montażu (dla ekipy)

**Przed zdjęciem (5–10 minut)**
- Wynieś narzędzia, odkurzacz, kartony, folie, kable i drabinę. Leżaczek też.
- Przetrzyj szkło, lustra i stal. Odciski palców na zdjęciu widać bardziej niż na żywo.
- Światło dzienne. Wyłącz żarówki o innej barwie. Wieczorem lampa LED z boku, bez lampy błyskowej.

**Ujęcia** (telefon na statywie, przetarty obiektyw, siatka w aparacie, proste piony)
1. 2× szeroko: cała realizacja w kontekście wnętrza, raz poziomo, raz pionowo.
2. 3× detal: spaw, łączenie, faktura (mosiądz, marmur, drewno, szkło ryflowane).
3. 1× skala: dłoń albo człowiek przy realizacji (bez twarzy albo za zgodą).
4. 1× wideo pionowe 15–30 s: wolny przejazd telefonem, bez komentarza.
5. 1× przed/po: z tego samego miejsca co zdjęcie „przed”.
6. W pracowni: 2–3 klipy po 10 s (iskry, gięcie, polerowanie).

**Na koniec:** wrzuć wszystko do folderu `Realizacje/RRRR-MM Nazwa – gmina` i dopisz 2 zdania: co to jest, z czego, kto projektował.

---

## B. Pierwsza odpowiedź na zapytanie (automat A1, e-mail lub WhatsApp)

```
Dzień dobry [Imię],

dziękuję za wiadomość. [Jedno zdanie o pomyśle klienta, np. „Schody na jednej belce
z dębowymi stopniami będą świetnie wyglądać w takim wnętrzu.”]

Tu dwie podobne realizacje: [link 1], [link 2].

Orientacyjnie takie projekty zaczynają się od [X zł]. Dokładną wycenę przygotuję
po krótkiej rozmowie i pomiarze. Proszę wybrać termin 15-minutowej rozmowy:
[link do kalendarza] albo odpisać, kiedy mogę zadzwonić.

Pozdrawiam
[Imię Nazwisko], [Firma]
[telefon]
```

---

## C. Odpowiedź na zlecenie z Fixly, OLX lub Oferteo

```
Dzień dobry, robimy dokładnie takie rzeczy: na wymiar, ze stali, mosiądzu, szkła
i drewna, z montażem w [miasto] i okolicy. [Jeśli platforma pozwala: Nasze realizacje: link]

Żeby podać rzetelne widełki, potrzebuję orientacyjnych wymiarów, zdjęcia miejsca
i planowanego terminu. Wycenę przygotuję w 48 godzin.

[Imię], [Firma]
```

---

## D. Prośba o opinię (automat A4, SMS lub WhatsApp)

```
Dzień dobry, tu [Imię] z [Firma]. Dziękujemy za zaufanie przy [schodach]!
Jeśli są Państwo zadowoleni, krótka opinia w Google bardzo pomoże małej pracowni:
[link do opinii]. Pozdrawiamy!
```

Bez rabatów i nagród za opinię.

---

## E. List do projektanta (do paczki, wysyłany pocztą)

```
Dzień dobry [Imię],

nazywam się [Imię] i prowadzę [Firma], pracownię w [miasto], która robi rzeczy,
których nie ma w katalogu: schody, ścianki loftowe, meble i lustra ze stali,
mosiądzu, szkła i kamienia. W paczce jest próbka [materiał] i kilka naszych realizacji.

Projektantom oferujemy konsultację techniczną w 24 godziny, rysunki warsztatowe,
wzornik wykończeń i jasne zasady współpracy. Szczegóły są pod kodem QR.
Tam też można zapisać się na nasz comiesięczny przegląd nowych realizacji i materiałów.

Chętnie zobaczę, co Pani/Pan rysuje. Może zrobimy razem coś, czego jeszcze nie było.

[Podpis odręczny]
[telefon]
```

---

## F. Prompt dla AI: realizacja → treści (automat A3)

```
Jesteś copywriterem pracowni [Firma] z [miasto]. Pracownia robi na wymiar meble
i konstrukcje ze stali, mosiądzu, szkła, kamienia i drewna; specjalizuje się
w projektach niestandardowych.

Ton: rzeczowy i ciepły, z dumą rzemieślnika. Bez korporacyjnych ozdobników,
bez wykrzykników, bez emoji w tekstach na stronę.

Na podstawie notatki i listy zdjęć poniżej napisz:
1) Podstronę realizacji: tytuł z frazą typu „[co] na wymiar, [gmina]”,
   150–250 słów w układzie wyzwanie → rozwiązanie → materiały → efekt.
2) 3 opisy na Instagram, każdy z innej perspektywy (efekt, detal, proces),
   maksymalnie 6 hashtagów lokalnych i branżowych.
3) 5 opisów pinów na Pinterest: tytuł do 100 znaków i opis do 300 znaków,
   słowa kluczowe wplecione naturalnie.
4) Post na wizytówkę Google do 1 000 znaków, zakończony zaproszeniem do wyceny.

Nie wymyślaj faktów, których nie ma w notatce (wymiarów, cen, terminów, nazwisk).
Zwróć wynik jako JSON z polami: strona, instagram[], pinterest[], google.

Notatka: [2 zdania od montażysty]
Zdjęcia: [lista plików]
```

---

## G. Prompt dla AI: ocena zapytania (automat A1)

```
Jesteś asystentem pracowni [Firma] ([miasto], montaż do ok. 50 km). Pracownia robi
niestandardowe meble i konstrukcje ze stali, mosiądzu, szkła, kamienia i drewna.
Najchętniej bierze projekty unikalne, od ok. [X] zł.

Oceń zapytanie i zwróć wyłącznie JSON:
{
  "typ": "schody | loft | mebel | lustro | komercyjne | nietypowe | inne",
  "lokalizacja_w_zasiegu": true,
  "budzet": "kwota lub przedział z zapytania albo 'brak'",
  "termin": "...",
  "ocena": "A | B | C",
  "uzasadnienie": "1 zdanie",
  "streszczenie": "maksymalnie 3 zdania dla właściciela",
  "szkic_odpowiedzi": "odpowiedź do klienta w tonie szablonu B"
}

Zasady oceny:
- A: pasuje typem i budżetem albo wygląda na projekt nietypowy.
- B: brakuje danych, ale może pasować.
- C: poza zasięgiem, prosta naprawa lub spawanie albo budżet wyraźnie poniżej minimum.
Nie podawaj cen ani terminów, których nie ma w danych pracowni.

Zapytanie: [treść zapytania i pola formularza]
```

---

## H. Formularz wyceny: pola (Tally)

1. **Co chcesz stworzyć?** schody lub balustrada · ścianka lub drzwi loftowe · mebel (stół, regał, bar, konsola) · lustro · wnętrze komercyjne · coś nietypowego
2. **Opisz pomysł** (pole tekstowe)
3. **Zdjęcie miejsca, inspiracji albo szkic** (do 10 MB)
4. **Orientacyjne wymiary**
5. **Miejscowość**
6. **Budżet:** do 3 tys. zł · 3–10 tys. zł · 10–30 tys. zł · powyżej 30 tys. zł · nie wiem, doradźcie
7. **Termin:** jak najszybciej · 1–3 miesiące · 3–6 miesięcy · bez pośpiechu
8. **Jestem:** klientem prywatnym · projektantem · firmą
9. **Kontakt:** imię, telefon, e-mail, preferowana forma kontaktu
10. **Zgody:** kontakt w sprawie tego zapytania (wymagana) · comiesięczny przegląd realizacji e-mailem (dobrowolna) · link do polityki prywatności

---

## I. Klauzula do umowy: zdjęcia realizacji (do konsultacji z prawnikiem)

```
Klient wyraża zgodę na wykonanie przez Wykonawcę zdjęć i nagrań przedmiotu umowy
oraz na ich wykorzystanie w materiałach promocyjnych Wykonawcy (strona internetowa,
media społecznościowe, portfolio), bez podawania adresu i bez wizerunku osób.
Zgodę można w każdej chwili wycofać ze skutkiem na przyszłość.
```
