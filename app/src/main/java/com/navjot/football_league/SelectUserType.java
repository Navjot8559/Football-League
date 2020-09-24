package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.navjot.football_league.Model.Users;
import com.navjot.football_league.Prevelant.Prevelant;

import io.paperdb.Paper;

public class SelectUserType extends AppCompatActivity {

    Button lManager,tManager,guest;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        lManager = findViewById(R.id.l_manager_btn);
        tManager = findViewById(R.id.t_manager_btn);
        guest = findViewById(R.id.guest_btn);
        mProgressDialog = new ProgressDialog(this);

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

        Paper.init(this);

        String userPhone = Paper.book().read(Prevelant.userPhone);
        String userPassword = Paper.book().read(Prevelant.userPassword);
        String userType = Paper.book().read(Prevelant.userType);

        if(userPhone != "" && userPassword != ""){
            if(!TextUtils.isEmpty(userPhone) && !TextUtils.isEmpty(userPassword)){
                mProgressDialog.setTitle("Already Logged In...");
                mProgressDialog.setMessage("please wait...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                checkAccess(userPhone,userPassword,userType);
            }
        }

    }

    private void checkAccess(String phone, final String password, final String userType) {
        userCollection = db.collection(userType);
        DocumentReference docIdRef = userCollection.document(phone);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                    DocumentSnapshot document = task.getResult();
                    //check if user exists
                    if (document.exists()) {
                        //get current user
                        Users currentUser = document.toObject(Users.class);
                        if(password.equals(currentUser.getPassword())){
                            //logged in
                            Toast.makeText(getApplicationContext(), "Successfully Logged In...", Toast.LENGTH_SHORT).show();
                            Prevelant.currentUser = currentUser;
                            Prevelant.userType = userType;
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("userType",userType);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"wrong password...",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"user not exists",Toast.LENGTH_LONG).show();
                    }
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error : please try again...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}