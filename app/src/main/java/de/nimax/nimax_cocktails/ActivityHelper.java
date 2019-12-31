package de.nimax.nimax_cocktails;

import android.app.Activity;
import android.os.Build;

public class ActivityHelper {

    /**
     * Method to disable the standard activity transitions
     * @param activity for which the transitions should be disabled
     */
    public static void disableDefaultTransition(Activity activity) {
        // Deactivate the old transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setEnterTransition(null);
            activity.getWindow().setExitTransition(null);
        }
    }
}
