package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;

import java.util.Calendar;
import java.util.HashMap;

public class CreateSchedule extends AppCompatActivity implements View.OnClickListener{

    private EditText team1Id,team2Id,matchLocation;
    private TextView matchDate,matchTime;
    private Button scheduleButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String team1,team2,date,time,location,scheduleId;
    DatePickerDialog datePickerDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");
    private CollectionReference scheduleRef = db.collection("Schedules");
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        team1Id = findViewById(R.id.team1_id);
        team2Id = findViewById(R.id.team2_id);
        matchDate = findViewById(R.id.match_date);
        matchTime = findViewById(R.id.match_time);
        matchLocation = findViewById(R.id.match_location);
        scheduleButton = findViewById(R.id.schedule_btn);
        mProgressDialog = new ProgressDialog(this);

        matchDate.setOnClickListener(this);
        matchTime.setOnClickListener(this);
        scheduleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == matchDate){
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            matchDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if(view == matchTime){
            // get current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // launch Time Picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            matchTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if(view == scheduleButton){
            validateMatchDetails();
        }
    }

    private void validateMatchDetails() {
        team1 = team1Id.getText().toString().trim();
        team2 = team2Id.getText().toString().trim();
        date = matchDate.getText().toString().trim();
        time = matchTime.getText().toString().trim();
        location = matchLocation.getText().toString().trim();

        if(TextUtils.isEmpty(team1)){
            team1Id.setError("team 1 id is required...");
            return;
        }else if(TextUtils.isEmpty(team2)){
            team2Id.setError("team 2 id is required...");
            return;
        }else if(TextUtils.isEmpty(date)){
            matchDate.setError("date is required...");
            return;
        }else if(TextUtils.isEmpty(time)){
            matchTime.setError("time is required...");
            return;
        }else if(TextUtils.isEmpty(location)){
            matchLocation.setError("location is required...");
            return;
        }

        scheduleId = date+time;
        //show progress dialog when schedule button is clicked
        mProgressDialog.setTitle("Scheduling Match");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        validateTeam1();
    }

    private void validateTeam1() {
        DocumentReference team1Ref = teamRef.document(team1);
        team1Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document1 = task.getResult();
                    if(document1.exists()){
                        validateTeam2(document1);
                    }else{
                        mProgressDialog.dismiss();
                        Toast.makeText(CreateSchedule.this,"Team 1 does not exists...", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateSchedule.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validateTeam2(final DocumentSnapshot document1) {
        DocumentReference team2Ref = teamRef.document(team2);
        team2Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document2 = task.getResult();
                    if(document2.exists()){
                        createSchedule(document1,document2);
                    }else{
                        mProgressDialog.dismiss();
                        Toast.makeText(CreateSchedule.this, "Team 2 does not exists...", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateSchedule.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createSchedule(DocumentSnapshot document1,DocumentSnapshot document2) {

        Team teamA = document1.toObject(Team.class);
        Team teamB = document2.toObject(Team.class);
        String team1Name = teamA.getTeamName();
        String team2Name = teamB.getTeamName();
        String team1Logo = teamA.getTeamLogo();
        String team2Logo = teamB.getTeamLogo();

        HashMap<String,Object> scheduleMap = new HashMap<>();
        scheduleMap.put("scheduleId",scheduleId);
        scheduleMap.put("team1Id",team1);
        scheduleMap.put("team2Id",team2);
        scheduleMap.put("team1",team1Name);
        scheduleMap.put("team2",team2Name);
        scheduleMap.put("team1Logo",team1Logo);
        scheduleMap.put("team2Logo",team2Logo);
        scheduleMap.put("matchDate",date);
        scheduleMap.put("matchTime",time);
        scheduleMap.put("matchLocation",location);

        scheduleRef.document(scheduleId).set(scheduleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateSchedule.this, "Schedule Created...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("userType",Prevelant.userType);
                    finish();
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateSchedule.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}