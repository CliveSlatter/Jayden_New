package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("BasketItems/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class BasketItems{
    @GET
    @Path("list")
    public String BasketsList(@CookieParam("token") String token) {
        System.out.println("Invoked Baskets.BasketsList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement user = Main.db.prepareStatement("SELECT  UserID FROM Users WHERE Token = ?");
            user.setString(1,token);
            ResultSet rs = user.executeQuery();
            PreparedStatement basket = Main.db.prepareStatement("INSERT INTO Baskets(UserID, DateAdded) VALUES (?,?)");
            basket.setInt(1,rs.getInt(1));
            basket.setString(2, String.valueOf(java.time.LocalDate.now()));
            basket.executeUpdate();
            PreparedStatement psBasket = Main.db.prepareStatement("SELECT BasketID FROM Baskets");
            ResultSet rsBasket = psBasket.executeQuery();
            PreparedStatement updateItems = Main.db.prepareStatement("UPDATE BasketItems SET BasketID=?");
            updateItems.executeUpdate();
            PreparedStatement ps = Main.db.prepareStatement("SELECT BasketID, PartDescription, Quantity FROM BasketItems INNER JOIN Parts P on P.PartID = BasketItems.PartID");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject row = new JSONObject();
                row.put("BasketID", results.getInt(1));
                row.put("Description", results.getInt(2));
                row.put("Quantity", results.getInt(3));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    @POST
    @Path("add")
    public String BasketAdd(@FormDataParam("ItemID") int ItemID, @FormDataParam("Qty") int Quantity, @CookieParam("Token") String token) {
        System.out.println("Invoked Baskets.BasketsAdd()");
        try {
            PreparedStatement user = Main.db.prepareStatement("SELECT UserID FROM Users WHERE Token=?");
            user.setString(1,token);
            ResultSet rs = user.executeQuery();
            //PreparedStatement basket = Main.db.prepareStatement("INSERT INTO Baskets(UserID, DateAdded) VALUES(?,?)");
            //basket.setInt(1,rs.getInt(1));
            //basket.setString(2, String.valueOf(java.time.LocalDate.now()));
            //basket.executeUpdate();
            //PreparedStatement basketID = Main.db.prepareStatement("SELECT BasketID FROM Baskets ORDER BY BasketID DESC LIMIT 1");
            //ResultSet rsBasket = basketID.executeQuery();
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO BasketItems (PartID,Quantity) VALUES (?,?)");
            ps.setInt(1, ItemID);
            ps.setInt(2,Quantity);
            System.out.println(ps.toString());
            ps.executeUpdate();
            return "{\"OK\": \"Added item to basket.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to save to basket, please see server console for more info.\"}";
        }

    }
}