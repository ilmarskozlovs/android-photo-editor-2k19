package com.example.android_photo_editor_2k19;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements EditImageFragment.EditImageFragmentListener, RotateImage.OnFragmentInteractionListener{


    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.image_preview)
    ImageView imageView;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;


    private float rotation = 0;
    Bitmap bitmap;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int SELECT_A_PHOTO = 2;

    Button btn_take;
    Button btn_list;
    Button btn_load;
    Button btn_wallpaper;
    ImageView imageView_photo;

    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeButtons();
        ButterKnife.bind(this);
        toolbar();


    }

    public void toolbar(){

        bitmap = BitmapFactory.decodeResource(getResources(),R.id.image_preview);



        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Edit Image"));
        tabLayout.addTab(tabLayout.newTab().setText("Rotate Image"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewpager = findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView img= (ImageView) findViewById(R.id.image_preview);
        img.setImageResource(R.drawable.monkey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_takePicture) {
            dispatchTakePictureIntent();
            return true;
        }

        if (id == R.id.action_save) {

            imageView.setImageBitmap(viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight()));

            try {
                galleryAddPic();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                resetColorFilter();
            }
            return true;
        }
        if (id == R.id.action_open){
            resetColorFilter();
            resetSliders();
            openImageFromGallery();
            return true;
        }
        if (id == R.id.WALLPAPER){
            setWallpaper();
            return true;
        }
        if (id == R.id.SHARE){
            shareButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_A_PHOTO);
                            Toast.makeText(getApplicationContext(),"Select a photo!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void initializeButtons(){
        btn_take = findViewById(R.id.action_takePicture);
        btn_list = findViewById(R.id.action_save);
        btn_load = findViewById(R.id.action_open);
        btn_wallpaper = findViewById(R.id.WALLPAPER);

        imageView_photo = findViewById(R.id.image_preview);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ImageView iv_photo;
            iv_photo = findViewById(R.id.image_preview);

            Glide.with(this).load(currentPhotoPath).into(iv_photo);
        }

        if (requestCode == SELECT_A_PHOTO && resultCode == RESULT_OK){
            Uri selectedPhoto = data.getData();
            ImageView iv_photo;
            iv_photo = findViewById(R.id.image_preview);

            Glide.with(this).load(selectedPhoto).into(iv_photo);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Toast.makeText(getApplicationContext(), "Take a Picture!", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Something went wrong. Could not create file.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android_photo_editor_2k19.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,          /* prefix */
                ".jpg",         /* suffix */
                storageDir           /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() throws IOException {


            BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Android/data/com.example.android_photo_editor_2k19/files/Pictures");
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Toast.makeText(this, "Picture is saved to app directory!", Toast.LENGTH_SHORT).show();
        }



    public void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight()));
            Toast.makeText(this, "Wallpaper is updated!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap viewToBitmap(View view, int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void shareButton(){

        String text = "Look at my awesome picture";
        Uri pictureUri = Uri.parse("Android/data/com.example.android_photo_editor_2k19/files/Pictures/" + this);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,  pictureUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,  text);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent,"Share via"));
    }

    public void resetColorFilter(){
        imageView.setColorFilter(new ColorMatrixColorFilter(
                new float[]{
                        1, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 1, 0}
        ));
    }

    public void rotateBitmap(float degrees){

        imageView.setRotation(rotation + degrees);
        rotation += degrees;

    }

    public void resetSliders(){
        SeekBar b = (SeekBar)findViewById(R.id.seekbar_brightness);
        b.setProgress(100);
        SeekBar c = (SeekBar)findViewById(R.id.seekbar_contrast);
        c.setProgress(0);
        SeekBar s = (SeekBar)findViewById(R.id.seekbar_saturation);
        s.setProgress(255);
    }

    public void onClickRight(View v) {

        rotateBitmap(90);
    }
    public void onClickLeft(View v){
        rotateBitmap(-90);
    }



}
