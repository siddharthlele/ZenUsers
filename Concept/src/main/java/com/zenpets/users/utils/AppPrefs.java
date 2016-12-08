package com.zenpets.users.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.zenpets.users.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppPrefs extends Application {

    /***** SHARED PREFERENCES INSTANCE *****/
    private SharedPreferences mPreferences;

    /** THE INITIAL SETUP STATUS **/
    private final String CURRENT_LATITUDE = "currentLatitude";
    private final String CURRENT_LONGITUDE = "currentLongitude";

    @Override
    public void onCreate() {
        super.onCreate();

        /** INSTANTIATE THE FACEBOOK SDK **/
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

        /** INSTANTIATE THE CALLIGRAPHY LIBRARY **/
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeueLTW1G-Cn.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        /** INSTANTIATE THE PREFERENCE MANAGER **/
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /** SET THE CURRENT COORDINATES **/
    public void setCoordinates(String latitude, String longitude) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(CURRENT_LATITUDE, latitude);
        edit.putString(CURRENT_LONGITUDE, longitude);
        edit.apply();
    }

    /** GET THE CURRENT COORDINATES **/
    public String[] getCoordinates()	{
        String latitude = mPreferences.getString(CURRENT_LATITUDE, null);
        String longitude = mPreferences.getString(CURRENT_LONGITUDE, null);

        return new String[]	{ latitude, longitude };
    }
}