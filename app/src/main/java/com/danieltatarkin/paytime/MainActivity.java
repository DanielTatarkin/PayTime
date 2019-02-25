package com.danieltatarkin.paytime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_time);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(LoginActivity.USER_RETURNED_FROM_MAIN);
    }
}
