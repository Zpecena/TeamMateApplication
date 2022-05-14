package com.cbd.teammate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cbd.database.entities.Player;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AbstractActivity {
    private EditText inputEmail, inputPassword, inputPasswordConf;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SignInButton gSignButton;
    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConf = (EditText) findViewById(R.id.confirm_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Firebase instance
        mAuth = FirebaseAuth.getInstance();

        //check if somebody is logged in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, SignedActivity.class));
            finish();
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        gSignButton = findViewById(R.id.g_sign_in_button);

        gSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });
    }


    private void signIn() {
        //mGoogleSignInClient.revokeAccess();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void onLoginClicked(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Use your already created account", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // clicking on register button, checks if fields are not empty, minimum length of password
    public void onRegisterClicked(View view) {
        String emailInput = this.inputEmail.getText().toString().trim();
        String password = this.inputPassword.getText().toString().trim();
        String passwordConf = this.inputPasswordConf.getText().toString().trim();

        boolean check = false;


        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(getApplicationContext(), "Enter e-mail", Toast.LENGTH_SHORT).show();
            check = true;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
            check = true;
        } else if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Minimum 6 characters in a password.", Toast.LENGTH_SHORT).show();
            check = true;
        }

        if (!password.equals(passwordConf)) {
            Toast.makeText(getApplicationContext(), R.string.password_match, Toast.LENGTH_SHORT).show();
            check = true;
        }


        // new user
        if (check == false) {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailInput, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            // failed sign in - message
                            // successful sign in - notify listener

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Authentification failed " + task.getException(), Toast.LENGTH_LONG).show();
                                Log.e("MyTag", task.getException().toString());
                            } else {
                                createNewPlayerWithEmail();
                                startActivity(new Intent(RegisterActivity.this, SignedActivity.class));
                                finish();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(RegisterActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(RegisterActivity.this, "Failed Sign in", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        if (account == null) {
            Toast.makeText(RegisterActivity.this, "FAILED", Toast.LENGTH_SHORT).show();

        } else {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        db.collection("players")
                                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.isEmpty()) {
                                            createNewPlayer();
                                        }
                                    }
                                });
                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Not a success", Toast.LENGTH_SHORT).show();
                    }
                    switchActivity();
                }
            });
        }
    }

    private void createNewPlayer() {
        try {
            FirebaseUser logged = FirebaseAuth.getInstance().getCurrentUser();
            Player newPlayer = new Player(logged.getUid(),
                    logged.getDisplayName(),
                    logged.getPhotoUrl() != null ? logged.getPhotoUrl().toString() : null,
                    logged.getPhoneNumber());

            db.collection("players").add(newPlayer);
        } catch (Throwable oops) {
            Toast.makeText(RegisterActivity.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewPlayerWithEmail() {
        try {
            FirebaseUser logged = FirebaseAuth.getInstance().getCurrentUser();
            Player newPlayer = new Player(logged.getUid(),
                    logged.getEmail(),
                    logged.getPhotoUrl() != null ? logged.getPhotoUrl().toString() : null,
                    logged.getPhoneNumber());

            db.collection("players").add(newPlayer);
        } catch (Throwable oops) {
            Toast.makeText(RegisterActivity.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchActivity() {
        startActivity(new Intent(this, SignedActivity.class));
        finish();
    }
}