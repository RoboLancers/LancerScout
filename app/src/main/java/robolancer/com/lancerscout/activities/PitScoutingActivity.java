package robolancer.com.lancerscout.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Objects;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.pit.Climb;
import robolancer.com.lancerscout.models.pit.CubeIntake;
import robolancer.com.lancerscout.models.pit.Drivetrain;
import robolancer.com.lancerscout.models.pit.LancerPit;
import robolancer.com.lancerscout.models.pit.LancerPitBuilder;
import robolancer.com.lancerscout.models.pit.ProgrammingLanguage;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = LancerScoutUtility.getDefaultSharedPreference(this);
        String currentPitJson = sharedPreferences.getString("currentPit", "");
        String pitHistoryJson = sharedPreferences.getString("pitHistory", "");
        String pitQueueJson = sharedPreferences.getString("pitQueue", "");

        LancerPit lancerPit = gson.fromJson(currentPitJson, LancerPit.class);

        if (lancerPit != null) {
            if (lancerPit.getTeamNumber() > 0) {
                teamNumberEditText.setText(String.valueOf(lancerPit.getTeamNumber()));
            } else {
                teamNumberEditText.setText("");
            }

            setSpinnerDrivetrain(lancerPit.getDrivetrain());
            setRadioCubeIntake(lancerPit.getCubeIntake());
            setRadioClimb(lancerPit.getClimb());
            robotWeightEditText.setText(String.valueOf(lancerPit.getRobotWeight()));
            setSpinnerProgrammingLanguage(lancerPit.getProgrammingLanguage());
            comments.setText(lancerPit.getComments());
        }

        if (!pitHistoryJson.isEmpty()) {
            PitHistoryActivity.pitHistory = gson.fromJson(pitHistoryJson, new TypeToken<List<LancerPit>>() {
            }.getType());
        }

        if (!pitQueueJson.isEmpty()) {
            PitQueueActivity.queuedPits = gson.fromJson(pitQueueJson, new TypeToken<List<LancerPit>>() {
            }.getType());
        }

        lancerPit = PitHistoryActivity.clickedPit;
        if (lancerPit != null) {
            PitHistoryActivity.clickedPit = null;

            teamNumberEditText.setText(String.valueOf(lancerPit.getTeamNumber()));
            setSpinnerDrivetrain(lancerPit.getDrivetrain());
            setRadioCubeIntake(lancerPit.getCubeIntake());
            setRadioClimb(lancerPit.getClimb());
            robotWeightEditText.setText(String.valueOf(lancerPit.getRobotWeight()));
            setSpinnerProgrammingLanguage(lancerPit.getProgrammingLanguage());
            comments.setText(lancerPit.getComments());
        }

        lancerPit = PitQueueActivity.clickedPit;
        if (lancerPit != null) {
            PitQueueActivity.clickedPit = null;

            teamNumberEditText.setText(String.valueOf(lancerPit.getTeamNumber()));
            setSpinnerDrivetrain(lancerPit.getDrivetrain());
            setRadioCubeIntake(lancerPit.getCubeIntake());
            setRadioClimb(lancerPit.getClimb());
            robotWeightEditText.setText(String.valueOf(lancerPit.getRobotWeight()));
            setSpinnerProgrammingLanguage(lancerPit.getProgrammingLanguage());
            comments.setText(lancerPit.getComments());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pit_send:
                if (teamNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(PitScoutingActivity.this, "Team number can't be empty", Toast.LENGTH_LONG).show();
                    break;
                }

                AlertDialog.Builder sendDialog = new AlertDialog.Builder(PitScoutingActivity.this);
                sendDialog.setTitle("Confirm Send")
                        .setMessage("Please get somewhat close to scouting server to send.")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> {
                            LancerPit lancerPit = createLancerPitFromField();
                            String json = gson.toJson(lancerPit);
                            PitHistoryActivity.pitHistory.add(lancerPit);

                            StringBuilder data = new StringBuilder("PIT-" + json + "\n");

                            for (LancerPit queuedPit : PitQueueActivity.queuedPits) {
                                json = gson.toJson(queuedPit);
                                data.append("PIT-").append(json).append("\n");
                                PitHistoryActivity.pitHistory.add(queuedPit);
                            }

                            bluetoothHelper.showPairedBluetoothDevices(true, data.toString(), this);
                        }).show();

                PitQueueActivity.queuedPits.clear();
                break;
            case R.id.pit_reset:
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(PitScoutingActivity.this);
                resetDialog.setTitle("Confirm Reset")
                        .setMessage("Are you sure you want to reset?")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> reset()).show();
                break;
            case R.id.pit_history:
                startActivity(new Intent(PitScoutingActivity.this, PitHistoryActivity.class));
                break;
            case R.id.pit_save:
                if (teamNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(PitScoutingActivity.this, "Team number can't be empty", Toast.LENGTH_LONG).show();
                    break;
                }

                LancerPit lancerPit = createLancerPitFromField();

                PitQueueActivity.queuedPits.add(lancerPit);

                reset();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private LancerPit createLancerPitFromField() {

        int robotWeight = 0;

        if (!robotWeightEditText.getText().toString().isEmpty()) {
            robotWeight = Integer.parseInt(robotWeightEditText.getText().toString());
        }

        return new LancerPitBuilder()
                .setTeamNumber(Integer.parseInt(teamNumberEditText.getText().toString()))
                .setDrivetrain((Drivetrain) drivetrainSpinner.getSelectedItem())
                .setCubeIntake(getCubeIntakeFromRadio())
                .setClimb(getClimbFromRadio())
                .setRobotWeight(robotWeight)
                .setProgrammingLanguage((ProgrammingLanguage) programmingLanguageSpinner.getSelectedItem())
                .setComments(comments.getText().toString())
                .createLancerPit();
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

    @Override
    public void save() {
        int team = 0, robotWeight = 0;

        if (!teamNumberEditText.getText().toString().isEmpty()) {
            team = Integer.parseInt(teamNumberEditText.getText().toString());
        }

        if (!robotWeightEditText.getText().toString().isEmpty()) {
            robotWeight = Integer.parseInt(robotWeightEditText.getText().toString());
        }

        String json = gson.toJson(new LancerPitBuilder()
                .setTeamNumber(team)
                .setDrivetrain((Drivetrain) drivetrainSpinner.getSelectedItem())
                .setCubeIntake(getCubeIntakeFromRadio())
                .setClimb(getClimbFromRadio())
                .setRobotWeight(robotWeight)
                .setProgrammingLanguage((ProgrammingLanguage) programmingLanguageSpinner.getSelectedItem())
                .setComments(comments.getText().toString())
                .createLancerPit());

        SharedPreferences.Editor editor = LancerScoutUtility.getDefaultSharedPreferenceEditor(this);
        editor.putString("currentPit", json).apply();
        editor.putString("pitHistory", gson.toJson(PitHistoryActivity.pitHistory));
        editor.putString("pitQueue", gson.toJson(PitQueueActivity.queuedPits));
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

    @Override
    public void onBackPressed() {
    }
}
