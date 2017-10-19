package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }

    public void init() {
        List<String> lauseet = sqliteLauseet();

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE RaakaAine (id integer PRIMARY KEY, nimi varchar(255));");
        lista.add("CREATE TABLE Drinkki (id integer PRIMARY KEY, nimi varchar(255));");
        lista.add("CREATE TABLE DrinkkiRaakaAine "
                + "(fk_drinnki_id integer, fk_raakaAine_id integer, ohje nChar(255), maara string, "
                + "FOREIGN KEY(fk_drinnki_id) REFERENCES Drinkki(id), FOREIGN KEY(fk_raakaAine_id) REFERENCES RaakaAine(id));");

        lista.add("INSERT INTO Drinkki (nimi) VALUES ('NullPointerException');");
        lista.add("INSERT INTO Drinkki (nimi) VALUES ('406 Not acceptable');");
        lista.add("INSERT INTO Drinkki (nimi) VALUES ('404 Not found');");
        lista.add("INSERT INTO Drinkki (nimi) VALUES ('403 Forbidden');");
        lista.add("INSERT INTO Drinkki (nimi) VALUES ('401 Unauthorized');");
        
         lista.add("INSERT INTO RaakaAine (nimi) VALUES ('Java.SQLite');");
         lista.add("INSERT INTO RaakaAine (nimi) VALUES ('Drop TABLE (RaakaAine);');");

        return lista;
    }
}
