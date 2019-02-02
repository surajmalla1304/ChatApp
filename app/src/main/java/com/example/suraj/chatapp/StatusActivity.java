package com.example.suraj.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText ed;
    private Button b;
    private DatabaseReference ref;
    private String sta;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar = (Toolbar)findViewById(R.id.bar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ed = (EditText)findViewById(R.id.editText3);

        b = (Button)findViewById(R.id.button7);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                sta = ed.getText().toString();
                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String u_id = current_user.getUid();
                ref = FirebaseDatabase.getInstance().getReference().child("Users").child(u_id).child("status");
                ref.setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                            Intent i = new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Not Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });
    }
}
