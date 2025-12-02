package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.models.PeakFlow;
import com.example.myapplication.models.IncidentLogEntry;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TriageActivity extends BaseChildActivity {

    private String username;

    ChipGroup chipGroup;
    Button nextButton;

    RadioGroup rescueAttemptGroup;
    RadioButton radioRescueYes;
    RadioButton radioRescueNo;

    Slider triagePEFSlider;
    TextView tvPeakFlowValue;
    SwitchMaterial peakFlowToggleSwitch;
    LinearLayout peakFlowInputContainer;

    int peakFlowValue = 74;

    private static final int COLOR_CHIP_RED = 0xFFF44336;
    private static final int COLOR_CHIP_WHITE = 0xFFFFFFFF;
    private static final int COLOR_CHIP_GREEN = 0xFF4CAF50;
    private static final int COLOR_CHIP_DEFAULT_BACKGROUND = 0xFFE0E0E0;

    private static final int COLOR_SLIDER_GREEN = 0xFF064200;
    private static final int COLOR_SLIDER_LIGHT_GRAY = 0xFFE0E0E0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        checkAndSetupUser();
    }

    private void checkAndSetupUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Error: User must be logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String uid = user.getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullEmailUsername = documentSnapshot.getString("emailUsername");

                        if (fullEmailUsername != null && !fullEmailUsername.isEmpty()) {
                            String cleanUsername;
                            int atIndex = fullEmailUsername.indexOf('@');

                            if (atIndex > 0) {
                                cleanUsername = fullEmailUsername.substring(0, atIndex);
                            } else {
                                cleanUsername = fullEmailUsername;
                            }

                            if (!cleanUsername.isEmpty()) {
                                this.username = cleanUsername;
                                setupViews();
                            } else {
                                Toast.makeText(this, "Error: Username part not found in profile.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "Error: User data is incomplete.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Error: User profile not found.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TriageActivity", "Failed to fetch user data: ", e);
                    Toast.makeText(this, "Error fetching user data.", Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void setupViews() {
        setContentView(R.layout.activity_triage);

        bindViews();
        setupSlider();
        setupSliderColors();
        setupChipListeners();
        setupRescueButtonListeners();

        nextButton.setOnClickListener(v -> redFlagCheck());
    }

    void bindViews(){
        chipGroup = findViewById(R.id.chipGroup);
        nextButton = findViewById(R.id.nextButton);

        rescueAttemptGroup = findViewById(R.id.rescueAttemptGroup);
        radioRescueYes = findViewById(R.id.radioRescueYes);
        radioRescueNo = findViewById(R.id.radioRescueNo);

        triagePEFSlider = findViewById(R.id.TriagePEFSlider);
        tvPeakFlowValue = findViewById(R.id.tvPeakFlowValue);
        peakFlowToggleSwitch = findViewById(R.id.peakFlowToggleSwitch);
        peakFlowInputContainer = findViewById(R.id.peakFlowInputContainer);
    }

    void setupSliderColors() {
        int[][] states = new int[][] {
                { android.R.attr.state_enabled},
                {-android.R.attr.state_enabled}
        };

        int[] activeColors = new int[] {
                COLOR_SLIDER_GREEN,
                COLOR_SLIDER_GREEN
        };

        int[] inactiveColors = new int[] {
                COLOR_SLIDER_LIGHT_GRAY,
                COLOR_SLIDER_LIGHT_GRAY
        };

        ColorStateList activeTintList = new ColorStateList(states, activeColors);
        ColorStateList inactiveTintList = new ColorStateList(states, inactiveColors);

        triagePEFSlider.setThumbTintList(ColorStateList.valueOf(COLOR_SLIDER_GREEN));
        triagePEFSlider.setTrackActiveTintList(activeTintList);
        triagePEFSlider.setTrackInactiveTintList(inactiveTintList);
        triagePEFSlider.setTickActiveTintList(activeTintList);
        triagePEFSlider.setTickInactiveTintList(inactiveTintList);
    }

    void setupSlider() {
        triagePEFSlider.addOnChangeListener((slider, value, fromUser) -> {
            peakFlowValue = (int) value;
            tvPeakFlowValue.setText("Value: " + peakFlowValue + " L/min");
        });

        triagePEFSlider.setValue(peakFlowValue);
        tvPeakFlowValue.setText("Value: " + peakFlowValue + " L/min");

        peakFlowToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                peakFlowInputContainer.setVisibility(View.VISIBLE);
            } else {
                peakFlowInputContainer.setVisibility(View.GONE);
            }
        });
    }

    void setupChipListeners() {
        ColorStateList defaultSelector = ColorStateList.valueOf(COLOR_CHIP_DEFAULT_BACKGROUND);

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;

                chip.setChipBackgroundColor(defaultSelector);
                chip.setChipStrokeColor(null);

                chip.setOnClickListener(v -> {
                    if (chip.isChecked()) {

                        chip.setChipBackgroundColor(ColorStateList.valueOf(COLOR_CHIP_RED));
                        chip.setChipStrokeColor(ColorStateList.valueOf(COLOR_CHIP_RED));
                    } else {
                        chip.setChipBackgroundColor(defaultSelector);
                        chip.setChipStrokeColor(null);
                    }
                });
            }
        }
    }

    private ColorStateList createGreenRadioTintList() {
        int states[][] = new int[][] {
                { android.R.attr.state_checked },
                {}
        };
        int colors[] = new int[] {
                COLOR_SLIDER_GREEN,
                getResources().getColor(android.R.color.darker_gray, getTheme())
        };
        return new ColorStateList(states, colors);
    }

    void setupRescueButtonListeners() {
        ColorStateList greenTintList = createGreenRadioTintList();

        radioRescueYes.setButtonTintList(greenTintList);
        radioRescueNo.setButtonTintList(greenTintList);

        rescueAttemptGroup.setOnCheckedChangeListener((group, checkedId) -> {

        });
    }


    void redFlagCheck() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selected.add(chip.getId());
                }
            }
        }

        boolean rescueAttemptSelected = rescueAttemptGroup.getCheckedRadioButtonId() != -1;
        boolean anyRedFlagSelected = !selected.isEmpty();

        if (!anyRedFlagSelected) {
            Toast.makeText(this, "Please select at least one physical red flag symptom.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!rescueAttemptSelected) {
            Toast.makeText(this, "Please indicate if there was any recent rescue attempt.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean rescueAttemptMade = rescueAttemptGroup.getCheckedRadioButtonId() == R.id.radioRescueYes;
        boolean peakFlowWasEntered = peakFlowToggleSwitch.isChecked();
        LocalDateTime timestamp = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timestamp = LocalDateTime.now();
        }

        String finalDecision;
        String pefZone = processPEF();

        if (selected.contains(R.id.chip11) || selected.contains(R.id.chip12) ||
                selected.contains(R.id.chip5) || selected.contains(R.id.chip4) ||
                pefZone.equals("red")) {

            finalDecision = "SOS";

            logIncidentAndNavigate(timestamp, selected, finalDecision, rescueAttemptMade, peakFlowWasEntered, peakFlowValue, TriageCriticalActivity.class);
            return;
        }

        finalDecision = "NOT SOS";

        logIncidentAndNavigate(timestamp, selected, finalDecision, rescueAttemptMade, peakFlowWasEntered, peakFlowValue, TriageNonCriticalActivity.class);
    }

    String processPEF(){
        if (!peakFlowToggleSwitch.isChecked()) {
            return "normal";
        }

        LocalDateTime submitTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            submitTime = LocalDateTime.now();
        }

        if (submitTime != null) {
            PeakFlow pef = new PeakFlow(peakFlowValue, submitTime);
            return "normal";
        }

        return "normal";
    }

    private void logIncidentAndNavigate(
            LocalDateTime timestamp,
            List<Integer> selectedSymptomIds,
            String finalDecision,
            boolean rescueAttemptMade,
            boolean peakFlowEntered,
            int peakFlowValue,
            Class<?> destinationActivity) {

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: User data missing, cannot log incident.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, destinationActivity));
            return;
        }

        if (timestamp == null) {
            Log.e("TriageActivity", "Timestamp is null, cannot create log.");
            Toast.makeText(this, "Error: Cannot log incident without timestamp.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, destinationActivity));
            return;
        }

        IncidentLogEntry newEntry = new IncidentLogEntry(
                username,
                timestamp,
                selectedSymptomIds,
                finalDecision,
                rescueAttemptMade,
                peakFlowEntered,
                peakFlowValue
        );

        saveIncidentToFirestore(newEntry, destinationActivity, finalDecision);
    }

    private void saveIncidentToFirestore(IncidentLogEntry entry, Class<?> destinationActivity, String decision) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("triage_incidents")
                .add(entry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("TriageActivity", "Incident Logged: " + documentReference.getId());
                        Toast.makeText(TriageActivity.this, "Triage Logged (" + decision + ")", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(TriageActivity.this, destinationActivity);
                        intent.putExtra("DECISION", decision);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TriageActivity", "Error saving incident to Firestore", e);
                        Toast.makeText(TriageActivity.this, "Error: Could not save log. Navigating anyway.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(TriageActivity.this, destinationActivity);
                        intent.putExtra("DECISION", decision);
                        startActivity(intent);
                    }
                });
    }
}