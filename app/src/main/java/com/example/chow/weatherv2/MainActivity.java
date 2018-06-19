package com.example.chow.weatherv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.chow.weatherv2.WeatherClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public void setLocale(int choice) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if(choice == 1)
            config.locale = Locale.TRADITIONAL_CHINESE;
        else
            config.locale = Locale.ENGLISH;

        resources.updateConfiguration(config, dm);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // this.recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.changeCity:

                return true;
            case R.id.Language:
                final CharSequence[] items = { "English", "中文"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.Selection));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position ) {

                        List<String> language = new ArrayList<String>();

                        if (position == 0) {
                            setLocale(2);
                        }
                        if (position == 1) {
                            setLocale(1);
                        }
                    }
                }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherGif.stop();
        earthquakeGif.stop();
        typhoonGif.stop();
    }
}