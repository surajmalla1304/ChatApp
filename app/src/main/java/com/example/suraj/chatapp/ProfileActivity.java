package com.example.suraj.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mprofilei;
    private TextView mprofilen, mprofiles, mprofilec;
    private Button br,bd;
    private DatabaseReference usersdatabase,mfriendrequest,friendlist, mnotification;
    private ProgressDialog pm;
    private String current_state ;
    private FirebaseUser mcurrentuser;
    private DatabaseReference mrootref,mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       final String u_id = getIntent().getStringExtra("userid");

       mrootref = FirebaseDatabase.getInstance().getReference();

       mprofilei = (ImageView)findViewById(R.id.imageView);
       mprofilen = (TextView)findViewById(R.id.textView6);
       mprofiles = (TextView)findViewById(R.id.textView8);
       mprofilec = (TextView)findViewById(R.id.textView9);
       br = (Button)findViewById(R.id.button8);
       bd = (Button)findViewById(R.id.button9);

       mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
       current_state = "notfriend";

        bd.setVisibility(View.INVISIBLE);
        bd.setEnabled(false);

        pm = new ProgressDialog(this);
        pm.setTitle("Loading Profile");
        pm.setMessage("Please Wait....");
        pm.setCanceledOnTouchOutside(false);
        pm.show();

       usersdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(u_id);
       mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mcurrentuser.getUid());
       mfriendrequest = FirebaseDatabase.getInstance().getReference().child("Friend Request");
       friendlist = FirebaseDatabase.getInstance().getReference().child("Friends");
       mnotification = FirebaseDatabase.getInstance().getReference().child("notifications");


       usersdatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               String dispay_name = dataSnapshot.child("username").getValue().toString();
               String status = dataSnapshot.child("status").getValue().toString();
               String image = dataSnapshot.child("image").getValue().toString();

               mprofilen.setText(dispay_name);
               mprofiles.setText(status);

               Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.empty_profile).into(mprofilei);

               // ---------------- Friends List / Request Feature

               mfriendrequest.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {

                       if(dataSnapshot.hasChild(u_id)){
                           String req_type = dataSnapshot.child(u_id).child("request_type").getValue().toString();

                           if(req_type.equals("received")){

                               current_state = "request_received";
                               br.setText("Accept Friend Request");

                               bd.setVisibility(View.VISIBLE);
                               bd.setEnabled(true);

                           }else if(req_type.equals("sent")){

                               current_state  = "request sent";
                               br.setText("Cancel Friend Request");

                               bd.setVisibility(View.INVISIBLE);
                               bd.setEnabled(false);

                           }

                           pm.dismiss();


                       }else{

                           friendlist.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {

                                   if(dataSnapshot.hasChild(u_id)){

                                       current_state = "Friends";
                                       br.setText("UnFriend");

                                       bd.setVisibility(View.INVISIBLE);
                                       bd.setEnabled(false);

                                   }

                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });

                       }



                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });

               pm.dismiss();


           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

       br.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               br.setEnabled(false);

               //    not friends state

               if(current_state.equals("notfriend")){

                   DatabaseReference newNotificationref = mrootref.child("notifications").child(u_id).push();
                   String newNotificationsId = newNotificationref.getKey();

                   final HashMap<String, String>NotificationData = new HashMap<>();
                   NotificationData.put("from", mcurrentuser.getUid());
                   NotificationData.put("type", "request");

                   Map requestMap = new HashMap();
                   requestMap.put("Friend Request/" + mcurrentuser.getUid() + "/" + u_id + "/request_type", "sent");
                   requestMap.put("Friend Request/" + u_id + "/" + mcurrentuser.getUid() + "/request_type", "received");
                   requestMap.put("notifications/" + u_id + "/" + newNotificationsId, NotificationData);

                   mrootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                       @Override
                       public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                           if(databaseError != null){
                               Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_LONG);
                           }

                           br.setEnabled(true);


                           current_state = "request sent";
                           br.setText("Cancel Friend Request");

                       }
                   });

               }

               //           cancel request state

               if(current_state.equals("request sent")){

                   mfriendrequest.child(mcurrentuser.getUid()).child(u_id).removeValue()
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           mfriendrequest.child(u_id).child(mcurrentuser.getUid()).removeValue()
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   br.setEnabled(true);
                                   current_state = "notfriend";
                                   br.setText("Send Friend Request");

                                   bd.setVisibility(View.INVISIBLE);
                                   bd.setEnabled(false);

                                 //  Toast.makeText(ProfileActivity.this, "Request not Sent", Toast.LENGTH_LONG).show();

                               }
                           });
                       }
                   });


               }

               //         Request received state

               if(current_state.equals("request_received")){

                   final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                   friendlist.child(mcurrentuser.getUid()).child(u_id).child("Date").setValue(currentDate)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           friendlist.child(u_id).child(mcurrentuser.getUid()).child("Date").setValue(currentDate)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           mfriendrequest.child(mcurrentuser.getUid()).child(u_id).removeValue()
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           mfriendrequest.child(u_id).child(mcurrentuser.getUid()).removeValue()
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {

                                                                           br.setEnabled(true);
                                                                           current_state = "Friends";
                                                                           br.setText("UnFriend");

                                                                           bd.setVisibility(View.INVISIBLE);
                                                                           bd.setEnabled(false);

                                                                           Toast.makeText(ProfileActivity.this, "You are now Friends with this User",
                                                                                   Toast.LENGTH_LONG).show();

                                                                       }
                                                                   });
                                                       }
                                                   });

                                       }
                                   });

                       }
                   });

               }

               //  unfriend

               if(current_state.equals("Friends")){

                   friendlist.child(mcurrentuser.getUid()).child(u_id).removeValue()
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   friendlist.child(u_id).child(mcurrentuser.getUid()).removeValue()
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {

                                                   br.setEnabled(true);
                                                   current_state = "notfriend";
                                                   br.setText("Send Friend Request");

                                                   bd.setVisibility(View.INVISIBLE);
                                                   bd.setEnabled(false);

                                                   Toast.makeText(ProfileActivity.this, "Successfull", Toast.LENGTH_LONG).show();

                                               }
                                           });
                               }
                           });

               }

           }
       });

       bd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Map cancel_request = new HashMap();

               cancel_request.put("Friend Request/" + mcurrentuser.getUid() + "/" + u_id, null);
               cancel_request.put("Friend_Request/" + u_id + "/" + mcurrentuser.getUid(), null);

               mrootref.updateChildren(cancel_request, new DatabaseReference.CompletionListener() {
                   @Override
                   public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                       if(databaseError == null){

                           current_state = "notfriend";
                           br.setText("Send Friend Request");

                           bd.setVisibility(View.INVISIBLE);
                           bd.setEnabled(false);

                       }else{

                           String error = databaseError.getMessage();

                           Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();

                       }

                       br.setEnabled(true);

                   }
               });


               mfriendrequest.child(mcurrentuser.getUid()).child(u_id).removeValue()
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               mfriendrequest.child(u_id).child(mcurrentuser.getUid()).removeValue()
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {

                                               br.setEnabled(true);
                                               current_state = "notfriend";
                                               br.setText("Send Friend Request");

                                               bd.setVisibility(View.INVISIBLE);
                                               bd.setEnabled(false);

                                               //  Toast.makeText(ProfileActivity.this, "Request not Sent", Toast.LENGTH_LONG).show();

                                           }
                                       });
                           }
                       });
           }
       });


    }

    public void onStart(){
        super.onStart();

        mUserRef.child("online").setValue(true);
    }

    public void onStop(){
        super.onStop();

        mUserRef.child("online").setValue(false);
    }
}
