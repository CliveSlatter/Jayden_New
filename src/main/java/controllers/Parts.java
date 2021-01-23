package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("Parts/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Parts{
    @GET
    @Path("list")
    public String PartsList() {
        System.out.println("Invoked Parts.PartsList()");
        JSONArray parts = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PartID, ProductCode ,PartDescription, CategoryID, Price FROM Parts");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject row = new JSONObject();
                row.put("PartID", results.getInt(1));
                row.put("ProductCode",results.getString(2));
                row.put("PartDescription", results.getString(3));
                row.put("CategoryID", results.getInt(4));
                row.put("Price",results.getInt(5));
                parts.add(row);
            }
            JSONObject response = new JSONObject();
            response.put("parts",parts);
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    @GET
    @Path("get/{PartID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String GetPart(@PathParam("PartID") Integer PartID) {
        System.out.println("Invoked Parts.GetPart() with PartID " + PartID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PartDescription, CategoryID FROM Parts WHERE PartID = ?");
            ps.setInt(1, PartID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                response.put("PartID", PartID);
                response.put("PartDescription", results.getString(1));
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
    public String PartsAdd(@FormDataParam("PartDescription") String PartDescription, @FormDataParam("CategoryID") Integer CategoryID) {
        System.out.println("Invoked Parts.PartsAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Parts ( PartDescription, CategoryID) VALUES (?, ?)");
            ps.setString(1, PartDescription);
            ps.setInt(2, CategoryID);
            ps.execute();
            return "{\"OK\": \"Added Part.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }


}
