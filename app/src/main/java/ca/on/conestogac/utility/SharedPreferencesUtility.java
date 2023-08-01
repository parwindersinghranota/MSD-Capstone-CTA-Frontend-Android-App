package ca.on.conestogac.utility;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtility {
    private static SharedPreferencesUtility mInstance;
    private static Context mCtx;

    private SharedPreferencesUtility(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPreferencesUtility getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesUtility(context);
        }
        return mInstance;
    }

    public void setString (String key, String value){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getString(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }

    public void setBoolean (String key, boolean value){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public boolean getBoolean(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }

    public boolean getBooleanWithDefaultTrue(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,true);
    }

    public void setInt (String key, int value){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public int getInt(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,-1);
    }

    public void removeKey(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }



}

