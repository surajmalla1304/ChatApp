package com.example.suraj.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar t6;
    private DatabaseReference mDatabase;
    private FirebaseUser mcurrentuser;
    private CircleImageView mimage;
    private TextView tv1,tv2;
    private Button b1,b2;
    private final static int Gallery_pic = 1;
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mimage = (CircleImageView)findViewById(R.id.settings_image);
        tv1 = (TextView)findViewById(R.id.textView5);
        tv2 = (TextView)findViewById(R.id.textView7);



        t6 = (Toolbar)findViewById(R.id.include2);

        setSupportActionBar(t6);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String current_u_id = mcurrentuser.getUid();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_u_id);
        mDatabase.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("username").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbnail = dataSnapshot.child("thumb_nail").getValue().toString();

                tv1.setText(name);
                tv2.setText(status);


                if(!image.equals("default")){ // bug2

                    /*
                    Picasso.with(SettingsActivity.this).load(image)
                            .placeholder(R.drawable.empty_profile) // bug3
                            .into(mimage);
                    */

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.empty_profile) // bug3
                            .into(mimage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(SettingsActivity.this).load(image)
                                            .placeholder(R.drawable.empty_profile) // bug3
                                            .into(mimage);

                                }
                            });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        b1 = (Button)findViewById(R.id.button5);
        b2 = (Button)findViewById(R.id.button6);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,Gallery_pic);  //calling mobile gallery

               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this); */

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i1 = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(i1);
            }
        });


    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == Gallery_pic && resultCode == RESULT_OK){
            Uri Imageuri = data.getData();

            CropImage.activity(Imageuri)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);

            //Toast.makeText(SettingsActivity.this,Imageuri, Toast.LENGTH_LONG).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                String  current_userid = mcurrentuser.getUid();

                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mStorageRef.child("profile_images").child(current_userid + ".jpg");
                final StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(current_userid + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap<>();
                                        update_hashMap.put("image",download_url);
                                        update_hashMap.put("thumb_nail", thumb_downloadUrl);

                                        mDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"Profile Picture Uploaded Successfully",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else{
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this,"Error Occured",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });



                        }else{
                            mProgressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this,"Error Occured",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("online").setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.child("online").setValue(false);
    }
}
