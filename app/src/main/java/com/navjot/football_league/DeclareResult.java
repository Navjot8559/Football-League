package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DeclareResult extends AppCompatActivity {

    private String team1,team2,team1Id,team2Id,team1Logo,team2Logo,date,time,scheduleId,winner;
    private TextView tvTeam1,tvTeam2,tvDate,tvTime;
    private ImageView ivTeam1Logo,ivTeam2Logo;
    private EditText team1Score,team2Score;
    private Button declareResult;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");
    private CollectionReference scheduleRef = db.collection("Schedules");
    private CollectionReference resultRef = db.collection("Results");
    private int score1,score2;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare_result);

        team1 = getIntent().getStringExtra("team1");
        team2 = getIntent().getStringExtra("team2");
        team1Id = getIntent().getStringExtra("team1Id");
        team2Id = getIntent().getStringExtra("team2Id");
        team1Logo = getIntent().getStringExtra("team1Logo");
        team2Logo = getIntent().getStringExtra("team2Logo");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        scheduleId = getIntent().getStringExtra("scheduleId");

        getSupportActionBar().setTitle("Declare Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvTeam1 = findViewById(R.id.team1_name);
        tvTeam2 = findViewById(R.id.team2_name);
        tvDate = findViewById(R.id.match_date);
        tvTime = findViewById(R.id.match_time);
        ivTeam1Logo = findViewById(R.id.team1_logo);
        ivTeam2Logo = findViewById(R.id.team2_logo);
        team1Score = findViewById(R.id.team1_score);
        team2Score = findViewById(R.id.team2_score);
        declareResult = findViewById(R.id.declare_match);
        mProgressDialog = new ProgressDialog(this);

        tvTeam1.setText(team1);
        tvTeam2.setText(team2);
        tvDate.setText(date);
        tvTime.setText(time);
        Picasso.get().load(team1Logo).into(ivTeam1Logo);
        Picasso.get().load(team2Logo).into(ivTeam2Logo);

        declareResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateScores();
            }
        });

    }

    private void validateScores() {
        String scoreA = team1Score.getText().toString().trim();
        String scoreB = team2Score.getText().toString().trim();

        if(TextUtils.isEmpty(scoreA)){
            team1Score.setError("team 1 score is required...");
            return;
        }else if(TextUtils.isEmpty(scoreB)){
            team2Score.setError("team 2 score is required...");
            return;
        }

        score1 = Integer.valueOf(scoreA);
        score2 = Integer.valueOf(scoreB);
        declareResult();
    }

    private void declareResult() {
        mProgressDialog.setTitle("Calculating Result");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        if(score1>score2){
            winner = team1;
        }else if(score1<score2){
            winner = team2;
        }else{
            winner = "tie";
        }

        updateTeamResult(team1Id);
        updateTeamResult(team2Id);
        removeSchedule();
    }

    private void removeSchedule() {
        scheduleRef.document(scheduleId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    uploadResult();
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(DeclareResult.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadResult() {
        HashMap<String,Object> resultMap = new HashMap<>();
        resultMap.put("team1",team1);
        resultMap.put("team2",team2);
        resultMap.put("team1Id",team1Id);
        resultMap.put("team2Id",team2Id);
        resultMap.put("team1Logo",team1Logo);
        resultMap.put("team2Logo",team2Logo);
        resultMap.put("matchDate",date);
        resultMap.put("matchTime",time);
        resultMap.put("team1Score",score1);
        resultMap.put("team2Score",score2);
        resultMap.put("winner",winner);
        resultMap.put("resultId",scheduleId);

        resultRef.document(scheduleId).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Toast.makeText(DeclareResult.this, "Result Declared", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("userType", Prevelant.userType);
                    startActivity(intent);
                    finish();
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(DeclareResult.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTeamResult(final String teamId) {
        teamRef.document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Team team = documentSnapshot.toObject(Team.class);
                        if(winner.equals(team.getTeamName())){
                            //increase 1 win
                            team.setWins(team.getWins()+1);
                            teamRef.document(teamId).update("wins",team.getWins());
                            //increase 4 points
                            team.setPoints(team.getPoints()+4);
                            teamRef.document(teamId).update("points",team.getPoints());
                        }else if(winner.equals("tie")){
                            //increase 1 tie
                            team.setTies(team.getTies()+1);
                            teamRef.document(teamId).update("ties",team.getTies());
                            //increase 2 points
                            team.setPoints(team.getPoints()+2);
                            teamRef.document(teamId).update("points",team.getPoints());
                        }else{
                            //increase 1 lose
                            team.setLoses(team.getLoses()+1);
                            teamRef.document(teamId).update("loses",team.getLoses());
                        }
                        //increase 1 match
                        team.setMatches(team.getMatches()+1);
                        teamRef.document(teamId).update("matches",team.getMatches());
                    }
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(DeclareResult.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
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