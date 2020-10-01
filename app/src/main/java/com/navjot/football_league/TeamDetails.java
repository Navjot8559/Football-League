package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;
import com.squareup.picasso.Picasso;

public class TeamDetails extends AppCompatActivity {

    private String teamId,team;
    private ImageView teamLogo;
    private TextView teamName,teamManager;
    private RecyclerView mRecyclerView;
    private Button addPlayer;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        teamId = getIntent().getStringExtra("teamId");
        team = getIntent().getStringExtra("teamName");

        getSupportActionBar().setTitle(team);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teamLogo = findViewById(R.id.team_logo);
        teamName = findViewById(R.id.team_name);
        teamName.setText(team);
        teamManager = findViewById(R.id.team_manager);
        addPlayer = findViewById(R.id.add_player_btn);
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.players_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Prevelant.userType.equals("lManager") || Prevelant.userPhone.equals(teamId)){
            addPlayer.setVisibility(View.VISIBLE);
        }

        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddPlayer.class));
            }
        });

        fetchTeamDetails();
    }

    private void fetchTeamDetails() {
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        teamRef.document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Team team = documentSnapshot.toObject(Team.class);
                        Picasso.get().load(team.getTeamLogo()).into(teamLogo);
                        teamManager.setText("Managed By : " + team.getManagerName());
                    }else{
                        Toast.makeText(TeamDetails.this, "team not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(TeamDetails.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}