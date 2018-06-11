package robolancer.com.lancerscout.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import robolancer.com.lancerscout.activities.MatchScoutingActivity;

public class BluetoothHelper implements Runnable{

    private OutputStream outputStream;
    private InputStream inputStream;

    private byte[] byteCommand;
    private String command;

    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    private AlertDialog pairedDeviceDialog, discoveredDeviceDialog;

    private BluetoothSocket socket;

    public BluetoothHelper(Context context, BluetoothAdapter bluetoothAdapter){
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void showPairedBluetoothDevices(boolean send, String data, MatchScoutingActivity matchScoutingActivity) {
        ArrayList<BluetoothDevice> pairedDeviceList = new ArrayList<>();
        ArrayList<String> pairedDeviceName = new ArrayList<>();

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    pairedDeviceList.addAll(bondedDevices);
                }
            }
        }

        for(BluetoothDevice device : pairedDeviceList){
            pairedDeviceName.add(device.getName());
        }

        ArrayAdapter<String> pairedDeviceAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        pairedDeviceAdapter.addAll(pairedDeviceName);
        pairedDeviceAdapter.add("Not on screen");

        pairedDeviceDialog = new AlertDialog.Builder(context).setAdapter(pairedDeviceAdapter, (dialog, which) -> {
            if(pairedDeviceAdapter.getItem(which).equals("Not on screen")){
                Toast.makeText(context, "Please pair with computer", Toast.LENGTH_LONG).show();
                dialog.dismiss();

                matchScoutingActivity.save();

                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else {
                try {
                    dialog.dismiss();
                    BluetoothDevice device = pairedDeviceList.get(which);
                    ParcelUuid uuid = new ParcelUuid(UUID.fromString("fba199f7-47a7-4ed4-b880-3073424d2e2c" + ""));
                    socket = device.createRfcommSocketToServiceRecord(uuid.getUuid());
                    bluetoothAdapter.cancelDiscovery();
                    Toast.makeText(context, "Connecting to " + device.getName(), Toast.LENGTH_LONG).show();
                    socket.connect();
                    Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                    if(send){
                        write(data);
                    }
                    Toast.makeText(context, "Sent", Toast.LENGTH_LONG).show();
                    matchScoutingActivity.reset();
                    socket.close();
                } catch (IOException e) {
                    Toast.makeText(context, "Can not connect to Lancer Scout Server! Please check if server is open or try again.", Toast.LENGTH_LONG).show();
                    showPairedBluetoothDevices(send, data, matchScoutingActivity);
                }
            }
        }).show();
    }

    private void showDiscoveredBluetooth(boolean send, String data){
        final ArrayAdapter<String> discoveredDeviceAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
        ArrayList<String> discoveredDevicesName = new ArrayList<>();

        discoveredDeviceDialog = new AlertDialog.Builder(context).setAdapter(discoveredDeviceAdapter, (dialog, which) -> {
            try {
                discoveredDeviceDialog.dismiss();
                BluetoothDevice device = discoveredDevices.get(which);

                discoveredDevices.clear();
                discoveredDevicesName.clear();

                ParcelUuid uuid = new ParcelUuid(UUID.fromString("fba199f7-47a7-4ed4-b880-3073424d2e2c" + ""));
                socket = device.createRfcommSocketToServiceRecord(uuid.getUuid());
                bluetoothAdapter.cancelDiscovery();
                Toast.makeText(context, "Connecting to " + device.getName(), Toast.LENGTH_LONG).show();
                socket.connect();
                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                if(send){
                    write(data);
                }
                Toast.makeText(context, "Sent", Toast.LENGTH_LONG).show();

                socket.close();
            } catch (IOException e) {
                Toast.makeText(context, "Can not connect to Lancer Scout Server! Please check if server is open. If it is please contact nearest programmer", Toast.LENGTH_LONG).show();
                showDiscoveredBluetooth(send, data);
            }
        }).show();

        Toast.makeText(context, "Searching...", Toast.LENGTH_LONG).show();
        bluetoothAdapter.startDiscovery();
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if(device != null && device.getName() != null && !device.getName().equals("")) {
                        Log.e("BluetoothHelper", device.getName());
                        ArrayAdapter<String> discoveredDeviceAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
                        discoveredDevices.add(device);
                        discoveredDevicesName.add(device.getName());
                        discoveredDeviceAdapter.addAll(discoveredDevicesName);
                        discoveredDeviceDialog.getListView().setAdapter(discoveredDeviceAdapter);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void run(){
        try {
            if(socket != null) {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }

            Log.e("BluetoothHelper","Waiting for input");

            while (true) {
                if (inputStream != null) {
                    processCommand(new String(readByteArrayCommand(inputStream)));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void processCommand(String command) {
        Log.e("BluetoothHelper", command);
        switch (command) {
            default:
                Log.e("BluetoothHelper", "Received " + command);
        }
    }

    public void write(int command) throws IOException {
        if(outputStream != null) {
            outputStream.write(command);
        }
    }

    public void write(String command) throws IOException {
        if(outputStream != null) {
            if (command != null && !command.isEmpty()) {
                outputStream.write(command.getBytes());
                outputStream.write(0);
            }
        }
    }

    private byte[] readByteArrayCommand(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = inputStream.read();

        while(read != -1 && read != 0){
            byteArrayOutputStream.write(read);
            read = inputStream.read();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public AlertDialog getPairedDeviceDialog() {
        return pairedDeviceDialog;
    }

    public AlertDialog getDiscoveredDeviceDialog() {
        return discoveredDeviceDialog;
    }
}
