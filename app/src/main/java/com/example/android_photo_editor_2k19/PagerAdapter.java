package com.example.android_photo_editor_2k19;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int aNoOfTabs;
    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.aNoOfTabs = numberOfTabs;
    }



    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0:
                EditImageFragment editImageFragment = new EditImageFragment();
                return editImageFragment;

            case 1:
                RotateImage rotateImage = new RotateImage();
                return rotateImage;
            default:
                return null;


        }



    }

    @Override
    public int getCount() {
        return aNoOfTabs;
    }
}
