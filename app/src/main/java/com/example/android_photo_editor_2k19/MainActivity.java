package com.example.android_photo_editor_2k19;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public String helloTeam(){
        return "hello team!!!";
    }
    public String success(){
        return "success!!!";
    }
    public String moreChanges(){
        return "more changes!!!";
    }

    public String asdasd(){
        return "MALADEC MALADEC ";
    }

    public boolean running(String maladec){

        return maladec.equals("MALADEC");

    }

    public String thisIsTooHard(){
        return "bad choice :(";
    }

    public String bbTeam(){
        return "goodbye team!";
    }

    public String aswdqw(){
        return "commit pleasdqwdqwdqwe!!!!";
    }
}
