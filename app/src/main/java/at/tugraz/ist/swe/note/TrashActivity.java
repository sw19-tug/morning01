package at.tugraz.ist.swe.note;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

public class TrashActivity extends AppCompatActivity {
    ArrayList<Note> noteList = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    ListView noteListView;


    NoteStorage noteStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        noteStorage = new NoteStorage(new DatabaseHelper(getApplicationContext()));

        setSupportActionBar((Toolbar) findViewById(R.id.TrashActivityToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initNoteView();


    }

    public void refreshNoteList() {
        Note[] allNotes = noteStorage.getAll(true, true);
        setNoteList(allNotes);
        customNoteAdapter.notifyDataSetChanged();
    }

    private void initNoteView() {
        Note[] allNotes = noteStorage.getAll(true, true);

        setNoteList(allNotes);

        customNoteAdapter = new NoteAdapter(this, noteList);
        noteListView = findViewById(R.id.trashList);
        noteListView.setAdapter(customNoteAdapter);
    }

    public void setNoteList(Note[] newNotes) {
        noteList.clear();
        for (Note node : newNotes) {
            noteList.add(node);
        }
    }



    public void setNoteStorage(NoteStorage noteStorage) {
        this.noteStorage = noteStorage;
    }

}
