package com.example.joey.collabart;

import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;

import org.json.JSONObject;

/**
 * Created by Joey on 12/4/2017.
 */

/**
 * Created by Joey on 12/2/2017.
 */

public class UploaderTask extends AsyncTask<ImagePairs, ImagePairs, Integer> {

    final int MAX_SIZE = 50;
    @Override
    protected Integer doInBackground(ImagePairs...images) {
        ImagePairs current = images[0];
        String cap = current.getCaption();
        Bitmap image = current.getImage();
        Double latitude = current.getLatitude();
        Double longitude = current.getLongitude();

        try {
            Database database = new Database();
            Location location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            database.upload(location, cap, image);

            return new Integer(1);
        }
        catch(Exception c)
        {
            System.out.println(c);
            System.out.println("Uploader Task");
        }
        return new Integer(0);
    }
}
