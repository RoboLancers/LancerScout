package robolancer.com.lancerscout.activities.pit;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.activities.LancerActivity;
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.pit.LancerPit;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

public class PitQueueActivity extends LancerActivity {
    public static LancerPit clickedPit;
    public static ArrayList<LancerPit> queuedPits = new ArrayList<>();

    BluetoothHelper bluetoothHelper;
    BluetoothAdapter bluetoothAdapter;
    Thread bluetoothThread;

    ListView pitQueueListView;
    ArrayAdapter<LancerPit> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_queue);

        findViews();
        setupListeners();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, queuedPits);
        pitQueueListView.setAdapter(adapter);

        bluetoothHelper = new BluetoothHelper(this, bluetoothAdapter);

        bluetoothThread = new Thread(bluetoothHelper);
        bluetoothThread.start();
    }

    private void findViews() {
        setupAppbar();
        pitQueueListView = findViewById(R.id.pitQueueListView);
    }

    private void setupListeners() {
        pitQueueListView.setOnItemClickListener((adapterView, view, i, l) -> {
            clickedPit = (LancerPit) adapterView.getItemAtPosition(i);
            startActivity(new Intent(PitQueueActivity.this, PitScoutingActivity.class));
        });
    }

    @Override
    public void save() {
        LancerScoutUtility.getDefaultSharedPreferenceEditor(this).putString("pitQueue", gson.toJson(queuedPits)).apply();
    }

    @Override
    public void reset() {
        queuedPits.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pit_queued_reset:
                new AlertDialog.Builder(PitQueueActivity.this).setTitle("Confirm Reset")
                        .setMessage("Are you sure you want to reset?")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> reset()).show();
                break;
            case R.id.pit_queued_send:
                String json;
                StringBuilder data = new StringBuilder();

                for (LancerPit queuedPit : PitQueueActivity.queuedPits) {
                    json = gson.toJson(queuedPit);
                    data.append("PIT-").append(json).append("\n");
                    PitHistoryActivity.pitHistory.add(queuedPit);
                }

                bluetoothHelper.showPairedBluetoothDevices(true, data.toString(), this);
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
