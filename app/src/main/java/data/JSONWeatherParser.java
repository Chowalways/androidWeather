package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.Place;
import model.Weather;

public class JSONWeatherParser {

    public static Weather getWeather(String data){
        Weather weather = new Weather();

        //create json object from data

       try{
           JSONObject jsonObject = new JSONObject(data);

           Place place = new Place();
           JSONObject coorObj = Utils.getObject("coord", jsonObject);
           place.setLat(Utils.getFloat("lat", coorObj));
           place.setLon(Utils.getFloat("lon", coorObj));
           //Get the sys obj from JSON

           JSONObject sysObj = Utils.getObject("sys", jsonObject);
           place.setCountry(Utils.getString("country", sysObj));
           place.setLastupdate(Utils.getInt("dt", jsonObject));
           place.setSunrise(Utils.getInt("sunrise", sysObj));
           place.setSunset(Utils.getInt("sunset", sysObj));
           place.setCity(Utils.getString("name", jsonObject));
           weather.place = place;

           //get weather info
           JSONArray jsonArray = jsonObject.getJSONArray("weather");
           JSONObject jsonWeahter = jsonArray.getJSONObject(0);
           weather.currentCondition.setWeatherId(Utils.getInt("id", jsonWeahter));
           weather.currentCondition.setDescription(Utils.getString("description", jsonWeahter));
           weather.currentCondition.setCondition(Utils.getString("main", jsonWeahter));
           //weather.currentCondition.setHumidity(Utils.getInt("humidity", jsonWeahter));

           //weather.currentCondition.setPressure(Utils.getInt("pressure", jsonWeahter));
           weather.currentCondition.setIcon(Utils.getString("icon", jsonWeahter));
//uninportnt
           // main obj
           JSONObject mainObj = Utils.getObject("main", jsonObject);
           weather.currentCondition.setTemperature(Utils.getDouble("temp", mainObj));
           weather.currentCondition.setPressure(Utils.getFloat("pressure", mainObj));
           weather.currentCondition.setHumidity(Utils.getFloat("humidity", mainObj));
           weather.currentCondition.setMaxTemp(Utils.getFloat("temp_min", mainObj));
           weather.currentCondition.setMinTemp(Utils.getFloat("temp_max", mainObj));

           JSONObject windObj = Utils.getObject("wind", jsonObject);
           weather.wind.setSpeed(Utils.getFloat("speed", windObj));
           weather.wind.setDeg(Utils.getFloat("deg", windObj));

           JSONObject cloudsObj = Utils.getObject("clouds", jsonObject);
           weather.clouds.setPrecepitation(Utils.getInt("all", cloudsObj));

           return weather;

       }catch (JSONException e) {
           e.printStackTrace();
            return null;
       }
    }
}
