package robolancer.com.lancerscout.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import robolancer.com.lancerscout.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
