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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddManager extends AppCompatActivity {

    private CircleImageView managerImage;
    private EditText managerName,managerPhone,managerPassword,managerTeam;
    private Button addManager;
    private Uri imageUri;
    private static final int galleryPick = 1;
    private StorageReference managerImageRef;
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("tManager");
    private ProgressDialog mProgressDialog;
    private String downloadUrl,managerId,name,phone,password,teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manager);

        getSupportActionBar().setTitle("Add Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        managerImage = findViewById(R.id.manager_image);
        managerName = findViewById(R.id.manager_name);
        managerPhone = findViewById(R.id.manager_phone);
        managerPassword = findViewById(R.id.manager_password);
        managerTeam = findViewById(R.id.manager_team);
        addManager = findViewById(R.id.add_manager);
        mProgressDialog = new ProgressDialog(this);
        managerImageRef = FirebaseStorage.getInstance().getReference().child("Manager Images");
        storageRef = FirebaseStorage.getInstance().getReference();

        managerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        
        addManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDetails();
            }
        });

    }

    private void validateDetails() {
        name = managerName.getText().toString().trim();
        phone = managerPhone.getText().toString().trim();
        password = managerPassword.getText().toString().trim();
        teamName = managerTeam.getText().toString().trim();

        if(imageUri == null) {
            Toast.makeText(getApplicationContext(), "image is required...", Toast.LENGTH_LONG).show();
            return;
        }else if(TextUtils.isEmpty(name)){
            managerName.setError("Name is required...");
            return;
        }else if(TextUtils.isEmpty(phone)){
            managerPhone.setError("phone is required...");
            return;
        }else if(TextUtils.isEmpty(password)){
            managerPassword.setError("password is required...");
            return;
        }else if(TextUtils.isEmpty(teamName)){
            managerTeam.setError("team name is reuired...");
            return;
        }

        storeManagerInfo();
    }

    private void storeManagerInfo() {
        mProgressDialog.setTitle("Add Manager");
        mProgressDialog.setMessage("Adding manager details, please wait...");
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

        managerId = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = managerImageRef.child(imageUri.getLastPathSegment() + managerId + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                //creating reference to firebase storage
                StorageReference pathReference = storageRef.child("Manager Images/"+imageUri.getLastPathSegment() + managerId + ".jpg");

                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //getting download url from firebase storage
                        downloadUrl = uri.toString();
                        //save player info
                        addManagerDetails();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AddManager.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void addManagerDetails() {
        final HashMap<String,Object> managerMap = new HashMap<>();
        managerMap.put("name",name);
        managerMap.put("phone",phone);
        managerMap.put("password",password);
        managerMap.put("teamName",teamName);
        managerMap.put("managerImage",downloadUrl);

       managerRef.document(phone).set(managerMap).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               mProgressDialog.dismiss();
               Toast.makeText(AddManager.this, "Manager Added...", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(getApplicationContext(),TeamManagers.class));
               finish();
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               mProgressDialog.dismiss();
               Toast.makeText(AddManager.this,e.getMessage(), Toast.LENGTH_SHORT).show();
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
            managerImage.setImageURI(imageUri);
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