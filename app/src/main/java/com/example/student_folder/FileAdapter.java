package com.example.student_folder;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private Context context;
    private List<File> file;
    private OnFileSelectedListener listener;


    public FileAdapter(Context context, List<File> file, OnFileSelectedListener listener) {
        this.context = context;
        this.file = file;
        this.listener = listener;

    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.tvName.setText(file.get(position).getName());
        holder.tvName.setSelected(true);
        int items = 0;
        if (file.get(position).isDirectory()) {
            File[] files = file.get(position).listFiles();
            for (File singleFile : files) {
                if (!singleFile.isHidden()) {
                    items += 1;
                }
            }
            holder.tvSize.setText(String.valueOf(items) + " Archivos");
        } else {
            holder.tvSize.setText(Formatter.formatShortFileSize(context, file.get(position).length()));
        }
        if (file.get(position).getName().toLowerCase().endsWith(".rtf")) {
            holder.imgFile.setImageResource(R.drawable.ic_rtf_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".jpg")) {
            holder.imgFile.setImageResource(R.drawable.ic_jpg_file);
        } else if (file.get(position).getName().toLowerCase().endsWith("png")) {
            holder.imgFile.setImageResource(R.drawable.ic_png_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".pdf")) {
            holder.imgFile.setImageResource(R.drawable.ic_pdf_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".docx")) {
            holder.imgFile.setImageResource(R.drawable.ic_docx_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".txt")) {
            holder.imgFile.setImageResource(R.drawable.ic_txt_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".pptx")) {
            holder.imgFile.setImageResource(R.drawable.ic_ppt_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".xlsx")) {
            holder.imgFile.setImageResource(R.drawable.ic_xls_file);
        } else if (file.get(position).getName().toLowerCase().endsWith(".csv")) {
            holder.imgFile.setImageResource(R.drawable.ic_csv_file);
        } else {
            holder.imgFile.setImageResource(R.drawable.ic_folder_file);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFileClicked (file.get(position));
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onFileLongClicked (file.get(position), position);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return file.size();
    }
}
