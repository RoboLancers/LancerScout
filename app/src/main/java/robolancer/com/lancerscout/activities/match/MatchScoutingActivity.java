package robolancer.com.lancerscout.activities.match;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import robolancer.com.lancerscout.R;
import robolancer.com.lancerscout.activities.LancerActivity;
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.match.AllianceColor;
import robolancer.com.lancerscout.models.match.Sandstorm;
import robolancer.com.lancerscout.models.match.EndGameAttempt;
import robolancer.com.lancerscout.models.match.LancerMatch;
import robolancer.com.lancerscout.models.match.LancerMatchBuilder;
import robolancer.com.lancerscout.models.match.StartingConfiguration;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

@SuppressWarnings("unchecked")
public class MatchScoutingActivity extends LancerActivity {
    EditText matchNumber, teamNumber;
    RadioGroup allianceColor, startingConfiguration;

    CheckBox crossedAutoLineBox;
    RadioGroup sandstorm;

    TextView rocketCargoText, rocketHatchText, shipCargoText, shipHatchText;
    Button rocketCargoAdd, rocketCargoSubtract,
            rocketHatchAdd, rocketHatchSubtract,
            shipCargoAdd, shipCargoSubtract,
            shipHatchAdd, shipHatchSubtract;

    Spinner endGameAttempt;
    CheckBox brokenRobotBox;

    EditText comments;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);

        findViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = LancerScoutUtility.getDefaultSharedPreference(this);
        String currentMatchJson = sharedPreferences.getString("currentMatch", "");
        String matchHistoryJson = sharedPreferences.getString("matchHistory", "");

        if (!currentMatchJson.isEmpty()) {
            LancerMatch lancerMatch = gson.fromJson(currentMatchJson, LancerMatch.class);

            if (lancerMatch.getMatchNumber() > 0) {
                matchNumber.setText(String.valueOf(lancerMatch.getMatchNumber()));
            } else {
                matchNumber.setText("");
            }

            if (lancerMatch.getTeamNumber() > 0) {
                teamNumber.setText(String.valueOf(lancerMatch.getTeamNumber()));
            } else {
                teamNumber.setText("");
            }

            setRadioAllianceColor(lancerMatch.getColor());
            setRadioStartingConfiguration(lancerMatch.getStartingConfiguration());
            crossedAutoLineBox.setChecked(lancerMatch.getCrossedAutoLine());
            setRadioSandstorm(lancerMatch.getSandstorm());

            rocketCargoText.setText(String.valueOf(lancerMatch.getRocketCargo()));
            rocketHatchText.setText(String.valueOf(lancerMatch.getRocketHatch()));
            shipCargoText.setText(String.valueOf(lancerMatch.getShipCargo()));
            shipHatchText.setText(String.valueOf(lancerMatch.getShipHatch()));

            setSpinnerEndGameAttempt(lancerMatch.getEndGameAttempt());
            brokenRobotBox.setChecked(lancerMatch.getRobotBrokeDown());
            comments.setText(lancerMatch.getComment());
        }

        if (!matchHistoryJson.equals("")) {
            MatchHistoryActivity.matchHistory = gson.fromJson(matchHistoryJson, new TypeToken<List<LancerMatch>>() {
            }.getType());
        }

        LancerMatch lancerMatch = MatchHistoryActivity.clickedMatch;
        if (lancerMatch != null) {
            MatchHistoryActivity.clickedMatch = null;

            matchNumber.setText(String.valueOf(lancerMatch.getMatchNumber()));
            teamNumber.setText(String.valueOf(lancerMatch.getTeamNumber()));
            setRadioAllianceColor(lancerMatch.getColor());
            setRadioStartingConfiguration(lancerMatch.getStartingConfiguration());
            crossedAutoLineBox.setChecked(lancerMatch.getCrossedAutoLine());
            setRadioSandstorm(lancerMatch.getSandstorm());

            rocketCargoText.setText(String.valueOf(lancerMatch.getRocketCargo()));
            rocketHatchText.setText(String.valueOf(lancerMatch.getRocketHatch()));
            shipCargoText.setText(String.valueOf(lancerMatch.getShipCargo()));
            shipHatchText.setText(String.valueOf(lancerMatch.getShipHatch()));

            setSpinnerEndGameAttempt(lancerMatch.getEndGameAttempt());
            brokenRobotBox.setChecked(lancerMatch.getRobotBrokeDown());
            comments.setText(lancerMatch.getComment());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.match_send:
                if (matchNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MatchScoutingActivity.this, "Match number can't be empty", Toast.LENGTH_LONG).show();
                    break;
                }

                if (teamNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MatchScoutingActivity.this, "Team number can't be empty", Toast.LENGTH_LONG).show();
                    break;
                }

                new AlertDialog.Builder(MatchScoutingActivity.this).setTitle("Confirm Send")
                        .setMessage("Are you sure this is correct? If not then we will hunt you down")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> {
                            LancerMatch lancerMatch = createLancerMatchFromField();

                            String json = gson.toJson(lancerMatch);
                            MatchHistoryActivity.matchHistory.add(lancerMatch);

                            bluetoothHelper.showPairedBluetoothDevices(true, "MATCH-" + json, this);
                        }).show();
                break;
            case R.id.match_reset:
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(MatchScoutingActivity.this);
                resetDialog.setTitle("Confirm Reset")
                        .setMessage("Are you sure you want to reset?")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Ok", (dialog, which) -> reset()).show();
                break;
            case R.id.match_history:
                startActivity(new Intent(MatchScoutingActivity.this, MatchHistoryActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void findViews() {
        setupAppbar();

        matchNumber = findViewById(R.id.matchNumberInput);
        teamNumber = findViewById(R.id.teamNumberInput);
        allianceColor = findViewById(R.id.allianceColor);
        startingConfiguration = findViewById(R.id.startingConfiguration);

        crossedAutoLineBox = findViewById(R.id.crossedAutoLineCheckBox);

        sandstorm = findViewById(R.id.sandstormRadioGroup);

        rocketCargoAdd = findViewById(R.id.rocketCargoAdd);
        rocketCargoText = findViewById(R.id.rocketCargoCounter);
        rocketCargoSubtract = findViewById(R.id.rocketCargoSubtract);

        rocketHatchAdd = findViewById(R.id.rocketHatchAdd);
        rocketHatchText = findViewById(R.id.rocketHatchCounter);
        rocketHatchSubtract = findViewById(R.id.rocketHatchSubtract);

        shipCargoAdd = findViewById(R.id.shipCargoAdd);
        shipCargoText = findViewById(R.id.shipCargoCounter);
        shipCargoSubtract = findViewById(R.id.shipCargoSubtract);

        shipHatchAdd = findViewById(R.id.shipHatchAdd);
        shipHatchText = findViewById(R.id.shipHatchCounter);
        shipHatchSubtract = findViewById(R.id.shipHatchSubtract);

        endGameAttempt = findViewById(R.id.endGameClimbSpinner);
        brokenRobotBox = findViewById(R.id.breakdownCheckBox);

        endGameAttempt.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, EndGameAttempt.values()));
        comments = findViewById(R.id.commentInput);
    }

    private void setupListeners() {
        rocketCargoAdd.setOnClickListener(v -> rocketCargoText.setText(String.valueOf(Integer.parseInt(rocketCargoText.getText().toString()) + 1)));
        rocketHatchAdd.setOnClickListener(v -> rocketHatchText.setText(String.valueOf(Integer.parseInt(rocketHatchText.getText().toString()) + 1)));
        shipCargoAdd.setOnClickListener(v -> shipCargoText.setText(String.valueOf(Integer.parseInt(shipCargoText.getText().toString()) + 1)));
        shipHatchAdd.setOnClickListener(v -> shipHatchText.setText(String.valueOf(Integer.parseInt(shipHatchText.getText().toString()) + 1)));

        rocketCargoSubtract.setOnClickListener(v -> {
            int allianceSwitch = Integer.parseInt(rocketCargoText.getText().toString());
            if (allianceSwitch > 0) {
                rocketCargoText.setText(String.valueOf(--allianceSwitch));
            }
        });

        rocketHatchSubtract.setOnClickListener(v -> {
            int rocketHatch = Integer.parseInt(rocketHatchText.getText().toString());
            if (rocketHatch > 0) {
                rocketHatchText.setText(String.valueOf(--rocketHatch));
            }
        });

        shipCargoSubtract.setOnClickListener(v -> {
            int shipCargo = Integer.parseInt(shipCargoText.getText().toString());
            if (shipCargo > 0) {
                shipCargoText.setText(String.valueOf(--shipCargo));
            }
        });

        shipHatchSubtract.setOnClickListener(v -> {
            int shipHatch = Integer.parseInt(shipHatchText.getText().toString());
            if (shipHatch > 0) {
                shipHatchText.setText(String.valueOf(--shipHatch));
            }
        });
    }

    public void reset() {
        matchNumber.getText().clear();
        teamNumber.getText().clear();

        allianceColor.check(R.id.blueAllianceRadioButton);
        startingConfiguration.check(R.id.level1StartingConfiguration);

        crossedAutoLineBox.setChecked(false);
        sandstorm.check(R.id.autonomousSandstorm);

        rocketCargoText.setText("0");
        rocketHatchText.setText("0");
        shipCargoText.setText("0");
        shipHatchText.setText("0");

        endGameAttempt.setSelection(0);
        brokenRobotBox.setChecked(false);

        comments.getText().clear();
    }

    @Override
    public void save() {
        int match = 0, team = 0;

        if (!matchNumber.getText().toString().isEmpty()) {
            match = Integer.parseInt(matchNumber.getText().toString());
        }

        if (!teamNumber.getText().toString().isEmpty()) {
            team = Integer.parseInt(teamNumber.getText().toString());
        }

        String currentMatch = gson.toJson(new LancerMatchBuilder()
                .setMatchNumber(match)
                .setTeamNumber(team)
                .setColor(getAllianceColorFromRadioButtons())
                .setStartingConfiguration(getStartingConfigurationFromRadioButtons())
                .setCrossedAutoLine(crossedAutoLineBox.isChecked())
                .setSandstorm(getSandstormFromRadioButtons())
                .setRocketCargo(Integer.parseInt(rocketCargoText.getText().toString()))
                .setRocketHatch(Integer.parseInt(rocketHatchText.getText().toString()))
                .setShipCargo(Integer.parseInt(shipCargoText.getText().toString()))
                .setShipHatch(Integer.parseInt(shipHatchText.getText().toString()))
                .setEndGameAttempt((EndGameAttempt) endGameAttempt.getSelectedItem())
                .setBrokeDown(brokenRobotBox.isChecked())
                .setComment(comments.getText().toString())
                .createLancerMatch());

        String matchHistoryJson = gson.toJson(MatchHistoryActivity.matchHistory);

        SharedPreferences.Editor editor = LancerScoutUtility.getDefaultSharedPreferenceEditor(this);
        editor.putString("currentMatch", currentMatch);
        editor.putString("matchHistory", matchHistoryJson);
        editor.apply();
    }

    private LancerMatch createLancerMatchFromField() {
        return new LancerMatchBuilder()
                .setMatchNumber(Integer.parseInt(matchNumber.getText().toString()))
                .setTeamNumber(Integer.parseInt(teamNumber.getText().toString()))
                .setColor(getAllianceColorFromRadioButtons())
                .setStartingConfiguration(getStartingConfigurationFromRadioButtons())
                .setCrossedAutoLine(crossedAutoLineBox.isChecked())
                .setSandstorm(getSandstormFromRadioButtons())
                .setRocketCargo(Integer.parseInt(rocketCargoText.getText().toString()))
                .setRocketHatch(Integer.parseInt(rocketHatchText.getText().toString()))
                .setShipCargo(Integer.parseInt(shipCargoText.getText().toString()))
                .setShipHatch(Integer.parseInt(shipHatchText.getText().toString()))
                .setEndGameAttempt((EndGameAttempt) endGameAttempt.getSelectedItem())
                .setBrokeDown(brokenRobotBox.isChecked())
                .setComment(comments.getText().toString())
                .createLancerMatch();
    }

    private AllianceColor getAllianceColorFromRadioButtons() {
        switch (allianceColor.getCheckedRadioButtonId()) {
            case R.id.blueAllianceRadioButton:
                return AllianceColor.BLUE;
            case R.id.redAllianceRadioButton:
                return AllianceColor.RED;
            default:
                return AllianceColor.BLUE;
        }
    }

    private StartingConfiguration getStartingConfigurationFromRadioButtons() {
        switch (startingConfiguration.getCheckedRadioButtonId()) {
            case R.id.level1StartingConfiguration:
                return StartingConfiguration.LEVEL_1;
            case R.id.level2StartingConfiguration:
                return StartingConfiguration.LEVEL_2;
            default:
                return StartingConfiguration.LEVEL_1;
        }
    }

    private Sandstorm getSandstormFromRadioButtons(){
        switch (sandstorm.getCheckedRadioButtonId()){
            case R.id.autonomousSandstorm:
                return Sandstorm.AUTONOMOUS;
            case R.id.driverControlledSandstorm:
                return Sandstorm.DRIVER_CONTROLLED;
            default:
                return Sandstorm.DRIVER_CONTROLLED;
        }
    }

    private void setRadioAllianceColor(AllianceColor color) {
        switch (color) {
            case BLUE:
                allianceColor.check(R.id.blueAllianceRadioButton);
                break;
            case RED:
                allianceColor.check(R.id.redAllianceRadioButton);
                break;
            default:
                allianceColor.check(R.id.blueAllianceRadioButton);
                break;
        }
    }

    private void setRadioStartingConfiguration(StartingConfiguration configuration) {
        switch (configuration) {
            case LEVEL_1:
                startingConfiguration.check(R.id.level1StartingConfiguration);
                break;
            case LEVEL_2:
                startingConfiguration.check(R.id.level2StartingConfiguration);
                break;
            default:
                startingConfiguration.check(R.id.level1StartingConfiguration);
                break;
        }
    }

    private void setRadioSandstorm(Sandstorm storm){
        switch (storm) {
            case AUTONOMOUS:
                sandstorm.check(R.id.autonomousSandstorm);
            case DRIVER_CONTROLLED:
                sandstorm.check(R.id.driverControlledSandstorm);
            default:
                sandstorm.check(R.id.driverControlledSandstorm);
        }
    }

    private void setSpinnerEndGameAttempt(EndGameAttempt attempt) {
        ArrayAdapter adapter = (ArrayAdapter) endGameAttempt.getAdapter();
        endGameAttempt.setSelection(adapter.getPosition(attempt));
    }

    @Override
    public void onBackPressed() {
    }
}