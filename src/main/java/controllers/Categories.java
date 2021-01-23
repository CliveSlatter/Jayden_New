package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("Categories/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Categories{
    @GET
    @Path("list")
    public String CategoriesList() {
        System.out.println("Invoked Categories.CategoriesList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CategoryID, Description FROM Categories");
            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();
                row.put("CategoryID", results.getInt(1));
                row.put("Description", results.getString(2));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    @GET
    @Path("get/{CategoryID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String GetCategory(@PathParam("CategoryID") Integer CategoryID) {
        System.out.println("Invoked Categories.GetCategory() with CategoryID " + CategoryID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Description, CategoryID FROM Categories WHERE CategoryID = ?");
            ps.setInt(1, CategoryID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                response.put("CategoryID", CategoryID);
                response.put("Description", results.getString(1));
                response.put("CategoryID", results.getInt(2));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("add")
    public String CategoriesAdd(@FormDataParam("Description") String Description) {
        System.out.println("Invoked Categories.CategoriesAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Categories (Description) VALUES (?)");
            ps.setString(1, Description);
            ps.execute();
            return "{\"OK\": \"Added Category.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }
}
