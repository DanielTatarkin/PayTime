package com.danieltatarkin.paytime;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class MainActivity extends AppCompatActivity {

    private TextInputLayout emailEditText;
    private TextInputLayout passwordEditText;
    private Button loginButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Setting up Views
        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginButton = findViewById(R.id.login_button);
        setViewListeners();
    }

    /**
     * This function takes text from Email/Password EditTexts.
     *      - Checks if it's empty.
     *      - Logs user in if credentials are correct.
     *          - If correct, then calls updateDatabase() with current user's info.
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
                                updateDatabase(FirebaseAuth.getInstance().getCurrentUser());
                                Toast.makeText(MainActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong user credentials", Toast.LENGTH_SHORT).show();
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
     * @param firebaseUser
     */
    private void updateDatabase(@NonNull FirebaseUser firebaseUser) {

        Map<String, Object> userLoginInfo = new HashMap<>();
        userLoginInfo.put("email", Objects.requireNonNull(firebaseUser.getEmail()));
        userLoginInfo.put("UID", firebaseUser.getUid());
        userLoginInfo.put("Last Login", Calendar.getInstance().getTime());

        db.collection("Users")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .set(userLoginInfo);

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
                loginUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateDatabase(FirebaseAuth.getInstance().getCurrentUser());
            Toast.makeText(this, "Logged in already!", Toast.LENGTH_SHORT).show();
        }
    }
}