package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

public class Statistics extends AppCompatActivity {

    private ImageView teamLogo;
    private TextView teamName,teamManager,teamId,matches,wins,loses,ties,points;
    private String Id;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Id = getIntent().getStringExtra("teamId");

        getSupportActionBar().setTitle("Team Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teamLogo = findViewById(R.id.team_logo);
        teamName = findViewById(R.id.team_name);
        teamManager = findViewById(R.id.team_manager);
        teamId = findViewById(R.id.team_id);
        matches = findViewById(R.id.matches);
        wins = findViewById(R.id.wins);
        loses = findViewById(R.id.loses);
        ties = findViewById(R.id.ties);
        points = findViewById(R.id.points);
        mProgressDialog = new ProgressDialog(this);

        teamId.setText(Id);
        fetchTeamStats();
    }

    private void fetchTeamStats() {
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        teamRef.document(Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Team team = documentSnapshot.toObject(Team.class);
                        Picasso.get().load(team.getTeamLogo()).into(teamLogo);
                        teamManager.setText("Managed By : " + team.getManagerName());
                        teamName.setText(team.getTeamName());
                        matches.setText(String.valueOf(team.getMatches()));
                        wins.setText(String.valueOf(team.getWins()));
                        loses.setText(String.valueOf(team.getLoses()));
                        ties.setText(String.valueOf(team.getTies()));
                        points.setText(String.valueOf(team.getPoints()));
                    }else{
                        Toast.makeText(Statistics.this, "team not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(Statistics.this,e.getMessage(), Toast.LENGTH_SHORT).show();
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