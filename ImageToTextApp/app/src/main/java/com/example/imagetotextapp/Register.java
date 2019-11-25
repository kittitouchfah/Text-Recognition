package com.example.imagetotextapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText etEmail, etPass;
    Button btRegister;

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        etEmail = (EditText) findViewById(R.id.etRemail);
        etPass = (EditText) findViewById(R.id.etRpass);
        btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegister();

            }
        });

    }

    private void UserRegister() {
        String email, pass;
        email = etEmail.getText().toString();
        pass = etPass.getText().toString();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)){
            Toast.makeText(Register.this, "Scan Fields are Empty", Toast.LENGTH_SHORT).show();
            return;
        }
       // progressDialog.setMessage("Registration ...");
      //  progressDialog.show();


        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Registration error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    public void LoginPage(View view){
        startActivity(new Intent(Register.this,Login.class));
    }
}