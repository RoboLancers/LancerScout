package robolancer.com.lancerscout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.models.pit.LancerPit;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

public class PitHistoryActivity extends LancerActivity {
    public static LancerPit clickedPit;
    public static ArrayList<LancerPit> pitHistory = new ArrayList<>();

    ListView pitHistoryListView;
    ArrayAdapter<LancerPit> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_history);

        findViews();
        setupListeners();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pitHistory);
        pitHistoryListView.setAdapter(adapter);
    }

    private void findViews() {
        setupAppbar();

        pitHistoryListView = findViewById(R.id.pitHistoryListView);
    }

    private void setupListeners() {
        pitHistoryListView.setOnItemClickListener((adapterView, view, i, l) -> {
            clickedPit = (LancerPit) adapterView.getItemAtPosition(i);

            Intent intent = new Intent(PitHistoryActivity.this, PitScoutingActivity.class);
            intent.putExtra("history_flag_", 1);
            startActivity(intent);
        });
    }

    @Override
    public void save() {
        LancerScoutUtility.getDefaultSharedPreferenceEditor(this).putString("pitHistory", gson.toJson(pitHistory)).apply();
    }

    @Override
    public void reset() {
        pitHistory.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pit_history_reset:
                new AlertDialog.Builder(PitHistoryActivity.this).setTitle("Confirm Reset")
                        .setMessage("Are you sure you want to reset?")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> reset()).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pit_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
    }
}
