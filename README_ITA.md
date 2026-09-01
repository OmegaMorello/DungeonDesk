# DungeonDesk

[English version](README.md)

DungeonDesk è un'applicazione web locale di supporto alle sessioni di Dungeons & Dragons giocate
dal vivo. Il Dungeon Master avvia il server e i giocatori partecipano dai propri browser sulla
stessa rete. L'applicazione comprende campagne, schede, mappa condivisa, dadi, iniziativa, chat e
note.

Il progetto è stato sviluppato come prova finale del corso di Ingegneria del Software al
Politecnico di Milano.

## Requisiti

- JDK 25
- Maven 3.9 oppure il Maven Wrapper incluso

Il client web è già compilato e il database H2 è incorporato. Per il normale utilizzo non servono
Node.js né un database esterno.

## Avvio dell'applicazione

Eseguire il comando seguente dalla cartella del progetto su macOS o Linux.

```bash
./mvnw spring-boot:run
```

Su Windows utilizzare il wrapper batch.

```powershell
mvnw.cmd spring-boot:run
```

Il plugin Maven compila i sorgenti e avvia direttamente l'applicazione. Non serve un JAR già
creato e il JAR non deve essere incluso nel repository.

Quando il server è avviato, aprire [http://localhost:8080](http://localhost:8080).

DungeonDesk salva il database e le immagini delle mappe nella cartella `./data`. Avviandolo sempre
dalla cartella del progetto verranno riutilizzati gli stessi dati. Il server si arresta con
`Ctrl+C`.

## Avvio di una sessione

1. Registrare un account Dungeon Master.
2. Creare una campagna e aggiungere i giocatori.
3. Creare le schede e assegnarle ai giocatori quando necessario.
4. Aprire la sessione di gioco e condividere il join code.
5. I giocatori selezionano il proprio nome e inseriscono il join code.

I giocatori collegati alla stessa rete locale devono usare l'indirizzo del computer host al posto
di `localhost`, per esempio `http://192.168.1.10:8080`.

## Creazione del JAR eseguibile

```bash
./mvnw clean package
java -jar target/dungeondesk-0.0.1-SNAPSHOT.jar
```

Il JAR deve essere avviato dalla cartella del progetto affinché i percorsi relativi continuino a
puntare a `./data`.

## Modifica del client web

Il client React compilato è già disponibile in `src/main/resources/static`. Per modificarlo e
ricompilarlo serve Node.js 20.19 o una versione successiva compatibile.

```bash
cd client
npm ci
npm run dev
```

Vite viene eseguito su [http://localhost:5173](http://localhost:5173) e inoltra `/api` e `/ws` al
server Spring Boot sulla porta 8080.

Prima di creare il pacchetto dell'applicazione, compilare il client e copiarlo nelle risorse del
server.

```bash
npm run build
cp -R dist/. ../src/main/resources/static/
```

## Esecuzione dei test

```bash
./mvnw test
```

Il report JaCoCo viene generato in `target/site/jacoco/index.html`.
