package robolancer.com.lancerscout.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Objects;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.pit.Climb;
import robolancer.com.lancerscout.models.pit.CubeIntake;
import robolancer.com.lancerscout.models.pit.Drivetrain;
import robolancer.com.lancerscout.models.pit.LancerPit;
import robolancer.com.lancerscout.models.pit.LancerPitBuilder;
import robolancer.com.lancerscout.models.pit.ProgrammingLanguage;

public class PitScoutingActivity extends LancerActivity {

    Toolbar appbar;

    BluetoothHelper bluetoothHelper;
    BluetoothAdapter bluetoothAdapter;
    Thread bluetoothThread;

    EditText teamNumberEditText;
    Spinner drivetrainSpinner;
    RadioGroup cubeIntakeGroup, climbGroup;
    EditText robotWeightEditText;
    Spinner programmingLanguageSpinner;
    EditText comments;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        findViews();

        appbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(appbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", (dialog, which) -> System.exit(0))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
        }

        bluetoothHelper = new BluetoothHelper(this, bluetoothAdapter);

        bluetoothThread = new Thread(bluetoothHelper);
        bluetoothThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bluetoothHelper.getPairedDeviceDialog() != null) {
            bluetoothHelper.getPairedDeviceDialog().cancel();
        }

        save();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("pitDetail", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("pit", "");
        Gson gson = new Gson();
        Log.e("JSON", json);
        if (!json.equals("")) {
            LancerPit lancerPit = gson.fromJson(json, LancerPit.class);

            if (lancerPit.getTeamNumber() > 0) {
                teamNumberEditText.setText(String.valueOf(lancerPit.getTeamNumber()));
            } else {
                teamNumberEditText.setText("");
            }

            setSpinnerDrivetrain(lancerPit.getDrivetrain());
            setRadioCubeIntake(lancerPit.getCubeIntake());
            setRadioClimb(lancerPit.getClimb());

            if (lancerPit.getRobotWeight() > 0) {
                robotWeightEditText.setText(String.valueOf(lancerPit.getRobotWeight()));
            } else {
                robotWeightEditText.setText("");
            }

            setSpinnerProgrammingLanguage(lancerPit.getProgrammingLanguage());
            comments.setText(lancerPit.getComments());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_match, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                if (teamNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(PitScoutingActivity.this, "Team number can't be empty", Toast.LENGTH_LONG).show();
                    break;
                }

                if (robotWeightEditText.getText().toString().isEmpty()) {
                    Toast.makeText(PitScoutingActivity.this, "Robot weight can't be empty", Toast.LENGTH_LONG).show();
                    break;
                }

                AlertDialog.Builder sendDialog = new AlertDialog.Builder(PitScoutingActivity.this);
                sendDialog.setTitle("Confirm Send")
                        .setMessage("Please get somewhat close to scouting server to send.")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> {
                            String json = new Gson().toJson(new LancerPitBuilder()
                                    .setTeamNumber(Integer.parseInt(teamNumberEditText.getText().toString()))
                                    .setDrivetrain((Drivetrain) drivetrainSpinner.getSelectedItem())
                                    .setCubeIntake(getCubeIntakeFromRadio())
                                    .setClimb(getClimbFromRadio())
                                    .setRobotWeight(Integer.parseInt(robotWeightEditText.getText().toString()))
                                    .setProgrammingLanguage((ProgrammingLanguage) programmingLanguageSpinner.getSelectedItem())
                                    .setComments(comments.getText().toString())
                                    .createLancerPit());

                            bluetoothHelper.showPairedBluetoothDevices(true, "PIT" + json, this);
                        }).show();
                break;
            case R.id.action_reset:
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(PitScoutingActivity.this);
                resetDialog.setTitle("Confirm Reset")
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
    public void onBackPressed() {
    }

    private void findViews() {
        appbar = findViewById(R.id.appbar);

        teamNumberEditText = findViewById(R.id.pitTeamNumber);
        drivetrainSpinner = findViewById(R.id.drivetrainSpinner);

        cubeIntakeGroup = findViewById(R.id.cubeIntakeRadioGroup);
        climbGroup = findViewById(R.id.climbingRadioGroup);

        robotWeightEditText = findViewById(R.id.robotWeight);
        programmingLanguageSpinner = findViewById(R.id.programmingLanguageSpinner);

        comments = findViewById(R.id.pitScoutingComments);

        drivetrainSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Drivetrain.values()));
        programmingLanguageSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ProgrammingLanguage.values()));
    }

    public void reset() {
        teamNumberEditText.setText("");
        drivetrainSpinner.setSelection(0);
        cubeIntakeGroup.check(R.id.noneIntakeRadioButton);
        climbGroup.check(R.id.noneClimberRadioButton);
        robotWeightEditText.setText("");
        programmingLanguageSpinner.setSelection(0);
        comments.setText("");
    }

    public void save() {
        int team = 0, robotWeight = 0;

        if (!teamNumberEditText.getText().toString().isEmpty()) {
            team = Integer.parseInt(teamNumberEditText.getText().toString());
        }

        if (!robotWeightEditText.getText().toString().isEmpty()) {
            robotWeight = Integer.parseInt(robotWeightEditText.getText().toString());
        }

        String json = new Gson().toJson(new LancerPitBuilder()
                .setTeamNumber(team)
                .setDrivetrain((Drivetrain) drivetrainSpinner.getSelectedItem())
                .setCubeIntake(getCubeIntakeFromRadio())
                .setClimb(getClimbFromRadio())
                .setRobotWeight(robotWeight)
                .setProgrammingLanguage((ProgrammingLanguage) programmingLanguageSpinner.getSelectedItem())
                .setComments(comments.getText().toString())
                .createLancerPit());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("pitDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pit", json);
        editor.apply();
    }

    private CubeIntake getCubeIntakeFromRadio() {
        switch (cubeIntakeGroup.getCheckedRadioButtonId()) {
            case R.id.floorIntakeRadioButton:
                return CubeIntake.FLOOR_INTAKE;
            case R.id.humanIntakeRadioButton:
                return CubeIntake.HUMAN_INTAKE;
            case R.id.bothIntakeRadioButton:
                return CubeIntake.BOTH_INTAKES;
            case R.id.noneIntakeRadioButton:
                return CubeIntake.NONE_INTAKE;
            default:
                return CubeIntake.FLOOR_INTAKE;
        }
    }

    private Climb getClimbFromRadio() {
        switch (climbGroup.getCheckedRadioButtonId()) {
            case R.id.soloClimberRadioButton:
                return Climb.SOLO_CLIMB;
            case R.id.climberRamp1RadioButton:
                return Climb.CLIMBER_WITH_RAMP_1;
            case R.id.climberRamp2RadioButton:
                return Climb.CLIMBER_WITH_RAMP_2;
            case R.id.ramp1RadioButton:
                return Climb.RAMP_1;
            case R.id.ramp2RadioButton:
                return Climb.RAMP_2;
            case R.id.noneClimberRadioButton:
                return Climb.NONE_INTAKE;
            default:
                return Climb.SOLO_CLIMB;
        }
    }

    private void setSpinnerDrivetrain(Drivetrain drivetrain) {
        ArrayAdapter adapter = (ArrayAdapter) drivetrainSpinner.getAdapter();
        drivetrainSpinner.setSelection(adapter.getPosition(drivetrain));
    }

    private void setRadioCubeIntake(CubeIntake cubeIntake) {
        switch (cubeIntake) {
            case FLOOR_INTAKE:
                cubeIntakeGroup.check(R.id.floorIntakeRadioButton);
                break;
            case HUMAN_INTAKE:
                cubeIntakeGroup.check(R.id.humanIntakeRadioButton);
                break;
            case BOTH_INTAKES:
                cubeIntakeGroup.check(R.id.bothIntakeRadioButton);
                break;
            case NONE_INTAKE:
                cubeIntakeGroup.check(R.id.noneIntakeRadioButton);
                break;
            default:
                cubeIntakeGroup.check(R.id.floorIntakeRadioButton);
                break;
        }
    }

    private void setRadioClimb(Climb climb) {
        switch (climb) {
            case SOLO_CLIMB:
                climbGroup.check(R.id.soloClimberRadioButton);
                break;
            case CLIMBER_WITH_RAMP_1:
                climbGroup.check(R.id.climberRamp1RadioButton);
                break;
            case CLIMBER_WITH_RAMP_2:
                climbGroup.check(R.id.climberRamp2RadioButton);
                break;
            case RAMP_1:
                climbGroup.check(R.id.ramp1RadioButton);
                break;
            case RAMP_2:
                climbGroup.check(R.id.ramp2RadioButton);
                break;
            case NONE_INTAKE:
                climbGroup.check(R.id.noneClimberRadioButton);
                break;
            default:
                climbGroup.check(R.id.soloClimberRadioButton);
                break;
        }
    }

    private void setSpinnerProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        ArrayAdapter adapter = (ArrayAdapter) programmingLanguageSpinner.getAdapter();
        programmingLanguageSpinner.setSelection(adapter.getPosition(programmingLanguage));
    }
}
