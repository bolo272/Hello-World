# Kocie Ławki na Androida

Natywna wersja gry: Kotlin + Jetpack Compose, bez WebView. Koty są rysowane wektorowo (gradienty, kontury, błyski w oczach), a wszystkie animacje działają w Compose:

- koty spadają kaskadą na ławki na starcie poziomu,
- przeskok po łuku ze zgnieceniem przy lądowaniu,
- oddychanie, mruganie i machanie ogonem,
- podskok i serduszka przy skompletowanej ławce,
- konfetti i sprężynowe okno przy wygranej,
- wibracje przy podnoszeniu i lądowaniu.

Logika (generator poziomów, solver, reguły) jest przeniesiona 1:1 z wersji webowej, więc poziomy są identyczne. Dźwięk jest na razie wyłączony.

## Budowanie

```
./gradlew assembleRelease      # app/build/outputs/apk/release/app-release.apk
./gradlew testDebugUnitTest    # test: 30 poziomów, każdy rozwiązywalny
./gradlew recordPaparazziDebug # zrzuty ekranu bez emulatora → app/src/test/snapshots/images
```

Wymaga JDK 17+ i Android SDK (platforma 35). Wersja release jest podpisana kluczem debug, żeby dało się ją od razu zainstalować; do Google Play trzeba podpiąć własny klucz w `app/build.gradle.kts`.

Czcionki Bagel Fat One i Nunito: SIL Open Font License, z Google Fonts.
