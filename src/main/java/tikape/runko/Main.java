package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:opiskelijat.db");
        database.init();

        OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");
            map.put("opiskelijat", opiskelijaDao.findAll());
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/drinkit", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());
        
                get("/raakaaineet", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "raakaaineet");
        }, new ThymeleafTemplateEngine());

        get("/drinkit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "drinkki");
        }, new ThymeleafTemplateEngine());
    }
}
