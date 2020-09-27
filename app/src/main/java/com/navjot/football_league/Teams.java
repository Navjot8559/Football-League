package com.navjot.football_league;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.navjot.football_league.Prevelant.Prevelant;

public class Teams extends AppCompatActivity {

    private Button addTeam;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        addTeam = findViewById(R.id.add_team_btn);
        mRecyclerView = findViewById(R.id.teams_list);

        if(Prevelant.userType.equals("lManager")){
            addTeam.setVisibility(View.VISIBLE);
        }

        addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddTeam.class));
            }
        });

    }
}