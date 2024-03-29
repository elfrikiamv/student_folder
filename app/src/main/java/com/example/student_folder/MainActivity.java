package com.example.student_folder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.student_folder.Fragments.CardFragment;
import com.example.student_folder.Fragments.HomeFragment;
import com.example.student_folder.Fragments.InternalFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //------------->Cuenta google
    GoogleSignInClient mGoogleSignInClient;
    //<-------------Cuenta google
    ImageView imageView;
    TextView name, email;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener (this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open_Drawer, R.string.Close_Drawer);
        drawerLayout.addDrawerListener (toggle);
        toggle.syncState();

        getSupportFragmentManager ().beginTransaction().replace(R.id.fragment_container, new HomeFragment ()).commit();
        navigationView.setCheckedItem(R.id.nav_home);

        //------------->Cuenta google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //<-------------Cuenta google

        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.cuenta_name);

        View headerPhoto = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) headerPhoto.findViewById(R.id.cuenta_avatar);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText (personName);

            Glide.with(this).load(String.valueOf(personPhoto)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText( MainActivity.this, "Mi Correo: " +"\n"+ personEmail + "\n" +
                            "Mi Id: "+"\n"+ personId, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.nav_home:
                    HomeFragment homeFragment = new HomeFragment();
                    getSupportFragmentManager (). beginTransaction().replace (R.id.fragment_container, homeFragment).addToBackStack(null).commit();
                    break;
                case R.id.nav_internal:
                    InternalFragment internalFragment = new InternalFragment();
                    getSupportFragmentManager (). beginTransaction().replace (R.id.fragment_container, internalFragment).addToBackStack(null).commit();
                    break;
                case R.id.nav_card:
                    CardFragment cardFragment = new CardFragment();
                    getSupportFragmentManager (). beginTransaction().replace (R.id.fragment_container, cardFragment).addToBackStack(null).commit();
                    break;
                    //al precionar el boton de configuracion
                case R.id.nav_settings:
                    //iniciar SettingsActivity
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
                case R.id.nav_note:
                    startActivity(new Intent(this, NoteActivity.class));
                    break;
                case R.id.nav_drive:
                    startActivity(new Intent(this, DriveActivity.class));
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager(). popBackStackImmediate();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            
            super.onBackPressed();
        }
    }
}