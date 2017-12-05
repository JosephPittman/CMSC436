package com.example.joey.collabart;

/**
 * Created by Joey on 12/4/2017.
 */
//Base for this code found at https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Double latitude, longitude;
    private File photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        Intent intent = getIntent();
        latitude = (Double) intent.getExtras().get("Lat");
        longitude = (Double) intent.getExtras().get("Long");
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File photofile = null;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(
                            imageFileName,  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                    photofile = image;
                    photo = photofile;
                }
                catch (Exception ex) {
                    System.out.println(ex);
                    // Error occurred while creating the File
                }
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photofile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //Bitmap photo =  (Bitmap) data.getExtras().get("data");
            Intent caption = new Intent(CameraActivity.this, CaptionActivity.class);
            //caption.putExtra("image", photo);
            caption.putExtra("Lat", latitude);
            caption.putExtra("Long", longitude);
            caption.putExtra("File", photo);
            startActivity(caption);
        }
    }
}
