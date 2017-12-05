package com.example.joey.collabart;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import java.util.UUID;
import android.util.Base64;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import android.media.ThumbnailUtils;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XLII on 11/29/2017.
 */

public class Database {
    boolean setup = false;
    public double percentSize = .2;

    //database connection properties
    String url = "jdbc:postgresql://limagestorage.csr4lh0sgaxr.us-east-1.rds.amazonaws.com:5432/lImageStorage";
    Properties props = new Properties();

    /**
     * sets the percent size for thumbnail conversions
     * @param percent new size, default 20% or .2
     */
    public void setPercentSize(Double percent){
        percentSize = percent;
    }

    public Database () throws ClassNotFoundException{
        setupDriver();
        props.setProperty("user", "iscearce");
        props.setProperty("password", "collaborativeArt436");
    }

    //retrieves the postgress driver at runtime
    public void setupDriver() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        setup = true;
    }

    //User query processing methods
    /**
     * Function for uploading a new image record
     * @param location, the location of the image to be uploaded
     * @param caption, the caption of the image to bu uploaded
     * @param image, the image to be uploaded
     * @return whether the upload succeeded or failed
     * TODO: Consider returning a JSONObject of the record instead of a boolean
     */
    public boolean upload(Location location, String caption, Bitmap image){
        //setup values for query
        String imageBase = encode(image);
        String thumbnailBase = encode(toThumbnail(image, percentSize));
        String point = "POINT(" + location.getLatitude() + " " + location.getLongitude() + ")";
        String mID = UUID.randomUUID().toString();

        //connect and process query
        try (Connection con = DriverManager.getConnection(url, props);
             //try (Connection con = DriverManager.getConnection(conn);
             //statement for image upload
             PreparedStatement stmt1 = con.prepareStatement("insert into images (id, image) values (uuid(?), ?)");
             //statement for thumbnail upload
             PreparedStatement stmt2 = con.prepareStatement("insert into thumbnails (id, image) values (uuid(?), ?)");
             //statement for record upload
             PreparedStatement stmt3 =
                     con.prepareStatement("insert into records (id, location, caption) values (uuid(?), ST_GeogFromText(?), ?)");
        ){
            //prepare all statements
            stmt1.setString(1, mID);
            stmt1.setString(2, imageBase);

            stmt2.setString(1, mID);
            stmt2.setString(2, thumbnailBase);

            stmt3.setString(1, mID);
            stmt3.setString(2, point);
            stmt3.setString(3, caption);

            //execute image upload (this is done first because it's better to have an image with no record than a record
            //with no image
            try {
                stmt1.executeUpdate();
                stmt1.close();
                //TODO: Process results
            }
            catch(Exception c)
            {

            }
            //execute second statement
            try {
                stmt2.executeUpdate();
                stmt2.close();
                //TODO: Process results
            }
            catch(Exception c)
            {

            }
            //execute third statement
            try {
                stmt3.executeUpdate();
                stmt3.close();
                con.close();
                //TODO: Process results
            }
            catch(Exception c)
            {

            }
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
            Log.i("Database manager: ", "CONNECTION/transaction FAIL");
            return false;
        }
    }
    //END FUNCTION upload

    /**
     * Function to update an existing image record
     * @param record is the record which is to be updated (use a single record from download which you want to update)
     * @param caption the new caption to the record, pass null if not to be updated
     * @param image the new image to be stored in the record, pass null if no update
     * @return whether the update succeeded or failed
     */
    public boolean update(JSONObject record, String caption, Bitmap image){
        //TODO: Implement
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve a list of JSONObjects containing relevant information
     * Structure of a JSONObject contains:
     *	Name: "location", type: Object (Just type cast it to location)
     *	Name: "caption", type: String
     *	The following item stored in the JSONObject is intended for internal use; It should not be changed, but feel free to
     *	use it if you feel so inclined:
     *	Name: "id", type: String; This is a unique identifier for the record in the database; Use it if you for some reason need a
     *		uuid string associated with the image you're retrieving
     * @param location User's current location (or any desired search location)
     * @param distance Distance from location from which records are to be retrieved; in meters
     * @param start Start index of the query (if this is the first query from this location, use 0)
     * @param count Max number of images to be retrieved.
     * @return a list of records retrieved from your location
     */
    public JSONObject[] download(Location location, double distance, int start, int count){
        String point = "POINT(" + location.getLatitude() + " " + location.getLongitude() + ")";

        try (Connection con = DriverManager.getConnection(url, props);
             //try (Connection con = DriverManager.getConnection(conn);
             PreparedStatement stmt =
                     con.prepareStatement("select concat(id,'') as id, ST_X(location::geometry) as lat, ST_Y(location::geometry) as lon, caption " +
                             "from records where ST_DWithin(records.location, ST_GeogFromText(?), ?) offset ? limit ?");
        ){
            stmt.setString(1, point);
            stmt.setDouble(2, distance);
            stmt.setInt(3, start);
            stmt.setInt(4, count);
            try (ResultSet res = stmt.executeQuery()) {
                //Process results of statement
                ArrayList<JSONObject> mList = new ArrayList<>();
                while(res.next()){
                    JSONObject mObject = new JSONObject();
                    //creates and populates new location
                    Location l = new Location("");
                    l.setLatitude(res.getDouble("lat"));
                    l.setLongitude(res.getDouble("lon"));
                    try {
                        //adds fields to json object
                        mObject.put("location", l);
                        mObject.put("caption", res.getString("caption"));
                        mObject.put("id", res.getString("id"));
                    }catch (JSONException e){
                        //something went wrong, hopefully this doesn't happen
                        e.printStackTrace();
                        Log.i("Database manager: ", "Failed to write to json object");
                        return null;
                    }
                    //add the next JSONObject to the list
                    mList.add(mObject);
                }
                //returns the list as an array
                JSONObject[] result = new JSONObject[mList.size()];
                for (int x = 0; x < result.length; x++)
                {
                    result[x] = (JSONObject) mList.get(x);
                }
                return result;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            Log.i("Database manager: ", "CONNECTION/transaction FAIL");
            return null;
        }
    }

    /**
     * Retrieves the thumbnail for this record
     * @param record A single record, retrieved from the download function
     * @return a thumbnail image from the record
     */
    public Bitmap getThumbnail(JSONObject record){
        String id;
        try {
            id = record.getString("id");
        }catch(JSONException e){
            e.printStackTrace();
            Log.i("Database manager: ", "Failed to read JSON file");
            return null;
        }
        try (Connection con = DriverManager.getConnection(url, props);
             //try (Connection con = DriverManager.getConnection(conn);
             PreparedStatement stmt =
                     con.prepareStatement("select image from thumbnails where images.id = uuid(?) limit 1");
        ){
            stmt.setString(1, id);
            try (ResultSet res = stmt.executeQuery()){
                while(res.next()){
                    return decode(res.getString("image"));

                }
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            Log.i("Database manager: ", "CONNECTION/transaction FAIL");
            return null;
        }
    }

    /**
     * Retrieves the image for this record
     * @param record A single record, retrieved from the download function
     * @return the image from the record
     */
    public Bitmap getImage(JSONObject record){
        String id;
        try {
            id = record.getString("id");
        }catch(JSONException e){
            e.printStackTrace();
            Log.i("Database manager: ", "Failed to read JSON file");
            return null;
        }
        try (Connection con = DriverManager.getConnection(url, props);
             //try (Connection con = DriverManager.getConnection(conn);
             PreparedStatement stmt =
                     con.prepareStatement("select image from images where images.id = uuid(?) limit 1");
        ){
            stmt.setString(1, id);
            try (ResultSet res = stmt.executeQuery()){
                while(res.next()) {
                    return decode(res.getString("image"));
                }
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            Log.i("Database manager: ", "CONNECTION/transaction FAIL");
            return null;
        }
    }


    //Query processing methiods end




    //The following are all private functions for internal use
    //used for modifying images for new values and storage

    /**
     * encodes a bitmap to a base64 string
     * @param bmp image to be converted
     * @return a base64 string version of the image, with PNG compression
     */
    private String encode(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * decodes a base64 to bitmap
     * @param b64 base64 to be converted
     * @return a bitmap image
     */
    private Bitmap decode(String b64){
        byte[] byteArray = Base64.decode(b64, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bmp;
    }

    /**
     * convert image to thumbnail
     * @param bmp image to be converted
     * @param percent double percentage size of image to be converted
     * @return thumbnail bitmap
     */
    private Bitmap toThumbnail(Bitmap bmp, double percent){
        int newWidth = (int) Math.ceil(bmp.getWidth() * percent);
        int newHeight = (int) Math.ceil(bmp.getHeight() * percent);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmp, newWidth, newHeight);
        return thumbnail;
    }

}