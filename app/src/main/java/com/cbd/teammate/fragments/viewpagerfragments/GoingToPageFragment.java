package com.cbd.teammate.fragments.viewpagerfragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbd.database.entities.Activity;
import com.cbd.teammate.R;
import com.cbd.teammate.fragments.ActivityViewFragment;
import com.cbd.teammate.holders.ActivityHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoingToPageFragment extends Fragment {

    private View view;
    private RecyclerView activitiesList;
    private FirestoreRecyclerAdapter<Activity, ActivityHolder> firestoreRecyclerAdapter;
    private FirebaseFirestore db;

    public GoingToPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_going_to_page, container, false);

        activitiesList = view.findViewById(R.id.venue_view_activities_going);
        activitiesList.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();

        requestActivities();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.startListening();
        }
    }

    private void requestActivities() {
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().
                getCurrentUser()).getUid();
        db.collection("players")
                .whereEqualTo("uid", currentUser).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String userId = queryDocumentSnapshots.iterator().next().getId();
                    Query query = createQuery(userId);
                    FirestoreRecyclerOptions<Activity> options = setOptions(query);
                    setFirestoreRecyclerAdapter(options);
                    activitiesList.setAdapter(firestoreRecyclerAdapter);
                    if (firestoreRecyclerAdapter != null)
                        firestoreRecyclerAdapter.startListening();
                });
    }

    private void setFirestoreRecyclerAdapter(FirestoreRecyclerOptions<Activity> options) {
        firestoreRecyclerAdapter
                = new FirestoreRecyclerAdapter<Activity, ActivityHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ActivityHolder holder, int position, @NonNull Activity model) {
                holder.setDetails(model);
            }

            @NonNull
            @Override
            public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout_venue, parent, false);

                return new ActivityHolder(view);
            }
        };
    }

    private Query createQuery(String userId) {
        return db.collection("activities")
                .whereArrayContains("players", userId);
    }

    private FirestoreRecyclerOptions<Activity> setOptions(Query query) {
        return new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();
    }
}
