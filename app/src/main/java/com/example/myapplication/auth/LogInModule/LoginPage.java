//package com.example.myapplication.auth.LogInModule;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.myapplication.MainActivity;
//import com.example.myapplication.R;
//import com.example.myapplication.auth.ForgotPassword;
//import com.example.myapplication.auth.Register;
//import com.google.android.material.textfield.TextInputLayout;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import jp.wasabeef.blurry.Blurry;
//
//public class LoginPage extends AppCompatActivity {
//
//
//
//    private SharedPreferences sharedPreferences;
//    private static final String PREFS_NAME = "LoginPrefs";
//    private static final String KEY_EMAIL = "email";
//    private static final String KEY_PASSWORD = "password";
//    private static final String KEY_REMEMBER = "remember";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        // Initialize SharedPreferences
//        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//
//
//
//
//
//
//
//
//
//
//    }
//
//    private void loadSavedCredentials() {
//        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER, false);
//
//        if (rememberMe) {
//            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
//            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
//
//            mailET.setText(savedEmail);
//            passwordET.setText(savedPassword);
//            rememberMeCheckBox.setChecked(true);
//        }
//    }
//
//    private void saveCredentials(String email, String password) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        if (rememberMeCheckBox.isChecked()) {
//            editor.putString(KEY_EMAIL, email);
//            editor.putString(KEY_PASSWORD, password);
//            editor.putBoolean(KEY_REMEMBER, true);
//        } else {
//            // Clear saved credentials if unchecked
//            editor.remove(KEY_EMAIL);
//            editor.remove(KEY_PASSWORD);
//            editor.putBoolean(KEY_REMEMBER, false);
//        }
//
//        editor.apply();
//    }
//
//
//
//
//    }
//}