package com.example.noushad.shopaholic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.noushad.shopaholic.model.UserInfo;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by tapos on 10/3/17.
 */

public class SharedPrefManager {
    private static final String KEY_USER_LOCATION = "keyuserlocation";
    private static SharedPrefManager mInstance;
    private static Context sContext;

    private static final String SHARED_PREF_NAME = "levirgon";
    private static final String KEY_USER_ID = "keyuserid";
    private static final String KEY_USER_NAME = "keyusername";
    private static final String KEY_USER_EMAIL = "keyuseremail";
    private static final String KEY_USER_PHONE = "keyuserphone";
    private static final String KEY_USER_PROFILE_PIC_SMALL = "keyuserprofilepicsmall";
    private static final String KEY_USER_PROFILE_PIC_MEDIUM = "keyuserprofilepicmedium";
    private static final String KEY_USER_PROFILE_PIC_LARGE = "keyuserprofilepiclarge";
    private static final String KEY_USER_TOKEN_TYPE = "keyusertokentype";
    private static final String KEY_USER_ACCESS_TOKEN = "keyuseraccesstoken";
    private static final String KEY_USER_REFRESH_TOKEN = "keyuserrefreshtoken";
    private static final String KEY_USER_TOKEN_EXPIRE = "keyusertokenexpire";
    private static final String TAG_TOKEN = "tagtoken";

    private SharedPrefManager(Context context) {
        sContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }



    public boolean userOwnDataUpdate(UserInfo user) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_LOCATION, user.getLocation());

        editor.apply();
        return true;
    }

    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    public String getAuthToken() {

        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String info = sharedPreferences.getString(KEY_USER_TOKEN_TYPE, null)
                + " " + sharedPreferences.getString(KEY_USER_ACCESS_TOKEN, null);
        System.out.println(info);
        return info;
    }

    public String getUserId() {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(KEY_USER_ID, null);
        return userId;

    }

    public UserInfo getUser() {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new UserInfo(
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_PHONE, null),
                sharedPreferences.getString(KEY_USER_ID, null),
                sharedPreferences.getString(KEY_USER_LOCATION, null)
        );
    }

    public boolean logout() {

        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();
        return true;
    }


    // for firebase notification

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken() {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN, null);
    }

    public void setUserId(String uid) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, uid);

    }
}