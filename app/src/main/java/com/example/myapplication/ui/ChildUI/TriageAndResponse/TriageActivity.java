package com.example.myapplication.ui.ChildUI.TriageAndResponse;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Log;
import android.content.res.ColorStateList;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.Child;
import com.example.myapplication.models.HealthProfile;
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

public class TriageActivity extends AppCompatActivity {

    private static final String TAG = "TriageActivity";
    private String username;

    private ChipGroup chipGroup;
    private Button nextButton;
    private RadioGroup rescueAttemptGroup;
    private RadioButton radioRescueYes;
    private RadioButton radioRescueNo;
    private Slider triagePEFSlider;
    private TextView tvPeakFlowValue;
    private SwitchMaterial peakFlowToggleSwitch;
    private LinearLayout peakFlowInputContainer;

    private HealthProfile hp;
    private Child currentChild;
    private int peakFlowValue = 74;

    private static final int COLOR_CHIP_RED = 0xFFF44336;
    private static final int COLOR_CHIP_WHITE = 0xFFFFFFFF; // ADDED WHITE COLOR CONSTANT
    private static final int COLOR_CHIP_DEFAULT_BACKGROUND = 0xFFE0E0E0;
    private static final int COLOR_SLIDER_GREEN = 0xFF064200;
    private static final int COLOR_SLIDER_LIGHT_GRAY = 0xFFE0E0E0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_triage);

        checkAndSetupUser();
    }

    private void checkAndSetupUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = getIntent().getStringExtra("id");

        if (id == null && user != null) {
            id = user.getUid();
            Log.w(TAG, "ID missing from Intent. Fetched ID from FirebaseAuth.");
        }

        if (id == null || user == null) {
            Toast.makeText(this, "CRITICAL: User ID missing or not logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Error: User profile not found.", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    currentChild = documentSnapshot.toObject(Child.class);
                    if (currentChild != null) {
                        hp = currentChild.getHealthProfile();
                        Log.d(TAG, "Child profile loaded successfully.");
                    }

                    String fullEmailUsername = documentSnapshot.getString("emailUsername");
                    if (fullEmailUsername != null && !fullEmailUsername.isEmpty()) {
                        int atIndex = fullEmailUsername.indexOf('@');
                        this.username = (atIndex > 0) ? fullEmailUsername.substring(0, atIndex) : fullEmailUsername;

                        if (!this.username.isEmpty()) {
                            setupViews();
                        } else {
                            Toast.makeText(this, "Error: Username part not found in profile.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Error: User data is incomplete.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching child data: ", e);
                    Toast.makeText(this, "Failed to load user profile.", Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void setupViews() {
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

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
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

        if (!peakFlowToggleSwitch.isChecked()) {
            peakFlowInputContainer.setVisibility(View.GONE);
        }
    }

    // FIX: Updated to set text color to white when chip is checked (Red fill)
    void setupChipListeners() {
        ColorStateList defaultBackground = ColorStateList.valueOf(COLOR_CHIP_DEFAULT_BACKGROUND);
        ColorStateList checkedBackground = ColorStateList.valueOf(COLOR_CHIP_RED);
        ColorStateList checkedText = ColorStateList.valueOf(COLOR_CHIP_WHITE);

        // Get the default text color (System Black for stability)
        ColorStateList defaultText = ColorStateList.valueOf(getResources().getColor(android.R.color.black, getTheme()));

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;

                // Initial/Unchecked state setup
                chip.setChipBackgroundColor(defaultBackground);
                chip.setChipStrokeColor(null);
                chip.setTextColor(defaultText);

                chip.setOnClickListener(v -> {
                    if (chip.isChecked()) {
                        // CHECKED State: Red fill and White text
                        chip.setChipBackgroundColor(checkedBackground);
                        chip.setChipStrokeColor(checkedBackground);
                        chip.setTextColor(checkedText); // Set text color to White
                    } else {
                        // UNCHECKED State: Reset to default
                        chip.setChipBackgroundColor(defaultBackground);
                        chip.setChipStrokeColor(null);
                        chip.setTextColor(defaultText); // Reset text color
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

        rescueAttemptGroup.setOnCheckedChangeListener((group, checkedId) -> { });
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

        // --- START OF CRITICAL DECISION LOGIC (Prioritizing chips) ---

        // 1. CRITICAL CHIP CHECK: Chips 1, 2, 3, or 4 selected (These should represent the most severe symptoms like Cyanosis, Retractions, Gasping, Difficulty Speaking)
        if (selected.contains(R.id.chip1) || selected.contains(R.id.chip2) ||
                selected.contains(R.id.chip3) || selected.contains(R.id.chip4)) {

            finalDecision = "SOS";
            logIncidentAndNavigate(timestamp, selected, finalDecision, rescueAttemptMade, peakFlowWasEntered, peakFlowValue, TriageCriticalActivity.class);
            return;
        }

        // 2. RED PEF CHECK: If no severe chips were selected, check if the PEF zone is red.
        if (pefZone.equals("red")) {

            finalDecision = "SOS";
            logIncidentAndNavigate(timestamp, selected, finalDecision, rescueAttemptMade, peakFlowWasEntered, peakFlowValue, TriageCriticalActivity.class);
            return;
        }

        // --- END OF CRITICAL DECISION LOGIC ---

        // 3. NON-CRITICAL DECISION (Green or Yellow PEF, or other symptoms selected)
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

            if (hp != null && currentChild != null) {
                return pef.computeZone(currentChild);
            }

            // Fallback for missing profile data (Yellow/Red Zone)
            final int FALLBACK_PB = 400;
            if (peakFlowValue < (0.5 * FALLBACK_PB)) {
                return "red";
            } else if (peakFlowValue < (0.8 * FALLBACK_PB)) {
                return "yellow";
            }
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
            Log.e(TAG, "Timestamp is null, cannot create log.");
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
                        Log.i(TAG, "Incident Logged: " + documentReference.getId());
                        Toast.makeText(TriageActivity.this, "Triage Logged (" + decision + ")", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(TriageActivity.this, destinationActivity);
                        intent.putExtra("DECISION", decision);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error saving incident to Firestore", e);
                        Toast.makeText(TriageActivity.this, "Error: Could not save log. Navigating anyway.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(TriageActivity.this, destinationActivity);
                        intent.putExtra("DECISION", decision);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}