package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("Builds/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Builds{
    @GET
    @Path("list")
    public String BuildsList() {
        System.out.println("Invoked Builds.BuildsList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT BuildID, Title, PartID FROM Builds");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject row = new JSONObject();
                row.put("BuildID", results.getInt(1));
                row.put("Title", results.getString(2));
                row.put("PartID", results.getInt(3));
                response.add(row);
            }
            JSONObject builds = new JSONObject();
            builds.put("Build",response);
            return builds.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    @GET
    @Path("get/{BuildID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String GetBuild(@PathParam("BuildID") Integer BuildID) {
        System.out.println("Invoked Builds.GetBuild() with BuildsyID " + BuildID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title, BuildID FROM Builds WHERE BuildID = ?");
            ps.setInt(1, BuildID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                response.put("BuildID", BuildID);
                response.put("Title", results.getString(1));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("add")
    public String BuildsAdd(@FormDataParam("Title") String Title) {
        System.out.println("Invoked Builds.BuildsAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Builds (Title) VALUES (?)");
            ps.setString(1, Title);
            ps.execute();
            return "{\"OK\": \"Added Build.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }

    @POST
    @Path("buildPartsList")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String buildPartsList(@FormDataParam("buildID") int buildID) {
        System.out.println("Invoked Parts.buildPartsList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Parts.PartID, PartDescription, CategoryID FROM Parts INNER JOIN Builds B on Parts.PartID = B.PartID WHERE BuildID=?");
            ps.setInt(1,buildID);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject row = new JSONObject();
                row.put("PartID", results.getInt(1));
                row.put("PartDescription", results.getString(2));
                PreparedStatement cat = Main.db.prepareStatement("SELECT Description FROM Categories WHERE CategoryID=?");
                cat.setInt(1,results.getInt(3));
                ResultSet rsCat = cat.executeQuery();
                row.put("Category", rsCat.getString(1));
                response.add(row);
            }
            JSONObject parts = new JSONObject();
            parts.put("parts",response);
            return parts.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
}
