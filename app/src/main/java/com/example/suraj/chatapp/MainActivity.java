package com.example.suraj.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private ViewPager vp;
    private Toolbar t1;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DatabaseReference mUserRef;

    private TabLayout mtablayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        t1 = (android.support.v7.widget.Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(t1);
        getSupportActionBar().setTitle("Chat App");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        //Tabs

        vp = (ViewPager)findViewById(R.id.tabPager);
        mtablayout = (TabLayout)findViewById(R.id.main_tabs);

        //Adding fragments

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.AddFragment(new RequestsFragment(),"REQUESTS");
        mSectionsPagerAdapter.AddFragment(new ChatsFragment(),"CHATS");
        mSectionsPagerAdapter.AddFragment(new FriendsFragment(),"FRIENDS");

        vp.setAdapter(mSectionsPagerAdapter);

        mtablayout.setupWithViewPager(vp);

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        if(currentUser == null){
            Intent i = new Intent(MainActivity.this,StartActivity.class);

            startActivity(i);
            finish();
        }else{
            mUserRef.child("online").setValue(true);
        }
    }

    public void onStop(){
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this,StartActivity.class);
            startActivity(i);
            finish();
        }
        if(item.getItemId() == R.id.main_setting){
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.main_All_Users){
            Intent i2 = new Intent(MainActivity.this,AllUsers.class);
            startActivity(i2);
        }
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

