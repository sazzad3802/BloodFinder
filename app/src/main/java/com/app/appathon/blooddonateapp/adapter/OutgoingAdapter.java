package com.app.appathon.blooddonateapp.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by IMRAN on 8/25/2017.
 */

public class OutgoingAdapter extends RecyclerView.Adapter<OutgoingAdapter.ViewHolder> {

    private final FirebaseUser firebaseUser;
    private ArrayList<Inbox> inboxArrayList;
    private Activity activity;
    private ArrayList<User> userArrayList;

    public OutgoingAdapter(ArrayList<User> userArrayList, ArrayList<Inbox> inboxArrayList, Activity activity) {
        this.inboxArrayList = inboxArrayList;
        this.activity = activity;
        this.userArrayList = userArrayList;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outbox, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.sender_name.setText(userArrayList.get(position).getName());
        holder.msg_thumb.setText(String.valueOf(userArrayList.get(position).getName().charAt(0)));
        holder.msg_time.setText(inboxArrayList.get(position).getSendTime());
        holder.msg_count.setText(String.valueOf(inboxArrayList.get(position).getCount()));
        holder.msg_body.setText(inboxArrayList.get(position).getMessage());

        final String uid = userArrayList.get(position).getId();
        String name = userArrayList.get(position).getName();
        String bld = userArrayList.get(position).getBloodGroup();

        final String data = name + "|" + bld;

        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(firebaseUser.getUid())
                        .child("favorites").child(uid).setValue(data);

                Toast.makeText(activity, "Added to Favorite", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView msg_body;
        private TextView sender_name;
        private TextView msg_thumb;
        private TextView msg_time;
        private TextView msg_count;
        private CardView cardView;
        private Button favButton;

        ViewHolder(View itemView) {
            super(itemView);

            msg_body = (TextView) itemView.findViewById(R.id.msg_body);
            sender_name = (TextView) itemView.findViewById(R.id.sender_name);
            msg_thumb = (TextView) itemView.findViewById(R.id.msg_thumb);
            msg_time = (TextView) itemView.findViewById(R.id.sending_time);
            msg_count = (TextView) itemView.findViewById(R.id.msg_count);
            cardView = (CardView) itemView.findViewById(R.id.inbox_card);
            favButton = (Button) itemView.findViewById(R.id.btn_fav);
        }
    }
}
