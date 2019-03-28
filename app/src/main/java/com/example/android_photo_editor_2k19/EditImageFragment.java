package com.example.android_photo_editor_2k19;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.zomato.photofilters.imageprocessors.Filter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditImageFragmentListener listener;
    Filter myfilter;



    Button selectImage;
    Button saveImage;
    ImageView imageView;
    private int REQUEST_CODE = 1;
    private Bitmap bitmap;
//    private SeekBar seekBarBrightness;
//    private SeekBar seekBarContrast;
//    private SeekBar seekBarSaturation;
    private PictureThread thread;

    @BindView(R.id.seekbar_brightness)
    SeekBar seekBarBrightness;

    @BindView(R.id.seekbar_contrast)
    SeekBar seekBarContrast;

    @BindView(R.id.seekbar_saturation)
    SeekBar seekBarSaturation;

    EditImageFragment editImageFragment;

    // modified image values



    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {

    }


    public static EditImageFragment newInstance(String param1, String param2) {
        EditImageFragment fragment = new EditImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

        ButterKnife.bind(this, view);

        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.monkey);
        ImageView mLogo = (ImageView)getActivity().findViewById(R.id.image_preview);
        mLogo.setImageBitmap(bitmap);

        thread = new PictureThread(mLogo,bitmap);
        thread.start();



        ButterKnife.bind(this, view);
        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);

        seekBarContrast.setMax(100);
        seekBarContrast.setProgress(20);

        seekBarSaturation.setMax(510);
        seekBarSaturation.setProgress(255);

        seekBarBrightness.setOnSeekBarChangeListener(this);
        seekBarContrast.setOnSeekBarChangeListener(this);
        seekBarSaturation.setOnSeekBarChangeListener(this);


        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                thread.adjustBrightness(seekBar.getProgress()-100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }


        });



        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thread.adjustContrast(seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });


        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thread.adjustSaturation(seekBar.getProgress()-255);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });

        return view;


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }



    public void resetSliders(){
//        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);

//        seekBarContrast.setMax(100);
        seekBarContrast.setProgress(10);

//        seekBarSaturation.setMax(510);
        seekBarSaturation.setProgress(255);
    }



    public interface EditImageFragmentListener {


        void onEditStarted();

        void onEditCompleted();
    }
}
