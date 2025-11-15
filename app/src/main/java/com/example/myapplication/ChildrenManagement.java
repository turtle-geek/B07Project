package com.example.myapplication;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChildrenManagement extends AppCompatActivity {

    private LinearLayout containersLayout;
    private FloatingActionButton addContainerButton;
    private ScrollView scrollView;
    private int containerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_children);

        containersLayout = findViewById(R.id.container1);
        addContainerButton = findViewById(R.id.addContainerButton);
        scrollView = (ScrollView) containersLayout.getParent();

        // Add the first container automatically
        addNewContainer();

        // Button to add more containers
        addContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContainer();
            }
        });
    }

    private void addNewContainer() {
        containerCount++;

        // Create the container layout
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundResource(R.drawable.blur_bg);
        container.setElevation(10);
        container.setPadding(30, 30, 30, 30);

        // Set layout parameters with margin
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(0, 0, 0, 40); // Bottom margin between containers
        container.setLayoutParams(containerParams);

        // Create title TextView (clickable)
        final TextView titleTextView = new TextView(this);
        titleTextView.setText("Child " + containerCount);
        titleTextView.setTextSize(26);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setPadding(30, 30, 30, 30);
        titleTextView.setBackgroundResource(android.R.attr.selectableItemBackground);
        titleTextView.setClickable(true);
        titleTextView.setFocusable(true);

        // Create title EditText (hidden initially)
        final EditText titleEditText = new EditText(this);
        titleEditText.setText("Child " + containerCount);
        titleEditText.setTextSize(26);
        titleEditText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleEditText.setPadding(30, 30, 30, 30);
        titleEditText.setVisibility(View.GONE);
        titleEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Make title editable on click
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTextView.setVisibility(View.GONE);
                titleEditText.setVisibility(View.VISIBLE);
                titleEditText.requestFocus();
                titleEditText.setSelection(titleEditText.getText().length());
            }
        });

        // Finish editing title
        titleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String newTitle = titleEditText.getText().toString().trim();
                    if (!newTitle.isEmpty()) {
                        titleTextView.setText(newTitle);
                    }
                    titleEditText.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        // Create divider
        View divider = new View(this);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        );
        dividerParams.setMargins(0, 20, 0, 40);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(0xFFCCCCCC);

        // Create input fields
        final EditText field1 = createEditText("Name");
        final EditText field2 = createEditText("Age");
        final EditText field3 = createEditText("Grade");

        // Create save button
        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setTextSize(20);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                (int) (280 * getResources().getDisplayMetrics().density),
                (int) (52 * getResources().getDisplayMetrics().density)
        );
        buttonParams.setMargins(0, 50, 0, 0);
        buttonParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        saveButton.setLayoutParams(buttonParams);

        // Save button click listener
        final int currentContainerNumber = containerCount;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTextView.getText().toString();
                String name = field1.getText().toString();
                String age = field2.getText().toString();
                String grade = field3.getText().toString();

                Toast.makeText(ChildrenManagement.this,
                        "Saved: " + title + "\nName: " + name + "\nAge: " + age + "\nGrade: " + grade,
                        Toast.LENGTH_SHORT).show();

                // Here you would save to Firebase/database
                // saveToFirebase(title, name, age, grade);
            }
        });

        // Add all views to container
        container.addView(titleTextView);
        container.addView(titleEditText);
        container.addView(divider);
        container.addView(field1);
        container.addView(field2);
        container.addView(field3);
        container.addView(saveButton);

        // Add container to main layout
        containersLayout.addView(container);

        // Scroll to the new container smoothly
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private EditText createEditText(String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 30);
        editText.setLayoutParams(params);
        return editText;
    }

}
