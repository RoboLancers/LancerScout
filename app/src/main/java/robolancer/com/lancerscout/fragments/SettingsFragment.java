package robolancer.com.lancerscout.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import robolancer.com.lancerscout.R;

public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
