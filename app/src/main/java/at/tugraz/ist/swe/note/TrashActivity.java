package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class TrashActivity extends AppCompatActivity {
    ArrayList<Note> noteList = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    ListView noteListView;
    public static final int NOTE_RESTORE_CODE = 2;
    public static final String NOTE_KEY = "note";
    public static final String REQUEST_CODE_KEY = "request_code";
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

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra(NOTE_KEY, noteList.get(position));
                intent.putExtra(REQUEST_CODE_KEY, NOTE_RESTORE_CODE);
                startActivityForResult(intent, NOTE_RESTORE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTE_RESTORE_CODE) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra(NoteActivity.NOTE_KEY);
                if (note.getTitle().isEmpty() && note.getContent().isEmpty()) {
                    return;
                }
                OptionFlag flag = (OptionFlag) data.getSerializableExtra(NoteActivity.FLAG_KEY);

                switch (flag) {
                    case REMOVE:
                        try {
                            noteStorage.delete(note.getId());
                            refreshNoteList();
                        }
                        catch (NotFoundException e){
                            e.printStackTrace();
                        }
                        break;
                    case RESTORE:
                        try {
                            noteStorage.restore(note.getId());
                            refreshNoteList();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        refreshNoteList();


                }
            }
        }
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
