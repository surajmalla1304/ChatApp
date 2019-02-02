package com.example.suraj.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsers extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;
    private DatabaseReference mUsersdatabase;
    private FirebaseUser mcurrentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = (Toolbar)findViewById(R.id.user_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        //mUsersdatabase.keepSynced(true);

        mUsersList = (RecyclerView)findViewById(R.id.users_v);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();



    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsersdatabase.child(mcurrentuser.getUid()).child("online").setValue(true);

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class, R.layout.users_single_layout, UsersViewHolder.class, mUsersdatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {

                UsersViewHolder.setName(model.getUsername());
                UsersViewHolder.setStatus(model.getStatus());
                UsersViewHolder.setUserImage(model.getThumb_nail(),getApplicationContext());

                final String user_id = getRef(position).getKey();

                UsersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(AllUsers.this,ProfileActivity.class);
                        i.putExtra("userid", user_id);
                        startActivity(i);

                    }
                });

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //mUsersdatabase.child(mcurrentuser.getUid()).child("online").setValue(false);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        static View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public static void setName(String name){
            TextView us =(TextView) mView.findViewById(R.id.textView3);
            us.setText(name);
        }

        public static  void setStatus(String status){

            TextView user_status_view = (TextView)mView.findViewById(R.id.textView4);
            user_status_view.setText(status);
        }

        public static void setUserImage(String thumb_image, Context context){

            CircleImageView UserImageView = (CircleImageView)mView.findViewById(R.id.circleImageView);

            Picasso.with(context).load(thumb_image).placeholder(R.drawable.empty_profile).into(UserImageView);

        }
    }


}
