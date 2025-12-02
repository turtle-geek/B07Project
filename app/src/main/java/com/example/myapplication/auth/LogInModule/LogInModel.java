//package com.example.myapplication.auth.LogInModule;
//
//import android.content.Context;
//import android.content.Intent;
//import android.widget.Toast;
//
//import com.example.myapplication.MainActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class LogInModel {
//    private FirebaseAuth fAuth;
//    public LogInModel() {
//        fAuth = FirebaseAuth.getInstance();
//    }
//
//    public void logIn(String email, String password){
//        fAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // TODO Successful Login
//                    } else {
//                       // TODO Login Failed
//                    }
//                });
//    }
//}
