package com.app.appathon.blooddonateapp.adapter;

/**
 * Created by IMRAN on 3/3/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.UserProfileActivity;
import com.app.appathon.blooddonateapp.model.FavoriteModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by IMRAN on 8/25/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private final FirebaseUser firebaseUser;
    private ArrayList<FavoriteModel> favoriteModelArrayList;
    private Activity activity;

    public FavoriteAdapter(ArrayList<FavoriteModel> inboxArrayList, Activity activity) {
        this.favoriteModelArrayList = inboxArrayList;
        this.activity = activity;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String name = favoriteModelArrayList.get(position).getName();
        String blood = favoriteModelArrayList.get(position).getBlood();
        holder.sender_name.setText(name);
        holder.msg_thumb.setText(blood);

        final String uid = favoriteModelArrayList.get(position).getUid();
        holder.msg_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete(uid);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UserProfileActivity.class);
                intent.putExtra("id", uid);
                activity.startActivity(intent);
            }
        });
    }

    private void onDelete(final String uid) {
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(activity)
                .title("Are you sure?")
                .iconRes(R.drawable.ic_delete_pressed)
                .backgroundColorRes(android.R.color.white)
                .titleColorRes(R.color.dialog_color)
                .negativeText("Cancel")
                .positiveText("Delete")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        database.child("users").child(firebaseUser.getUid()).child("favorites").child(uid).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(activity, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return favoriteModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sender_name;
        private TextView msg_thumb;
        private TextView msg_count;
        private CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            sender_name = (TextView) itemView.findViewById(R.id.fav_name);
            msg_thumb = (TextView) itemView.findViewById(R.id.fav_thumb);
            msg_count = (TextView) itemView.findViewById(R.id.fav_delete);
            cardView = (CardView) itemView.findViewById(R.id.fav_card);
        }
    }
}

