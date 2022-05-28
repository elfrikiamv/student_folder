package com.example.student_folder;

import java.io.File;

public interface OnFileSelectedListener {
    void onFileClicked(File file);
    void onFileLongClicked (File file, int position);
}
