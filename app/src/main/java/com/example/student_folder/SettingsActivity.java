package com.example.student_folder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    //------------->themeMode
    SwitchCompat switchButton;
    SharedPreferences preferences = null;
    //<-------------themeMode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
                    editor.commit();
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchButton.setChecked(false);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });
        //<-------------themeMode switch

    }
}