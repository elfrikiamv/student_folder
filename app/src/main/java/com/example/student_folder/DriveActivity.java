package com.example.student_folder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Collections;


public class DriveActivity extends AppCompatActivity {
    private static final String TAG = "DriveActivity";

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    private DriveServiceHelper mDriveServiceHelper;
    private String mOpenFileId;

    /*private EditText mFileTitleEditText;
    private EditText mDocContentEditText;*/

    //file list dirve
    public EditText nameDriveFileList;
    public EditText textDriveFileList;

    FloatingActionMenu actionMenu;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        //------------->backButton
        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        //<-------------backButton

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //nav

        /*Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);*/



        /*
        //fondiwis
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_drive);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //fondiwis

         */

        // Store the EditText boxes to be updated when files are opened/created/modified.
        //mFileTitleEditText = findViewById(R.id.file_title_edittext);
        //mDocContentEditText = findViewById(R.id.doc_content_edittext);

        //---------------->Store the EditText boxes to be updated when files are opened/created/modified.
        nameDriveFileList = findViewById(R.id.et_nameFileList);
        textDriveFileList = findViewById(R.id.et_textFileList);
        //<----------------Store the EditText boxes to be updated when files are opened/created/modified.

        // Set the onClick listeners for the button bar.
        /*findViewById(R.id.open_btn).setOnClickListener(view -> openFilePicker());
        findViewById(R.id.create_btn).setOnClickListener(view -> createFile());
        findViewById(R.id.menu_file_pdf).setOnClickListener(view -> saveFilePdf());
        findViewById(R.id.menu_file_txt).setOnClickListener(view -> saveFileTxt());
        findViewById(R.id.menu_file_docx).setOnClickListener(view -> saveFileRtf());*/

        //---------------->floating button crear .txt en drive
        findViewById(R.id.btn_createFrileTxt).setOnClickListener(view -> createFileTxt());

        //---------------->floating button mostar DriveFileList
        //findViewById(R.id.btn_queryFiles).setOnClickListener(view -> queryFiles());
        //<----------------floating button mostar DriveFileList

        //------------->button show the file list or not
        findViewById(R.id.btn_showFiles).setOnClickListener(view -> showFiles());
        //<-------------button show the file list or not

        //------------->floating menu
        actionMenu = (FloatingActionMenu) findViewById(R.id.menuDriveFile);
        actionMenu.setClosedOnTouchOutside(true);
        //<-------------floating menu

        // Authenticate the user. For most apps, this should be done when the user performs an
        // action that requires Drive access rather than in onCreate.
        requestSignIn();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_favorite:
                Toast.makeText(this, "Atras xd", Toast.LENGTH_LONG).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {
                        openFileFromFilePicker(uri);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);

    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }

    /**
     * Opens the Storage Access Framework file picker using {@link #REQUEST_CODE_OPEN_DOCUMENT}.
     */
    private void openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.");


            Intent pickerIntent = mDriveServiceHelper.createFilePickerIntent();

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT);
        }
    }

    /**
     * Opens a file from its {@code uri} returned from the Storage Access Framework file picker
     * initiated by {@link #openFilePicker()}.
     */
    private void openFileFromFilePicker(Uri uri) {
        if (mDriveServiceHelper != null) {
            //Log.d(TAG, "Opening " + uri.getPath());
            Toast.makeText(this, "Abriendo " + uri.getPath()+"..", Toast.LENGTH_LONG).show();

            mDriveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        nameDriveFileList.setText(name);
                        textDriveFileList.setText(content);

                        // Files opened through SAF cannot be modified.
                        setReadOnlyMode();
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to open file from picker.", exception));
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createFile() {
        if (mDriveServiceHelper != null) {
            //Log.d(TAG, "Creating a file.");
            Toast.makeText(this, "Creando archivo...", Toast.LENGTH_LONG).show();

            mDriveServiceHelper.createFile()
                    .addOnSuccessListener(fileId -> readFile(fileId))
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
                            //Toast.makeText(this, "Couldn't create file.", exception+"..", Toast.LENGTH_LONG).show();
        }
    }

    private void createFileTxt() {

        /*if (mDriveServiceHelper != null && mOpenFileId != null) {
            //Log.d(TAG, "Saving " + mOpenFileId);
            Toast.makeText(this, "Guardando..." + mOpenFileId+"..", Toast.LENGTH_SHORT).show();

            //String fileName = nameDriveFileList.getText().toString();
            //String fileContent = textDriveFileList.getText().toString();

            mDriveServiceHelper.saveFileTxt(mOpenFileId, fileName, fileContent)
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to save file via REST.", exception));
        }*/

        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file txt.");
            //Toast.makeText(this, "Creando archivo...", Toast.LENGTH_LONG).show();

            String fileName = nameDriveFileList.getText().toString();
            String fileContent = textDriveFileList.getText().toString();

            if (fileName.isEmpty() || fileContent.isEmpty()) {
                Toast.makeText(this, "El nombre y el contenido del archivo no pueden estar vacíos.", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(this, "Creando archivo", Toast.LENGTH_SHORT).show();

                mDriveServiceHelper.createFileTxt(fileName, fileContent)
                        .addOnSuccessListener(fileId ->  readFile(fileId))
                        .addOnFailureListener(exception ->
                                Log.e(TAG, "Couldn't create file.", exception));
            }
        }

        actionMenu.close(true);
    }

    /**
     * Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
     */
    private void readFile(String fileId) {
        if (mDriveServiceHelper != null) {

            Toast.makeText(this, "Leyendo " + fileId+"..", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Reading " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        nameDriveFileList.setText(name);
                        textDriveFileList.setText(content);

                        // Enable file saving now that a file is open.
                        setReadWriteMode(fileId);
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to read file.", exception));
        }
    }

    /**
     * Saves the currently opened file created via {@link #createFile()} if one exists.
     */
    private void saveFilePdf() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            //Log.d(TAG, "Saving " + mOpenFileId);
            Toast.makeText(this, "Guardando.." + mOpenFileId+"..", Toast.LENGTH_LONG).show();

            String fileName = nameDriveFileList.getText().toString();
            String fileContent = textDriveFileList.getText().toString();

            mDriveServiceHelper.saveFilePdf(mOpenFileId, fileName, fileContent)
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to save file via REST.", exception));
        }
        actionMenu.close(true);
    }

    private void saveFileRtf() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            //Log.d(TAG, "Saving " + mOpenFileId);
            Toast.makeText(this, "Guardando.." + mOpenFileId+"..", Toast.LENGTH_LONG).show();

            String fileName = nameDriveFileList.getText().toString();
            String fileContent = textDriveFileList.getText().toString();

            mDriveServiceHelper.saveFileRtf(mOpenFileId, fileName, fileContent)
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to save file via REST.", exception));
        }
        actionMenu.close(true);
    }

    //------------->Queries the Drive REST API for files visible to this app and lists them in the content view
    private void queryFiles() {

        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()

                    .addOnSuccessListener(fileList -> {
                        StringBuilder builder = new StringBuilder();
                        for (File file : fileList.getFiles()) {
                            builder.append(file.getName()).append("\n");
                        }
                        String fileNames = builder.toString();

                        nameDriveFileList.setText("Lista de archivos");
                        textDriveFileList.setText(fileNames);

                        setReadOnlyMode();
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }

        return;
    }
    //<-------------Queries the Drive REST API for files visible to this app and lists them in the content view

    //------------->show the file list or not
    private void showFiles() {

        String nameEditText = nameDriveFileList.getText().toString().trim();

        if (nameEditText.isEmpty()) {

            queryFiles();
            findViewById(R.id.btn_showFiles).setBackgroundResource(R.drawable.ic_eye_crossed);
        } else {

            nameDriveFileList.getText().clear();
            textDriveFileList.getText().clear();
            nameDriveFileList.setEnabled(true);

            findViewById(R.id.btn_showFiles).setBackgroundResource(R.drawable.ic_eye);
        }
    }
    //<-------------show the file list or not



    /**
     * Updates the UI to read-only mode.
     */
    private void setReadOnlyMode() {
        //mFileTitleEditText.setEnabled(false);
        nameDriveFileList.setEnabled(false);
        mOpenFileId = null;
    }

    /**
     * Updates the UI to read/write mode on the document identified by {@code fileId}.
     */
    private void setReadWriteMode(String fileId) {
        nameDriveFileList.setEnabled(true);
        textDriveFileList.setEnabled(true);
        mOpenFileId = fileId;
    }

    //------------->backButton
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    //<-------------backButton

}