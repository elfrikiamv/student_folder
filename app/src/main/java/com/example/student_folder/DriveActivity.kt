package com.example.student_folder

import androidx.appcompat.app.AppCompatActivity
import com.example.student_folder.DriveServiceHelper
import android.widget.EditText
import android.widget.ImageButton
import com.github.clans.fab.FloatingActionMenu
import androidx.drawerlayout.widget.DrawerLayout
import android.os.Bundle
import com.example.student_folder.R
import android.widget.Toast
import android.content.Intent
import com.example.student_folder.DriveActivity
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.util.Pair
import android.view.MenuItem
import android.view.View
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.drive.DriveScopes
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.gson.GsonFactory
import com.google.android.gms.tasks.OnFailureListener
import com.google.api.services.drive.model.FileList
import java.lang.Exception
import java.lang.StringBuilder

class DriveActivity : AppCompatActivity() {
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var mOpenFileId: String? = null

    /*private EditText mFileTitleEditText;
    private EditText mDocContentEditText;*/
    //------------->file list dirve
    var nameDriveFileList: EditText? = null
    var textDriveFileList: EditText? = null

    //<-------------file list dirve
    //------------->save file button
    var saveFileButton: ImageButton? = null

    //<-------------save file button
    //------------->save file button openFileFromFilePicker
    var saveFileOpenDriveButton: ImageButton? = null

    //<-------------save file button openFileFromFilePicker
    //------------->save file button
    var showFileListButton: ImageButton? = null

    //<-------------save file button
    //------------->close file button
    var closeFileButton: ImageButton? = null

    //<-------------close file button
    //------------->delete file button
    var deleteFileButton: ImageButton? = null

    //<-------------delete file button
    //------------->create File Txt Button
    var createFileTxtButton: FloatingActionButton? = null

    //<-------------create File Txt Button
    //------------->open File Txt Button
    var openFilePickerButton: FloatingActionButton? = null

    //<-------------open File Txt Button
    var actionMenu: FloatingActionMenu? = null
    private val drawerLayout: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive)

        //------------->backButton
        val backButton = findViewById<View>(R.id.back_button)
        backButton.setOnClickListener { onBackPressed() }
        //<-------------backButton

        //---------------->Store the EditText boxes to be updated when files are opened/created/modified.
        nameDriveFileList = findViewById(R.id.et_nameFileList)
        textDriveFileList = findViewById(R.id.et_textFileList)
        //<----------------Store the EditText boxes to be updated when files are opened/created/modified.

        //---------------->floating button crear .txt en drive
        createFileTxtButton = findViewById(R.id.btn_createFileTxt)
        createFileTxtButton!!.setOnClickListener {
            createFileTxt()
        }
        //<----------------floating button crear .txt en drive

        //---------------->open file txt
        openFilePickerButton = findViewById(R.id.btn_openFileTxt)
        openFilePickerButton!!.setOnClickListener {
            openFilePicker()
        }
        //<----------------open file txt

        //------------->button show the file list or not
        showFileListButton = findViewById(R.id.btn_showFiles)
        showFileListButton!!.setOnClickListener {
            showFiles()
        }
        //<-------------button show the file list or not

        //------------->button save the file
        saveFileButton = findViewById(R.id.btn_saveFile)
        saveFileButton!!.setOnClickListener {
            saveFileId()
        }
        //<-------------button save the file

        //------------->save file button openFileFromFilePicker
        saveFileOpenDriveButton = findViewById(R.id.btn_saveFileOpenDrive)
        saveFileOpenDriveButton!!.setOnClickListener {
            saveFilePickerDrive()
        }
        //<-------------save file button openFileFromFilePicker

        //------------->close file button
        closeFileButton = findViewById(R.id.btn_closeFile)
        closeFileButton!!.setOnClickListener {
            closeFileId()
        }
        //<-------------close file button

        //------------->delete file button
        deleteFileButton = findViewById(R.id.btn_deleteFile)
        deleteFileButton!!.setOnClickListener {
            deleteFileId()
        }
        //<-------------delete file button

        //------------->floating menu
        actionMenu = findViewById<View>(R.id.menuDriveFile) as FloatingActionMenu
        actionMenu!!.setClosedOnTouchOutside(true)
        //<-------------floating menu

        // Authenticate the user. For most apps, this should be done when the user performs an
        // action that requires Drive access rather than in onCreate.
        requestSignIn()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                Toast.makeText(this, "Atras xd", Toast.LENGTH_LONG).show()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
            REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == RESULT_OK && resultData != null) {
                val uri = resultData.data
                uri?.let { openFileFromFilePicker(it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    //------------->Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
    private fun requestSignIn() {
        Log.d(TAG, "Requesting sign-in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    //<-------------Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
    //------------->Handles the {@code result} of a completed sign-in activity initiated from {@link requestSignIn()}.
    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                Log.d(TAG, "Signed in as " + googleAccount.email)

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(
                    this, setOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential
                )
                    .setApplicationName("Drive API Migration")
                    .build()

                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                // Its instantiation is required before handling any onClick actions.
                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
            }
            .addOnFailureListener { exception: Exception? ->
                Log.e(
                    TAG,
                    "Unable to sign in.",
                    exception
                )
            }
    }

    //<-------------Handles the {@code result} of a completed sign-in activity initiated from {@link requestSignIn()}.
    //------------->Opens the Storage Access Framework file picker using {@link #REQUEST_CODE_OPEN_DOCUMENT}.
    private fun openFilePicker() {

        //hide buttons
        hideButtons()
        //limpia los campos
        emptyDriveFileList()
        if (mDriveServiceHelper != null) {
            val pickerIntent = mDriveServiceHelper!!.createFilePickerIntent()

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT)
            Log.d(TAG, "Opening file picker.")
            Toast.makeText(this, "Opening file picker.", Toast.LENGTH_SHORT).show()
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "mDriveServiceHelper is null.", Toast.LENGTH_SHORT).show()
        }
        actionMenu!!.close(true)
    }

    //<-------------Opens the Storage Access Framework file picker using {@link #REQUEST_CODE_OPEN_DOCUMENT}.
    private fun openFileFromFilePicker(uri: Uri) {
        if (mDriveServiceHelper != null) {
            mDriveServiceHelper!!.getPickerFileName(contentResolver, uri)
                .addOnSuccessListener { nameFile: String ->
                    mDriveServiceHelper!!.getIdForName(nameFile)
                        .addOnSuccessListener { idFile: String? ->
                            if (idFile != null) {
                                Log.d(TAG, "File opened from picker: $nameFile")
                                Toast.makeText(this, "Abriendo: $nameFile", Toast.LENGTH_SHORT)
                                    .show()
                                readFileId(idFile)
                            } else {
                                Toast.makeText(
                                    this,
                                    "No se encontro el archivo en MyDrive.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception: Exception? ->
                            Log.e(TAG, "Unable to get id file.", exception)
                            Toast.makeText(this, "Unable to get id file.", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "Unable to open file from picker.", exception)
                    Toast.makeText(this, "Unable to open file from picker.", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "mDriveServiceHelper is null.", Toast.LENGTH_SHORT).show()
        }
    }

    //------------->Opens a file from its {@code uri} returned from the Storage Access Framework file picker initiated by {@link #openFilePicker()}.
    /*private void openFileFromFilePicker(Uri uri) {
        if (mDriveServiceHelper != null) {

            mDriveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        nameDriveFileList.setText(name);
                        textDriveFileList.setText(content);
                        nameDriveFileList.setEnabled(false);

                        //muiestra el boton de guardar el archivo
                        saveFileOpenDriveButton.setVisibility(View.VISIBLE);
                        //muestra el boton de cerrar el archivo
                        closeFileButton.setVisibility(View.VISIBLE);
                        //muestra el boton de eliminar el archivo
                        //deleteFileButton.setVisibility(View.VISIBLE);

                        Log.d(TAG, "Opening " + uri.getLastPathSegment());
                        Toast.makeText(this, "Abriendo " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(exception -> {

                        Log.e(TAG, "Unable to open file from picker.", exception);
                        Toast.makeText(this, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show();
                    });
        } else {

            Log.e(TAG, "mDriveServiceHelper is null.");
            Toast.makeText(this, "mDriveServiceHelper is null.", Toast.LENGTH_SHORT).show();
        }
    }*/
    //<-------------Opens a file from its {@code uri} returned from the Storage Access Framework file picker initiated by {@link #openFilePicker()}.
    //------------->create file
    private fun createFileTxt() {

        //hide buttons
        hideButtons()
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file txt.")
            val fileName = nameDriveFileList!!.text.toString()
            val fileContent = textDriveFileList!!.text.toString()
            if (fileName.isEmpty() && fileContent.isEmpty()) {
                Toast.makeText(
                    this,
                    "El nombre y el contenido del archivo no pueden estar vacíos.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Creando archivo", Toast.LENGTH_SHORT).show()
                mDriveServiceHelper!!.createFileTxt(fileName, fileContent)
                    .addOnSuccessListener { fileId: String ->
                        emptyDriveFileList()
                        readFileId(fileId)
                    }
                    .addOnFailureListener { exception: Exception? ->
                        Log.e(TAG, "Couldn't create file.", exception)
                        Toast.makeText(this, "No se pudo crear el archivo.", Toast.LENGTH_LONG)
                            .show()
                    }
            }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "No se pudo crear el archivo.", Toast.LENGTH_SHORT).show()
        }
        actionMenu!!.close(true)
    }

    //------------->create file
    //------------->Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
    private fun readFileId(fileId: String) {
        if (mDriveServiceHelper != null) {
            Toast.makeText(this, "Leyendo $fileId..", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Reading $fileId")
            mDriveServiceHelper!!.readFile(fileId)
                .addOnSuccessListener { nameAndContent: Pair<String, String> ->
                    val name = nameAndContent.first
                    val content = nameAndContent.second
                    nameDriveFileList!!.setText(name)
                    textDriveFileList!!.setText(content)
                    showButtons()

                    // Enable file saving now that a file is open.
                    setReadWriteMode(fileId)
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "Imposible leer el archivo.", exception)
                    Toast.makeText(this, "No se pudo leer el archivo.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "No se pudo leer el archivo.", Toast.LENGTH_SHORT).show()
        }
    }

    //<-------------Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
    //------------->Queries the Drive REST API for files visible to this app and lists them in the content view
    private fun queryFiles() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.")
            mDriveServiceHelper!!.queryFiles()
                .addOnSuccessListener { fileList: FileList ->
                    val builder = StringBuilder()
                    for (file in fileList.files) {
                        builder.append(file.name).append("\n")
                    }
                    val fileNames = builder.toString()
                    nameDriveFileList!!.setText("Lista de archivos")
                    nameDriveFileList!!.isEnabled = false
                    textDriveFileList!!.setText(fileNames)
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "Unable to query files.", exception)
                    Toast.makeText(this, "No se pudo consultar los archivos.", Toast.LENGTH_LONG)
                        .show()
                }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "No se pudo consultar los archivos.", Toast.LENGTH_SHORT).show()
        }
        return
    }

    //<-------------Queries the Drive REST API for files visible to this app and lists them in the content view
    //------------->show the file list or not
    private fun showFiles() {

        //hide buttons
        hideButtons()
        val nameEditText = nameDriveFileList!!.text.toString().trim { it <= ' ' }
        if (nameEditText.isEmpty()) {
            queryFiles()
            showFileListButton!!.setBackgroundResource(R.drawable.ic_eye_crossed)
            Toast.makeText(this, "Mostrando lista de archivos", Toast.LENGTH_SHORT).show()
        } else {
            emptyDriveFileList()
            //nameDriveFileList.setEnabled(true);
            showFileListButton!!.setBackgroundResource(R.drawable.ic_eye)
            Toast.makeText(this, "Ocultando lista de archivos", Toast.LENGTH_SHORT).show()
        }
    }

    //<-------------show the file list or not
    //------------->save newly created file
    private fun saveFileId() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            //Log.d(TAG, "Saving " + mOpenFileId);
            Toast.makeText(this, "Guardando archivo", Toast.LENGTH_SHORT).show()
            val fileName = nameDriveFileList!!.text.toString()
            val fileContent = textDriveFileList!!.text.toString()
            mDriveServiceHelper!!.saveFileCreateDrive(mOpenFileId, fileName, fileContent)
                .addOnSuccessListener { fileId: String ->

                    //ejecuta el metodo para leer el archivo con el id del archivo
                    readFileId(fileId)

                    //ocultar el boton de guardar el archivo
                    saveFileButton!!.visibility = View.GONE
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "No se puede guardar el archivo a través de REST.", exception)
                    Toast.makeText(this, "No se pudo guardar el archivo.", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "No hay archivo abierto", Toast.LENGTH_SHORT).show()
        }

        //nameDriveFileList.setEnabled(true);
        hideButtons()
        emptyDriveFileList()
    }

    //<-------------save newly created file
    //------------->save file from openFileFromFilePicker google drive
    private fun saveFilePickerDrive() {
        if (mDriveServiceHelper != null) {
            //Log.d(TAG, "Saving " + mOpenFileId);
            val fileName = nameDriveFileList!!.text.toString()
            mDriveServiceHelper!!.getIdForName(fileName)
                .addOnSuccessListener { fileId: String? ->
                    if (fileId != null) {

                        //ocultar los botones
                        hideButtons()
                        println("fileId: $fileId")
                        Toast.makeText(this, "Guardando archivo.", Toast.LENGTH_SHORT).show()

                        //set the id of the file
                        mOpenFileId = fileId

                        //ejecuta el metodo para guardar el archivo
                        saveFileId()
                    } else {
                        Toast.makeText(this, "No se pudo guardar el archivo.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "No se puede guardar el archivo a través de REST.", exception)
                    Toast.makeText(this, "No se pudo guardar el archivo.", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "No hay archivo abierto", Toast.LENGTH_SHORT).show()
        }
    }

    //<-------------save file from openFileFromFilePicker google drive
    //------------->close the file
    private fun closeFileId() {
        if (mOpenFileId != null) {
            mOpenFileId = null
            emptyDriveFileList()
            hideButtons()
            Log.d(TAG, "Closed file.")
            Toast.makeText(this, "Archivo cerrado", Toast.LENGTH_SHORT).show()
        } else {
            emptyDriveFileList()
            hideButtons()
            Log.d(TAG, "No file open.")
            Toast.makeText(this, "No hay archivo abierto", Toast.LENGTH_SHORT).show()
        }
    }

    //<-------------close the file
    //------------->delete the file
    private fun deleteFileId() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            //Log.d(TAG, "Deleting " + mOpenFileId);
            Toast.makeText(this, "Eliminando archivo", Toast.LENGTH_SHORT).show()
            mDriveServiceHelper!!.deleteFile(mOpenFileId)
                .addOnSuccessListener { aVoid: String? ->
                    if (aVoid == null) {

                        //ejecuta el metodo para cerrar el archivo
                        closeFileId()
                        Toast.makeText(this, "Archivo eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No se pudo eliminar el archivo.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "Unable to delete file.", exception)
                    Toast.makeText(this, "No se pudo eliminar el archivo.", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Log.e(TAG, "mDriveServiceHelper is null.")
            Toast.makeText(this, "No hay archivo abierto", Toast.LENGTH_SHORT).show()
        }
        emptyDriveFileList()
        hideButtons()
    }

    //<-------------delete the file
    //------------->show buttons
    private fun showButtons() {

        //muestra el boton de guardar el archivo
        saveFileButton!!.visibility = View.VISIBLE
        //muestra el boton de cerrar el archivo
        closeFileButton!!.visibility = View.VISIBLE
        //muestra el boton de eliminar el archivo
        deleteFileButton!!.visibility = View.VISIBLE
    }

    //<-------------show buttons
    //------------->hide the save file button
    private fun hideButtons() {

        //hide save buttons
        saveFileButton!!.visibility = View.GONE
        saveFileOpenDriveButton!!.visibility = View.GONE
        //hide close button
        closeFileButton!!.visibility = View.GONE
        //hide delete button
        deleteFileButton!!.visibility = View.GONE
    }

    //<-------------hide the save file button
    //------------->empty EditText
    private fun emptyDriveFileList() {
        nameDriveFileList!!.text.clear()
        textDriveFileList!!.text.clear()
        nameDriveFileList!!.isEnabled = true
        textDriveFileList!!.isEnabled = true
    }

    //<-------------empty EditText
    //------------->Updates the UI to read-only mode
    private fun setReadOnlyMode() {
        textDriveFileList!!.isEnabled = false
        nameDriveFileList!!.isEnabled = false
        mOpenFileId = null
    }

    //<-------------Updates the UI to read-only mode
    //------------->Updates the UI to read/write mode on the document identified by {@code fileId}
    private fun setReadWriteMode(fileId: String) {
        nameDriveFileList!!.isEnabled = false
        textDriveFileList!!.isEnabled = true
        mOpenFileId = fileId
    }

    //<-------------Updates the UI to read/write mode on the document identified by {@code fileId}
    //------------->backButton
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    } //<-------------backButton

    companion object {
        private const val TAG = "DriveActivity"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }
}