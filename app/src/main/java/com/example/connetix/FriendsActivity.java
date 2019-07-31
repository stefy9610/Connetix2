package com.example.connetix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList = (RecyclerView) findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        myFriendList.setLayoutManager(linearLayoutManager);

//        DisplayAllFriends();
    }

    @Override
    protected void onStart() {

        super.onStart();

       FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter =
               new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>() {
                   @Override
                   protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friends model) {

                       holder.friendsDate.setText(model.getDate());

                       final String usersIDs = getRef(position).getKey();

                       UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if(dataSnapshot.exists()) {

                                   final String userName = dataSnapshot.child("fullname").getValue().toString();
                                   final String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                                   holder.fullName.setText(model.getFullname());
                                   holder.mView.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           CharSequence options[] = new CharSequence[]
                                                   {
                                                           userName + "'s Profile",
                                                           "Send Message"
                                                   };
                                           AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                                           builder.setTitle("Select Option");
                                           builder.setItems(options, new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   if (which == 0) {
                                                        Intent profileIntent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
                                                        profileIntent.putExtra("visit_user_id", usersIDs);
                                                        startActivity(profileIntent);
                                                   }

                                                   if (which == 1) {
                                                       Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                                                       chatIntent.putExtra("visit_user_id", usersIDs);
                                                       startActivity(chatIntent);
                                                   }
                                               }
                                           });


                                       }
                                   });


                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });

                   }

                   @NonNull
                   @Override
                   public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_users_display_layout, viewGroup,false);
                       FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                       return viewHolder;
                   }

               };
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView fullName, friendsDate;
        CircleImageView profileImage;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            friendsDate = itemView.findViewById(R.id.all_users_status);
            fullName = itemView.findViewById(R.id.all_users_profile_name);
            profileImage = itemView.findViewById(R.id.all_users_profile_image);

        }

    }
}
