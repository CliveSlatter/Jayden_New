package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("Baskets/")

public class Baskets{
    @GET
    @Path("list")
    public String BasketsList(@CookieParam("Token") String token) {
        System.out.println("Invoked Baskets.BasketsList() with token "+token);
        JSONObject response = new JSONObject();
        JSONObject user = new JSONObject();
        JSONArray items = new JSONArray();
        try {
            PreparedStatement userID = Main.db.prepareStatement("SELECT  UserID,FirstName,LastName FROM Users WHERE Token = ?");
            userID.setString(1,token);
            ResultSet rs = userID.executeQuery();
            response.put("firstName",rs.getString(2));
            response.put("lastName",rs.getString(3));
            PreparedStatement basket = Main.db.prepareStatement("INSERT INTO Baskets(UserID, DateAdded) VALUES (?,?)");
            basket.setInt(1,rs.getInt(1));
            basket.setString(2, String.valueOf(java.time.LocalDate.now()));
            basket.executeUpdate();
            PreparedStatement psBasket = Main.db.prepareStatement("SELECT BasketID FROM Baskets");
            ResultSet rsBasket = psBasket.executeQuery();
            PreparedStatement updateItems = Main.db.prepareStatement("UPDATE BasketItems SET BasketID=?");
            updateItems.setInt(1,rsBasket.getInt(1));
            updateItems.executeUpdate();
            PreparedStatement ps = Main.db.prepareStatement("SELECT BasketID, BasketItems.PartID, PartDescription,Quantity,Price FROM BasketItems INNER JOIN Parts P on P.PartID = BasketItems.PartID");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject row = new JSONObject();
                row.put("PartID",results.getInt(2));
                row.put("Quantity", results.getInt(4));
                row.put("Description", results.getString(3));
                row.put("Price", results.getInt(5));
                items.add(row);
            }
            response.put("User", user);
            response.put("Items", items);
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @POST
    @Path("removeItem")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String removeItem(@FormDataParam("PartID") int PartID){
        try{
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM BasketItems WHERE PartID=?");
            ps.setInt(1,PartID);
            ps.executeUpdate();
            return "{\"Success\": \"Item successfully removed from basket.\"}";
        }catch (Exception e){
            return "{\"Error\": \"Unable to remove selected item.\"}";
        }
    }
    /*
    @POST
    @Path("add")
    public String BasketAdd(@FormDataParam("Title") String Title) {
        System.out.println("Invoked Baskets.BasketsAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Baskets (Title) VALUES (?)");
            ps.setString(1, Title);
            ps.execute();
            return "{\"OK\": \"Added Basket.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }*/

    @GET
    @Path("buyNow")
    public String buyNow(){
        try{

            // The first three lines  are there to check whether the basket is empty as SQLite only supports TYPE_FORWARD_ONLY
            // result sets so you can't set the pointer to the start again after the .next() statement.
            PreparedStatement itemsCheck = Main.db.prepareStatement("SELECT PartID,Quantity FROM BasketItems");
            ResultSet resCheck = itemsCheck.executeQuery();
            if(!resCheck.next()) return "{\"Error\": \"No items in the basket.\"}";
            PreparedStatement items = Main.db.prepareStatement("SELECT PartID,Quantity FROM BasketItems");
            ResultSet resItems = items.executeQuery();
            while(resItems.next()){
                PreparedStatement qtyUpdate = Main.db.prepareStatement("SELECT InStock FROM Parts WHERE PartID = ?");
                qtyUpdate.setInt(1,resItems.getInt(1));
                ResultSet rsQty = qtyUpdate.executeQuery();
                if(rsQty.getInt(1) < resItems.getInt(2)){
                    return "{\"Error\": \"Insufficient stock for some of the items.\"}";
                }else{
                    PreparedStatement update = Main.db.prepareStatement("UPDATE Parts SET InStock = Instock - ? WHERE PartID=?");
                    update.setInt(1,resItems.getInt(2));
                    update.setInt(2,resItems.getInt(1));
                    update.executeUpdate();
                }
            }
            PreparedStatement clearBasketItems = Main.db.prepareStatement("DELETE FROM BasketItems");
            PreparedStatement clearBasket = Main.db.prepareStatement("DELETE FROM Baskets");
            clearBasketItems.executeUpdate();
            clearBasket.executeUpdate();

            return "{\"Success\": \"Purchase is complete.\"}";
        }catch (Exception e){
            return "{\"Error\": \"Unable to process the purchase.\"}";
        }
    }
}
