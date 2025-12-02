package com.example.myapplication.auth.LogInModule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.auth.ForgotPassword;
import com.example.myapplication.auth.Register;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import jp.wasabeef.blurry.Blurry;

public class LogInViewActivity extends AppCompatActivity {

    private TextInputEditText mailET, passwordET;
    private Button loginButton;
    private CheckBox rememberMeCheckBox;
    private TextView registerTextView, forgotPasswordTextView;
    private LogInPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        presenter = new LogInPresenter(this, new LogInModel(this));

        // Initialize UI components
        TextInputLayout mailLayout = findViewById(R.id.mailLayoutLogin);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayoutLogin);

        mailET = findViewById(R.id.mailET);
        passwordET = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);
        rememberMeCheckBox = findViewById(R.id.checkBox);
        registerTextView = findViewById(R.id.registerTextView);
        forgotPasswordTextView = findViewById(R.id.textView2);

        // Load saved credentials for display
        presenter.loadSavedCredentials();

        // Apply blur effect to the login container
        ViewGroup loginContainer = findViewById(R.id.loginContainer);
        Blurry.with(this)
                .radius(25)              // Blur intensity (higher = more blur)
                .sampling(2)             // Down sampling (higher = faster but lower quality)
                .color(Color.argb(66, 255, 255, 255))  // White overlay with transparency
                .async()                 // Do it asynchronously for better performance
                .animate(500);           // Fade in animation (milliseconds)



        // Register text click listener
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });

        // Forgot password text click listener
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPassword.class);
            startActivity(intent);
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            // validate input
            presenter.validateInput(mailET.getText().toString().trim(),
                    passwordET.getText().toString().trim());

            // Check for remember me
            presenter.checkRememberMe(
                    rememberMeCheckBox.isChecked(),
                    mailET.getText().toString().trim(),
                    passwordET.getText().toString().trim());
        });
    }

    public void showInputError(String message) {
        mailET.setError(message);
        mailET.requestFocus();
    }

    public void showPasswordError(String message) {
        passwordET.setError(message);
        passwordET.requestFocus();
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void successLogin(boolean status) {
        if (status) {
            presenter.checkRememberMe(
                    rememberMeCheckBox.isChecked(),
                    mailET.getText().toString().trim(),
                    passwordET.getText().toString().trim());

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void fillSavedCredentials(String input, String password) {
        if (input != null && password != null) {
            mailET.setText(input);
            passwordET.setText(password);
            rememberMeCheckBox.setChecked(true);
        }
    }
}
