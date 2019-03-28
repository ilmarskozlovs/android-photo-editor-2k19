package com.example.android_photo_editor_2k19;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.ButterKnife;




public class RotateImage extends Fragment implements Button.OnClickListener{

    private float rotation = 0;

    ImageView imageView;
    ImageView Test;
    private Bitmap bitmap;
    private PictureThread thread;

    private Button rotateLeft;


    private Button rotateRight;




    public RotateImage() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rotate_image, container, false);

        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.monkey);
        ImageView mLogo = (ImageView)getActivity().findViewById(R.id.image_preview);
        mLogo.setImageBitmap(bitmap);
        init();
        thread = new PictureThread(mLogo,bitmap);
        thread.start();
        Test = (ImageView)getActivity().findViewById(R.id.image_preview);

        Test.setImageBitmap(bitmap);

        ButterKnife.bind(this, view);





        return view;
    }



    public void init(){
        rotateLeft = getActivity().findViewById(R.id.rotate_left);
        rotateRight = getActivity().findViewById(R.id.rotate_right);
    }


    @Override
    public void onClick(View v) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}