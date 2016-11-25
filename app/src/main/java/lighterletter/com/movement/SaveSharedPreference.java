package lighterletter.com.movement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by john on 11/22/16.
 */

public class SaveSharedPreference {
    private static final String PREF_USER_KEY= "com.lighterletter.movement.pref_user_key";

    public static String currentUser = "";

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static String getCurrentUser(){
        return currentUser;
    }

    public static void setUserKey(Context ctx, String userKey)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_KEY, userKey);
        editor.apply();
    }

    public static String getUserKey(Context ctx)
    {
        currentUser = getSharedPreferences(ctx).getString(PREF_USER_KEY, "");
        return currentUser;
    }

    public static void clearUserKey(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }
}
