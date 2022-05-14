package com.cbd.teammate.holders;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbd.database.entities.Activity;
import com.cbd.database.entities.Request;
import com.cbd.teammate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private Context context;
    private Button accButton;
    private Button decButton;

    public RequestViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        view = itemView;
        this.context = context;
    }

    public void setDetails(Request request) {

        TextView PlayerName = view.findViewById(R.id.requesterName);
        TextView requestLevel = view.findViewById(R.id.requestLevel);
        this.accButton = view.findViewById(R.id.accept);
        this.decButton = view.findViewById(R.id.decline);
        String pictureReference = request.getPlayer().getPhotoLink();
        TextView requestDescription = view.findViewById(R.id.requestDescription);
        CircleImageView imageView = view.findViewById(R.id.profileImage);
        TextView acSport = view.findViewById(R.id.activityRequestSport);
        TextView acDate = view.findViewById(R.id.activityRequestDate);
        if (pictureReference != null) {
            Picasso.get().load(pictureReference).into(imageView);
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.profile_icon_black));
        }
        acDate.setText(request.getActivity().getDate());
        acSport.setText(request.getActivity().getSport());
        PlayerName.setText(request.getPlayer().getName());
        requestDescription.setText(request.getDescription());
        requestLevel.setText(request.getPlayerType());
        accButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("requests")
                        .whereEqualTo("player", request.getPlayer())
                        .whereEqualTo("uid", request.getUid())
                        .whereEqualTo("accepted", false)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                DocumentReference docRef = db.collection("requests")
                                        .document(queryDocumentSnapshots.getDocuments().get(0).getId());
                                docRef.update("accepted", true);
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Activity a = documentSnapshot.toObject(Request.class).getActivity();
                                        db.collection("activities")
                                                .whereEqualTo("creatorId", a.getCreatorId())
                                                .whereEqualTo("date", a.getDate())
                                                .whereEqualTo("description", a.getDescription())
                                                .whereEqualTo("playersNeeded", a.getPlayersNeeded())
                                                .whereEqualTo("playerType", a.getPlayerType())
                                                .whereEqualTo("price", a.getPrice())
                                                .whereEqualTo("sport", a.getSport()).get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if (!queryDocumentSnapshots.isEmpty()) {
                                                            Activity activity = queryDocumentSnapshots.iterator().next().toObject(Activity.class);
                                                            activity.getPlayers().add(request.getPlayer().getUid());
                                                            String activityDocId = queryDocumentSnapshots.iterator().next().getId();
                                                            db.collection("activities").document(activityDocId)
                                                                    .update("players", activity.getPlayers());
                                                        }
                                                    }
                                                });
                                    }
                                });

                            }
                        });

            }
        });
        decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("requests")
                        .whereEqualTo("player", request.getPlayer())
                        .whereEqualTo("uid", request.getUid())
                        .whereEqualTo("accepted", false)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                DocumentReference docRef = db.collection("requests")
                                        .document(queryDocumentSnapshots.getDocuments().get(0).getId());
                                docRef.delete();
                                docRef.get();

                            }
                        });

            }
        });

    }

}
