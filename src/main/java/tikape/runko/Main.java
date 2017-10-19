package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.DrinkkiDao;
import tikape.runko.database.RaakaAineDao;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:drinkit.db");
        database.init();

        DrinkkiDao drinkkiDao = new DrinkkiDao(database);
        RaakaAineDao raakaDao = new RaakaAineDao(database);
        
        drinkkiDao.findAll().stream().forEach(drinkki -> {
            System.out.println(drinkki.getNimi() + " " + drinkki.getId());
        });
           System.out.println("");
           
       raakaDao.findAll().stream().forEach(raakile -> {
            System.out.println(raakile.getNimi() + " " + raakile.getId());
        });

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAll());
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/drinkit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAll());
            map.put("raakaaineet", raakaDao.findAll());
            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());
        
        post("/drinkit", (req, res) -> {
            String nimi = req.queryParams("name");
            drinkkiDao.addDrink(nimi);
            res.redirect("/drinkit");
            return "";
        });
        
        get("/raakaaineet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raakaaineet", raakaDao.findAll());
            return new ModelAndView(map, "raakaaineet");
        }, new ThymeleafTemplateEngine());
        
        post("/poista", (req, res) -> {
            String nimi = req.queryParams("name");
            raakaDao.delete(0);
            res.redirect("/raakaaineet");
            return "";
        });
        
        post("/raakaaineet", (req, res) -> {
            String nimi = req.queryParams("name");
            raakaDao.addRaakaAine(nimi);
            res.redirect("/raakaaineet");
            return "";
        });

        get("/drinkit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkki", drinkkiDao.findOne(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "drinkki");
        }, new ThymeleafTemplateEngine());


    }
}
