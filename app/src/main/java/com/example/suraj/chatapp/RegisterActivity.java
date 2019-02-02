package com.example.suraj.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText ed1,ed2,ed3;
    private Button b1;
    private FirebaseAuth mAuth;
    private Toolbar t2;
    private ProgressDialog mregprogress;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public Users us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        ed1 = (EditText)findViewById(R.id.e_uname);
        ed2 = (EditText)findViewById(R.id.e_email);
        ed3 = (EditText)findViewById(R.id.e_password);
        mregprogress = new ProgressDialog(this);

        b1 = (Button)findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ed1.getText().toString();
                String email = ed2.getText().toString();
                String password = ed3.getText().toString();
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

                    Toast.makeText(RegisterActivity.this,"Please enter the Details",Toast.LENGTH_SHORT).show();

                }else{
                    mregprogress.setTitle("Registering User");
                    mregprogress.setMessage("Please wait while we create your Account");
                    mregprogress.setCanceledOnTouchOutside(false);
                    mregprogress.show();
                    us = new Users(username);
                    register(username,email,password);
                }

            }
        });

        t2 = (Toolbar)findViewById(R.id.bar1);
        setSupportActionBar(t2);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void register(String u, String e, String p){
        mAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String u_id = current_user.getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    myRef = database.getReference().child("Users").child(u_id);

                    myRef.setValue(us).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String currend_uid = mAuth.getCurrentUser().getUid();

                                myRef.child("device_token").setValue(deviceToken);
                                mregprogress.dismiss();

                                Toast.makeText(RegisterActivity.this,"Registration Successfull",Toast.LENGTH_SHORT).show();
                                Intent i1 = new Intent(RegisterActivity.this,MainActivity.class);
                                i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i1);
                                finish();
                            }
                        }
                    });



                }else{

                    mregprogress.hide();

                    Toast.makeText(RegisterActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
