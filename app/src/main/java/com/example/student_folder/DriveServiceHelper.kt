package com.example.student_folder

import com.google.api.services.drive.Drive
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.FileContent
import com.example.student_folder.DriveServiceHelper
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.model.FileList
import kotlin.Throws
import android.content.Intent
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.util.Pair
import com.google.android.gms.tasks.Task
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    //------------->upload file to drive
    fun updateFile(file: File): Task<String> {
        return Tasks.call(mExecutor) {


            // Upload file file.getName on drive.
            val fileMetadata = com.google.api.services.drive.model.File()
            fileMetadata.name = file.name

            // File's content.
            val filePath = File(file.absolutePath)

            // Specify media type and file-path for file.
            val mediaContent = FileContent("text/plain", filePath)
            try {
                val googleFile = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
                Log.d(TAG, "File ID: " + googleFile.id)
                //System.out.println("File ID: " + file.getId());
                return@call googleFile.id
            } catch (e: GoogleJsonResponseException) {
                // TODO(developer) - handle error appropriately
                Log.d(TAG, "Unable to upload file: " + e.details)
                throw e
            }
        }
    }

    //------------->upload file to drive
    //------------->Creates a text file in the user's My Drive folder and returns its file ID.
    fun createFileTxt(name: String, content: String?): Task<String> {
        return Tasks.call(mExecutor) {

            // Create a File containing any metadata changes.
            val metadata = com.google.api.services.drive.model.File().setName("$name.txt")

            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", content)

            // Update the metadata and contents.
            val googleFile = mDriveService.files().create(metadata, contentStream).execute()
                ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        }
    }

    //<-------------Creates a text file in the user's My Drive folder and returns its file ID.
    //------------->Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and contents.
    fun readFile(fileId: String?): Task<Pair<String, String>> {
        return Tasks.call(mExecutor) {

            // Retrieve the metadata as a File object.
            val metadata = mDriveService.files()[fileId].execute()
            val name = metadata.name
            mDriveService.files()[fileId].executeMediaAsInputStream().use { `is` ->
                BufferedReader(
                    InputStreamReader(`is`)
                ).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    val contents = stringBuilder.toString()
                    return@call Pair.create(name, contents)
                }
            }
        }
    }
    //<-------------Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and contents.
    /**
     *
     * The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the [Google
 * Developer's Console](https://play.google.com/apps/publish) and be submitted to Google for verification.
     */
    //------------->Returns a {@link FileList} containing all the visible files in the user's My Drive.
    fun queryFiles(): Task<FileList> {
        return Tasks.call(mExecutor) { mDriveService.files().list().setSpaces("drive").execute() }
    }

    //<-------------Returns a {@link FileList} containing all the visible files in the user's My Drive.
    //------------->Returns an {@link Intent} for opening the Storage Access Framework file picker.
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        return intent
    }

    //<-------------Returns an {@link Intent} for opening the Storage Access Framework file picker.
    //------------->Opens the file identified by {@code uri} and returns a {@link Pair} of its nameFile
    fun getPickerFileName(contentResolver: ContentResolver, uri: Uri?): Task<String?> {
        return Tasks.call(mExecutor) {


            // Retrieve the document's display name from its metadata.
            var nameFile: String? = null
            contentResolver.query(uri!!, null, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    nameFile = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
            nameFile
        }
    }

    //<-------------Opens the file identified by {@code uri} and returns a {@link Pair} of its nameFile
    //------------->Updates the file identified by {@code fileId} with the given {@code name} and {@code content}.
    fun saveFileCreateDrive(
        mOpenFileId: String?,
        fileName: String?,
        fileContent: String?
    ): Task<String> {
        return Tasks.call(mExecutor) {


            // Create a File containing any metadata changes.
            val metadata = com.google.api.services.drive.model.File().setName(fileName)

            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", fileContent)

            // Update the metadata and contents.
            val googleFile =
                mDriveService.files().update(mOpenFileId, metadata, contentStream).execute()
                    ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        }
    }

    //<-------------Updates the file identified by {@code fileId} with the given {@code name} and {@code content}.
    //---------------->get the id of the file through its name
    fun getIdForName(fileName: String): Task<String> {
        return Tasks.call(mExecutor) {


            // Create a File containing any metadata changes.
            mDriveService.files().list().setSpaces("drive").execute()
            val result = mDriveService.files().list()
                .setQ("name = '$fileName'")
                .setSpaces("drive")
                .execute()
            val files = result.files
            if (files != null && files.size > 0) {
                return@call files[0].id
            } else {
                return@call null
            }
        }
    }

    //<----------------get the id of the file through its name
    //---------------->delete the file through its id
    fun deleteFile(mOpenFileId: String?): Task<String?> {
        return Tasks.call(mExecutor) {
            mDriveService.files().delete(mOpenFileId).execute()
            null
        }
    } //<----------------delete the file through its id

    companion object {
        private const val TAG = "DriveServiceHelper"
    }
}