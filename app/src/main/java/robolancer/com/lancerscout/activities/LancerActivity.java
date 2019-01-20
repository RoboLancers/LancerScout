package robolancer.com.lancerscout.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.util.Objects;

import robolancer.com.lancerscout.R;


public abstract class LancerActivity extends AppCompatActivity {
    protected Gson gson = new Gson();
    protected Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void setupAppbar() {
        appbar = findViewById(R.id.appbar);

        appbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(appbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        save();
    }

    public abstract void save();

    public abstract void reset();
}
