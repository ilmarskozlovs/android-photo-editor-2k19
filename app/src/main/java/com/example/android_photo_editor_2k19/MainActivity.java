package com.example.android_photo_editor_2k19;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    File directory;
    final int TYPE_PHOTO = 1;
    final int REQUEST_CODE_PHOTO = 1;
    final String TAG = "myLogs";
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();
        imageView = (ImageView)findViewById(R.id.imageView);
    }

    /*
    open camera
    if you uncomment that 3rd line code then we can save picture on folder which named "MyFolder"
     */
    public void onClickPhoto(MenuItem item) {
        Intent intent  = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }
/*
take a photo and show you that photo on screen (I guess :D)
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent) {
        if(requestCode == REQUEST_CODE_PHOTO) {
            if(resultCode == RESULT_OK){
                if(intent == null){
                    Log.d(TAG, "Intent is null");
                }else {
                    Log.d(TAG, "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if(bndl != null){
                        Object obj = intent.getExtras().get("data");
                        if(obj instanceof Bitmap){
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x " + bitmap.getHeight());
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }
            } else if(resultCode == RESULT_CANCELED){
                Log.d(TAG, "Canceled");
            }
        }
    }
/*
here we can save picture in folder
 */
    private Uri generateFileUri(int type){
        File file = null;
        if(type == TYPE_PHOTO){
            file = new File(directory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg");
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }
/*
here we create folder where we can save picture
 */
    private void createDirectory(){
        directory = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MyFolder");
        if(!directory.exists())
            directory.mkdir();

    }
    /*
    connecting action bar
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    public String test2(){
        return "testtesttest";
    }

    public String test(){
        return "Commit test to Jevgenijs branch";
    }
}
