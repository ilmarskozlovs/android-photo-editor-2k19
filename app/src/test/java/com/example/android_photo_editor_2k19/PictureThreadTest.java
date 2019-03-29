package com.example.android_photo_editor_2k19;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)

public class PictureThreadTest {
    @Mock
    Bitmap bitmap;
    @Mock
    ImageView mLogo;
    @Mock
    PictureThread thread = new PictureThread(mLogo,bitmap);


    @Test
    public void adjustBrightness() {
        thread.start();
        float d = 1;
        float[] matrixTest = new float[]{
                1, 0, 0, 0, 50,
                0, 1, 0, 0, 50,
                0, 0, 1, 0, 50,
                0, 0, 0, 1, 0};
        thread.adjustBrightness(50);
        assertArrayEquals(matrixTest, thread.colorMatrixBr.getArray(), d);
    }

    @Test
    public void adjustContrast() {
    }

    @Test
    public void adjustSaturation() {
    }

    @Test
    public void run() {
    }
}