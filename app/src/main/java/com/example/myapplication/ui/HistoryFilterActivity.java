package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class HistoryFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_filter);

        // Symptom dropdown toggles
        setupSymptomFilter("nw", R.id.filterNightWaking, R.id.nwFilterHeader, R.id.nwFilterDropdownOptions, R.id.nwBtnToggleDropdown, R.id.nwCbFilterMain);
        setupSymptomFilter("al", R.id.filterActivityLimits, R.id.alFilterHeader, R.id.alFilterDropdownOptions, R.id.alBtnToggleDropdown, R.id.alCbFilterMain);
        setupSymptomFilter("cw", R.id.filterCoughWheeze, R.id.cwFilterHeader, R.id.cwFilterDropdownOptions, R.id.cwBtnToggleDropdown, R.id.cwCbFilterMain);

        // Custom date range toggle
        setupDateRangeToggle();

        // Apply filter button placeholder
        findViewById(R.id.btnApplyFilter).setOnClickListener(v -> applyFilters());
    }

    // =========================================================================
    // Dropdown checkbox logic
    // =========================================================================

    /**
     * Reusable method to set up the visibility toggle and master checkbox logic for one symptom filter group.
     */
    private void setupSymptomFilter(String prefix, int containerId, int headerId, int dropdownId, int toggleBtnId, int mainCbId) {
        LinearLayout dropdownLayout = findViewById(dropdownId);
        ImageButton toggleButton = findViewById(toggleBtnId);
        CheckBox mainCheckbox = findViewById(mainCbId);

        LinearLayout header = findViewById(headerId);

        View.OnClickListener toggleListener = v -> {
            toggleVisibility(dropdownLayout);
        };

        header.setOnClickListener(toggleListener);
        toggleButton.setOnClickListener(toggleListener);

        mainCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleChildCheckboxes(dropdownLayout, isChecked);
        });

        mainCheckbox.setOnClickListener(toggleListener);
    }

    /**
     * Toggles the visibility of a given layout.
     */
    private void toggleVisibility(LinearLayout layout) {
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    /**
     * Iterates through a parent LinearLayout and sets all child CheckBoxes to the given state.
     */
    private void toggleChildCheckboxes(LinearLayout parentLayout, boolean isChecked) {
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            if (child instanceof CheckBox) {
                ((CheckBox) child).setChecked(isChecked);
            }
        }
    }

    // =========================================================================
    // Date and Range Logic
    // =========================================================================

    private void setupDateRangeToggle() {
        // Have to cast result to radio group
        ((RadioGroup)findViewById(R.id.radioGroupDateRange)).setOnCheckedChangeListener((group, checkedId) -> {
            LinearLayout customRangeLayout = findViewById(R.id.customRangeLayout);
            if (checkedId == R.id.rbCustomRange) {
                customRangeLayout.setVisibility(View.VISIBLE);
            } else {
                customRangeLayout.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.tvStartDate).setOnClickListener(v -> showDatePicker(R.id.tvStartDate));
        findViewById(R.id.tvEndDate).setOnClickListener(v -> showDatePicker(R.id.tvEndDate));
    }

    private void showDatePicker(int textViewId) {
        // Placeholder for DatePicker logic NO IMPLEMENTATION YET
        Toast.makeText(this, "Opening Date Picker...", Toast.LENGTH_SHORT).show();
    }

    // =========================================================================
    // Apply Filter Logic
    // =========================================================================

    private void applyFilters() {
        // Logic to gather and apply filters will be implemented here
        Toast.makeText(this, "Applying Filters (Logic to be implemented)", Toast.LENGTH_LONG).show();
    }
}