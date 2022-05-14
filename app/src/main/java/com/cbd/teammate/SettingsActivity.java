package com.cbd.teammate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SettingsActivity extends AbstractActivity {
    private EditText phoneNumber;
    private Button saveSettings;
    private Button deleteAccount;
    private Button discardButton;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveSettings = findViewById(R.id.save_settings_button);
        deleteAccount = findViewById(R.id.delete_account_button);
        discardButton = findViewById(R.id.no_save_button);

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClicked(v.getContext());
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onDiscardClicked();
            }
        });
    }

    private void onSaveClicked(){
        phoneNumber = findViewById(R.id.phone_number);
        if (!phoneNumber.getText().toString().isEmpty()) {
            db = FirebaseFirestore.getInstance();
            db.collection("players").whereEqualTo("uid", firebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String id = queryDocumentSnapshots.iterator().next().getId();
                    Log.w("IDWORKS", id);
                    db.collection("players").document(id).update("phone", phoneNumber.getText().toString());
                }
            });
            startActivity(new Intent(this, SignedActivity.class));
            Toast.makeText(getApplicationContext(), "Phone Number was changed", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_LONG).show();
        }


    }

    public void onDeleteClicked(Context context) {

        firebaseAuth.getInstance().getCurrentUser().delete();

        startActivity(new Intent(context, RegisterActivity.class));

    }

    public void onDiscardClicked() {
        this.finish();

      //  startActivity(new Intent(this, ProfileFragment.class));

    }
}
