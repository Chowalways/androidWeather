package data;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences pref;

    public CityPreference(Activity activity){

        pref = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return pref.getString("city", "Taichung,TW");

    }

    public void setCity(String city){
        pref.edit().putString("city", city).commit();
    }


}
