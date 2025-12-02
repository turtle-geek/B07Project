package com.example.myapplication;

import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import android.text.Editable;
import android.widget.Button;

import com.example.myapplication.auth.LogInModule.LogInModel;
import com.example.myapplication.auth.LogInModule.LogInPresenter;
import com.example.myapplication.auth.LogInModule.LogInViewActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    @Mock
    LogInViewActivity view;
    @Mock
    LogInModel model;
    @Mock
    TextInputEditText input, password;
    @Mock
    Editable editInput, editPassword;
    @Mock
    Button loginButton;

    @Test
    public void TestValidateInputEmptyInput(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.validateInput("", "pass");
        verify(view).showInputError("Email or Username required");
    }

    @Test
    public void TestValidateInputNullInput(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.validateInput(null, "pass");
        verify(view).showInputError("Email or Username required");
    }

    @Test
    public void TestValidateInputEmptyPassword(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.validateInput("username", "");
        verify(view).showPasswordError("Password is required");
    }

    @Test
    public void TestValidateInputNullPassword(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.validateInput("username", null);
        verify(view).showPasswordError("Password is required");
    }

    @Test
    public void TestValidateInputValidUsername(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.validateInput("input", "password");
        verify(model).logIn("input@mcjerry.app", "password", presenter);
    }

    @Test
    public void TestValidateInputValidEmail(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.validateInput("input@mcjerry.app", "password");
        verify(model).logIn("input@mcjerry.app", "password", presenter);
    }

    @Test
    public void TestOutputMessage(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.outputMessage("Message");
        verify(view).showMessage("Message");
    }

    @Test
    public void TestCheckRememberMeTrue(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.checkRememberMe(true, "input", "password");
        verify(model).saveCredentials("input", "password");
    }

    @Test
    public void TestCheckRememberMeFalse(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.checkRememberMe(false, "input", "password");
        verify(model).clearCredentials();
    }

    @Test
    public void TestLoadSavedCredentials(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.loadSavedCredentials();
        verify(model).fetchSavedCredentials(presenter);
    }
    @Test
    public void TestReturnSavedCredentials(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.returnSavedCredentials("input", "password");
        verify(view).fillSavedCredentials("input", "password");
    }
    @Test
    public void TestOnLoginSuccess(){
        LogInPresenter presenter = new LogInPresenter(view, model);
        presenter.onLoginSuccess(true);
        verify(view).successLogin(true);
    }
}