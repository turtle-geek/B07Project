package com.example.myapplication.auth.LogInModule;

import android.text.TextUtils;

public class LogInPresenter {
    LogInViewActivity view;
    LogInModel model;
    public LogInPresenter(LogInViewActivity view, LogInModel model){
        this.view = view;
        this.model = model;
    }
    public void validateInput(String input, String password){
        if (input == null || input.isEmpty()) {
            view.showInputError("Email or Username required");
            return;
        } else if (!input.contains("@")){
            input=input+"@mcjerry.app";
        }

        if (password==null || password.isEmpty()) {
            view.showPasswordError("Password is required");
            return;
        }

        model.logIn(input, password, this);
    }

    public void outputMessage(String message) {
        view.showMessage(message);
    }

    public void checkRememberMe(boolean checked, String input, String password) {
        if (checked) {
            model.saveCredentials(input, password);
        } else {
            model.clearCredentials();
        }
    }

    public void loadSavedCredentials(){
        model.fetchSavedCredentials(this);
    }

    public void returnSavedCredentials(String input, String password){
        view.fillSavedCredentials(input, password);
    }

    public void onLoginSuccess(boolean status){
        view.successLogin(true);
    }

}