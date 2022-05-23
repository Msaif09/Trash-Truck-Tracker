package com.saif.trashtrucktracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class DriverRagistrationActivity extends AppCompatActivity {

    private EditText driverEmailReg;
    private EditText driverEmailPass;
    private EditText driverEmailPassMatch;
    private Button driverRegBtn;
    private TextView goToDriverLogin;
    private ImageView driverGoogleSignIn;
    private ImageView driverFacebookSignIn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ragistration);

        driverEmailReg=findViewById(R.id.driverRegistrationEmail);
        driverEmailPass=findViewById(R.id.driverRegistrationPass);
        driverEmailPassMatch=findViewById(R.id.driverRegistrationPassMatch);
        driverRegBtn=findViewById(R.id.Driver_Registartion_Btn);
        goToDriverLogin=findViewById(R.id.goToDriverLogin);
        driverGoogleSignIn=findViewById(R.id.driverGoogleSignin);
        driverFacebookSignIn=findViewById(R.id.driverFacebookSignin);
        mAuth=FirebaseAuth.getInstance();

        driverGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverRagistrationActivity.this,GoogleSignin.class);
                startActivity(intent);
            }
        });
        goToDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverRagistrationActivity.this,DriverLoginRagisterActivity.class));
            }
        });

        driverRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                creatUser();
            }
        });

    }

    private void creatUser() {
        String Email=driverEmailReg.getText().toString();
        String Pass=driverEmailPass.getText().toString();
        String PassMatch=driverEmailPassMatch.getText().toString();
        if (TextUtils.isEmpty(Email)){
            Toast.makeText(this, "Email Can Not Be Empty", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(Pass)){
            Toast.makeText(this, "Password Can Not Be Empty", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(PassMatch)){
            Toast.makeText(this, "Re-Enter Password Can Not Be Empty", Toast.LENGTH_SHORT).show();
        }else {
            if (Pass.length()==PassMatch.length()){
                mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(DriverRagistrationActivity.this, "User Register Sucessesfully", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(DriverRagistrationActivity.this,DriverMapActivity.class));
                       }else {
                           Toast.makeText(DriverRagistrationActivity.this, "Something Went Wrong" + task.getException(), Toast.LENGTH_SHORT).show();
                       }
                    }
                });
            }else {
                Toast.makeText(this, "Match Password Can Both Field Password", Toast.LENGTH_SHORT).show();

            }
        }

    }
}