package com.example.suraj.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button regis,bn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        regis = (Button)findViewById(R.id.b_reg);
        bn = (Button)findViewById(R.id.button3);
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ie = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(ie);
            }
        });
    }
}