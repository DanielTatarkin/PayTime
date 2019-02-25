package com.danieltatarkin.paytime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.danieltatarkin.paytime.utilities.BiometricUtils;
import com.danieltatarkin.paytime.utilities.NetworkUtils;
import com.danieltatarkin.paytime.utilities.PayTimeConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    //
    public static final int USER_RETURNED_FROM_MAIN = 301;

    private TextInputLayout emailEditText;
    private TextInputLayout passwordEditText;
    private Button loginButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Creating Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        // Setting up Views
        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginButton = findViewById(R.id.login_button);
        setViewListeners();

        if (BiometricUtils.isSdkVersionSupported()) {
            BiometricUtils.createFingerPrompt(this);
        }
    }

    /**
     * This function takes text from Email/Password EditTexts.
     * - Checks if it's empty.
     * - Logs user in if credentials are correct, and updates database with user's info.
     */
    private void loginUser() {
        String email = emailEditText.getEditText().getText().toString();
        String password = passwordEditText.getEditText().getText().toString();

        if (!email.trim().isEmpty() && !password.trim().isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                updateDatabase(task.getResult().getUser());
                                Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                                userLoggedIn();
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong user credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Email/Password field is empty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This function updates Firestore db with provided user's information
     *
     * @param firebaseUser - instance of a Firebase User from Firebase Auth
     */
    private void updateDatabase(@NonNull FirebaseUser firebaseUser) {

        Map<String, Object> userLoginInfo = new HashMap<>();
        userLoginInfo.put("email", Objects.requireNonNull(firebaseUser.getEmail()));
        userLoginInfo.put("UID", firebaseUser.getUid());
        userLoginInfo.put("Last Login", Calendar.getInstance().getTime());

        db.collection("Users")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .set(userLoginInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "DB updated with user's info", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Something went wrong | DB", Toast.LENGTH_SHORT).show();
                        Log.e(PayTimeConstants.TAG, e.toString());
                    }
                });

    }

    /**
     * Setting listeners for views presented in this activity
     */
    private void setViewListeners() {

        // Setting listener for Done/Return key press on user's keyboard when in passwordEditText
        passwordEditText.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginUser();
                }
                return false;
            }
        });

        // Login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.checkNetworkConnection(getApplicationContext())) loginUser();
                else
                    Toast.makeText(LoginActivity.this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void userLoggedIn() {
        Intent goToMainActivity = new Intent(this, MainActivity.class);
        startActivityForResult(goToMainActivity, USER_RETURNED_FROM_MAIN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateDatabase(FirebaseAuth.getInstance().getCurrentUser());
            Toast.makeText(this, "Logged in already!", Toast.LENGTH_SHORT).show();
            userLoggedIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == USER_RETURNED_FROM_MAIN) {
            firebaseAuth.signOut();
        }
    }
}