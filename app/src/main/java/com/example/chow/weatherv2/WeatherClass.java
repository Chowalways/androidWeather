package com.example.chow.weatherv2;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pavlospt.CircleView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class WeatherClass extends AppCompatActivity {
    private TextView cityName;
    private CircleView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //removeTitleBar();
        setContentView(R.layout.weather_class);
        cityName = (TextView)findViewById(R.id.cityText);
        iconView = (ImageView)findViewById(R.id.thumbnailIcon);
        temp = (CircleView) findViewById(R.id.temperatureText);
        description = (TextView)findViewById(R.id.cloudText);
        humidity = (TextView)findViewById(R.id.humidityText);
        pressure = (TextView)findViewById(R.id.pressureText);
        wind = (TextView)findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView)findViewById(R.id.sunsetText);
        updated = (TextView)findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(WeatherClass.this);
        renderWeatherData(cityPreference.getCity());
    }


//uninportnt
    public void renderWeatherData (String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city});
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }
        private Bitmap downloadImage (String code){
            final DefaultHttpClient client  = new DefaultHttpClient();
            final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code+ ".png");
            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode != HttpStatus.SC_OK){
                    Log.e("DownloadImage", "Error:" + statusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if (entity != null){
                    InputStream inputStream = null;
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

//    protected void removeTitleBar() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        if (getSupportActionBar() != null)
//            getSupportActionBar().hide();
//        //Remove notification bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{

        @Override
        protected Weather doInBackground(String... params) {
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            weather = JSONWeatherParser.getWeather(data);
            weather.iconData = weather.currentCondition.getIcon();

            Log.v("Data:", weather.place.getCity() + "" + weather.currentCondition.getDescription());

            new DownloadImageAsyncTask().execute(weather.iconData);
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);


            //sunrise date
            Long lsunrise = Long.valueOf(weather.place.getSunrise())*1000;
            Date dsunrise = new java.util.Date(lsunrise);
            String sunriseDate = new SimpleDateFormat("hh:mm:a").format(dsunrise);

            //sunset date
            Long lsunset = Long.valueOf(weather.place.getSunset())*1000;
            Date dsunset = new java.util.Date(lsunset);
            String sunsetDate = new SimpleDateFormat("hh:mm:a").format(dsunset);

            // last update date
            Long lupdate = Long.valueOf(weather.place.getLastupdate())*1000;
            Date dupdate = new java.util.Date(lupdate);
            String updateDate = new SimpleDateFormat("hh:mm:a").format(dupdate);




            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setTitleText("" + tempFormat + getString(R.string.temperature));
            humidity.setText(/*"Humidity: " + */weather.currentCondition.getHumidity() + "%");
            pressure.setText(/*"Pressure: " + */weather.currentCondition.getPressure() + getString(R.string.hpa));
            wind.setText(/*"Wind: \n" + */weather.wind.getSpeed() + getString(R.string.mps));
            sunrise.setText(/*"Sunrise: " + */sunriseDate);
            sunset.setText(/*"Sunset: " + */sunsetDate);
            updated.setText(getString(R.string.Last_Updated)+": " + updateDate);
            description.setText(/*"Condition: " + weather.currentCondition.getCondition() + "(" +*/
                    weather.currentCondition.getDescription() /*+ ")"*/);
        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherClass.this);

        builder.setTitle(getString(R.string.ChangeCity));
        final EditText cityInput = new EditText(WeatherClass.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Taipei,TW");

        builder.setView(cityInput);
        builder.setPositiveButton(getString(R.string.Submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CityPreference cityPreference = new CityPreference(WeatherClass.this);
                cityPreference.setCity(cityInput.getText().toString());
                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);
            }
        });
        builder.show();
    }

    /** Bilingual
     *
     *  In Order to prevent to load original configuration from system ,Set the configuration to our own setting in advance
     */
    public void setLocale(int choice) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if(choice == 1)
            config.locale = Locale.TRADITIONAL_CHINESE;
        else
            config.locale = Locale.ENGLISH;

        resources.updateConfiguration(config, dm);

        Intent intent = new Intent(this, WeatherClass.class);
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
                showInputDialog();
                return true;
            case R.id.Language:
                final CharSequence[] items = { "English", "中文"};

                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherClass.this);
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
}
