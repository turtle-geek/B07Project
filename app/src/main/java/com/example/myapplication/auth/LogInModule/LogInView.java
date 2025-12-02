//package com.example.myapplication.auth.LogInModule;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.myapplication.R;
//import com.example.myapplication.auth.ForgotPassword;
//import com.example.myapplication.auth.Register;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.android.material.textfield.TextInputLayout;
//
//import jp.wasabeef.blurry.Blurry;
//
//public class LogInView extends AppCompatActivity {
//    private TextInputEditText mailET, passwordET;
//    private Button loginButton;
//    private CheckBox rememberMeCheckBox;
//    private TextView registerTextView, forgotPasswordTextView;
//    private LogInPresenter presenter;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login_page);
//
//        presenter = new LogInPresenter(this, new LogInModel());
//
//        // Initialize UI components
//        TextInputLayout mailLayout = findViewById(R.id.mailLayoutLogin);
//        TextInputLayout passwordLayout = findViewById(R.id.passwordLayoutLogin);
//
//        mailET = findViewById(R.id.mailET);
//        passwordET = findViewById(R.id.passwordET);
//        loginButton = findViewById(R.id.loginButton);
//        rememberMeCheckBox = findViewById(R.id.checkBox);
//        registerTextView = findViewById(R.id.registerTextView);
//        forgotPasswordTextView = findViewById(R.id.textView2);
//
//        // Apply blur effect to the login container
//        ViewGroup loginContainer = findViewById(R.id.loginContainer);
//        Blurry.with(this)
//                .radius(25)              // Blur intensity (higher = more blur)
//                .sampling(2)             // Down sampling (higher = faster but lower quality)
//                .color(Color.argb(66, 255, 255, 255))  // White overlay with transparency
//                .async()                 // Do it asynchronously for better performance
//                .animate(500);           // Fade in animation (milliseconds)
//
//        // Load saved credentials if "Remember Me" was checked
//        loadSavedCredentials();
//
//        // Login button click listener
//        loginButton.setOnClickListener(v -> loginUser());
//
//        // Register text click listener
//        registerTextView.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginPage.this, Register.class);
//            startActivity(intent);
//        });
//
//        // Forgot password text click listener
//        forgotPasswordTextView.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginPage.this, ForgotPassword.class);
//            startActivity(intent);
//        });
//    }
//}
