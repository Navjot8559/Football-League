package com.navjot.football_league;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectUserType extends AppCompatActivity {

    Button lManager,tManager,guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        lManager = findViewById(R.id.l_manager_btn);
        tManager = findViewById(R.id.t_manager_btn);
        guest = findViewById(R.id.guest_btn);

        lManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                intent.putExtra("userType","lManager");
                startActivity(intent);
            }
        });

        tManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                intent.putExtra("userType","tManager");
                startActivity(intent);
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("userType","guest");
                startActivity(intent);
            }
        });
    }
}