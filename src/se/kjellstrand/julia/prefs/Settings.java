
package se.kjellstrand.julia.prefs;

import se.kjellstrand.julia.JuliaWallpaperService;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public final class Settings {

    private static final String PREFERENCES_FILE = "RecommendationPreferencesFile";

    private static final String LOG_TAG = Settings.class.getCanonicalName();

    private static final String PREFS_ZOOM = "zoom";

    private static final String PREFS_TOUCH_X_ACC = "touch_x_acc";

    private static final String PREFS_TOUCH_Y_ACC = "touch_y_acc";

    private static SharedPreferences.Editor openSharedPreferencesForEditing(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE,
                Context.MODE_PRIVATE);
        return prefs.edit();
    }

    private static SharedPreferences loadSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static void setZoom(Context context, float zoom) {
        SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
        editor.putFloat(PREFS_ZOOM, zoom);
        editor.apply();
    }

    public static float getZoom(Context context) {
        return loadSharedPreferences(context).getFloat(PREFS_ZOOM, JuliaWallpaperService.INITIAL_ZOOM);
    }

    public static void setTouchYaccumulated(Context context, float touchYaccumulated) {
        SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
        editor.putFloat(PREFS_TOUCH_Y_ACC, touchYaccumulated);
        editor.apply();
    }


    public static void setTouchXaccumulated(Context context, float touchXaccumulated) {
        SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
        editor.putFloat(PREFS_TOUCH_X_ACC, touchXaccumulated);
        editor.apply();
    }

    public static float getTouchYaccumulated(Context context) {
        return loadSharedPreferences(context).getFloat(PREFS_TOUCH_Y_ACC, 0);
    }

    public static float getTouchXaccumulated(Context context) {
        return loadSharedPreferences(context).getFloat(PREFS_TOUCH_X_ACC, 0);
    }

    public static void reset(Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
            editor.clear();
            editor.apply();
        } else {
            Log.e(LOG_TAG,
                    "Reset: context == null.");
        }
    }

}
