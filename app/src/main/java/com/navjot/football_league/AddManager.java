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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddManager extends AppCompatActivity {
    
    private EditText managerName,managerPhone,managerPassword,managerTeam;
    private Button addManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("tManager");
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manager);

        getSupportActionBar().setTitle("Add Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        managerName = findViewById(R.id.manager_name);
        managerPhone = findViewById(R.id.manager_phone);
        managerPassword = findViewById(R.id.manager_password);
        managerTeam = findViewById(R.id.manager_team);
        addManager = findViewById(R.id.add_manager);
        mProgressDialog = new ProgressDialog(this);
        
        addManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDetails();
            }
        });

    }

    private void validateDetails() {
        String name = managerName.getText().toString().trim();
        String phone = managerPhone.getText().toString().trim();
        String password = managerPassword.getText().toString().trim();
        String teamName = managerTeam.getText().toString().trim();
        
        if(TextUtils.isEmpty(name)){
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
        
        addManagerDetails(name,phone,password,teamName);
    }

    private void addManagerDetails(String name,String phone,String password,String teamName) {
        mProgressDialog.setTitle("Add Manager");
        mProgressDialog.setMessage("Adding manager details, please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        final HashMap<String,Object> managerMap = new HashMap<>();
        managerMap.put("name",name);
        managerMap.put("phone",phone);
        managerMap.put("password",password);
        managerMap.put("teamName",teamName);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}