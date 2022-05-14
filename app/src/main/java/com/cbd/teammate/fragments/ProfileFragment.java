package com.cbd.teammate.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cbd.maps.LocationProvider;
import com.cbd.teammate.R;
import com.cbd.teammate.RegisterActivity;
import com.cbd.teammate.SettingsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private Button logout;
    private TextView textMail;
    private TextView textNumber;
    private ImageView profileImage;
    private ImageButton settingsButton;
    private TextView requests;
    private int numberOfRequests;
    private LocationProvider lp;

    public ProfileFragment(LocationProvider lp) {
        this.lp = lp;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        numberOfRequests = 0;
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        textMail = view.findViewById(R.id.eMail);
        textNumber = view.findViewById(R.id.telNumber);
        profileImage = view.findViewById(R.id.profilePicture);
        settingsButton = view.findViewById(R.id.settings_button);
        requests = view.findViewById(R.id.RequestsPending);

        auth = FirebaseAuth.getInstance();
        FirebaseUser theUser = auth.getCurrentUser();
        Query query1 = createQuery();
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                numberOfRequests = queryDocumentSnapshots.size();
                requests.setText("You have " + numberOfRequests + "unreviewed requests!");
            }
        });

        if (theUser == null) {
            textMail.setText("No Login");
            textNumber.setText("No Login");
        } else {
            textMail.setText(theUser.getEmail());
            textNumber.setText(theUser.getPhoneNumber());
            requests.setText("You have " + numberOfRequests + "unreviewed requests!");
            if (theUser.getPhotoUrl() != null)
                Picasso.get().load(theUser.getPhotoUrl()).into(profileImage);
            else
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_icon_black));

        }

        logout = view.findViewById(R.id.logout);
        addListener(logout);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SettingsActivity.class));
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                        .getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragment_above_nav, new RequestsReviewFragment(lp))
                        .addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private Query createQuery() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser theUser = auth.getCurrentUser();
        return FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("uid", theUser.getUid())
                .whereEqualTo("accepted", false)
                ;
    }

    private void addListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view, view.getContext());
            }
        });
    }

    public void logout(View view, Context context) {

        FirebaseAuth.getInstance().signOut();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Toast.makeText(context.getApplicationContext(), "still not null", Toast.LENGTH_SHORT);

        }
        startActivity(new Intent(context, RegisterActivity.class));

    }

}
