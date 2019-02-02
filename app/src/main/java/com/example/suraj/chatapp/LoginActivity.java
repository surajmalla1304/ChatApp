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

public class LoginActivity extends AppCompatActivity {
    private Toolbar t3;
    private Button b1;
    private EditText ed1,ed2;
    private ProgressDialog p1;
    private FirebaseAuth mAuth;
    private DatabaseReference userdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        t3 = (Toolbar)findViewById(R.id.bar2);
        setSupportActionBar(t3);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);
        b1 = (Button)findViewById(R.id.button4);
        userdatabase = FirebaseDatabase.getInstance().getReference().child("Users");



        p1 = new ProgressDialog(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = ed1.getText().toString();
                String Password = ed2.getText().toString();

                if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
                    Toast.makeText(LoginActivity.this,"Enter the Details",Toast.LENGTH_SHORT).show();
                }else{
                    p1.setTitle("Logging In");
                    p1.setMessage("Please wait while we validate your credentials");
                    p1.setCanceledOnTouchOutside(false);
                    p1.show();
                    loginuser(Email,Password);
                }
            }
        });
    }

    private void loginuser(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            p1.dismiss();

                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String currend_uid = mAuth.getCurrentUser().getUid();

                            userdatabase.child(currend_uid).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Intent ie = new Intent(LoginActivity.this,MainActivity.class);

                                    //bug 1
                                    ie.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(ie);
                                    finish();
                                    //updateUI(user);

                                }
                            });



                        } else {
                            p1.hide();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }
}

