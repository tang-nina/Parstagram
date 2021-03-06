package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    Button btnLogin;
    Button btnSignUp;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null){
            goToMainActivity();
        }

        btnLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                
                login(username, password);
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                signUp(username, password);
            }
        });
    }
    
    private void login(String username, String password){
        Log.i(TAG, "login: attempting to login in");

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e !=null){
                    Toast.makeText(LoginActivity.this, "Login unsuccessful.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with login.", e);
                    return;
                }else{
                    goToMainActivity();
                }
            }
        });
    }

    private void signUp(final String username, final String password){
        Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login unsuccessful.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with login.", e);
                    return;
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}