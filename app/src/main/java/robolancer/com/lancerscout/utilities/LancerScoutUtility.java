package robolancer.com.lancerscout.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LancerScoutUtility {

    private static final String PREFERENCE_NAME = "LancerScout";

    public static SharedPreferences getDefaultSharedPreference(Activity activity) {
        return activity.getApplicationContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getDefaultSharedPreferenceEditor(Activity activity) {
        return getDefaultSharedPreference(activity).edit();
    }
}
