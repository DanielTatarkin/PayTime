package com.danieltatarkin.paytime.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.danieltatarkin.paytime.R;
import com.danieltatarkin.paytime.activities.loginactivity.LoginActivityView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_time);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(LoginActivityView.USER_RETURNED_FROM_MAIN);
    }
}
