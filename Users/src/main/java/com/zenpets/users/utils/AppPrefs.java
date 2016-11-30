package com.zenpets.users.utils;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.zenpets.users.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppPrefs extends Application {

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
    }
}