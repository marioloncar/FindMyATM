package com.mario.findmyatm;

import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by mario on 12/9/15.
 */
public class AppDelegate extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Parse services
        Parse.initialize(this, "9PRc2dDa2Ld94x68yuCulqWQIaMO1768dbS8SFXU", "nAz94MTAVp9FRNmjUVYsfQAvdWqRhQTVw8H0SHoZ");
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d("Find My ATM", "Anonymous login failed.");
                } else {
                    Log.d("Find My ATM", "Anonymous user logged in.");
                }
            }
        });
    }
}
