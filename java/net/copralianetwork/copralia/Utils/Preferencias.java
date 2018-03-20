package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Marcos on 16/10/2015.
 */
public class Preferencias {

    public static String getString(Context ctx, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return pref.getString(key,"");
    }

    public static void saveString(Context ctx,String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
