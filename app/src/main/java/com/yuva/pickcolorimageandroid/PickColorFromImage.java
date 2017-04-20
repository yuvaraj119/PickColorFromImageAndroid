package com.yuva.pickcolorimageandroid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuvaraj on 10/21/2015.
 */
public class PickColorFromImage extends AppCompatActivity implements View.OnTouchListener {

    Context context;

    Uri uri = null;

    ImageView picked_imageView;
    TextView setRGBColorTextView,setHEXColorTextView;
    Button setRGBColorButton,setHEXColorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpickedimage);
        context = this;
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                uri = null;
            } else {
                uri = intent.getParcelableExtra("imageUri");
            }
        } else {
            uri = savedInstanceState.getParcelable("imageUri");
        }

        setRGBColorButton=(Button) findViewById(R.id.button);
        setHEXColorButton=(Button) findViewById(R.id.button2);

        setRGBColorTextView=(TextView) findViewById(R.id.textView2);
        setHEXColorTextView=(TextView) findViewById(R.id.textView3);
        picked_imageView = (ImageView) findViewById(R.id.imageView2);
        picked_imageView.setOnTouchListener(this);

        String uriString = getRealPathFromURI(uri);

        picked_imageView.setImageBitmap(decodeSampledBitmapFromResource(uriString, 100, 100));

    }

    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap decodeSampledBitmapFromResource(String resId,int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Matrix inverse = new Matrix();
        picked_imageView.getImageMatrix().invert(inverse);
        float[] touchPoint = new float[] {event.getX(), event.getY()};
        inverse.mapPoints(touchPoint);
        int x = Integer.valueOf((int)touchPoint[0]);
        int y = Integer.valueOf((int) touchPoint[1]);

        int bitmapHeight=((BitmapDrawable) picked_imageView.getDrawable()).getBitmap().getHeight();

        if(y>0 && y<bitmapHeight) {

            int pixel = ((BitmapDrawable) picked_imageView.getDrawable()).getBitmap().getPixel(x, y);

            //then do what you want with the pixel data, e.g
            int redValue = Color.red(pixel);
            int blueValue = Color.blue(pixel);
            int greenValue = Color.green(pixel);

            setRGBColorTextView.setText("R" + redValue + ",G" + greenValue + ",B" + blueValue);
            setRGBColorButton.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));

            String hexColor = String.format("#%06X", (0xFFFFFF & pixel));

            setHEXColorTextView.setText(hexColor);
            setHEXColorButton.setBackgroundColor(Color.parseColor(hexColor));

        }

        System.gc();

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
