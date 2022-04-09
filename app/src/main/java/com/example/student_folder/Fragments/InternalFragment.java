package com.example.student_folder.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_folder.DriveServiceHelper;
import com.example.student_folder.FileAdapter;
import com.example.student_folder.FileOpener;
import com.example.student_folder.OnFileSelectedListener;
import com.example.student_folder.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class InternalFragment extends Fragment implements OnFileSelectedListener {

    DriveServiceHelper driveServiceHelper;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private ImageView img_back;
    private TextView tv_pathHolder;
    File storage;
    String data;
    String[] items = {"Renombrar", "Borrar", "Enviar"};

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_internal, container, false);

        tv_pathHolder = view.findViewById(R.id.tv_pathHolder);
        img_back = view.findViewById(R.id.img_back);

        String internalStorage = System.getenv("EXTERNAL_STORAGE");
        storage = new File(internalStorage);

        try {
            data = getArguments().getString("path");
            File file = new File(data);
            storage = file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_pathHolder.setText(storage.getAbsolutePath());

        runtimePermission();

        return view;
    }

    private void runtimePermission() {

        Dexter.withContext(getContext()).withPermissions(
                //Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();

    }

    public ArrayList<File> findFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.add(singleFile);
            }
        }
        for (File singleFile : files) {
            if (singleFile.getName().toLowerCase().endsWith(".rtf") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png") ||
                    singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".pptx") || singleFile.getName().toLowerCase().endsWith(".xlsx") ||
                    singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".csv") || singleFile.getName().toLowerCase().endsWith(".docx")) {
                arrayList.add(singleFile);
            }
        }
        return arrayList;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(storage));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalFragment internalFragment = new InternalFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();

        } else {
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options = (ListView) optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();


        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                switch (selectedItem) {
                    case "Details":
                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Detalles: ");
                        final TextView details = new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified = new Date(file.lastModified());

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
                        String formattedDate = formatter.format(lastModified);

                        details.setText("Nombre del archivo: " + file.getName() + "\n" +
                                "Tamaño: " + Formatter.formatShortFileSize(getContext(), file.length()) + "\n" +
                                "Ruta: " + file.getAbsolutePath() + "\n" +
                                "Última modificación: " + formattedDate);


                        detailDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });

                        AlertDialog alertdialog_detail = detailDialog.create();
                        alertdialog_detail.show();
                        break;

                    case "Renombrar":
                        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Renombrar archivo: ");
                        final EditText name = new EditText(getContext());
                        renameDialog.setView(name);

                        renameDialog.setPositiveButton("Hecho", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String new_name = name.getEditableText().toString();
                                String extention =file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current = new File(file.getAbsolutePath());
                                File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extention);
                                if (current.renameTo(destination)) {
                                    fileList.set(position, destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Renombrado :D", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Algo fallo ):", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        renameDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertdialog_rename = renameDialog.create();
                        alertdialog_rename.show();
                        break;

                    case "Borrar":
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder (getContext());
                        deleteDialog.setTitle("¿Quiere borrar "+ file.getName() + "? ");
                                deleteDialog.setPositiveButton( "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            file.delete();
                            fileList.remove(position);
                            fileAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Borrado", Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleteDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            optionDialog.cancel();
                        }
                    });
                        AlertDialog alertDialog_delete = deleteDialog.create();
                        alertDialog_delete.show();
                        break;

                    case "Enviar":

                        //requestSingIn();
                        //uploadPdfFile();
                        String fileName = file.getName();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file));
                        //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(share, "Enviar: " + fileName));
                        break;

                       /*
                        String filePath = file.getPath();
                        Drive mDriveService;


                        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                        fileMetadata.setName("Amv.pdf");
                        java.io.File file = new java.io.File(filePath);
                        FileContent mediaContent = new FileContent("application/pdf", file);
                        com.google.api.services.drive.model.File myFile = null;
                        try {
                            myFile = mDriveService.files().create(fileMetadata, mediaContent)
                                    .setFields("id")
                                    .execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //System.out.println("File ID: " + file.getId());
                        if (myFile == null){
                            throw new IOException("No se pudo crear tu archivo ):");

                        }
                        return myFile.getId();
                        //---------------------------------------------
                       driveServiceHelper.createFilePDF(filePath).addOnSuccessListener(new OnSuccessListener<String>() {

                            @Override
                            public void onSuccess(@NonNull String s) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),"Si se pudo!!",Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(),"No se conecto a Drive :#",Toast.LENGTH_LONG).show();

                                    }
                                });
                            break;

                        /*private void saveFileToDrive() {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // File's binary content
                                    java.io.File fileContent = new java.io.File(fileUri.getPath());
                                    FileContent mediaContent = new FileContent("image/jpeg", fileContent);

                                    // File's metadata.
                                    File body = new File();
                                    body.setTitle(fileContent.getName());
                                    body.setMimeType("image/jpeg");

                                    File file = service.files().insert(body, mediaContent).execute();
                                    if (file != null) {
                                        showToast("Photo uploaded: " + file.getTitle());
                                        startCameraIntent();
                                    }
                                } catch (UserRecoverableAuthIOException e) {
                                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.start();
                    }

                        File fileMetadata = new File();
                        fileMetadata.setName(file.getName());
                        java.io.File filePath = new java.io.File(file.getPath());
                        FileContent mediaContent = new FileContent("application/pdf", filePath);
                        File file = driveService.files().create(fileMetadata, mediaContent).execute();
                        break;

                    /*case "Share":
                        String fileName = file.getName();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file));
                        //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(share, "Respaldar " + fileName));
                        break;*/
                }
            }
        });

    }


    /*
    private void requestSingIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        //GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        GoogleSignInClient client = GoogleSignIn.getClient(getActivity(),signInOptions);
        startActivityForResult(client.getSignInIntent(), 400);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 400:
                if(resultCode == RESULT_OK)
                {
                    handleSignInIntent(data);
                }
                break;
        }
    }

    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(@NonNull GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(), Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("Respaldo Drive")
                                .build();
                        driveServiceHelper = new DriveServiceHelper(googleDriveService);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void uploadPdfFile() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle ("Estamos subiendo tu archivo a Drive :D");
        progressDialog.setMessage("Espera un poco!");
        progressDialog.show();

        String filePath = "/sdcard/-mexico-reglamento-de-insumos-para-la-salud-es.pdf";

        driveServiceHelper.createFilePDF(filePath).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String s) {
                progressDialog.dismiss();
                Toast.makeText (getContext(),"Si se pudo!!",Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"No se conecto a Drive :#",Toast.LENGTH_LONG).show();

                    }
                });
    }
    /*public void uploadPdfFile (View v){
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle ("Estamos subiendo tu archivo a Drive :D");
        progressDialog.setMessage("Espera un poco!");
        progressDialog.show();
        String filePath = "/sdcard/-mexico-reglamento-de-insumos-para-la-salud-es.pdf";

        driveServiceHelper.createFilePDF(filePath).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String s) {
                progressDialog.dismiss();
                Toast.makeText (getContext(),"Si se pudo!!",Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"No se conecto a Drive :#",Toast.LENGTH_LONG).show();

                    }
                });
    }
    */

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView txtOptions = myView.findViewById(R.id.txt0ption);
            ImageView imgOption = myView.findViewById(R.id.imgOption);
            txtOptions.setText(items[i]);
            if (items[i].equals("Details")) {
                imgOption.setImageResource(R.drawable.ic_details_f);
            }
            else if (items[i].equals("Renombrar")) {
                imgOption.setImageResource(R.drawable.ic_rename_f);
            }
            else if (items[i].equals("Borrar")) {
                imgOption.setImageResource(R.drawable.ic_delete_f);
            }
            else if (items[i].equals("Enviar")) {
                imgOption.setImageResource(R.drawable.ic_share_f);
            }
            return myView;
        }
    }
}