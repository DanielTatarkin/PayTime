package com.danieltatarkin.paytime.utilities;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseInstances {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAnalytics firebaseAnalytics;

    public FirebaseInstances(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.firebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @NonNull
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    @NonNull
    public FirebaseFirestore getDb() {
        return db;
    }

    @NonNull
    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    // Creating Firebase Auth, DB, Analytics instances


}
