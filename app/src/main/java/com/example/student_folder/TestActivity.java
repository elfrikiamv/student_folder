package com.example.student_folder;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TestActivity extends AppCompatActivity {

    FloatingActionMenu actionMenu;
    GoogleSignInClient mGoogleSignInClient;
    ImageView imageView;
    TextView name, email, id;
    Button signOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signOut = findViewById(R.id.btn_signOut);
        signOut.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    // ...
                    case R.id.btn_signOut:
                        signOut();
                        break;
                    // ...
                }
            }
        });

        imageView = findViewById (R.id.imageAvatar);
        name = findViewById (R.id.textName);
        //email = findViewById (R.id.textEmail);
        //id = findViewById (R.id.textID);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            //String personEmail = acct.getEmail();
            //String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText(personName);
            //email.setText (personEmail);
            //id.setText (personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(imageView);

        }
        //floating menu
        actionMenu = (FloatingActionMenu) findViewById(R.id.fabPrincipal);
        actionMenu.setClosedOnTouchOutside(true);

    }

    private void signOut() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText( TestActivity.this,"Camara!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
    /*
    //floating menu
    public void clicSubMenu1 (View view) {
        Toast.makeText(this, "Sub Menu 1 tocado", Toast.LENGTH_SHORT).show();
        actionMenu.close(true);
    }

    public void clicSubMenu2 (View view) {
        Toast.makeText(this, "Sub Menu 2 tocado", Toast.LENGTH_SHORT).show();
        actionMenu.close(true);
    }

     */



}