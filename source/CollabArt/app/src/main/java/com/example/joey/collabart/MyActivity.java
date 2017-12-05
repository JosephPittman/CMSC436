package com.example.joey.collabart;

/**
 * Created by Rui.Qian on 11/26/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*On click of see gallery button, start this activity. Somehow send the
* images and load the drawables and captions to the ArrayList. This will
* send the information to the adapter*/

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery_layout);

        //Set up RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.list);

        //Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter

        /*TODO Here loads the images and captions into their respective arraylist */
        DownloaderTask task = new DownloaderTask();
        Intent intent = getIntent();
        Double latitude = intent.getDoubleExtra("Lat", 0);
        Double longitude = intent.getDoubleExtra("Long", 0);
        ImagePairs[] images = new ImagePairs[0];
        try {
            task.execute(latitude, longitude).get();
            images = task.getResult();
            ArrayList<Bitmap> names = new ArrayList<>();
            ArrayList<String> captions = new ArrayList<>();
            for (int x = 0; x < images.length; x++)
            {
                names.add(images[x].getImage());
                captions.add(images[x].getCaption());
            }

            /*Bitmap i1 = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.giraffe);
            names.add(i1);
            captions.add("caption1 of a giraffe");

            i1 = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.panda);
            captions.add("caption2 of a panda");
            names.add(i1);

            i1 = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.sloth);
            names.add(i1);
            captions.add("caption3 of a sloth");*/

        /*done loading images and captions, now start the adapter.*/

            MyAdapter mAdapter = new MyAdapter(names, captions, R.layout.image_item);
            mRecyclerView.setAdapter(mAdapter);
        }
        catch (Exception e)
        {
            System.out.println (e);
            System.out.println("My Activity");
        }
    }


}