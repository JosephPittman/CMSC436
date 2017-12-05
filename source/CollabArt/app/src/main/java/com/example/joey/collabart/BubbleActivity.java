package com.example.joey.collabart;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/*CREDIT FOR THIS ACTIVITY:
Adam Porter - for creating the GraphicsCanvasBubble activity that this activity used as an initial
                base and for teaching the concepts that one needs to know in order to make such an
                activity
Dinesh and Darshan Rivka Whittle at Stack Overflow - for giving the algorithms for drawing and for
                the undo/redo features. Their code can be found here:
                https://stackoverflow.com/questions/11114625/android-canvas-redo-and-undo-operation
 */

public class BubbleActivity extends Activity {
    private static final String TAG = "BubbleActivity";
    final private Paint mPainter = new Paint();
    ArrayList<Path> previous_states = new ArrayList<Path>();
    ArrayList<Path> undone_states = new ArrayList<Path>();
    ArrayList<Paint> previous_paints = new ArrayList<Paint>();
    ArrayList<Paint> undone_paints = new ArrayList<Paint>();
    Double latitude, longitude;
    int width, height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final RelativeLayout frame = findViewById(R.id.frame);
        Intent intent = getIntent();
        File photo = (File) intent.getExtras().get("File");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        //Bitmap bitmap = (Bitmap) intent.getExtras().get("picture");
        final String caption = (String) intent.getExtras().get("caption");
        latitude = (Double) intent.getExtras().get("Lat");
        longitude = (Double) intent.getExtras().get("Long");
        final DrawingView drawingView = new DrawingView(getApplicationContext(),
                bitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                drawingView.mBitmapWidthAndHeight, drawingView.mBitmapWidthAndHeight);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        drawingView.setLayoutParams(params);

        frame.addView(drawingView);
        drawingView.setDrawingCacheEnabled(true);
        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        undone_states.clear();
                        drawingView.line.reset();
                        drawingView.line.moveTo(x, y);
                        drawingView.initial_x = x;
                        drawingView.initial_y = y;
                        drawingView.invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float distance_x = Math.abs(x - drawingView.initial_x);
                        float distance_y = Math.abs(y - drawingView.initial_y);
                        if (distance_x >= 4 || distance_y >= 4) {
                            drawingView.line.quadTo(drawingView.initial_x, drawingView.initial_y,
                                    (x + drawingView.initial_x)/2, (y + drawingView.initial_y)/2);
                            drawingView.initial_x = x;
                            drawingView.initial_y = y;
                        }
                        drawingView.invalidate();
                        return true;
                    case MotionEvent.ACTION_UP:
                        drawingView.line.lineTo(drawingView.initial_x, drawingView.initial_y);
                        previous_states.add(new Path(drawingView.line));
                        previous_paints.add(new Paint(mPainter));
                        drawingView.line = new Path();
                        drawingView.invalidate();
                        return true;
                }
                return true;
            }
        });

        Button color_button = (Button) findViewById(R.id.color_button);
        color_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(BubbleActivity.this, ColorPicker.class);
                startActivityForResult(intent, 1);
            }
        });

        Button eraser_button = (Button) findViewById(R.id.eraser_button);
        eraser_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (previous_states.size() > 0) {
                    undone_states.add(previous_states.remove(previous_states.size() - 1));
                    undone_paints.add(previous_paints.remove(previous_paints.size() - 1));
                    drawingView.invalidate();
                }
                else {
                    Toast.makeText(BubbleActivity.this, "No previous state to go back to",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button redo_button = (Button) findViewById(R.id.redo_button);
        redo_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (undone_states.size() > 0) {
                    previous_states.add(undone_states.remove(undone_states.size() - 1));
                    previous_paints.add(undone_paints.remove(undone_paints.size() - 1));
                    drawingView.invalidate();
                }
                else {
                    Toast.makeText(BubbleActivity.this, "No additional state to redo",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button publish_button = (Button) findViewById(R.id.publish_button);
        publish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    UploaderTask upload = new UploaderTask();
                    //View image = findViewById(R.id.image);
                    //System.out.println(width + " " + height);
                    //Bitmap final_image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    //Canvas canvas = new Canvas(final_image);
                    //image.draw(canvas);
                    Bitmap final_image = drawingView.getDrawingCache();
                    ImagePairs[] current = new ImagePairs[1];
                    current[0]= new ImagePairs(caption, final_image);
                    current[0].setLoc(latitude, longitude);
                    upload.execute(current);
                    Intent intent = new Intent(BubbleActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                catch(Exception e)
                {
                    System.out.print(e);
                    System.out.println("Bubble Activity");
                }

            }
        });

        final TextView brush_size_number = (TextView) findViewById(R.id.brush_size);
        SeekBar brush_size = (SeekBar) findViewById(R.id.brush_size_picker);
        brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brush_size_number.setText(String.valueOf(i));
                mPainter.setStrokeWidth((float) i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            int color = data.getIntExtra("color", -1);
            if (color != -1) {
                mPainter.setColor(color);
            }
        }
    }

    private class DrawingView extends View {

        private static final int STEP = 100;
        private final Bitmap mBitmap;

        private int[] touch_location;

        final private DisplayMetrics mDisplayMetrics;
        final private int mDisplayWidth;
        final private int mDisplayHeight;
        final private int mBitmapWidthAndHeight, mBitmapWidthAndHeightAdj;
        public Path line;
        float initial_x, initial_y;
        Canvas mCanvas;
        Paint bitmap_paint = new Paint(Paint.DITHER_FLAG);

        public DrawingView(Context context, Bitmap bitmap) {
            super(context);

            // Scale bitmap
            mBitmapWidthAndHeight = (int) getResources().getDimension(
                    R.dimen.image_height);
            this.mBitmap = Bitmap.createScaledBitmap(bitmap,
                    mBitmapWidthAndHeight, mBitmapWidthAndHeight, false);
            mBitmapWidthAndHeightAdj = mBitmapWidthAndHeight + 20;

            // Get display size info
            mDisplayMetrics = new DisplayMetrics();
            BubbleActivity.this.getWindowManager().getDefaultDisplay()
                    .getMetrics(mDisplayMetrics);
            mDisplayWidth = mDisplayMetrics.widthPixels;
            mDisplayHeight = mDisplayMetrics.heightPixels;

            // Add some painting directives
            mPainter.setAntiAlias(true);
            mPainter.setDither(true);
            mPainter.setColor(Color.rgb(0,0,0));
            mPainter.setStyle(Paint.Style.STROKE);
            mPainter.setStrokeJoin(Paint.Join.ROUND);
            mPainter.setStrokeCap(Paint.Cap.ROUND);
            mPainter.setStrokeWidth(12);

            mCanvas = new Canvas(mBitmap);
            line = new Path();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, 0, 0, bitmap_paint);
            for (int x = 0; x < previous_states.size(); x++) {
                canvas.drawPath(previous_states.get(x), previous_paints.get(x));
            }
            canvas.drawPath(line, mPainter);
        }
    }
}