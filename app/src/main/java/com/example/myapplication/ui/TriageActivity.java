package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.res.ColorStateList;

import androidx.activity.EdgeToEdge;

import com.example.myapplication.R;
import com.example.myapplication.models.PeakFlow;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TriageActivity extends BaseChildActivity {

    ChipGroup chipGroup;
    Button nextButton;

    // Peak Flow elements
    Slider triagePEFSlider;
    TextView tvPeakFlowValue;
    SwitchMaterial peakFlowToggleSwitch;
    LinearLayout peakFlowInputContainer;

    int peakFlowValue = 74;

    // Hex colors for manual control
    private static final int COLOR_CHIP_RED = 0xFFF44336;
    private static final int COLOR_CHIP_WHITE = 0xFFFFFFFF;

    // Slider Colors
    private static final int COLOR_SLIDER_GREEN = 0xFF064200;
    private static final int COLOR_SLIDER_LIGHT_GRAY = 0xFFE0E0E0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_triage);

        bindViews();
        setupSlider();
        setupSliderColors();
        setupChipListeners();
        nextButton.setOnClickListener(v -> redFlagCheck());
    }

    void bindViews(){
        chipGroup = findViewById(R.id.chipGroup);
        nextButton = findViewById(R.id.nextButton);

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

        // 1. Set Thumb (Handle) Color: Green
        triagePEFSlider.setThumbTintList(ColorStateList.valueOf(COLOR_SLIDER_GREEN));

        // 2. Set Active Track (Slid Portion): Green
        triagePEFSlider.setTrackActiveTintList(activeTintList);

        // 3. Set Inactive Track (Unslid Portion): Light Gray/White
        triagePEFSlider.setTrackInactiveTintList(inactiveTintList);

        // 4. Tick Marks: Set to match track colors
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
        // 1. ATTEMPT to get the default color selector applied in XML
        ColorStateList defaultSelector = null;
        if (chipGroup.getChildCount() > 0) {
            View firstChild = chipGroup.getChildAt(0);
            if (firstChild instanceof Chip) {
                defaultSelector = ((Chip) firstChild).getChipBackgroundColor();
            }
        }

        // Fallback to a neutral white/gray selector if XML inheritance fails completely
        if (defaultSelector == null) {
            defaultSelector = ColorStateList.valueOf(0xFFE0E0E0);
        }

        // 2. APPLY listeners to all chips
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;

                // Ensure initial state uses the default selector
                chip.setChipBackgroundColor(defaultSelector);
                chip.setChipStrokeColor(null);

                ColorStateList finalDefaultSelector = defaultSelector;
                chip.setOnClickListener(v -> {
                    if (chip.isChecked()) {
                        // Apply RED fill and stroke when selected
                        chip.setChipBackgroundColor(ColorStateList.valueOf(COLOR_CHIP_RED));
                        chip.setChipStrokeColor(ColorStateList.valueOf(COLOR_CHIP_RED));
                    } else {
                        // Revert to the default gray selector when deselected
                        chip.setChipBackgroundColor(finalDefaultSelector);
                        chip.setChipStrokeColor(null);
                    }
                });
            }
        }
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

        Intent intent = new Intent(this, TriageCriticalActivity.class);
        String pefZone = processPEF();

        // 1. CRITICAL RED FLAG CHECK (Triggers 911/SOS immediately)
        // These are the four most severe symptoms indicating immediate danger:
        // - chip11: Blue/gray lips or nails
        // - chip12: Chest pulling in (retractions)
        // - chip5: Difficulty speaking
        // - chip4: Gasping for air

        if (selected.contains(R.id.chip11) || selected.contains(R.id.chip12) ||
                selected.contains(R.id.chip5) || selected.contains(R.id.chip4) ||
                pefZone.equals("red")) {

            intent.putExtra("DECISION", "SOS");
            startActivity(intent);
            return; // Stop further checks if an emergency is detected
        }

        // 2. URGENT/SECONDARY CHECK
        // If no critical red flags, check for other severe symptoms that warrant urgency
        // (but might not require auto-dial 911 if not combined with critical signs).
        // This includes:
        // - chip6: Symptoms worsen when lying on back
        // - chip7: Severe sweating
        // - chip2: Chest pain or tightness

        if (selected.contains(R.id.chip6) || selected.contains(R.id.chip7) ||
                selected.contains(R.id.chip2) || pefZone.equals("yellow")) {

            // Assuming "NOT SOS" leads to a less severe triage outcome (e.g., call doctor/ER visit)
            intent.putExtra("DECISION", "NOT SOS");
            startActivity(intent);
            return;
        }

        // 3. DEFAULT (If none of the above are selected, or only minor symptoms like chip1 'Short breaths' exist)
        intent.putExtra("DECISION", "NORMAL"); // You might want to define a "NORMAL" or "GREEN" decision
        startActivity(intent);
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
            // hp.addPEFToLog(pef);
            // return pef.computeZone(currentChild); // Assuming this returns "red", "yellow", or "green/normal"

            // Placeholder return for now, replace with actual PEF zone computation
            return "normal";
        }

        return "normal";
    }
}