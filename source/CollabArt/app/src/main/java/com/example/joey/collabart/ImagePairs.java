package com.example.joey.collabart;

import android.graphics.Bitmap;

/**
 * Created by Joey on 12/3/2017.
 */

public class ImagePairs {
    private String cap;
    private Bitmap image;
    private Double latitude, longitude;
    public ImagePairs(String caption, Bitmap pic)
    {
        cap = caption;
        image = pic;
    }

    public String getCaption()
    {
        return cap;
    }

    public Bitmap getImage()
    {
        return image;
    }

    public void setLoc(Double latitude, Double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }
}
