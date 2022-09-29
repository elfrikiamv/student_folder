package com.example.student_folder;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private static final String TAG = "DriveServiceHelper";

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    //------------->upload file to drive
    public Task<String> updateFile(java.io.File file) {
        return Tasks.call(mExecutor, () -> {

            // Upload file file.getName on drive.
            File fileMetadata = new File();
            fileMetadata.setName(file.getName());

            // File's content.
            java.io.File filePath = new java.io.File(file.getAbsolutePath());

            // Specify media type and file-path for file.
            FileContent mediaContent = new FileContent("text/plain", filePath);
            try {
                File googleFile = mDriveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                Log.d(TAG, "File ID: " + googleFile.getId());
                //System.out.println("File ID: " + file.getId());
                return googleFile.getId();
            } catch (GoogleJsonResponseException e) {
                // TODO(developer) - handle error appropriately
                Log.d(TAG, "Unable to upload file: " + e.getDetails());
                //System.err.println("Unable to upload file: " + e.getDetails());
                throw e;
            }

            /*File metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType("text/plain")
                    .setName("Subiendo archivo");

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();*/
        });
    }
    //------------->upload file to drive

    //------------->Creates a text file in the user's My Drive folder and returns its file ID.
    public Task<String> createFileTxt(String name, String content) {
        return Tasks.call(mExecutor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File().setName(name + ".txt");

            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

            // Update the metadata and contents.
            File googleFile = mDriveService.files().create(metadata, contentStream).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();
        });
    }
    //<-------------Creates a text file in the user's My Drive folder and returns its file ID.

    //------------->Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and contents.
    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the metadata as a File object.
            File metadata = mDriveService.files().get(fileId).execute();
            String name = metadata.getName();

            // Stream the file contents to a String.
            try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String contents = stringBuilder.toString();

                return Pair.create(name, contents);
            }
        });
    }
    //<-------------Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and contents.

    /**
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    //------------->Returns a {@link FileList} containing all the visible files in the user's My Drive.
    public Task<FileList> queryFiles() {

        return Tasks.call(mExecutor, new Callable<FileList>() {
            @Override
            public FileList call() throws Exception {

                return mDriveService.files().list().setSpaces("drive").execute();
            }
        });
    }
    //<-------------Returns a {@link FileList} containing all the visible files in the user's My Drive.

    //------------->Returns an {@link Intent} for opening the Storage Access Framework file picker.
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }
    //<-------------Returns an {@link Intent} for opening the Storage Access Framework file picker.

    //------------->Opens the file identified by {@code uri} and returns a {@link Pair} of its nameFile
    public Task<String> getPickerFileName(ContentResolver contentResolver, Uri uri) {

        return Tasks.call(mExecutor, () -> {

            // Retrieve the document's display name from its metadata.
            String nameFile = null;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {

                    nameFile = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }

            return nameFile;
        });
    }
    //<-------------Opens the file identified by {@code uri} and returns a {@link Pair} of its nameFile

    //------------->Updates the file identified by {@code fileId} with the given {@code name} and {@code content}.
    public Task<String> saveFileCreateDrive(String mOpenFileId, String fileName, String fileContent) {
        return Tasks.call(mExecutor, () -> {

            // Create a File containing any metadata changes.
            File metadata = new File().setName(fileName);

            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", fileContent);

            // Update the metadata and contents.
            File googleFile = mDriveService.files().update(mOpenFileId, metadata, contentStream).execute();
            if (googleFile == null) {

                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();
        });
    }
    //<-------------Updates the file identified by {@code fileId} with the given {@code name} and {@code content}.

    //---------------->get the id of the file through its name
    public Task<String> getIdForName(String fileName) {

        return Tasks.call(mExecutor, () -> {

            // Create a File containing any metadata changes.
            mDriveService.files().list().setSpaces("drive").execute();
            FileList result = mDriveService.files().list()
                    .setQ("name = '" + fileName + "'")
                    .setSpaces("drive")
                    .execute();

            List<File> files = result.getFiles();
            if (files != null && files.size() > 0) {

                return files.get(0).getId();
            } else {

                return null;
            }
        });
    }
    //<----------------get the id of the file through its name

    //---------------->delete the file through its id
    public Task<String> deleteFile(String mOpenFileId) {

        return Tasks.call(mExecutor, () -> {

            mDriveService.files().delete(mOpenFileId).execute();
            return null;
        });
    }
    //<----------------delete the file through its id
}