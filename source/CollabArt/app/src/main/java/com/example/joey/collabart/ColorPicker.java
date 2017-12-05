package com.example.joey.collabart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorPicker extends Activity {

    int chosen_color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        RelativeLayout relativeLayout = findViewById(R.id.frame);

        final ImageView colorView = new ImageView(getApplicationContext());

        BitmapDrawable tmp = (BitmapDrawable) getDrawable(R.drawable.b512);
        BitmapDrawable color_wheel = (BitmapDrawable) getDrawable(R.drawable.color_wheel);
        if (null != tmp) {
            tmp.setTint(Color.WHITE);
            colorView.setImageDrawable(tmp);
        }

        int width = (int) getResources().getDimension(R.dimen.image_height);
        int height = (int) getResources().getDimension(R.dimen.image_height);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                width, height);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        colorView.setLayoutParams(params);
        colorView.setImageBitmap(color_wheel.getBitmap());

        relativeLayout.addView(colorView);

        final ImageView colorPreview = new ImageView(getApplicationContext());

        BitmapDrawable temp = (BitmapDrawable) getDrawable(R.drawable.b512);
        final BitmapDrawable color_preview = (BitmapDrawable) getDrawable(R.drawable.white);
        if (null != temp) {
            temp.setTint(Color.WHITE);
            colorPreview.setImageDrawable(temp);
        }

        RelativeLayout.LayoutParams parameters = new RelativeLayout.LayoutParams(
                100, 100);
        parameters.addRule(RelativeLayout.ALIGN_TOP, R.id.color_text);
        parameters.addRule(RelativeLayout.RIGHT_OF, R.id.color_text);
        colorPreview.setLayoutParams(parameters);
        colorPreview.setImageBitmap(color_preview.getBitmap());

        /*TextView preview_parent = (TextView) findViewById(R.id.color_text);
        int[] coordinates = new int[2];
        preview_parent.getLocationOnScreen(coordinates);
        colorPreview.setX(500);
        colorPreview.setY(800);*/

        relativeLayout.addView(colorPreview);

        colorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                BitmapDrawable new_image = (BitmapDrawable) colorView.getDrawable();
                Bitmap new_image_bitmap = new_image.getBitmap();
                BitmapDrawable preview = (BitmapDrawable) colorPreview.getDrawable();
                Bitmap new_preview = preview.getBitmap();
                Bitmap mutable_preview = new_preview.copy(Bitmap.Config.ARGB_8888 , true);
                int current_color = -1;
                if (x > 0 && y > 0 && x < new_image_bitmap.getWidth() && y < new_image_bitmap.getHeight()) {
                    current_color = new_image_bitmap.getPixel((int) x, (int) y);
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (current_color != 0) {
                            mutable_preview.eraseColor(current_color);
                            colorPreview.setImageBitmap(mutable_preview);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (current_color != 0) {
                            mutable_preview.eraseColor(current_color);
                            colorPreview.setImageBitmap(mutable_preview);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (current_color != 0) {
                            mutable_preview.eraseColor(current_color);
                            colorPreview.setImageBitmap(mutable_preview);
                            chosen_color = current_color;
                        }
                        return true;
                }
                return false;
            }
        });

        Button ok_button = (Button) findViewById(R.id.ok);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                BitmapDrawable color_drawable = (BitmapDrawable) colorPreview.getDrawable();
                Bitmap color_bitmap = color_drawable.getBitmap();
                intent.putExtra("color", color_bitmap.getPixel(0,0));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button black_button = (Button) findViewById(R.id.black_button);
        black_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BitmapDrawable preview = (BitmapDrawable) colorPreview.getDrawable();
                Bitmap new_preview = preview.getBitmap();
                Bitmap mutable_preview = new_preview.copy(Bitmap.Config.ARGB_8888 , true);
                mutable_preview.eraseColor(Color.BLACK);
                colorPreview.setImageBitmap(mutable_preview);

            }
        });
        Button white_button = (Button) findViewById(R.id.white_button);
        white_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BitmapDrawable preview = (BitmapDrawable) colorPreview.getDrawable();
                Bitmap new_preview = preview.getBitmap();
                Bitmap mutable_preview = new_preview.copy(Bitmap.Config.ARGB_8888 , true);
                mutable_preview.eraseColor(Color.WHITE);
                colorPreview.setImageBitmap(mutable_preview);

            }
        });
    }

    int change_brightness(int color, int factor) {
        int new_red = Math.round(Color.red(color) * factor);
        int new_green = Math.round(Color.green(color) * factor);
        int new_blue = Math.round(Color.blue(color) * factor);
        return Color.rgb(Math.min(new_red, 255), Math.min(new_green, 255), Math.min(new_blue, 255));
    }

}
