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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.navjot.football_league.Model.Users;
import com.navjot.football_league.Prevelant.Prevelant;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeam extends AppCompatActivity {

    private ImageView logo;
    private EditText phone;
    private Button addTeam;
    private String teamId,teamName,managerName,teamLogo;
    private int wins,loses,ties,points;
    private Uri imageUri;
    private static final int galleryPick = 1;
    private ProgressDialog mProgressDialog;
    private StorageReference teamLogoRef;
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerCollection = db.collection("tManager");
    private CollectionReference teamCollection = db.collection("Teams");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        getSupportActionBar().setTitle("Add Team");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        logo = findViewById(R.id.team_logo);
        phone = findViewById(R.id.manager_phone);
        addTeam = findViewById(R.id.add_team_btn);
        mProgressDialog = new ProgressDialog(this);
        teamLogoRef = FirebaseStorage.getInstance().getReference().child("Team Logos");
        storageRef = FirebaseStorage.getInstance().getReference();

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openGallery();
            }
        });

        addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               teamId = phone.getText().toString().trim();

                if(imageUri == null){
                    Toast.makeText(getApplicationContext(),"image is required...",Toast.LENGTH_LONG).show();
                    return;
                }else if(TextUtils.isEmpty(teamId)){
                    phone.setError("number is required...");
                    return;
                }

                checkManagerNumber();
            }
        });
    }

    private void checkManagerNumber() {

        //show progress dialog when add button is clicked
        mProgressDialog.setTitle("Checking Info");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        //create reference to firestore
        DocumentReference docIdRef = managerCollection.document(teamId);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                    DocumentSnapshot document = task.getResult();
                    //check if user exists or not
                    if (document.exists()) {
                        Users manager = document.toObject(Users.class);
                        teamName = manager.getTeamName();
                        managerName = manager.getName();
                        storeTeamInfo();
                    } else {
                        //user not exists
                        Toast.makeText(AddTeam.this, "team manager does not exists...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //network error
                    mProgressDialog.dismiss();
                    Toast.makeText(AddTeam.this, "Error : please try again...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeTeamInfo() {

        //show progress dialog when add button is clicked
        mProgressDialog.setTitle("Adding Team");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        final StorageReference filePath = teamLogoRef.child(imageUri.getLastPathSegment() + teamId + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(AddTeam.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                //creating reference to firebase storage
                StorageReference pathReference = storageRef.child("Team Logos/"+imageUri.getLastPathSegment() + teamId + ".jpg");

                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //getting download url from firebase storage
                        teamLogo = uri.toString();
                        //save details to firestore
                        saveProductInfoToDatabase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTeam.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void saveProductInfoToDatabase() {
        //create hashmap to store product details
        HashMap<String,Object> teamMap = new HashMap<>();
        teamMap.put("teamId",teamId);
        teamMap.put("teamName",teamName);
        teamMap.put("managerName",managerName);
        teamMap.put("teamLogo",teamLogo);
        teamMap.put("wins",wins);
        teamMap.put("loses",loses);
        teamMap.put("ties",ties);
        teamMap.put("points",points);

        //set hashmap to firestore reference
        teamCollection.document(teamId).set(teamMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Toast.makeText(AddTeam.this, "team added successfully...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),Teams.class));
                    finish();
                }else{
                    mProgressDialog.dismiss();
                    Toast.makeText(AddTeam.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
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
            logo.setImageURI(imageUri);

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