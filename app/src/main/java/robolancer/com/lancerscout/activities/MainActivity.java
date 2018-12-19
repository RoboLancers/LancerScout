package robolancer.com.lancerscout.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Set;

import robolancer.com.lancerscout.R;

public class MainActivity extends LancerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.matchScoutingButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MatchScoutingActivity.class)));
        findViewById(R.id.pitScoutingButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PitScoutingActivity.class)));

        setupBluetooth();

        Toast.makeText(getApplicationContext(), "Please pair with computer before using app", Toast.LENGTH_LONG).show();
    }

    public void setupBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

                if (bondedDevices.size() == 0) {
                    DialogInterface.OnClickListener onClickListener = (dialogInterface, i) -> {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                dialogInterface.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                            default:
                                finishAffinity();
                                break;
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Have you paired with scouting server?")
                            .setPositiveButton("Yes", onClickListener)
                            .setNegativeButton("No", onClickListener)
                            .show();
                }
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
                }
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", (dialog, which) -> System.exit(0))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void save() {

    }

    @Override
    public void reset() {

    }
}
