# android-vinyla
## Doel van de app:
De bedoeling is om met een eenvoudige login je favoriete artiesten te zien in een handige lijst. Deze data haal ik uit mijn vorige project, vinyla, van het vak webapplicaties IV.

De gebruiker kan meerdere artiesten aanduiden door op de foto van een artiest te drukken. Vervolgens kan de gebruiker ervoor kiezen om een persoonlijk station / playlist af te spelen van de gekozen artiest(en). Hiervoor verwijst de app automatisch naar door de gebruiker gekozen muziek-streaming app. Deze kan worden ingesteld via de instellingen knop.

## Login gegevens voor leerkrachten / HOGENT:
- email: webiv@hogent.be
- wachtwoord: H0gentP@ss

U mag natuurlijk ook altijd zelf een account aanmaken! :)  
Wachtwoorden worden automatisch geëncrypteerd in de databank.  

Vergeet niet om na het aanmaken van uw account naar de website van vinyla te surfen om je favoriete albums te kunnen toevoegen. Hiervoor kan u gebruik maken van de web knop, naast de create station knop.

Kleine toelichting: de refresh knop werkt niet naar behoren. De data wordt effectief opnieuw opgehaald maar de lijst van foto's wordt niet geüpdatet. Indien u de artiesten van de nieuwe albums wilt weergeven dient u de applicatie opnieuw te starten. Dit is een kleine bug en is mij niet gelukt om op te lossen.

## Hoe vinyla voor Android werkt:
#### Origine vinyla:
Voor het vak Webapplicaties IV had ik een databank, API en webapplicatie geschreven waarbij de gebruiker zijn / haar favoriete albums kan opzoeken en toevoegen als favoriet, om er zo een mooi overzicht van te kunnen hebben.

#### API's:

Deze API gebruik ik voor android-vinyla ook. De applicatie maakt gebruik van 3 verschillende API's om zo tot het einddoel te komen:

1. vinyla-API: zelfgeschreven om de gebruiker zijn / haar favoriete albums op te halen.
2. Spotify-Token-API: een Spotify API om een vertrouwde token te kunnen ontvangen. Deze token is nodig om gebruik te maken van de volledige Spotify-API in stap 3.
3. Spotify-API: een andere Spotify API om met een query de foto's op te halen van de gezochte artiesten. 

#### Werkwijze app:
1. Gebruiker logt in met zijn / haar logingegevens.
2. De app communiceert met vinyla-api om te controleren of de logingegevens correct zijn en krijgt een BearerAuth Token terug.
3. Er wordt gecommuniceert met de Spotify-Token-API om een vertrouwde BearerAuth Token te krijgen van Spotify.
4. Met de vinyla-token worden alle favoriete albums van vinyla gehaald.
5. Aantal albums worden gefilterd per artiest, gesorteerd op hoe vaak een album van een artiest voorkomt. Deze lijst wordt gelimiteerd tot maximaal 30 artiesten.
6. Aangezien Spotify een limiet heeft gesteld van batch-requests worden alle artiestenfoto's één voor één opgehaald van Spotify-API, samen met de Spotify-token.
7. De gebruiker stelt zijn / haar favoriete muziekstreamingdienst in via de instellingen knop.
8. De gebruiker duidt enkele artiesten aan en klikt vervolgens op de 'create station' knop om zo een persoonlijk station te ontvangen op zijn / haar gekozen steamingservice. (Het effectief afspelen van de muziek is niet geïmplementeerd.)

## Vereisten opdracht die al toegelicht kunnen worden:
- UI opbouw: Er werd zoveel mogelijk gewerkt met recyclerviews zodat het scherm altijd wordt aangepast naargelang de schermgrootte. Daarnaast zijn er ook vele navigaties geanimeerd.
- UI technisch: Er worden binding adapters en gewone binding gebruikt. Er werd voor gezorgd dat de app een unieke en mooie look & feel heeft.
- Testing: Alle fragments worden getest met behulp van Espresso.
- Codekwaliteit: De meeste functies zijn onderverdeeld in kleinere subfuncties om de code duidelijker te maken. Daarnaast werden de meeste functies uitgelegd door boven de functies annotaties te schrijven.
- Lifecycle: Aangezien de applicatie geen gebruik maakt van CPU-heavy animaties werd er weinig gebruik gemaakt van een custom lifecycle. Enkel bij de onResume van het welkom scherm wordt er gecontroleerd of de applicatie alreeds een ingelogde gebruiker heeft door de Room Database te controleren.
- Netwerk: Er worden heel wat API-calls gebruikt met behulp van Retrofit, namelijk naar vinyla-api, spotify-token-api & spotify-api.
- Persistentie: De applicatie is voorzien van een Room Database die de gebruiker zelf opslaat en de instellingen die werden gekozen door de gebruiker. Deze worden automatisch opgehaald bij het openen van de app.

## Screenshots van de applicatie:
Welkom scherm: de gebruiker wordt begroet door een welkom scherm met een willekeurige achtergrond van albumhoezen.

<img width="913" alt="image" src="https://user-images.githubusercontent.com/56795157/185234870-dca9bb9d-d04e-41fd-9db0-adf506137ac1.png">
&nbsp;

Login en registreer scherm: alle gegevens zoals wachtwoord- en emailvereisten worden gecontroleerd. Ook een al gebruikt emailadres kan niet meer gebruikt worden.

<img width="913" alt="image" src="https://user-images.githubusercontent.com/56795157/185236022-45749d01-641a-4c63-b8a2-efb27a36bf79.png">
&nbsp;

Hoofd scherm: de gebruiker kan meerdere artiesten aanduiden en vervolgens klikken op 'create station'. De streamingservice kan ingesteld worden via de instellingen knop.

<img width="913" alt="image" src="https://user-images.githubusercontent.com/56795157/185237323-1c091a17-e35d-41b0-9d69-3a3097c4179a.png">

Kleine toelichting bij de schermen:  
Er zijn niet enorm veel fragments gebruikt, maar het hoofdscherm bevat enorm veel code. De popup voor de instellingen of zelfs de redirect popup zouden ook aparte fragments kunnen zijn waardoor de applicatie groter lijkt. Maar dit zou volgens mij de look & feel van de applicatie beïnvloeden.

## Testen:
Alle schermen worden getest met behulp van het Espresso framework. Onderliggende code zonder de schermen effectief te gebruiken zijn niet gelukt om te testen.  


De welcomeFragmentVisible test werkt alleen wanneer deze als eerste wordt uitgevoerd. Anders denkt de applicatie dat er al een gebruiker is ingelogd en wordt het welkoms scherm niet weergegeven.

<img width="913" alt="image" src="https://user-images.githubusercontent.com/56795157/185238459-71be0520-cdfe-4eb5-b018-f5e26982c8e9.png">
<img width="913" alt="image" src="https://user-images.githubusercontent.com/56795157/185238986-173ae532-3136-45f3-b20b-b147db84157b.png">

## Eerste designs:
Designs gemaakt in Figma met de UI van iOS maar zijn geprogrammeerd voor Android.

<img width="913" alt="image" src="https://user-images.githubusercontent.com/56795157/181994703-c37e5c7d-38c4-4fd8-8b5a-3123cd276c2c.png">

<img width="893" alt="image" src="https://user-images.githubusercontent.com/56795157/181994616-983e3cd1-9089-4b73-9b20-e298dd5f2ace.png">
