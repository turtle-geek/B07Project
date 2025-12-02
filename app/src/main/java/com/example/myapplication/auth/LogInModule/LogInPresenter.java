//package com.example.myapplication.auth.LogInModule;
//
//import android.content.Intent;
//import android.text.TextUtils;
//import android.widget.Toast;
//
//import com.example.myapplication.MainActivity;
//
//public class LogInPresenter {
//    private LogInView view;
//    private LogInModel model;
//
//
//    public LogInPresenter(LogInView view, LogInModel model) {
//        this.view = view;
//        this.model = model;
//    }
//
//    public boolean validateInput() {
//        String input = mailET.getText().toString().trim();
//        String password = passwordET.getText().toString().trim();
//        String email = input;
//
//        // Validation
//        if (TextUtils.isEmpty(input)) {
//            mailET.setError("Email or Username required");
//            mailET.requestFocus();
//            return false;
//        } else if (!input.contains("@")) {
//            email = input + "@mcjerry.app";
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            passwordET.setError("Password is required");
//            passwordET.requestFocus();
//            return true;
//        }
//    }
//
//    public String loginUser(String email, String password) {
//        if (validateInput()) {
//            model.logIn(email, password);
//            // TODO: If Log in Successful
////            Toast.makeText(LoginPage.this, "Login successful!",
////                    Toast.LENGTH_SHORT).show();
////
////            // Save credentials if "Remember Me" is checked
////            saveCredentials(input, password);
////
////            // Navigate to MainActivity (which will check role and redirect)
////            Intent intent = new Intent(LoginPage.this, MainActivity.class);
////            startActivity(intent);
////            finish();
//                return ""; // TODO
//            // TODO: If Log in Failed
////            Toast.makeText(LoginPage.this,
////                    "Login failed: " + task.getException().getMessage(),
////                    Toast.LENGTH_LONG).show();
//        } else {
//            return "Validation failed";
//        }
//    }
//}
