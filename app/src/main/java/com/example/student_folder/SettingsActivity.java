package com.example.student_folder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity {

    //------------->themeMode
    SwitchCompat switchButton;
    SharedPreferences preferences = null;
    //<-------------themeMode

    //------------->Cuenta google
    GoogleSignInClient mGoogleSignInClient;
    //<-------------Cuenta google

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //------------->backButton
        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        //<-------------backButton

        //------------->themeMode
        switchButton = findViewById(R.id.switchTheme);

        preferences = getSharedPreferences("night", Context.MODE_PRIVATE);
        boolean booleanValue = preferences.getBoolean("night_mode",true);

        if (booleanValue) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchButton.setChecked(true);
        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchButton.setChecked(false);
        }
        //<-------------themeMode

        //------------->themeMode switch
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchButton.setChecked(true);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.apply();
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchButton.setChecked(false);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.apply();
                }
            }
        });
        //<-------------themeMode switch

        //------------->tv_signOut
        TextView tv_signOut = findViewById(R.id.tv_signOut);

        tv_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });
        //<-------------tv_signOut

        //------------->Cuenta google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //<-------------Cuenta google
    }

    //------------->Cuenta google signOut
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText( SettingsActivity.this, "Cerrando sesión..",Toast. LENGTH_LONG). show();
                    }
                });
    }
    //<-------------Cuenta google signOut

    //------------->backButton
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    //<-------------backButton
}