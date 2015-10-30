# Min Busskompi s
*Teknisk Dokumentation*

## Parse
För att snabbt kunna få till en lösning för kommunikation och datadelning mellan enheter valde vi att använda oss av Parse. Parse är en MBaaS med bland annat stöd för datalagring och Push-notifikationer.

All hantering av Parse i vår applikation ligger i modulen **[parsebuss](https://github.com/maffan/MinBusskompis/tree/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss)**. Här kommer en kort beskrivning av de viktigaste klasserna i denna modul.

**[ParseCloudManager](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/ParseCloudManager.java)** hanterar all interaktion med datalagringen i Parse. Det är till exempel via denna klass barn-klienter uppdaterar sin position och status. Det är även via denna klass som föräldrar-klienter kan hämta ny data om de barn de följer.
**[BussRelationMessenger](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussRelationMessenger.java)** är den klass som sköter kontinuerlig kommunikation mellan barn- och föräldrar-klienter. Här anger föräldrar-klienten vilka barn de vill “prenumerera” på och få meddelanden från. Det är även denna klass som barn använder för att skicka meddelanden till de föräldrar som följer barnet. Här finns till exempel funktioner för att meddela föräldrar att man sparat ny information i molnet som de kan hämta.

**[BussSyncer](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussSyncer.java)** är den klass som har ansvar för att synkronisera en barn- och föräldra-klient med varandra. Barnet använder sig av en **[BussSyncCodeGenerator](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussSyncCodeGenerator.java)** för att få fram en slumpmässig kod som sedan används under synkroniseringsprocessen. Under synkroniseringen skapar bägge klienter var sin temporär **[BussParseSyncMessenger](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussParseSyncMessenger.java)** som ansvarar för att skicka och ta emot synkroniseringsmeddelanden mellan enheterna. En översikt av hela synkroniseringsprocessen finns bifogat som diagram.

**[BussParsePushBroadcastReceiver](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussParsePushBroadcastReceiver.java)** är den klass Android kallar på då enheten tagit emot en push-notifikation från Parse. Den ansvarar för att undersöka vilken typ av meddelande som inkommit och leverera detta till rätt instans. Antingen ska den till en **[BussRelationMessenger](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussRelationMessenger.java)**, en **[BussParseSyncMessenger](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/parsebuss/BussParseSyncMessenger.java)** eller visas upp i telefonen som en push-notis.


## Backgroundtasks
Under barnets resa finns det mycket som behöver skötas kontinuerligt i bakgrunden. Vi har valt att lägga allt sådant i modulen **[backgroundtasks](https://github.com/maffan/MinBusskompis/tree/master/app/src/main/java/se/grupp4/minbusskompis/backgroundtasks)**. 

Viktiga klasser i **[backgroundtasks](https://github.com/maffan/MinBusskompis/tree/master/app/src/main/java/se/grupp4/minbusskompis/backgroundtasks)** är bland andra **[UpdateLocToParseService](https://github.com/maffan/MinBusskompis/blob/master/app/src/main/java/se/grupp4/minbusskompis/backgroundtasks/UpdateLocToParseService.java)** som är den tjänst som ansvarar för att kontinuerligt under hela resans gång se till att uppdatera barnets senaste position och status till Parse. Tjänsten använder sig i sin tur av en UpdateLocGpsAndSettings som hanterar en UpdateLocListener som är själva positionslyssnaren. 

Så fort barnet förflyttat sig ett visst avstånd eller en viss tid har gått så skickar tjänsten den senaste positionen och statusen till Parse och meddelar eventuella föräldrar om att det finns ny data att hämta.
WifiCheckerStart hanterar applikationens interaktion med bussarnas accesspunkter. Den använder sig i sin tur av två mer specialiserade klasser. WifiCheckerLookReceiver letar efter närliggande accesspunkter och försöker med hjälp av periodiska sökningar och signalstyrka avgöra när barnet gått på en buss. WifiCheckerLeaveReceiver vet vilken accesspunkt barnets buss har och avgör på liknande sett om barnet lämnat busssen.
Västtrafik/Innovationsplatformen
Modulen api hanterar all hämtning av information från Västtrafik och Electricitys innovationsplatform. Klassen Methods innehåller en mängd statiska metoder som är de som är tänkta att användas av applikationen. Denna klass kallar i sin tur på metoder i mer specialiserade klasser. InnovationPlatform innehåller metoder riktade mot electricitys innovationsplatform. Den har funktionalitet för att hämta information om specifika bussar. BusData innehåller statisk information om bussar. Här hittar man bland annat information om MAC-adresser till accesspunkter i specifika bussar. VastTrafik innehåller i sin tur metoder för att få resvägar uträknade via Västtrafiks öppna api.





Externa beroenden
Vår applikation har i dagsläget två externa beroenden. Parse och Google Play Services. Parse använder vi för att lösa vår datalagring och kommunikation mellan enheter. Google Play Services behöver vi för att kunna visa positioner på en karta.

Vi är i dag också beroende av Google Navigation för att navigera till fots. För att kunna guida ett barn till eller från en busshållplats måste vi idag lämna vår egen applikation och helt hoppa över till Google Maps. Vi hoppas kunna slippa detta i framtiden.

Övrigt
BussRelationMessenger är även en Observable. Detta utnyttjar vi i vår föräldravy genom att kunna reagera omedelbart när ett meddelande kommer in. Så fort ett meddelande kommer in kan vi direkt hämta den senaste informationen från molnet (Parse) och visa denna för användaren.

Komponenter i projektet
Västtrafik - API
Vi använder ett flertal olika anrop för att få resturer och annan relevant data som vi behöver visa upp i appen.
ElectriCity - API
Ett flertal olika anrop för att få realtidsdata ifrån bussarna då användaren av appen befinner sig på en buss med de möjligheterna (16 bussarna)

Wifi/GPS-services
Vi använder telefonens kapabilieter då vi vill detekterar närvaron av en 16 buss via wifi. Då för att kunna veta om vi kan visa realtids data ifrån Electricity API. 
Telefonens gps för att hitta vår nuvarande position som vi sedan använder för att hitta en resväg via Västtrafiks API.


Koppling mot Västtrafik/ElectriCity
Koppling mot Parse
Wifi/GPS-services
Design decisions (such as API level, etc.)
UML
Flowchart
Protocol (Client/Server
Parse
External dependencies
Parse
Google Play Services
Google Navigation
Övrigt


