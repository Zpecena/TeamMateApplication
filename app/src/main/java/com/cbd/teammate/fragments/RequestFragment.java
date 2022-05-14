package com.cbd.teammate.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cbd.database.entities.Activity;
import com.cbd.database.entities.Player;
import com.cbd.database.entities.Request;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class RequestFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText desc;
    private String descText;
    private String level;
    private Spinner spinnerLevel;
    private Button signUpBut;
    private Activity activity;
    private String uId;
    private Player player;
    private boolean alreadyApplied;
    private LocationProvider LP;

    public RequestFragment(Activity activity, Player player, LocationProvider lp) {
        this.activity = activity;
        this.uId = activity.getCreatorId();
        this.player = player;
        this.LP = lp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        alreadyApplied = false;
        desc = view.findViewById(R.id.description_request);
        spinnerLevel = view.findViewById(R.id.skill_level);
        signUpBut = view.findViewById(R.id.sign_to_activity);
        level = "Amateur";


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.player_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);
        spinnerLevel.setOnItemSelectedListener(this);
        addListener(signUpBut);
        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        level = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), level, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void addListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRequest();
            }
        });
    }

    private com.google.firebase.firestore.Query createQuery() {
        return FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("activity", activity)
                .whereEqualTo("player", player);
    }

    private void DuplicateCheck() {
        com.google.firebase.firestore.Query query1 = createQuery();
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    descText = desc.getText().toString();
                    level = spinnerLevel.getSelectedItem().toString();
                    Request request = new Request(uId, level, descText, Boolean.FALSE, activity, player);
                    FirebaseFirestore.getInstance().collection("requests")
                            .add(request);


                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                            .getSupportFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_above_nav, new NearbyFragment(LP));
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(), "You are already registered!", Toast.LENGTH_SHORT).show();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                    fragmentTransaction.replace(R.id.fragment_above_nav, new NearbyFragment(LP));
                    fragmentTransaction.commit();

                }
            }
        });
    }

    private void addRequest() {
        if (desc.getText().toString().length() > 10 && desc.getText().toString().length() < 80) {
            DuplicateCheck();

        } else {
            Toast.makeText(getContext(), "Enter description minimum 20 letters long", Toast.LENGTH_SHORT).show();

        }


    }
}

