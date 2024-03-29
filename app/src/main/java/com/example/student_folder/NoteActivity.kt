package com.example.student_folder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NoteActivity : AppCompatActivity(), NoteClickDeleteInterface, NoteClickInterface {
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: com.github.clans.fab.FloatingActionButton
    lateinit var viewModal: NoteViewModal
    //, NoteClickDeleteInterface, NoteClickInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        notesRV = findViewById (R.id.idRVNotes)
        addFAB = findViewById (R.id.idFABAddNote)
        notesRV.layoutManager = LinearLayoutManager(this)

        //------------->backButton
        val backButton = findViewById<View>(R.id.back_button)
        backButton.setOnClickListener { onBackPressed() }
        //<-------------backButton

        val noteRVAdapter = NoteRVAdapter(this, this, this)

        notesRV.adapter = noteRVAdapter
        viewModal = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModal::class.java)
        viewModal.allNotes.observe(this, Observer{ list->
            list?.let {
                noteRVAdapter.updateList(it)
            }
        })
        addFAB.setOnClickListener {
            val intent = Intent(this@NoteActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onDeleteIconClick(note: Note) {
        viewModal.deleteNote(note)
        Toast.makeText(this, "Borrando ${note.noteTitle}..", Toast.LENGTH_LONG).show()
    }
    override fun onNoteClick(note: Note) {
        val intent = Intent(this@NoteActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType","Edit")
        intent.putExtra("noteTitle",note.noteTitle)
        intent.putExtra("noteDescription",note.noteDescription)
        intent.putExtra("noteID",note.id)
        startActivity(intent)
        this.finish()
    }

    //------------->backButton
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    //<-------------backButton
}