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

public class DriverLoginRagisterActivity extends AppCompatActivity {

    private EditText driverEmail;
    private EditText driverPass;
    private Button driverLoginBtn;
    private TextView goToDriverRagistration;
    private ImageView driverGoogleLogin;
    private ImageView driverFacebookLogin;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_ragister);

        driverEmail=findViewById(R.id.driverLoginEmail);
        driverPass=findViewById(R.id.driverLoginPass);
        driverLoginBtn=findViewById(R.id.Driver_Login_Btn);
        goToDriverRagistration=findViewById(R.id.goToDriverRagistration);
        driverGoogleLogin=findViewById(R.id.driverGoogleLogin);
        driverFacebookLogin=findViewById(R.id.driverFacebookLogin);

        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        goToDriverRagistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverLoginRagisterActivity.this,DriverRagistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        driverGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverLoginRagisterActivity.this,GoogleSignin.class);
                startActivity(intent);
            }
        });

        driverLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=driverEmail.getText().toString();
                String pass=driverPass.getText().toString();

                LoginDriver(email,pass);
            }
        });

    }

    private void LoginDriver(String email, String pass) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please Enter Email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(), "Please Enter Password...", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setTitle("Driver Login");
            progressDialog.setMessage("Please wait while we are checking your data");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Driver Login Successesfull...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        updateUI(mUser);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Login unSuccessesfull Please Try Again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }
    private void updateUI(FirebaseUser user)
    {
        Intent intent=new Intent(DriverLoginRagisterActivity.this,DriverMapActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser!=null) {
            updateUI(mUser);
        }
    }
}