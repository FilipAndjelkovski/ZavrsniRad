# JavaFX Chat Aplikacija

Jednostavna chat aplikacija napravljena u JavaFX-u sa serverom i klijentima koji mogu da komuniciraju u realnom vremenu.

## Funkcionalnosti

- **Real-time chat**: Korisnici mogu da razmenjuju poruke u realnom vremenu
- **File transfer**: Mogućnost slanja fajlova između korisnika
- **Private messaging**: Direktne poruke između specifičnih korisnika
- **Broadcast messaging**: Slanje poruka svim povezanim korisnicima
- **Database storage**: Čuvanje chat istorije u SQLite bazi podataka
- **Multi-threaded server**: Server podržava više istovremenih konekcija

## Arhitektura

Projekat se sastoji od tri glavna dela:

### 1. Server (`src/main/java/server/`)
- **Server.java**: Glavni server koji osluškuje konekcije na portu 5000
- **ServerThread.java**: Nit za svakog povezanog klijenta
- **DatabaseHandler.java**: Upravljanje SQLite bazom podataka za čuvanje poruka

### 2. Client (`src/main/java/client/`)
- **Client.java**: JavaFX aplikacija koja pokreće klijentski interfejs
- **ChatController.java**: Kontroler za JavaFX UI komponente
- **ClientHandler.java**: Mrežna komunikacija sa serverom
- **SimpleClient.java**: Jednostavan tekstualni klijent za testiranje

### 3. Common (`src/main/java/common/`)
- **Message.java**: Klasa za chat poruke
- **FileTransfer.java**: Klasa za prenos fajlova

## Potrebni alati

- **Java 11+**
- **JavaFX SDK** (uključen u `lib/` folder)
- **SQLite JDBC driver** (uključen u `lib/`)

## Kako pokrenuti

### macOS Instrukcije (Mac)

**Sve je već kompajlirano i spremno za pokretanje!**

#### 1. Pokretanje servera

**Opcija A: Koristi skriptu (preporučeno)**
```bash
./run-server.sh
```

**Opcija B: Ručno pokretanje**
```bash
cd /Users/filip/Desktop/zavrsniRad
java -cp "lib/*:out" server.Server
```

**Napomena:** Ako dobiješ grešku "Address already in use", port 5000 je zauzet. Zaustavi proces:
```bash
lsof -ti:5000 | xargs kill
```

#### 2. Pokretanje klijenta

**Opcija A: Koristi skriptu (preporučeno)**
```bash
./run-client.sh
```

**Opcija B: Ručno pokretanje**
```bash
cd /Users/filip/Desktop/zavrsniRad
java --module-path lib/javafx-sdk-21.0.2/lib --add-modules javafx.controls,javafx.fxml -cp "lib/*:out" client.Client
```

### Windows Instrukcije

#### 1. Pokretanje servera
```bash
# Kompajliranje
javac -cp "lib/*" -d out src/main/java/server/*.java src/main/java/common/*.java

# Pokretanje
java -cp "lib/*;out" server.Server
```

#### 2. Pokretanje klijenta
```bash
# Kompajliranje
javac -cp "lib/javafx-sdk-21.0.2/lib/*;lib/*" -d out src/main/java/client/*.java src/main/java/common/*.java

# Pokretanje JavaFX klijenta
java --module-path lib/javafx-sdk-21.0.2/lib --add-modules javafx.controls,javafx.fxml -cp "lib/*;out" client.Client
```

## Struktura projekta

```
src/
├── main/
│   ├── java/
│   │   ├── client/          # Klijentski kod
│   │   ├── server/          # Server kod
│   │   └── common/          # Deljeni kod
│   └── resources/
│       └── fxml/           # JavaFX UI fajlovi
├── lib/                    # Eksterne biblioteke
│   ├── javafx-sdk-21.0.2/ # JavaFX SDK
│   ├── sqlite-jdbc-3.44.1.0.jar
│   └── slf4j-*.jar
└── server_files/           # Fajlovi za slanje
```

## Funkcionalnosti aplikacije

### Chat funkcionalnosti:
- **Public chat**: Poruke poslate bez specificiranja primaoca idu svim korisnicima
- **Private chat**: Poruke sa specificiranim primaocem idu samo toj osobi
- **File sharing**: Korisnici mogu da odaberu i pošalju fajlove

### Server funkcionalnosti:
- **Multi-client support**: Podržava više istovremenih korisnika
- **Message routing**: Usmerava poruke na odgovarajuće primaoce
- **Database logging**: Čuva sve poruke u SQLite bazi
- **Connection management**: Upravlja konekcijama i diskonekcijama

## Tehnologije

- **JavaFX**: Za grafički interfejs
- **Java Sockets**: Za mrežnu komunikaciju
- **SQLite**: Za čuvanje podataka
- **Multi-threading**: Za istovremeno rukovanje klijentima

## Napomene

- Server sluša na portu 5000
- Baza podataka se kreira automatski kao `chat_history.db`
- Aplikacija koristi JavaFX za moderni grafički interfejs
- Podržava slanje fajlova bilo koje veličine (pažnja na memoriju za velike fajlove)
