package com.havedroid.dddsched.Util;

import android.content.Context;
import android.content.SharedPreferences;
import com.havedroid.dddsched.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 22/09/2012
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
public class DDDPreferences {
    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }
}
