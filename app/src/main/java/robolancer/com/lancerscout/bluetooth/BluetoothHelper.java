package robolancer.com.lancerscout.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import robolancer.com.lancerscout.activities.SettingsActivity;

public class BluetoothHelper implements Runnable{

    private OutputStream outputStream;
    private InputStream inStream;

    private byte[] byteCommand;
    private String command;

    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    private AlertDialog dialog;

    public BluetoothHelper(Context context, BluetoothAdapter bluetoothAdapter){
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void showBluetoothDevices(){
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        ArrayList<String> deviceName = new ArrayList<>();

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    deviceList.addAll(bondedDevices);
                }
            }
        }

        for(BluetoothDevice device : deviceList){
            deviceName.add(device.getName());
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(deviceName);

        dialog = new AlertDialog.Builder(context).setAdapter(arrayAdapter, (dialog, which) -> {
            try {
                dialog.dismiss();
                BluetoothDevice device = deviceList.get(which);
                ParcelUuid uuid = new ParcelUuid(UUID.fromString(getUUID() + ""));
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid.getUuid());
                bluetoothAdapter.cancelDiscovery();
                Toast.makeText(context, "Connecting to " + device.getName(), Toast.LENGTH_LONG).show();
                socket.connect();
                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                inStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(context, "Can not connect for some reason! Please restart", Toast.LENGTH_LONG).show();
                showBluetoothDevices();
            }
        }).setCancelable(false).show();
    }

    public String getUUID(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch(sharedPreferences.getString("pref_scoutingPhoneNumber", "")){
            case "Scouting Phone 1":
                return "fba199f7-47a7-4ed4-b880-3073424d2e2c";
            case "Scouting Phone 2":
                return "2e02adb8-9ce7-45ee-9810-5ec6f3fff542";
            case "Scouting Phone 3":
                return "c2c7e59e-d5d5-4c70-a565-06f74f9d54cf";
            case "Scouting Phone 4":
                return "d33724ac-4c4c-4acf-a309-e3567acefbf8";
            case "Scouting Phone 5":
                return "126a612f-2ff3-4a81-a159-596c3a1ecfe8";
            case "Scouting Phone 6":
                return "0e2f69a9-16d1-4283-b00b-a246ac96fc93";
            default:
                return "";
        }
    }

    @Override
    public void run(){
        try {
            while (true) {
                if (inStream != null) {
                    byteCommand = readByteArrayCommand(inStream);
                    command = new String(byteCommand);

                    if(command.equals("")){
                        Log.e("BluetoothHelper", "Exit Command Received. Finished");
                        break;
                    }

                    processCommand(command);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void processCommand(String command) {
        switch (command) {
            case "ADDTEAM":
                Log.e("BluetoothHelper", "ADDED TEAM");
                break;
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
                outputStream.write("MATCH".getBytes());
                outputStream.write(0);
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


    public AlertDialog getDialog() {
        return dialog;
    }
}
