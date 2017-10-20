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

        drinkkiDao.getDrinkIngredients(1).stream().forEach(ingred -> {
            System.out.println(ingred);
        });
        System.out.println("");

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

        post("/addDrink", (req, res) -> {
            String nimi = req.queryParams("name");
            drinkkiDao.addDrink(nimi);
            res.redirect("/drinkit");
            return "";
        });

        post("/addRawIngredient", (req, res) -> {
            String drinkName = req.queryParams("drinkki");
            String rawIngredientName = req.queryParams("raakaaine");
            String instruction = req.queryParams("instruction");
            String amount = req.queryParams("amount");
            drinkkiDao.addRawIngredientToDrink(drinkName, rawIngredientName, instruction, amount);
            res.redirect("/drinkit");
            return "";
        });

        get("/raakaaineet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raakaaineet", raakaDao.findAll());
            return new ModelAndView(map, "raakaaineet");
        }, new ThymeleafTemplateEngine());

        post("/raakaaineet", (req, res) -> {
            String nimi = req.queryParams("name");
            raakaDao.addRaakaAine(nimi);
            res.redirect("/raakaaineet");
            return "";
        });
        
        post("/poistaDrinkki", (req, res) -> {
            int id = Integer.parseInt(req.queryParams("id"));
            drinkkiDao.delete(id);
            res.redirect("/");
            return "";
        });
        
        post("/poistaRaakaAine", (req, res) -> {
            int id = Integer.parseInt(req.queryParams("id"));
            raakaDao.delete(id);
            res.redirect("/raakaaineet");
            return "";
        });

        get("/drinkit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkki", drinkkiDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("raakaaine", drinkkiDao.getDrinkIngredients(Integer.parseInt(req.params("id"))));
            map.put("ohje", drinkkiDao.getInstructions(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "drinkki");
        }, new ThymeleafTemplateEngine());

    }
}
