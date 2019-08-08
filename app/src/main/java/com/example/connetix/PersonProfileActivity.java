package com.example.connetix;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    final static int Gallery_Pick = 1;
    private TextView userName, userProfileName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestButton, DeclineFriendRequestButton;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef, NotificationRef;
    private FirebaseAuth mAuth;

    private String senderUserId, receiverUserId, current_state ,saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        InitializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@" + myUserName);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: " + myDOB);
                    userCountry.setText("Country: " + myCountry);
                    userGender.setText("Gender: " + myGender);
                    userRelation.setText("Relationship Status: " + myRelationStatus);

                    MaintenanceOfButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);

        if(!senderUserId.equals(receiverUserId)) {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendRequestButton.setEnabled(false);
                    if(current_state.equals("not_friends")) {
                        SendFriendRequestToPerson();
                    }
                    if(current_state.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                    if(current_state.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if(current_state.equals("friends")) {
                        UnfriendExistingFriend();
                    }
                }
            });
        }
        else {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void UnfriendExistingFriend() {
        FriendsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FriendsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                SendFriendRequestButton.setEnabled(true);
                                                current_state = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        FriendsRef.child(senderUserId)
                .child(receiverUserId).child("date")
                .setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserId)
                                    .child(senderUserId).child("date")
                                    .setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()) {
                                                                                        SendFriendRequestButton.setEnabled(true);
                                                                                        current_state = "friends";
                                                                                        SendFriendRequestButton.setText("Unfriend this Person");

                                                                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendRequestButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                SendFriendRequestButton.setEnabled(true);
                                                current_state = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintenanceOfButtons() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)) {
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if(request_type.equals("sent")) {
                                current_state = "request_sent";
                                SendFriendRequestButton.setText("Cancel Friend Request");

                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }
                            else if(request_type.equals("received")) {
                                current_state = "request_received";
                                SendFriendRequestButton.setText("Accept Friend Request");

                                DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestButton.setEnabled(true);

                                DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }

                        }
                        else {
                            FriendsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserId)) {
                                                current_state = "friends";
                                                SendFriendRequestButton.setText("Unfriend this Person");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                HashMap<String, String> newFriendNotificationMap = new HashMap<>();
                                                newFriendNotificationMap.put("from", senderUserId);
                                                newFriendNotificationMap.put("type", "request");

                                                NotificationRef.child(receiverUserId).push()
                                                        .setValue(newFriendNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    SendFriendRequestButton.setEnabled(true);
                                                                    current_state = "request_sent";
                                                                    SendFriendRequestButton.setText("Cancel friend request");

                                                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                    DeclineFriendRequestButton.setEnabled(false);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitializeFields() {

        userName = (TextView) findViewById(R.id.person_username);
        userProfileName = (TextView) findViewById(R.id.person_full_name);
        userStatus = (TextView) findViewById(R.id.person_status);
        userCountry = (TextView) findViewById(R.id.person_country);
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation = (TextView) findViewById(R.id.person_gender);
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);

        SendFriendRequestButton = (Button) findViewById(R.id.person_send_friend_request);
        DeclineFriendRequestButton = (Button) findViewById(R.id.person_decline_friend_request);

        current_state = "not_friends";


    }
}
