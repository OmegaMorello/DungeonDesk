# DungeonDesk

[Versione italiana](README_ITA.md)

DungeonDesk is a local web companion for in-person Dungeons & Dragons sessions. The Dungeon
Master runs the server, while players join from their browsers on the same network. It provides
campaign management, character sheets, a shared map, dice rolls, initiative, chat and notes.

This project was developed as the final assignment for the Software Engineering course at
Politecnico di Milano.

## Requirements

- JDK 25
- Maven 3.9 or the included Maven Wrapper

The application includes the compiled web client and uses an embedded H2 database. Node.js and an
external database are not required for normal use.

## Run the application

Run the following command from the project directory on macOS or Linux.

```bash
./mvnw spring-boot:run
```

On Windows, use the wrapper batch file.

```powershell
mvnw.cmd spring-boot:run
```

The Maven plugin compiles the sources and starts the application directly. A pre-built JAR is not
required.

Open [http://localhost:8080](http://localhost:8080) after the server has started.

DungeonDesk stores its database and map images under `./data`. Always start it from the project
directory if you want to reuse the same data. Stop the server with `Ctrl+C`.

## Start a session

1. Register a Dungeon Master account.
2. Create a campaign and add the players.
3. Create character sheets and assign them to the players when needed.
4. Open the game session and share its join code.
5. Players select their name and enter the join code.

Players on the same local network must use the host computer's address instead of `localhost`, for
example `http://192.168.1.10:8080`.

## Build an executable JAR

```bash
./mvnw clean package
java -jar target/dungeondesk-0.0.1-SNAPSHOT.jar
```

Run the JAR from the project directory so that relative data paths continue to point to `./data`.

## Modify the web client

The compiled React client is already available in `src/main/resources/static`. Editing and
rebuilding it requires Node.js 20.19 or a compatible newer release.

```bash
cd client
npm ci
npm run dev
```

Vite runs on [http://localhost:5173](http://localhost:5173) and forwards `/api` and `/ws` to the
Spring Boot server on port 8080.

Build the client and copy it into the server resources before packaging the application.

```bash
npm run build
cp -R dist/. ../src/main/resources/static/
```

## Run the tests

```bash
./mvnw test
```

The JaCoCo report is generated at `target/site/jacoco/index.html`.
