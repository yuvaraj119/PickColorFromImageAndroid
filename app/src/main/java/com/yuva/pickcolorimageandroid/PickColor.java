package com.yuva.pickcolorimageandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by yuvaraj on 10/21/2015.
 */
public class PickColor extends AppCompatActivity {


    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;

    Button pick_color;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_color);

        context = this;

        pick_color = (Button) findViewById(R.id.button_pick_color);

        pick_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(context, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(PickColor.this, PERMISSIONS, 1);
                    }else{
                        showCameraGalleryDialog();
                    }
                }

            }
        });

    }

    public static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("permission", "" + permission);
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    Intent intent = new Intent(PickColor.this, PickColorFromImage.class);
                    intent.putExtra("imageUri", selectedImage);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            } else if (requestCode == GALLERY_PICTURE) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    Intent intent = new Intent(PickColor.this, PickColorFromImage.class);
                    intent.putExtra("imageUri", selectedImage);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void showCameraGalleryDialog() {

        ArrayList<String> optionItems=new ArrayList<String>();
        optionItems.add("Camera");
        optionItems.add("Gallery");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(  PickColor.this, android.R.layout.simple_list_item_1 ,optionItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        };

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case 1:
                        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
