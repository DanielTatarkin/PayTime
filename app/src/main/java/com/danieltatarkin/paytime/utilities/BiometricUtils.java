package com.danieltatarkin.paytime.utilities;

import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BiometricUtils {

    public static boolean isSdkVersionSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Allows for creating and showing Biometrics authentication prompt
     *
     * @param fragmentActivity
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static void createFingerPrompt(FragmentActivity fragmentActivity) {

        // New thread executor for BiometricPrompt constructor
        ExecutorService executor = Executors.newSingleThreadExecutor();

        BiometricPrompt biometricPrompt = new BiometricPrompt(fragmentActivity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Looper.prepare();


                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(fragmentActivity, "FP Prompt cancelled by user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                Looper.prepare();
                Toast.makeText(fragmentActivity, "FP SUCCESS!", Toast.LENGTH_SHORT).show();
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                Looper.prepare();
                Toast.makeText(fragmentActivity, "FP FAILED!", Toast.LENGTH_SHORT).show();
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo.Builder promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Daniel Title")
                .setSubtitle("Daniel Subtitle")
                .setDescription("Daniel Description...")
                .setNegativeButtonText("Cancel");

        biometricPrompt.authenticate(promptInfo.build());
    }
}
