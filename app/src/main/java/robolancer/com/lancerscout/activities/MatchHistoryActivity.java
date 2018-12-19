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
import robolancer.com.lancerscout.models.match.LancerMatch;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

public class MatchHistoryActivity extends LancerActivity {

    public static LancerMatch clickedMatch;
    public static ArrayList<LancerMatch> matchHistory = new ArrayList<>();
    ListView matchHistoryListView;
    ArrayAdapter<LancerMatch> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        findViews();
        setupListeners();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchHistory);
        matchHistoryListView.setAdapter(adapter);
    }

    private void findViews() {
        setupAppbar();
        matchHistoryListView = findViewById(R.id.matchHistoryListView);
    }

    private void setupListeners() {
        matchHistoryListView.setOnItemClickListener((adapterView, view, i, l) -> {
            clickedMatch = (LancerMatch) adapterView.getItemAtPosition(i);

            Intent intent = new Intent(MatchHistoryActivity.this, MatchScoutingActivity.class);
            intent.putExtra("history_flag_", 1);
            startActivity(intent);
        });
    }

    @Override
    public void save() {
        LancerScoutUtility.getDefaultSharedPreferenceEditor(this).putString("matchHistory", gson.toJson(matchHistory)).apply();
    }

    @Override
    public void reset() {
        matchHistory.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.match_history_reset:
                new AlertDialog.Builder(MatchHistoryActivity.this).setTitle("Confirm Reset")
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
        getMenuInflater().inflate(R.menu.menu_match_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
    }
}
