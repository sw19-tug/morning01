package at.tugraz.ist.swe.note;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class ProtectedActivity  extends AppCompatActivity {
    ArrayList<Note> noteList = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    ListView noteListView;
    public static final int NOTE_PROTECT_CODE = 1;
    public static final String NOTE_KEY = "note";
    public static final String REQUEST_CODE_KEY = "request_code";
    NoteStorage noteStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protected);

        noteStorage = new NoteStorage(new DatabaseHelper(getApplicationContext()));

        setSupportActionBar((Toolbar) findViewById(R.id.ProtectedActivityToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initNoteView();

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra(NOTE_KEY, noteList.get(position));
                intent.putExtra(REQUEST_CODE_KEY, NOTE_PROTECT_CODE);
                startActivityForResult(intent, NOTE_PROTECT_CODE);
            }
        });
        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                restoreNote(i);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTE_PROTECT_CODE) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra(NoteActivity.NOTE_KEY);
                if (note.getTitle().isEmpty() && note.getContent().isEmpty()) {
                    return;
                }
                OptionFlag flag = (OptionFlag) data.getSerializableExtra(NoteActivity.FLAG_KEY);

                switch (flag) {
                    case PROTECT:
                        try {
                            noteStorage.delete(note.getId());
                            refreshNoteList();
                        }
                        catch (NotFoundException e){
                            e.printStackTrace();
                        }
                        break;
                    case UNPROTECT:
                        try {
                            noteStorage.unprotect(note.getId());
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
        Note[] allNotes = noteStorage.getAll(true, false, true);
        setNoteList(allNotes);
        customNoteAdapter.notifyDataSetChanged();
    }

    private void initNoteView() {
        Note[] allNotes = noteStorage.getAll(true, false, true);

        setNoteList(allNotes);

        customNoteAdapter = new NoteAdapter(this, noteList);
        noteListView = findViewById(R.id.protectedList);
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


    private void restoreNote(final int position){
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                ProtectedActivity.this);

        confirmDeleteDialog.setTitle("Confirm unprotection");
        confirmDeleteDialog.setMessage("Are you sure you want move the note to the unprotected view?");
        confirmDeleteDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            noteStorage.restore(noteList.get(position).getId());
                            refreshNoteList();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

        confirmDeleteDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmDeleteDialog.show();
    }

}