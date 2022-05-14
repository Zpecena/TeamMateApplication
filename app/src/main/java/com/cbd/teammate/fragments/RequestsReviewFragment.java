package com.cbd.teammate.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbd.database.entities.Request;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.R;
import com.cbd.teammate.holders.RequestViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RequestsReviewFragment extends Fragment {
    private View requestsView;
    private RecyclerView myRequestsView;
    private FirestoreRecyclerAdapter<Request, RequestViewHolder> firestoreRecyclerAdapter;
    private LocationProvider lp;
    private FirebaseAuth auth;

    public RequestsReviewFragment(LocationProvider lp) {
        this.lp = lp;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestsView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestsView = requestsView.findViewById(R.id.recycler_view);
        myRequestsView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestRequests();


        return requestsView;
    }

    private void requestRequests() {
        Query query = createQuery();
        FirestoreRecyclerOptions<Request> options = setOptions(query);
        setFirestoreRecyclerAdapter(options);
        myRequestsView.setAdapter(firestoreRecyclerAdapter);
        if (firestoreRecyclerAdapter != null)
            firestoreRecyclerAdapter.startListening();
    }

    private Query createQuery() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser theUser = auth.getCurrentUser();
        return FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("uid", theUser.getUid())
                .whereEqualTo("accepted", false);
    }


    public FirestoreRecyclerOptions<Request> setOptions(Query query) {
        return new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();
    }

    public void setFirestoreRecyclerAdapter(FirestoreRecyclerOptions<Request> options) {
        firestoreRecyclerAdapter
                = new FirestoreRecyclerAdapter<Request, RequestViewHolder>(options) {
            protected void onBindViewHolder(@NonNull RequestViewHolder holder,
                                            int position, @NonNull Request model) {
                try {
                    holder.setDetails(model);
                } catch (Throwable oops) {
                    Toast.makeText(requestsView.getContext().getApplicationContext(),
                            "Oops! Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_layout_request, parent, false);

                return new RequestViewHolder(view, getContext());
            }
        };
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
}

