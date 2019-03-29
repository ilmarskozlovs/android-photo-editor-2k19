package com.example.android_photo_editor_2k19;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

public class PictureThread extends Thread {

    private ImageView imageView;
    private Bitmap bitmap;
    private Bitmap temp_bitmap;
    private Canvas canvas;
    private Paint paint;
    private ColorMatrix colorMatrixBr = new ColorMatrix();
    private ColorMatrix colorMatrixCon = new ColorMatrix();
    private ColorMatrix colorMatrixSat = new ColorMatrix();

    private ColorMatrix colorMatrixConcat = new ColorMatrix();

    private ColorMatrixColorFilter colorMatrixColorFilter;
    private Handler handler;
    private boolean running = false;
    Matrix matrix = new Matrix();

    public PictureThread(ImageView imageView, Bitmap bitmap){
        this.imageView = imageView;
        this.bitmap = bitmap;
        temp_bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        canvas = new Canvas(temp_bitmap);
        paint = new Paint();
        handler = new Handler();
    }

    public void adjustBrightness(float brightness){

        brightness = brightness * 2.55f;
        colorMatrixBr.set(new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0});

        colorMatrixConcat.setConcat(colorMatrixSat, colorMatrixCon);
        colorMatrixConcat.setConcat(colorMatrixConcat, colorMatrixBr);

        colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrixConcat);

        running = true;

    }

    public void adjustContrast(float contrast){
        contrast /= 10;
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;

        colorMatrixCon.set(new float[]{

                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0});

        colorMatrixConcat.setConcat(colorMatrixSat, colorMatrixBr);
        colorMatrixConcat.setConcat(colorMatrixConcat, colorMatrixCon);

        colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrixConcat);

        running = true;

    }

    public void adjustSaturation(float value){
        float x = 1 + ((value > 0) ? 3 * value / 255 : value / 255);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;
        colorMatrixSat.set(new float[]{

                lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0,
                lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0, 0,
                lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0, 0,
                0, 0, 0, 1, 0});

        colorMatrixConcat.setConcat(colorMatrixCon, colorMatrixBr);
        colorMatrixConcat.setConcat(colorMatrixConcat, colorMatrixSat);

        colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrixConcat);

        running = true;

    }
    @Override
    public void run() {
        while (true){

            if (running){
                canvas.drawBitmap(bitmap, 0, 0, paint);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        imageView.setColorFilter(colorMatrixColorFilter);
                        running = false;

                    }
                });
            }
        }
    }
}