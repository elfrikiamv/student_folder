package com.example.student_folder.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_folder.FileAdapter;
import com.example.student_folder.FileOpener;
import com.example.student_folder.OnFileSelectedListener;
import com.example.student_folder.R;
import com.example.student_folder.WebActivitys.MeetActivity;
import com.example.student_folder.WebActivitys.TeamsActivity;
import com.example.student_folder.WebActivitys.ZoomActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements OnFileSelectedListener {

    FloatingActionMenu actionMenu;
    GoogleSignInClient mGoogleSignInClient;
    private WebView webView;

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private LinearLayout linearImage, linearDocs, linearDownloads;
    File storage;
    String data;
    String[] items = {"Renombrar", "Borrar", "Enviar"};
    View view;

    //SignInButton signin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_home, container, false);
        //return inflater.inflate(R.layout.fragment_home, container, false);
        //View view = inflater.inflate(R.layout.fragment_home, container,false);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //floating menu
        actionMenu = (FloatingActionMenu) view.findViewById(R.id.fabPrincipal);
        actionMenu.setClosedOnTouchOutside(true);

        /*
        //fondiwis
        //LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.linear_home);
        //LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.linear_home);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_home);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //fondiwis

         */

        linearImage= view.findViewById(R.id.linearImage);
        linearDocs = view.findViewById(R.id.linearDocs);
        linearDownloads = view.findViewById(R.id.linearDownloads);


        linearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle ();
                args.putString("fileType", "image");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        linearDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle ();
                args.putString("fileType", "docs");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });
        linearDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle ();
                args.putString("fileType", "downloads");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });
        /*
        //byttomDrive
        //View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button btnDriveActivity = (Button) view.findViewById(R.id.btnDrive);
        btnDriveActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DriveActivity.class);
                startActivity(intent);
            }
        });

        Button btnNoteActivity = (Button) view.findViewById(R.id.btnNote);
        btnNoteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                startActivity(intent);
            }
        });

         */
        /*
        Button btnTestActivity = (Button) view.findViewById(R.id.btnTest);
        btnTestActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);

            }
        });

         */

        FloatingActionButton MenuWebTeams = (FloatingActionButton) view.findViewById(R.id.menu_web_teams);
        MenuWebTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), TeamsActivity.class));

                Toast.makeText(getActivity(), "Abriendo la web Microsoft Teams..", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        FloatingActionButton MenuWebMeet = (FloatingActionButton) view.findViewById(R.id.menu_web_meet);
        MenuWebMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), MeetActivity.class));

                Toast.makeText(getActivity(), "Abriendo la web Google Meet..", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        FloatingActionButton MenuWebZoom = (FloatingActionButton) view.findViewById(R.id.menu_web_zoom);
        MenuWebZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), ZoomActivity.class));

                Toast.makeText(getActivity(), "Abriendo la web Zoom..", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        runtimePermission();
        return view;
    }

/*
    //floating menu
    public void clicSubMenu1 (View view) {
        Toast.makeText(getActivity(), "Sub Menu 1 tocado", Toast.LENGTH_SHORT).show();
        actionMenu.close(true);
    }

/*
    public void clicSubMenu2 (View view) {
        Toast.makeText(getActivity(), "Sub Menu 2 tocado", Toast.LENGTH_SHORT).show();
        actionMenu.close(true);
    }

     */
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
                arrayList.addAll(findFiles(singleFile));
            }
            else if(singleFile.getName().toLowerCase().endsWith(".rtf") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png") ||
                    singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".pptx") || singleFile.getName().toLowerCase().endsWith(".xlsx") ||
                    singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".csv") || singleFile.getName().toLowerCase().endsWith(".docx"))
            {
                arrayList.add(singleFile);
            }
        }
        arrayList.sort(Comparator.comparing(File::lastModified).reversed());
        return arrayList;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_recents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(Environment.getExternalStorageDirectory()));
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
                        detailDialog.setTitle("Details:");
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
                        renameDialog.setTitle("Renombrar archivo:");
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
                        deleteDialog.setTitle("¿Quiere borrar "+ file.getName() + "?");
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

                        String fileName = file.getName();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file));
                        //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(share, "Enviar: " + fileName));
                        break;
                }
            }
        });

    }



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
