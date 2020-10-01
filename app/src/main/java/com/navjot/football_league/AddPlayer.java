package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.navjot.football_league.Model.Team;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPlayer extends AppCompatActivity {

    private CircleImageView playerImage;
    private EditText playerName,playerAge,playerPosition;
    private Button addPlayer;
    private Uri imageUri;
    private static final int galleryPick = 1;
    private ProgressDialog mProgressDialog;
    private StorageReference playerImageRef;
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference playerCollection = db.collection("Players");
    private CollectionReference teamCollection = db.collection("Teams");
    private String teamId,teamName,playerId,name,age,position,downloadUrl;
    private int teamSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        getSupportActionBar().setTitle("Add Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teamId = getIntent().getStringExtra("teamId");
        playerImage = findViewById(R.id.player_image);
        playerName = findViewById(R.id.player_name);
        playerAge = findViewById(R.id.player_age);
        playerPosition = findViewById(R.id.player_position);
        addPlayer = findViewById(R.id.add_player_btn);
        mProgressDialog = new ProgressDialog(this);
        playerImageRef = FirebaseStorage.getInstance().getReference().child("Player Images");
        storageRef = FirebaseStorage.getInstance().getReference();

        playerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDetails();
            }
        });
    }

    private void validateDetails() {
        name = playerName.getText().toString().trim();
        age = playerAge.getText().toString().trim();
        position = playerPosition.getText().toString().trim();

        if(imageUri == null){
            Toast.makeText(getApplicationContext(),"image is required...",Toast.LENGTH_LONG).show();
            return;
        }else if(TextUtils.isEmpty(name)){
            playerName.setError("number is required...");
            return;
        }else if(TextUtils.isEmpty(age)){
            playerAge.setError("age is required...");
            return;
        }else if(TextUtils.isEmpty(position)){
            playerPosition.setError("position is required...");
            return;
        }

        storePlayerInfo();
    }

    private void storePlayerInfo() {
        //show progress dialog when add button is clicked
        mProgressDialog.setTitle("Adding Player");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String saveCurrentDate,saveCurrentTime;

        //using calendar class to save current date and time
        Calendar dateCalendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(dateCalendar.getTime());

        Calendar timeCalendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(timeCalendar.getTime());

        playerId = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = playerImageRef.child(imageUri.getLastPathSegment() + playerId + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                //creating reference to firebase storage
                StorageReference pathReference = storageRef.child("Player Images/"+imageUri.getLastPathSegment() + playerId + ".jpg");

                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //getting download url from firebase storage
                        downloadUrl = uri.toString();
                        //check size of team
                        checkTeamSize();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AddPlayer.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void checkTeamSize() {

        //first check if team is full or not
        teamCollection.document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Team team = documentSnapshot.toObject(Team.class);
                    teamSize = team.getTeamSize();
                    teamName = team.getTeamName();
                    if(teamSize==11) {
                        Toast.makeText(AddPlayer.this, "team is full...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    mProgressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(AddPlayer.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        savePlayerInfoToDatabase();
    }

    private void savePlayerInfoToDatabase() {
        //create hashmap to store player details
        HashMap<String,Object> playerMap = new HashMap<>();
        playerMap.put("playerId",playerId);
        playerMap.put("playerName",name);
        playerMap.put("playerAge",age);
        playerMap.put("playerPosition",position);
        playerMap.put("playerImage",downloadUrl);

        final HashMap<String,Object> teamMap = new HashMap<>();

        teamCollection.document(teamId).collection("Players").document(playerId).set(playerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    teamSize++;
                    teamMap.put("teamSize",teamSize);
                    teamCollection.document(teamId).update(teamMap);
                    Toast.makeText(AddPlayer.this, "player added...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),TeamDetails.class);
                    intent.putExtra("teamId",teamId);
                    intent.putExtra("teamName",teamName);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(AddPlayer.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            playerImage.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}