package com.navjot.football_league;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    private EditText number,password;
    private Button loginButton;
    private TextView user;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userType = getIntent().getStringExtra("userType");
        number = findViewById(R.id.number);
        password = findViewById(R.id.password);
        user = findViewById(R.id.user);

        if(userType.equals("lManager")){
            user.setText("League Manager");
        }else{
            user.setText("Team Manager");
        }

    }
}