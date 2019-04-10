package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> mNoteList = new ArrayList<>();
    NoteAdapter mCustomNoteAdapter;
    ListView mNoteListView;
    NoteStorage mNoteStorage;

    private static int NOTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNoteStorage = new NoteStorage(new DatabaseHelper(this));
        initAddNoteButton();

        initNoteView();
        showNotes();

    }

    public void setmNoteList(Note[] newNotes){
        mNoteList.clear();
        for(Note n: newNotes){
            mNoteList.add(n);
        }
    }

    public void initAddNoteButton(){
        FloatingActionButton addNoteButton = findViewById(R.id.createNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == NOTE_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Note note = (Note) data.getSerializableExtra("note");
                mNoteList.add(note);
                mNoteStorage.insert(note);
            }
            if (resultCode == RESULT_CANCELED) {
               throw new RuntimeException("Note was not saved correctly.");
            }
        }
    }

    private void initNoteView(){
        Note[] allNotes = mNoteStorage.getAll();

        setmNoteList(allNotes);

        mCustomNoteAdapter = new NoteAdapter(this, mNoteList);
        mNoteListView = findViewById(R.id.notesList);
        mNoteListView.setAdapter(mCustomNoteAdapter);
    }

    private void showNotes(){
        //noteTitles should contain title of notes. Need to be dynamically loaded.
        //This could for example be just <Note i> for i in [0 ... length of notes list]
        //or maybe the first few words of the corresponding note.

        mNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "load the note view and remove this box!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
