package robolancer.com.lancerscout.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.AllianceColor;
import robolancer.com.lancerscout.models.AutonomousAttempt;
import robolancer.com.lancerscout.models.EndGameAttempt;
import robolancer.com.lancerscout.models.LancerMatchBuilder;
import robolancer.com.lancerscout.models.StartingConfiguration;

public class MatchScoutingActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    static BluetoothHelper bluetoothHelper;

    EditText matchNumber, teamNumber;
    RadioGroup allianceColor, startingConfiguration;

    CheckBox crossedAutoLineBox, wrongSideAutoBox;
    Spinner autonomousAttempt;

    TextView allianceSwitchText, centerScaleText, opponentSwitchText, exchangeText;
    Button allianceSwitchAdd, allianceSwitchSubtract, centerScaleAdd, centerScaleSubtract, opponentSwitchAdd, opponentSwitchSubtract, exchangeAdd, exchangeSubtract;

    Spinner endGameAttempt;
    CheckBox brokenRobotBox;

    EditText comments;

    Toolbar appbar;

    Thread bluetoothThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);

        findViews();
        setupListeners();

        setSupportActionBar(appbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
        }

        bluetoothHelper = new BluetoothHelper(this, bluetoothAdapter);

        bluetoothThread = new Thread(bluetoothHelper);
        bluetoothThread.start();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static Handler handler = new Handler(msg -> {
        if(Objects.requireNonNull(msg.getData().getCharSequence("disconnect")).toString().equals("true")){
            bluetoothHelper.showBluetoothDevices();
            return true;
        }

        return false;
    });

    @Override
    protected void onResume(){
        super.onResume();
        bluetoothHelper.showBluetoothDevices();
    }

    @Override
    protected void onPause(){
        super.onPause();
        bluetoothHelper.getDialog().cancel();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                AlertDialog.Builder sendDialog = new AlertDialog.Builder(MatchScoutingActivity.this);
                sendDialog.setTitle("Confirm Send")
                        .setMessage("Are you sure this is correct? If not then we will hunt you down")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> {
                            try {
                                bluetoothHelper.write("MATCH");
                                bluetoothHelper.write(new Gson().toJson(new LancerMatchBuilder()
                                        .setMatchNumber(Integer.parseInt(matchNumber.getText().toString()))
                                        .setTeamNumber(Integer.parseInt(teamNumber.getText().toString()))
                                        .setColor(getAllianceColor())
                                        .setStartingConfiguration(getStartingConfiguration())
                                        .setCrossedAutoLine(crossedAutoLineBox.isChecked())
                                        .setAutonomousAttempt((AutonomousAttempt)autonomousAttempt.getSelectedItem())
                                        .setWrongSideAuto(wrongSideAutoBox.isChecked())
                                        .setAllianceSwitch(Integer.parseInt(allianceSwitchText.getText().toString()))
                                        .setCenterScale(Integer.parseInt(centerScaleText.getText().toString()))
                                        .setOpponentSwitch(Integer.parseInt(opponentSwitchText.getText().toString()))
                                        .setExchange(Integer.parseInt(exchangeText.getText().toString()))
                                        .setEndGameAttempt((EndGameAttempt)endGameAttempt.getSelectedItem())
                                        .setBrokeDown(brokenRobotBox.isChecked())
                                        .setComment(comments.getText().toString())
                                        .createLancerMatch()));
                                reset();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NumberFormatException e){
                                Toast.makeText(MatchScoutingActivity.this, "Match or Team number can't be empty", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }).show();
                break;
            case R.id.action_reset:
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(MatchScoutingActivity.this);
                resetDialog.setTitle("Confirm Reset")
                        .setMessage("Are you sure you want to reset?")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> {
                            reset();
                            bluetoothHelper.showBluetoothDevices();
                        }).show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_match, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed(){}

    private void findViews(){
        appbar = findViewById(R.id.appbar);

        matchNumber = findViewById(R.id.matchNumberInput);
        teamNumber = findViewById(R.id.teamNumberInput);
        allianceColor = findViewById(R.id.allianceColor);
        startingConfiguration = findViewById(R.id.startingConfiguration);

        crossedAutoLineBox = findViewById(R.id.crossedAutoLineCheckBox);
        wrongSideAutoBox = findViewById(R.id.wrongSideAutoCheckBox);
        autonomousAttempt = findViewById(R.id.autoCubePlacementSpinner);

        allianceSwitchAdd = findViewById(R.id.allianceSwitchAdd);
        allianceSwitchText = findViewById(R.id.allianceSwitchCounter);
        allianceSwitchSubtract = findViewById(R.id.allianceSwitchSubtract);

        centerScaleAdd = findViewById(R.id.centerScaleAdd);
        centerScaleText = findViewById(R.id.centerScaleCounter);
        centerScaleSubtract = findViewById(R.id.centerScaleSubtract);

        opponentSwitchAdd = findViewById(R.id.opponentSwitchAdd);
        opponentSwitchText = findViewById(R.id.opponentSwitchCounter);
        opponentSwitchSubtract = findViewById(R.id.opponentSwitchSubtract);

        exchangeAdd = findViewById(R.id.exchangeAdd);
        exchangeText = findViewById(R.id.exchangeCounter);
        exchangeSubtract = findViewById(R.id.exchangeSubtract);

        endGameAttempt = findViewById(R.id.endGameClimbSpinner);
        brokenRobotBox = findViewById(R.id.breakdownCheckBox);

        comments = findViewById(R.id.commentInput);

        autonomousAttempt.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, AutonomousAttempt.values()));
        endGameAttempt.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, EndGameAttempt.values()));
    }

    private void setupListeners(){
        allianceSwitchAdd.setOnClickListener(v -> allianceSwitchText.setText(String.valueOf(Integer.parseInt(allianceSwitchText.getText().toString()) + 1)));
        centerScaleAdd.setOnClickListener(v -> centerScaleText.setText(String.valueOf(Integer.parseInt(centerScaleText.getText().toString()) + 1)));
        opponentSwitchAdd.setOnClickListener(v -> opponentSwitchText.setText(String.valueOf(Integer.parseInt(opponentSwitchText.getText().toString()) + 1)));
        exchangeAdd.setOnClickListener(v -> exchangeText.setText(String.valueOf(Integer.parseInt(exchangeText.getText().toString()) + 1)));

        allianceSwitchSubtract.setOnClickListener(v -> {
            int allianceSwitch = Integer.parseInt(allianceSwitchText.getText().toString());
            if(allianceSwitch > 0){
                allianceSwitchText.setText(String.valueOf(--allianceSwitch));
            }
        });

        centerScaleSubtract.setOnClickListener(v -> {
            int centerScale = Integer.parseInt(centerScaleText.getText().toString());
            if(centerScale > 0){
                centerScaleText.setText(String.valueOf(--centerScale));
            }
        });

        opponentSwitchSubtract.setOnClickListener(v -> {
            int opponentSwitch = Integer.parseInt(opponentSwitchText.getText().toString());
            if(opponentSwitch > 0){
                opponentSwitchText.setText(String.valueOf(--opponentSwitch));
            }
        });

        exchangeSubtract.setOnClickListener(v -> {
            int exchange = Integer.parseInt(exchangeText.getText().toString());
            if(exchange > 0){
                exchangeText.setText(String.valueOf(--exchange));
            }
        });
    }

    private void reset(){
        matchNumber.getText().clear();
        teamNumber.getText().clear();

        allianceColor.check(R.id.blueAllianceRadioButton);
        startingConfiguration.check(R.id.leftStartingConfiguration);

        crossedAutoLineBox.setChecked(false);
        autonomousAttempt.setSelection(0, true);
        wrongSideAutoBox.setChecked(false);

        allianceSwitchText.setText("0");
        centerScaleText.setText("0");
        opponentSwitchText.setText("0");
        exchangeText.setText("0");

        endGameAttempt.setSelection(0, true);
        brokenRobotBox.setChecked(false);

        comments.getText().clear();

        matchNumber.requestFocus();
    }

    private AllianceColor getAllianceColor(){
        switch(allianceColor.getCheckedRadioButtonId()){
            case R.id.blueAllianceRadioButton:
                return AllianceColor.BLUE;
            case R.id.redAllianceRadioButton:
                return AllianceColor.RED;
            default:
                return AllianceColor.BLUE;
        }
    }

    private StartingConfiguration getStartingConfiguration(){
        switch (startingConfiguration.getCheckedRadioButtonId()){
            case R.id.leftStartingConfiguration:
                return StartingConfiguration.LEFT;
            case R.id.middleStartingConfiguration:
                return StartingConfiguration.MIDDLE;
            case R.id.rightStartingConfiguration:
                return StartingConfiguration.RIGHT;
            default:
                return StartingConfiguration.LEFT;
        }
    }
}
