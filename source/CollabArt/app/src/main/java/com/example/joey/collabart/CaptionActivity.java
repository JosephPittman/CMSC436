package com.example.joey.collabart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class CaptionActivity extends Activity{
    EditText caption;
    Bitmap image;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Inflate the layout
        setContentView(R.layout.caption_activity_linear);
        //Get the intent and the image file associated with with it
        Intent fromCamera = getIntent();
        Bundle intentExtras = fromCamera.getExtras();

        final File photo = (File) intentExtras.get("File");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);

        //image = (Bitmap) intentExtras.get("image");
        final Double latitude = (Double) intentExtras.get("Lat");
        final Double longitude = (Double) intentExtras.get("Long");
        ImageView imageView = (ImageView) findViewById(R.id.image);
        //Bitmap bitmapConversion = BitmapFactory.decodeFile(image.toString());
        imageView.setImageBitmap(bitmap);
        Button record_caption = this.findViewById(R.id.record_caption);
        caption = this.findViewById(R.id.caption);
        record_caption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (CaptionActivity.this, BubbleActivity.class);
                intent.putExtra("picture", image);
                intent.putExtra("caption", caption.getText().toString());
                intent.putExtra("Lat", latitude);
                intent.putExtra("Long", longitude);
                intent.putExtra("File", photo);
                startActivity(intent);
            }
        });
    }
    //Upon clicking the submit button, get the text in the textview and store in database
}