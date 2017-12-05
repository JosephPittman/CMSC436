package com.example.joey.collabart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Joey on 12/2/2017.
 */

public class DownloaderTask extends AsyncTask<Double, Double, ImagePairs[]> {

    final int MAX_SIZE = 50;
    ImagePairs[] images;
    @Override
    protected ImagePairs[] doInBackground(Double...doubles) {
        JSONObject[] jason;
        ImagePairs[] result;
        Double latitude = doubles[0];
        Double longitude = doubles[1];
        try {
            Database database = new Database();
            Location location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            jason = database.download(location, 1000, 0, MAX_SIZE);
            result = new ImagePairs[jason.length];

            for (int x  = 0; x < jason.length; x++)
            {
                Bitmap temp = database.getImage(jason[x]);
                result[x] = new ImagePairs(jason[x].getString("caption"), temp);
            }
            images = result;
            return result;
        }
        catch(Exception c)
        {
            System.out.println(c);
            System.out.println("Downloader");
        }
        return null;
    }

    public ImagePairs[] getResult()
    {
        return images;
    }
}
