package com.danieltatarkin.paytime.activities.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.danieltatarkin.paytime.R;
import com.danieltatarkin.paytime.activities.loginactivity.LoginActivityView;
import com.danieltatarkin.paytime.utilities.FirebaseInstances;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_time);

        setupViews();
    }

    private void setupViews() {

        logoutButton = findViewById(R.id.sign_out_btn);

        setupClickListeners();
    }

    private void setupClickListeners() {

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void signOut() {
        LoginActivityView.FbInstance.getFirebaseAuth().signOut();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(LoginActivityView.USER_RETURNED_FROM_MAIN);
    }
}
