package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    private EditText number,password;
    private Button loginButton;
    private TextView user;
    private String userType = "";
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userType = getIntent().getStringExtra("userType");
        number = findViewById(R.id.number);
        password = findViewById(R.id.password);
        user = findViewById(R.id.user);
        loginButton = findViewById(R.id.loginButton);
        userCollection = db.collection(userType);
        mProgressDialog = new ProgressDialog(this);

        Paper.init(this);

        if(userType.equals("lManager")){
            user.setText("League Manager");
        }else{
            user.setText("Team Manager");
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = number.getText().toString().trim();
                String pass = password.getText().toString().trim();

                //check for empty inputs
                if(TextUtils.isEmpty(phone)){
                    number.setError("phone no is required...");
                    return;
                }else if(TextUtils.isEmpty(pass)){
                    password.setError("password is required");
                    return;
                }

                loginUser(phone,pass);
            }
        });

    }

    private void loginUser(String phone, String pass) {
        //show progress dialog
        mProgressDialog.setTitle("Login Account");
        mProgressDialog.setMessage("checking the credentials...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        //check if user is validate or not
        checkAccess(phone,pass);
    }

    private void checkAccess(final String phone, final String pass) {

        Paper.book().write(Prevelant.userPhone,phone);
        Paper.book().write(Prevelant.userPassword,pass);
        Paper.book().write(Prevelant.userType,userType);

        //create reference to firestore
        DocumentReference docIdRef = userCollection.document(phone);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                    DocumentSnapshot document = task.getResult();
                    //check if user exists or not
                    if (document.exists()) {
                        Users currentUser = document.toObject(Users.class);
                        if(pass.equals(currentUser.getPassword())){
                            //user logged in
                            Toast.makeText(Login.this, "Successfully Logged In...", Toast.LENGTH_SHORT).show();
                            Prevelant.currentUser = currentUser;
                            Prevelant.userType = userType;
                            Prevelant.userPhone = phone;
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("userType",userType);
                            startActivity(intent);

                        }else{
                            //wrong password
                            Toast.makeText(Login.this, "wrong password entered...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //user not exists
                        Toast.makeText(Login.this, "user does not exists...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //network error
                    mProgressDialog.dismiss();
                    Toast.makeText(Login.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}