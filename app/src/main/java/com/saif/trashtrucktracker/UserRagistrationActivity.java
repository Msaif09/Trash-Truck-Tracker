package com.saif.trashtrucktracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRagistrationActivity extends AppCompatActivity {

    private EditText userEmailReg;
    private EditText userEmailPass;
    private EditText userEmailPassMatch;
    private Button userRegBtn;
    private TextView goToUserLogin;
    private ImageView userGoogleSignIn;
    private ImageView userFacebookSignIn;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ragistration);

        userEmailReg=findViewById(R.id.userRegistrationEmail);
        userEmailPass=findViewById(R.id.userRegistrationPass);
        userEmailPassMatch=findViewById(R.id.userRegistrationPassMatch);
        userRegBtn=findViewById(R.id.User_Registartion_Btn);
        goToUserLogin=findViewById(R.id.goToUserLogin);
        userGoogleSignIn=findViewById(R.id.userGoogleSignin);
        userFacebookSignIn=findViewById(R.id.userFacebookSignin);

        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        goToUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserRagistrationActivity.this,userGoogleSignin.class);
                startActivity(intent);
                finish();
            }
        });
        userGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserRagistrationActivity.this,userGoogleSignin.class);
                startActivity(intent);
                finish();
            }
        });

        userRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=userEmailReg.getText().toString();
                String pass=userEmailPass.getText().toString();
                String passMatch=userEmailPassMatch.getText().toString();

                if (pass.length()==passMatch.length()){

                    RegisterUser(Email,pass);

                }else
                {
                    Toast.makeText(getApplicationContext(), "Match Field Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void RegisterUser(String email, String pass)
    {

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please Enter Email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(), "Please Enter Password...", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setTitle("User Registration");
            progressDialog.setMessage("Please wait while we are register your data");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "User Register Successesfull...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        updateUI(mUser);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Registration unSuccessesfull Please Try Again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }

    }
    private void updateUI(FirebaseUser user)
    {
        Intent intent=new Intent(UserRagistrationActivity.this,UserMapActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}