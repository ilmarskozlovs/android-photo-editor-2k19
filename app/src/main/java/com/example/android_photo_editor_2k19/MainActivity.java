package com.example.android_photo_editor_2k19;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android_photo_editor_2k19.utils.BitmapUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements EditImageFragment.EditImageFragmentListener, CropImage.OnFragmentInteractionListener{
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private TextView mTextMessage;

    Bitmap bitmap;


    @BindView(R.id.image_preview)
    ImageView imageView;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;


    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

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
        ButterKnife.bind(this);
        bitmap = BitmapFactory.decodeResource(getResources(),R.id.image_preview);
        init();
//        imageView = findViewById(R.id.image_preview);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Edit Image"));
        tabLayout.addTab(tabLayout.newTab().setText("Crop Image"));
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

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

        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);

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
            }
//            saveImageToGallery();
            return true;
        }
        if (id == R.id.action_open){
            openImageFromGallery();
            return true;
        }
        if (id == R.id.WALLPAPER){
            startWall();
            return true;
        }
        if (id == R.id.SHARE){
            View content = findViewById(R.id.image_preview);
            content.setDrawingCacheEnabled(true);

            Bitmap bitmap = content.getDrawingCache();
            File root = Environment.getExternalStorageDirectory();
            File cachePath = new File(root.getAbsolutePath() + "Android/data/com.example.android_photo_editor_2k19/files/Pictures");
            try {
                cachePath.createNewFile();
                FileOutputStream ostream = new FileOutputStream(cachePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
            startActivity(Intent.createChooser(share,"Share via"));

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


    private void saveImageToGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                            if (!TextUtils.isEmpty(path)) {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openImage(path);
                                            }
                                        });

                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Image saved!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
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

    private void openImage(String path) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.parse(path), "image/*");
//        startActivity(intent);
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
    }


    public void init(){
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
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
        BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/Android/data/com.example.android_photo_editor_2k19/files/Pictures");
//        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        outStream = new FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        outStream.flush();
        outStream.close();
    }

    public void startWall() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight()));
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void shareButton(){
        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.setType("image/jpg");
        final File photoFile = new File(getFilesDir(), "foo.jpg");
        intent1.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
        startActivity(Intent.createChooser(intent1, "Share image using"));
//        intent1.putExtra(Intent.EXTRA_SUBJECT,shareSub);
//        intent1.putExtra(Intent.EXTRA_TEXT,shareBody);
//        startActivity(Intent.createChooser(intent1, "Share using: "));
    }
}
