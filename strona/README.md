# Strona Metalove

Statyczna strona pracowni (26 podstron): strona główna, realizacje, 6 podstron oferty, współpraca z projektantami, formularz wyceny, polityka prywatności. Bez frameworków i bez bazy danych, więc hosting może być darmowy.

| Plik / katalog | Co to jest |
|---|---|
| `tresc.py` | Wszystkie teksty, dane firmy i lista realizacji. Tu wprowadzasz zmiany. |
| `build.py` | Generator: z `tresc.py` i zdjęć buduje gotową stronę. |
| `assets/` | Style, skrypt, ikona i fonty (Archivo, IBM Plex, licencja OFL). |
| `zdjecia/` | Zdjęcia źródłowe (JPG). **Nie ma ich w repozytorium**, patrz niżej. |
| `public/` | Gotowa strona do wgrania na serwer. |

## Jak zbudować

```bash
pip install pillow
python3 build.py            # gotowa strona w public/
python3 build.py --podglad  # wersja do podglądu w Claude
```

## Zdjęcia

Repozytorium jest publiczne, a zdjęcia pokazują domy klientów, więc zdjęcia i katalog `public/img/` są w `.gitignore`. Zanim strona trafi do sieci:

1. Upewnij się, że klienci zgodzili się na publikację zdjęć (klauzula w `../marketing/szablony.md`, sekcja I).
2. Wrzuć zdjęcia po retuszu do `zdjecia/` pod tymi nazwami: `salon-stanowiska.jpg`, `schody-balustrada.jpg`, `schody-na-belce.jpg`, `regal-art-deco.jpg`, `przeszklenie-kuchnia.jpg`, `lazienka-drzwi-loft.jpg`, `drzwi-loft-zblizenie.jpg`, `lustro-korytarz.jpg`, `lustro-led.jpg`, `lustro-lawka.jpg`, `polki-zygzak.jpg`, `stol.jpg`, `stolik-bialy-marmur.jpg`, `stolik-zielony-marmur.jpg`.
3. Uruchom `python3 build.py`.

Nowa realizacja to nowy wpis w `REALIZACJE` w `tresc.py` i zdjęcie w `zdjecia/`.

## Do uzupełnienia przed publikacją

W `tresc.py`, w słowniku `FIRMA`:

- telefon, e-mail, Instagram, Facebook,
- domena (metalove.pl jest zajęta; np. `https://metalove-warszawa.pl`), potrzebna do mapy strony i podglądu linków w social mediach,
- dane do polityki prywatności (treść sprawdź z prawnikiem): przy starcie bez firmy (`forma='nierejestrowana'`) wystarczy imię i nazwisko; po rejestracji w CEIDG ustaw `forma='firma'` i wpisz pełną nazwę, adres i NIP,
- `czas_odpowiedzi`: strona obiecuje odpowiedź w 48 godzin; zmień, jeśli to za krótko.

W `REALIZACJE` możesz dopisać lokalizację (dzielnicę, bez adresu), rok i nazwisko projektanta. Pola puste nie pokazują się na stronie.

Do potwierdzenia: obietnice na stronie „Dla projektantów” (konsultacja przed wyceną, rysunek do akceptacji, próbki wykończeń, zdjęcia do portfolio).

## Publikacja

Najprościej przez **Netlify** (darmowy plan):

1. Wejdź na app.netlify.com/drop i przeciągnij folder `public/`. Strona działa po kilku sekundach.
2. W ustawieniach strony: *Forms → Enable form detection*, potem ponownie wgraj `public/`. Formularze wyceny i dla projektantów zaczną zbierać zgłoszenia (z załącznikami), a Netlify może wysyłać je na e-mail.
3. Podłącz domenę w *Domain management*.

Alternatywa: Cloudflare Pages. Tam formularz wymaga osobnej obsługi (np. Formspree albo funkcja Cloudflare).

Strona nie używa analityki ani plików cookie, więc nie potrzebuje banera zgód. Jeśli dodasz Google Analytics albo Pixel Meta, dodaj baner z Consent Mode v2 i zaktualizuj politykę prywatności.
