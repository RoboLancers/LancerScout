package robolancer.com.lancerscout.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import robolancer.com.lancerscout.bluetooth.BluetoothHelper;
import robolancer.com.lancerscout.models.match.AllianceColor;
import robolancer.com.lancerscout.models.match.AutonomousAttempt;
import robolancer.com.lancerscout.models.match.EndGameAttempt;
import robolancer.com.lancerscout.models.match.LancerMatch;
import robolancer.com.lancerscout.models.match.LancerMatchBuilder;
import robolancer.com.lancerscout.models.match.StartingConfiguration;
import robolancer.com.lancerscout.utilities.LancerScoutUtility;

@SuppressWarnings("unchecked")
public class MatchScoutingActivity extends LancerActivity {
    BluetoothHelper bluetoothHelper;
    BluetoothAdapter bluetoothAdapter;
    Thread bluetoothThread;

    EditText matchNumber, teamNumber;
    RadioGroup allianceColor, startingConfiguration;

    CheckBox crossedAutoLineBox, wrongSideAutoBox;
    Spinner autonomousAttempt;

    TextView allianceSwitchText, centerScaleText, opponentSwitchText, exchangeText;
    Button allianceSwitchAdd, allianceSwitchSubtract, centerScaleAdd, centerScaleSubtract, opponentSwitchAdd, opponentSwitchSubtract, exchangeAdd, exchangeSubtract;

    Spinner endGameAttempt;
    CheckBox brokenRobotBox;

    EditText comments;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);

        findViews();
        setupListeners();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
            setSpinnerAutonomousAttempt(lancerMatch.getAutonomousAttempt());
            wrongSideAutoBox.setChecked(lancerMatch.getWrongSideAuto());
            allianceSwitchText.setText(String.valueOf(lancerMatch.getAllianceSwitch()));
            centerScaleText.setText(String.valueOf(lancerMatch.getCenterScale()));
            opponentSwitchText.setText(String.valueOf(lancerMatch.getOpponentSwitch()));
            exchangeText.setText(String.valueOf(lancerMatch.getExchange()));
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
            setSpinnerAutonomousAttempt(lancerMatch.getAutonomousAttempt());
            wrongSideAutoBox.setChecked(lancerMatch.getWrongSideAuto());
            allianceSwitchText.setText(String.valueOf(lancerMatch.getAllianceSwitch()));
            centerScaleText.setText(String.valueOf(lancerMatch.getCenterScale()));
            opponentSwitchText.setText(String.valueOf(lancerMatch.getOpponentSwitch()));
            exchangeText.setText(String.valueOf(lancerMatch.getExchange()));
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

    private void setupListeners() {
        allianceSwitchAdd.setOnClickListener(v -> allianceSwitchText.setText(String.valueOf(Integer.parseInt(allianceSwitchText.getText().toString()) + 1)));
        centerScaleAdd.setOnClickListener(v -> centerScaleText.setText(String.valueOf(Integer.parseInt(centerScaleText.getText().toString()) + 1)));
        opponentSwitchAdd.setOnClickListener(v -> opponentSwitchText.setText(String.valueOf(Integer.parseInt(opponentSwitchText.getText().toString()) + 1)));
        exchangeAdd.setOnClickListener(v -> exchangeText.setText(String.valueOf(Integer.parseInt(exchangeText.getText().toString()) + 1)));

        allianceSwitchSubtract.setOnClickListener(v -> {
            int allianceSwitch = Integer.parseInt(allianceSwitchText.getText().toString());
            if (allianceSwitch > 0) {
                allianceSwitchText.setText(String.valueOf(--allianceSwitch));
            }
        });

        centerScaleSubtract.setOnClickListener(v -> {
            int centerScale = Integer.parseInt(centerScaleText.getText().toString());
            if (centerScale > 0) {
                centerScaleText.setText(String.valueOf(--centerScale));
            }
        });

        opponentSwitchSubtract.setOnClickListener(v -> {
            int opponentSwitch = Integer.parseInt(opponentSwitchText.getText().toString());
            if (opponentSwitch > 0) {
                opponentSwitchText.setText(String.valueOf(--opponentSwitch));
            }
        });

        exchangeSubtract.setOnClickListener(v -> {
            int exchange = Integer.parseInt(exchangeText.getText().toString());
            if (exchange > 0) {
                exchangeText.setText(String.valueOf(--exchange));
            }
        });
    }

    public void reset() {
        matchNumber.getText().clear();
        teamNumber.getText().clear();

        allianceColor.check(R.id.blueAllianceRadioButton);
        startingConfiguration.check(R.id.leftStartingConfiguration);

        crossedAutoLineBox.setChecked(false);
        autonomousAttempt.setSelection(0);
        wrongSideAutoBox.setChecked(false);

        allianceSwitchText.setText("0");
        centerScaleText.setText("0");
        opponentSwitchText.setText("0");
        exchangeText.setText("0");

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
                .setAutonomousAttempt((AutonomousAttempt) autonomousAttempt.getSelectedItem())
                .setWrongSideAuto(wrongSideAutoBox.isChecked())
                .setAllianceSwitch(Integer.parseInt(allianceSwitchText.getText().toString()))
                .setCenterScale(Integer.parseInt(centerScaleText.getText().toString()))
                .setOpponentSwitch(Integer.parseInt(opponentSwitchText.getText().toString()))
                .setExchange(Integer.parseInt(exchangeText.getText().toString()))
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
                .setAutonomousAttempt((AutonomousAttempt) autonomousAttempt.getSelectedItem())
                .setWrongSideAuto(wrongSideAutoBox.isChecked())
                .setAllianceSwitch(Integer.parseInt(allianceSwitchText.getText().toString()))
                .setCenterScale(Integer.parseInt(centerScaleText.getText().toString()))
                .setOpponentSwitch(Integer.parseInt(opponentSwitchText.getText().toString()))
                .setExchange(Integer.parseInt(exchangeText.getText().toString()))
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
            case LEFT:
                startingConfiguration.check(R.id.leftStartingConfiguration);
                break;
            case MIDDLE:
                startingConfiguration.check(R.id.middleStartingConfiguration);
                break;
            case RIGHT:
                startingConfiguration.check(R.id.rightStartingConfiguration);
                break;
            default:
                startingConfiguration.check(R.id.leftStartingConfiguration);
                break;
        }
    }

    private void setSpinnerAutonomousAttempt(AutonomousAttempt attempt) {
        ArrayAdapter<AutonomousAttempt> adapter = (ArrayAdapter) autonomousAttempt.getAdapter();
        autonomousAttempt.setSelection(adapter.getPosition(attempt));
    }

    private void setSpinnerEndGameAttempt(EndGameAttempt attempt) {
        ArrayAdapter adapter = (ArrayAdapter) endGameAttempt.getAdapter();
        endGameAttempt.setSelection(adapter.getPosition(attempt));
    }

    @Override
    public void onBackPressed() {
    }
}