## Reflektion, Grupp 4, Min Busskompis
> Tobias Edvardsson, Marcus Flyckt, Jesper Larsson, Marcus Johnsson, Tobias Nielsen

#### Vilka processer och procedurer har ni använt i ert projekt?

Vi har jobbat enligt scrum, vi hade veckolånga sprintar, standups via skype och sprintreviews efter varje sprint. Vi inledde även projektet med att skriva de user stories vi ville få lösta.

#### Uppskattningsvis, hur mycket tid spenderades (totalt och per gruppmedlem) på de olika delar/aktiviteter relaterade och projektet som helhet?

<table>
  <tr>
    <td>Totalt</td>
    <td></td>
  </tr>
  <tr>
    <td>Tobias E</td>
    <td>193</td>
  </tr>
  <tr>
    <td>Tobias N</td>
    <td>133</td>
  </tr>
  <tr>
    <td>Marcus F</td>
    <td>138</td>
  </tr>
  <tr>
    <td>Marcus J</td>
    <td>135</td>
  </tr>
  <tr>
    <td>Jesper</td>
    <td>112</td>
  </tr>
  <tr>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>Totalt medel</td>
    <td>142,2</td>
  </tr>
  <tr>
    <td>Totalt</td>
    <td>711</td>
  </tr>
  <tr>
    <td>Per vecka och person</td>
    <td>15,8</td>
  </tr>
</table>


*Tiderna är delvis uppskattade, delvis tidförda*

#### För varje metod och teknik använd (t ex standups, parprogrammering, TDD, osv) i ditt projekt 

Uppskattat per vecka och beräknat på genomsnitt vad vi lagt i tid per vecka och person:

<table>
  <tr>
    <td>Per vecka</td>
    <td></td>
  </tr>
  <tr>
    <td>Scrum relaterat</td>
    <td>1</td>
  </tr>
  <tr>
    <td>Möte angående projekt</td>
    <td>2</td>
  </tr>
  <tr>
    <td>Föreläsningar</td>
    <td>2</td>
  </tr>
  <tr>
    <td>Personlig inlärning</td>
    <td>4</td>
  </tr>
  <tr>
    <td>Android programmering</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Tävlingsrelaterat</td>
    <td>2</td>
  </tr>
  <tr>
    <td>Totalt</td>
    <td>16</td>
  </tr>
</table>


#### Vad var fördelen av denna tekniken baserad på din erfarenhet i projektet?

Genom att ha user stories och en backlog hade vi någorlunda koll på vad som man kunde ta tag i. Att vi hade sprints baserade på vecka gav också viss påtryckning att man behövde göra något regelbundet. Det gav oss också en anledning att regelbundet stämma av vad man hade gjort.

#### Vad var nackdelen av denna tekniken baserad på din erfarenhet i projektet?

Mycket tid och fokus gick åt till arbete runtomkring själva utvecklingen. Mycket energi gick åt till att lära sig Scrum. Vi märkte också att det var inte helt effektivt att använda metoden då vi ej dagligen träffades och utförde vad vi gjorde. I slutet använde vi snarare projektets backlog för att ha koll på vad som skall göras snarare än att planera sin veckas arbete. Det pratades om att Kanban kunde vara något som fungerar bättre i ett kort projekt som detta men det var inget vi hade tid på att sätta oss in i.

#### Hur effektiv var tekniken givet tiden det tog att använda den?

Tekniken ledde till en viss effektivitetökning då man kontinuerligt påmindes om vad som behöver göras. Genom att lägga till på att kontinuerligt försöka göra uppgifter till varje sprint gjorde det enklare att kunna arbeta med något och mer effektivt utföra fler uppgifter.

#### I vilka situationer skulle du använda tekniken i framtida projekt?

I alla situationer där man utvecklar komplicerade projekt och behöver koordinera och dela upp arbetsuppgifter mellan många medarbetare. Projekt som snabbt kan ändras under tidens gång då man hela tiden kan omprioritera vad som skall utföras.

#### I vilka situationer skulle du inte använda tekniken i framtida projekt?

I enklare projekt med färre personer inblandade. Där krav och tillvägagångsätt är helt tydliga från början. Även ifall det är väldigt korta projekt kan uppdelning i sprintar vara svårt.

#### Om du hade tekniken i en del av projektet och inte hela, hur var det att använda det jämfört mot att inte använda det?

Vi använde Scrum under hela projektet.

#### Vad funkade bra i hur ni arbetade i projektet?

Vi lyckades med att realisera vår idé till en fungerande prototyp med all den funktionalitet vi ville ha. Genom att använda oss av trello och Scrum fick vi en överblick på vad som var gjort varje vecka samt behövde göras. Kommunikationen mellan oss gruppmedlemmar fungerade bättre än vid tidigare projekt som vi arbetat tillsammans med. I samband med att vi började träffas även för att utföra arbete fick vi även upp produktiviteten jämfört med att vi jobbade enskilt hemifrån.

#### Vad fungerade inte bra i hur ni arbetade i projektet?

En av de svåra sakerna var att hålla en jämn ambitionsnivå. I slutet speglar det sig på olika stora arbetsinsatser. Till viss del användes ej Scrum i den utsträckning vi hade som mål från början.

#### Reflektera över ur icke process specifika bestlut (t ex icke obligatoriska hjälpmedel, APIs osv, inlärning och fika).

Vi hade några målsättningar med vår applikation, vi ville ha någon form av kommunikation mellan enheter, nyttja flera APIs och göra en teknisk avancerat applikation. Mest för att lära oss själva hur man gör intressanta saker till Android. 

När vi diskuterade hur vi skulle lösa kommunikationen mellan enheter hade vi fått nys om GCM (Google Cloud Messaging) vilket kunde lösa många problem för oss. Tobias E hade fått tips om tjänsten Parse. Då vi la tid på att undersöka Parse kände vi att det var en tjänst vi skulle kunna nyttja till detta projekt då vi kom fram till att tiden det skulle ta att bygga en helt eget back-end var för omfattande och att det skulle ta bort fokus från att utveckla själva applikationen.

När det kom till att nyttja APIs var det första självklara Electricity’s API, dock visade det sig svårt att hitta relevant information till applikationen vi valt att utveckla. Det blev sedan klart för oss att Västtrafiks API skulle lösa många problem för oss under resans gång.

En utav punkterna i vår reseplanerare var att vi ville erbjuda navigering för resenären. Från början trodde vi helt att Google Navigering kunde implementeras i tredjeparts-appar. Tyvärr insåg vi mot slutet att detta ej var möjligt. Vilket ledde till att vi var tvungna att hitta en alternativ lösning. I slutändan blev det så att vi ej implementerade navigering i vår applikation, utan snarare nyttjade Googles produkt för detta, mycket även här för att att ta fram en egen lösning skulle tagit alldeles för mycket tid.

#### Hur arbetade ni tillsammans som grupp i projektet? Vad funkade och inte i era interaktioner?

Närvaro på bokade möten har varit bra, vi har haft minst två möten per vecka, vilket gav bra översikt på gruppen som helhet. Gruppen har tillsammans diskuterat och kommit fram till en produkt samtliga medlemmar är nöjda med. Vad som fungerat dåligt är att folk haft olika ambitionsnivåer vilket resulterade i olika stor arbetsinsats.

#### Vad skulle ni göra annorlunda i framtida liknande projekt?

Vi skulle nog försöka sitta ner och arbeta i grupp i en större utsträckning. Detta gav väldigt bra resultat när vi väl gjorde det. Även att försöka arbeta mer över gränserna under projektets gång. Att man hjälps åt och byter uppgifter så alla får större förståelse hur hela applikationen fungerar.

#### Hur gjorde ni med de workshops som var organiserade?

Vi medverkade inte i någon av de workshops som gavs utöver den första. Vi tyckte inte att de kändes tillräckligt relevanta för att avvara tid från utveckling av vår produkt. I efterhand så tror vi att det kanske hade gynnat oss att medverka på vissa, framförallt den som hanterade hur man tar fram en produkt man kan sälja och tjäna pengar på. Det kändes även som en stor nackdel att de låg sent på kvällstid då flera av oss har mycket annat utöver skolan att göra.

#### Personliga reflektioner:

Inför att vi skrev ihop reflektionen satte vi oss personligen och skrev ner svar på frågorna nedan. Vi diskuterade sedan igenom allas svar och tankar kring projektet för att få även en personlig utvärdering av projektet. Den sammanfattning vi fick ut av detta finns nedan

* Vad har gått bäst med detta grupparbete? 

* Utvecklades projektet så som jag trodde det skulle göra?

* Vad anser jag om min egna prestation till projektet? 

 

* Hur anser jag att gruppen som helhet har presterat?

* Vad skulle jag gjort annorlunda ifall vi gjorde om projektet?

* Vad tar jag med mig för lärdomar från projektet?

Tobias Edvardsson

Jag är absolut mest nöjd med att vår slutprodukt blev såpass teknisk avancerad och välfungerande som den är. Jag tycker även att vi lyckades få till en bra presentation och en bra stund på mässan. Att vi gick och kammade hem pris för vår applikation känns ju väldigt roligt det med. 

Då vi som grupp har genomfört ett mindre projekt i en tidigare kurs visste jag att svårigheterna kring vårt projekt inte skulle ligga i vår tekniska kunskap eller vad vi kan lära oss, utan snarare att få alla jobba tillsammans mot ett gemensamt mål och ta ansvar.

Från början fick jag känslan av att alla hade lika stora ambitioner med projektet som jag själv, men under projektets gång visade det sig inte riktigt vara sant. En av svårigheterna jag hade som projektledare var att försöka få alla att ta de initiativ som behövdes för att vår applikationen skulle utvecklas i rätt riktning och inom utsatt tid.

Sammanfattande kring vår arbetsprestation tycker jag att vi fick fram en bra slutprodukt och lyckades ta fram bra bidrag till tävlingen. Dock med en väldigt sned fördelad arbetsbörda.

Det jag skulle gjort annorlunda ifall jag skulle gjort ett liknande projekt i skolmiljö är att trycka på att vi skall ses mer på plats, de tillfällen vi väl träffats och arbetat ihop har det hänt väldigt mycket till skillnad då det har legat på eget ansvar att lösa uppgifter. Slutligen finns det många småsaker jag tar med mig från projektet kring projektledandet i sig och även utförandet av projektet. Android känner jag mig numera bra på. Men slutligen väljer jag att ta med mig en positiv lärdom i att jag skall tro på mina idéer och vad man klarar av att genomföra utan tidigare kunskap. 

Tobias Nielsen

Det som har gått bäst i projektet, i min mening, är att de saker som folk har åtagit sig för respektive sprint har blivit löst med, få undantag. Kommunikationen har fungerat ganska bra, betydligt bättre än i det tidigare projektet vi har gjort tillsammans. Projektet har utvecklats inom de ramar som vi satte från början, vi lade ganska mycket tid på att komma på idéer och koncept, vilket betalade sig senare i projektet. Mitt eget deltagande är jag ganska nöjd med, kunde ha tagit åt mig mer saker som ur min synvinkel verkar svåra samt varit aktivare utanför kodandet. Som helhet så anser jag att projektet har gått bra och vi har tillsammans ändå levererat en produkt som vi kan vara nöjda med. Vill ge en eloge till Edvardsson som agerade projektledare och styrde upp och höll oss samman på ett adekvat sätt. Från projektet så tar jag främst med mig hur man arbetar med Android. Projektmässigt så var det inte lika mycket nytt för mig, då jag har arbetat i liknande former innan Chalmers.

Marcus Flyckt

Jag är väldigt nöjd med resultatet av det här grupparbetet. Vi fick ihop en bra prototyp som bygger på en fin idé hämtad från verkliga problem.

Från tidigare erfarenhet av grupparbete vet jag att det alltid kommer en dipp i produktivitet ungefär halvvägs in i ett projekt. Då jag denna gång var medveten om detta i förväg så blev det lättare att hantera när det väl hände. Det blev även rätt naturligt då arbetsbördan från andra kurser blev större under denna period.

Jag är väldigt nöjd med min egen insats. Min kod står för en stor del av applikationens funktionalitet och har visat sig vara relativt stabil trots sin komplexitet.

Gruppens prestation som helhet resulterade i, som jag jag skrev tidigare, ett riktigt bra resultat som jag är stolt över. Med detta sagt så anser jag att ambitionsnivån tyvärr varierat i gruppen. Om jag skulle gjorde om projektet så skulle jag lägga mindra krut på kodande och mer på grupparbete. Se till så att alla alltid visste vad de skulle göra och hjälpa till i den mån jag kan. Det jag tar med mig från projektet är inte bara hur kul och givande det är att sätta sig in i ett helt nytt område och ta fram en produkt, utan också att grupparbete och gruppdynamik kräver mycket jobb utöver själva utvecklandet.

Marcus Johnsson

Generellt så är jag nöjd med mitt och övriga medlemmars insatser i projektet. Vi nåde målet med en fungerande prototyp och alla tillförde något. Personligen är jag nöjd med min egna insats, men i efterhand kan jag inte förneka till att jag själv tidigare kunde lagt mer tid och mer regelbundet ge min tid till projektet. Det finns saker i den kod jag skrivit som definitivt kunde förbättrats, kunde även ha gett min tid till övriga medlemmar för att ge hjälp eller vad än kunde ha behövts. Kommunikationen har överlag varit bra men i perioder så har den varierat, även insatser i projektet har varierat från person till person och över tid. I framtida projekt bör jag tänkar mer på att lägga mer till på att lära mig, koda, och lägga tid till andra om hjälp kan behövas.

Jesper Larsson

Jag är mycket nöjd med projektet som helhet, att vi satte oss ner och spånade fram en idé som på riktigt löser ett problem vissa kan uppleva och det gjorde att det blev häftigare och mer intressant än andra projekt jag har varit del i då detta kändes mer riktigt. Jag gillade även att jobba med scrum då jag vet att det används på arbetsplatser som jag vill vara en del av i framtiden så att ha koll på hur det fungerar och ha en liten smak av det känns väldigt positivt. De andras prestation i projektet har varit väldigt bra och i synnerhet Edvardsson som i rollen som projektledare drog projektet framåt som ett lok med ett klart mål i sin vision för appen. Min egen prestation har dock inte varit på samma nivå som resten av gruppens och även om jag har bidragit har det varit jag som bidragit mins. Av diverse anledningar vilka är irrelevanta i detta perspektiv har jag inte åtagit mig lika stor del av jobbet och framför allt inte tagit initiativ att hjälpa mer när jag haft möjlighet utan varit nästan rädd att armbåga mig in i något och komma ivägen för något som någon satt och arbeta med. Men även om min last varit minns har jag lärt mig enormt mycket villket gör mig nöjd med projektet och de lärdomar och insikter jag fått kommer jag ha stor nytta av i framtiden.

