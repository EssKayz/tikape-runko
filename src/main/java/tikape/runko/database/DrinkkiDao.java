/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Drinkki;
import tikape.runko.domain.RaakaAine;

public class DrinkkiDao implements Dao<Drinkki, Integer> {

    private Database database;

    public DrinkkiDao(Database database) {
        this.database = database;
    }

    @Override
    public Drinkki findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Drinkki o = new Drinkki(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Drinkki> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki");

        ResultSet rs = stmt.executeQuery();
        List<Drinkki> drinkit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            drinkit.add(new Drinkki(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return drinkit;
    }
    
        public List<String> getDrinkIngredients(int id) throws SQLException{
        RaakaAineDao raakaDao = new RaakaAineDao(database);
           Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM DrinkkiRaakaAine WHERE fk_drinkki_id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        List<String> rivit = new ArrayList<>();
        
        while (rs.next()) {
            int rawID = rs.getInt("fk_raakaAine_id");
            String ohje = rs.getString("ohje");
            String maara = rs.getString("maara");
            RaakaAine raakile = raakaDao.findOne(rawID);
            rivit.add(raakile.getNimi() + " - amount: " + maara);
        }
        rs.close();
        stmt.close();
        connection.close();

        return rivit;
    }
    
    public List<String> getInstructions(int id) throws SQLException{
        RaakaAineDao raakaDao = new RaakaAineDao(database);
           Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM DrinkkiRaakaAine WHERE fk_drinkki_id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        List<String> rivit = new ArrayList<>();
        
        while (rs.next()) {
            int rawID = rs.getInt("fk_raakaAine_id");
            String ohje = rs.getString("ohje");
            RaakaAine raakile = raakaDao.findOne(rawID);
            rivit.add(raakile.getNimi() + ": " + ohje);
        }
        rs.close();
        stmt.close();
        connection.close();

        return rivit;
    }

    public void addDrink(String name) throws SQLException {
        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("INSERT INTO Drinkki(nimi) VALUES (?);");
        stmt.setString(1, name);
        stmt.executeUpdate();
        stmt.close();
        con.close();
    }

    public void addRawIngredientToDrink(String drinkName, String rawIngredientName, String instruction, String amount) throws SQLException {
        Connection con = database.getConnection();
        PreparedStatement stmt
                = con.prepareStatement("INSERT INTO DrinkkiRaakaAine(fk_drinkki_id, fk_raakaAine_id, ohje, maara)"
                        + "VALUES ("
                        + "(SELECT id FROM Drinkki WHERE (nimi = ?)),"
                        + "(SELECT id FROM RaakaAine WHERE (nimi = ?))," +
                        "?, ?)");
        stmt.setString(1, drinkName);
        stmt.setString(2, rawIngredientName);
        stmt.setString(3, instruction);
        stmt.setString(4, amount);
        stmt.executeUpdate();
        stmt.close();
        con.close();
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("DELETE FROM DrinkkiRaakaAine WHERE fk_drinkki_id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();
        stmt = con.prepareStatement("DELETE FROM Drinkki WHERE id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();
        stmt.close();
        con.close();
    }

}
