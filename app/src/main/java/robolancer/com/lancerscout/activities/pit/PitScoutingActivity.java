package robolancer.com.lancerscout.activities.pit;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.activities.LancerActivity;
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.pit.Climb;
import robolancer.com.lancerscout.models.pit.Intake;
import robolancer.com.lancerscout.models.pit.Drivetrain;
import robolancer.com.lancerscout.models.pit.LancerPit;
import robolancer.com.lancerscout.models.pit.LancerPitBuilder;
import robolancer.com.lancerscout.models.pit.ProgrammingLanguage;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

@SuppressWarnings("unchecked")
public class PitScoutingActivity extends LancerActivity {
    EditText teamNumberEditText;
    Spinner drivetrainSpinner;

    RadioGroup hatchIntakeGroup, cargoIntakeGroup, climbGroup;

    EditText robotWeightEditText;
    Spinner programmingLanguageSpinner;
    EditText comments;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        findViews();
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

            setRadioCargoIntake(lancerPit.getCargoIntake());
            setRadioHatchIntake(lancerPit.getHatchIntake());
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

            setRadioCargoIntake(lancerPit.getCargoIntake());
            setRadioHatchIntake(lancerPit.getHatchIntake());
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

            setRadioCargoIntake(lancerPit.getCargoIntake());
            setRadioHatchIntake(lancerPit.getHatchIntake());
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
                    if(PitQueueActivity.queuedPits.isEmpty()){
                        Toast.makeText(PitScoutingActivity.this, "Team number or pit queue can't be empty", Toast.LENGTH_LONG).show();
                    }else{

                    }

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

                            StringBuilder data = new StringBuilder("PIT-" + json + " ");

                            Log.e("Debuggings", Integer.toString(PitQueueActivity.queuedPits.size()));

                            for (LancerPit queuedPit : PitQueueActivity.queuedPits) {
                                json = gson.toJson(queuedPit);
                                data.append(json).append(" ");

                                PitHistoryActivity.pitHistory.add(queuedPit);
                            }

                            bluetoothHelper.showPairedBluetoothDevices(true, data.toString(), this);
                        }).show();
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

                PitQueueActivity.queuedPits.add(createLancerPitFromField());
                Log.e("Debuggings", Integer.toString(PitQueueActivity.queuedPits.size()));

                reset();
                break;
            case R.id.pit_queue:
                startActivity(new Intent(PitScoutingActivity.this, PitQueueActivity.class));
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
                .setCargoIntake(getCargoIntakeFromRadio())
                .setHatchIntake(getHatchIntakeFromRadio())
                .setClimb(getClimbFromRadio())
                .setRobotWeight(robotWeight)
                .setProgrammingLanguage((ProgrammingLanguage) programmingLanguageSpinner.getSelectedItem())
                .setComments(comments.getText().toString())
                .createLancerPit();
    }

    private void findViews() {
        setupAppbar();

        teamNumberEditText = findViewById(R.id.pitTeamNumber);
        drivetrainSpinner = findViewById(R.id.drivetrainSpinner);

        hatchIntakeGroup = findViewById(R.id.hatchIntakeRadioGroup);
        cargoIntakeGroup = findViewById(R.id.cargoIntakeRadioGroup);
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
        cargoIntakeGroup.check(R.id.cargoNoneIntakeRadioButton);
        hatchIntakeGroup.check(R.id.hatchNoneIntakeRadioButton);
        climbGroup.check(R.id.level1Climb);
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
                .setCargoIntake(getCargoIntakeFromRadio())
                .setHatchIntake(getHatchIntakeFromRadio())
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

    private Intake getCargoIntakeFromRadio() {
        switch (cargoIntakeGroup.getCheckedRadioButtonId()) {
            case R.id.cargoFloorIntakeRadioButton:
                return Intake.FLOOR_INTAKE;
            case R.id.cargoHumanIntakeRadioButton:
                return Intake.HUMAN_INTAKE;
            case R.id.cargoBothIntakeRadioButton:
                return Intake.BOTH_INTAKES;
            case R.id.cargoNoneIntakeRadioButton:
                return Intake.NO_INTAKE;
            default:
                return Intake.FLOOR_INTAKE;
        }
    }

    private Intake getHatchIntakeFromRadio() {
        switch (hatchIntakeGroup.getCheckedRadioButtonId()) {
            case R.id.hatchFloorIntakeRadioButton:
                return Intake.FLOOR_INTAKE;
            case R.id.hatchHumanIntakeRadioButton:
                return Intake.HUMAN_INTAKE;
            case R.id.hatchBothIntakeRadioButton:
                return Intake.BOTH_INTAKES;
            case R.id.hatchNoneIntakeRadioButton:
                return Intake.NO_INTAKE;
            default:
                return Intake.FLOOR_INTAKE;
        }
    }

    private Climb getClimbFromRadio() {
        switch (climbGroup.getCheckedRadioButtonId()) {
            case R.id.level1Climb:
                return Climb.LEVEL_1;
            case R.id.level2Climb:
                return Climb.LEVEL_2;
            case R.id.level3Climb:
                return Climb.LEVEL_3;
            default:
                return Climb.LEVEL_1;
        }
    }

    private void setSpinnerDrivetrain(Drivetrain drivetrain) {
        ArrayAdapter adapter = (ArrayAdapter) drivetrainSpinner.getAdapter();
        drivetrainSpinner.setSelection(adapter.getPosition(drivetrain));
    }

    private void setRadioCargoIntake(Intake intake) {
        switch (intake) {
            case FLOOR_INTAKE:
                hatchIntakeGroup.check(R.id.cargoFloorIntakeRadioButton);
                break;
            case HUMAN_INTAKE:
                hatchIntakeGroup.check(R.id.cargoHumanIntakeRadioButton);
                break;
            case BOTH_INTAKES:
                hatchIntakeGroup.check(R.id.cargoBothIntakeRadioButton);
                break;
            case NO_INTAKE:
                hatchIntakeGroup.check(R.id.cargoNoneIntakeRadioButton);
                break;
            default:
                hatchIntakeGroup.check(R.id.cargoFloorIntakeRadioButton);
                break;
        }
    }

    private void setRadioHatchIntake(Intake intake) {
        switch (intake) {
            case FLOOR_INTAKE:
                hatchIntakeGroup.check(R.id.hatchFloorIntakeRadioButton);
                break;
            case HUMAN_INTAKE:
                hatchIntakeGroup.check(R.id.hatchHumanIntakeRadioButton);
                break;
            case BOTH_INTAKES:
                hatchIntakeGroup.check(R.id.hatchBothIntakeRadioButton);
                break;
            case NO_INTAKE:
                hatchIntakeGroup.check(R.id.hatchNoneIntakeRadioButton);
                break;
            default:
                hatchIntakeGroup.check(R.id.hatchFloorIntakeRadioButton);
                break;
        }
    }

    private void setRadioClimb(Climb climb) {
        switch (climb) {
            case LEVEL_1:
                climbGroup.check(R.id.level1Climb);
            case LEVEL_2:
                climbGroup.check(R.id.level2Climb);
            case LEVEL_3:
                climbGroup.check(R.id.level3Climb);
            default:
                climbGroup.check(R.id.level1Climb);
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
