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
    private static final int COLOR_SLIDER_GREEN = 0xFF064200; // Active track/thumb
    private static final int COLOR_SLIDER_WHITE = 0xFFFFFFFF; // Inactive track

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_triage);

        bindViews();
        setupSlider();
        setupSliderColors(); // Applying colors immediately after binding
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

    /**
     * Applies custom Green/White colors to the slider track and thumb via Java code.
     * This is the fix for the persistent resource linking errors in XML.
     */
    void setupSliderColors() {
        // Define states for ColorStateList: enabled/active (green) vs default (white/gray)
        int[][] states = new int[][] {
                { android.R.attr.state_enabled}, // Enabled/Active State
                {-android.R.attr.state_enabled}  // Disabled/Inactive State
        };

        // Green color for active states (thumb and slid track)
        int[] activeColors = new int[] {
                COLOR_SLIDER_GREEN,
                COLOR_SLIDER_GREEN
        };

        // White color for inactive track (right of thumb)
        int[] inactiveColors = new int[] {
                COLOR_SLIDER_WHITE,
                COLOR_SLIDER_WHITE
        };

        // 1. Set Thumb (Handle) Color: Green
        triagePEFSlider.setThumbTintList(ColorStateList.valueOf(COLOR_SLIDER_GREEN));

        // 2. Set Active Track (Slid Portion): Green
        triagePEFSlider.setTrackActiveTintList(new ColorStateList(states, activeColors));

        // 3. Set Inactive Track (Unslid Portion): White/Light Gray
        // Using a slightly transparent gray for contrast, or pure white:
        triagePEFSlider.setTrackInactiveTintList(ColorStateList.valueOf(0xFFE0E0E0)); // Light Gray/White

        // You can also simplify by using your colors directly for the track:
        // triagePEFSlider.setTrackActiveTintList(ColorStateList.valueOf(COLOR_SLIDER_GREEN));
        // triagePEFSlider.setTrackInactiveTintList(ColorStateList.valueOf(0xFFE0E0E0));
    }


    /**
     * Sets up the slider's value update and the toggle switch visibility.
     */
    void setupSlider() {
        triagePEFSlider.addOnChangeListener((slider, value, fromUser) -> {
            peakFlowValue = (int) value;
            tvPeakFlowValue.setText("Value: " + peakFlowValue);
        });

        triagePEFSlider.setValue(peakFlowValue);
        tvPeakFlowValue.setText("Value: " + peakFlowValue);

        peakFlowToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                peakFlowInputContainer.setVisibility(View.VISIBLE);
            } else {
                peakFlowInputContainer.setVisibility(View.GONE);
            }
        });
    }

    void setupChipListeners() {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setOnClickListener(v -> {
                    if (chip.isChecked()) {
                        chip.setChipBackgroundColor(ColorStateList.valueOf(COLOR_CHIP_RED));
                    } else {
                        chip.setChipBackgroundColor(ColorStateList.valueOf(COLOR_CHIP_WHITE));
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

        Intent intent = new Intent(this, TriageDecisionCard.class);
        String pefZone = processPEF();

        if (selected.contains(R.id.chip10) || selected.contains(R.id.chip9) ||
                selected.contains(R.id.chip8) || selected.contains(R.id.chip6) ||
                selected.contains(R.id.chip7) || pefZone.equals("red")) {
            intent.putExtra("DECISION", "SOS");
            startActivity(intent);
        } else {
            intent.putExtra("DECISION", "NOT SOS");
            startActivity(intent);
        }
    }

    /**
     * Processes PEF. If the toggle is OFF, returns "normal" (skips PEF assessment).
     */
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
            // return pef.computeZone(currentChild);

            return "normal";
        }

        return "normal";
    }
}