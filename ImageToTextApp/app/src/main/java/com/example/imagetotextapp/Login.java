package com.example.imagetotextapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    Button btLogin,btRegister;
    EditText etEmail, etPass;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btRegister =(Button)findViewById(R.id.bTLRegister);
        btLogin =(Button)findViewById(R.id.btLlogin);
        etEmail = (EditText) findViewById(R.id.eTLemail);
        etPass = (EditText) findViewById(R.id.eTLpass);
        mAuth = FirebaseAuth.getInstance();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));

            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signin();


            }
        });
        }

        public void Signin(){
            final String email,pass;
            email = etEmail.getText().toString();
            pass = etPass.getText().toString();
            if(TextUtils.isEmpty(email)){
                Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(pass)){
                Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Welcome "+email, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,MainActivity.class));
                            } else {
                                Toast.makeText(Login.this, "Login error", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        }

    }


