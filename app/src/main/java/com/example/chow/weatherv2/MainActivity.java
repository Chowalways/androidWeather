package com.example.chow.weatherv2;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.chow.weatherv2.WeatherClass;

public class MainActivity extends AppCompatActivity {

    AnimationDrawable weatherGif;
    AnimationDrawable typhoonGif;
    AnimationDrawable earthquakeGif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton weatherImage = (ImageButton) findViewById(R.id.weatherbtn);
        weatherImage.setBackgroundResource(R.drawable.rain);
        weatherGif = (AnimationDrawable) weatherImage.getBackground();

        ImageButton typhoonImage = (ImageButton) findViewById(R.id.typhoonbtn);
        typhoonImage.setBackgroundResource(R.drawable.typhoon);
        typhoonGif = (AnimationDrawable) typhoonImage.getBackground();

        ImageButton earthQuake = (ImageButton)findViewById(R.id.earthquakebtn);
        earthQuake.setBackgroundResource(R.drawable.earthquake);
        earthquakeGif = (AnimationDrawable) earthQuake.getBackground();

    }
    @Override
    protected void onStart() {
        super.onStart();
        weatherGif.start();
        typhoonGif.start();
        earthquakeGif.start();

    }
    public void WeatherClass(View view){
        Intent intent = new Intent(this, WeatherClass.class);
        startActivity(intent);
    }


    public  void TyphoonMap(View view){
        Intent intent = new Intent(this, TyphoonMap.class);
        startActivity(intent);
   }
    public void EarthQuakeWarnings(View view){
        Intent intent = new Intent(this, EarthQuakeWarnings.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherGif.stop();
        earthquakeGif.stop();
        typhoonGif.stop();
    }
}