package cz.lsrom.webviewtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Lukas Srom <lukas.srom@gmail.com>
 */
class SPManager {
    private static final String TAG = SPManager.class.getSimpleName();

    private static final String SP_NAME = "webview_tester_sp";
    private static final String TRUST_SSL_TAG = "trust_all_ssl";

    public static void setTrustAllSslCerts (boolean trust, @NonNull Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(TRUST_SSL_TAG, trust);
        editor.apply();
    }

    public static boolean trustAllSslCerts (@Nullable Context context){
        if (context == null){
            Log.d(TAG, "Context null. Cannot get trust all SSL certificates.");
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(TRUST_SSL_TAG, false);
    }
}
