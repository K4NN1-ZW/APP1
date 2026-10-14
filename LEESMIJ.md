# Scheet App 💨

Een Android-app die een scheetgeluid laat horen elke keer dat je je telefoon oppakt.

De app reageert op twee dingen:
- **Het scherm gaat aan.** Dat werkt altijd betrouwbaar.
- **Beweging na stilliggen.** De bewegingssensor merkt het op als de telefoon minstens 2 seconden stil lag en daarna wordt opgepakt.

Tussen twee scheten zit minimaal 4 seconden.

## APK maken (zonder iets te installeren) via GitHub

1. Maak een gratis account op github.com en maak een nieuwe repository aan.
2. Upload alle bestanden uit deze map, inclusief de map `.github`.
   Gebruik daarvoor "Add file → Upload files" en sleep de inhoud erin.
3. Ga naar het tabblad **Actions**. De build "Bouw APK" start vanzelf en duurt ongeveer 3 minuten.
4. Klik op de voltooide run en download onderaan **ScheetApp-apk**. Dat is een zip met `app-debug.apk` erin.

## APK maken met Android Studio

Open deze map in Android Studio. Kies daarna *Build → Build App Bundle(s) / APK(s) → Build APK(s)*.

## Installeren op je telefoon

1. Zet de APK op je telefoon en tik erop.
2. Sta "installeren uit onbekende bronnen" toe.
3. Open de app, geef toestemming voor meldingen en tik op **Aanzetten**.

Er blijft een kleine melding staan zolang de app actief is. Android verplicht dat voor apps die op de achtergrond draaien.

**Tip:** sommige telefoons, zoals Samsung en Xiaomi, stoppen achtergrond-apps agressief. Zet daarom bij *Instellingen → Apps → Scheet App → Batterij* de optie op "Onbeperkt".
