package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> noteList = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    ListView noteListView;
    NoteStorage noteStorage;
    private Menu menu;
    private boolean sortByCreatedDate = true;

    private static final int NOTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteStorage = new NoteStorage(new DatabaseHelper(this));
        initAddNoteButton();

        initNoteView();
        showNotes();
        createToolbar();
    }

    public void setNoteList(Note[] newNotes){
        noteList.clear();
        for(Note node : newNotes){
            noteList.add(node);
        }
    }

    public void refreshNoteList() {
        Note[] allNotes = noteStorage.getAll(sortByCreatedDate);
        setNoteList(allNotes);
        customNoteAdapter.notifyDataSetChanged();
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
                if (note.getTitle().isEmpty() && note.getContent().isEmpty())
                {
                    return;
                }
                boolean editFlag = (boolean) data.getSerializableExtra("editFlag");
                if(editFlag) {
                    try {
                        noteStorage.update(note);
                        refreshNoteList();

                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    noteStorage.insert(note);
                    refreshNoteList();
                }
            }
            if (resultCode == RESULT_CANCELED) {
               throw new RuntimeException("Note was not saved correctly.");
            }
        }
    }

    private void initNoteView(){
        Note[] allNotes = noteStorage.getAll();

        setNoteList(allNotes);

        customNoteAdapter = new NoteAdapter(this, noteList);
        noteListView = findViewById(R.id.notesList);
        noteListView.setAdapter(customNoteAdapter);
    }

    private void showNotes(){
        //noteTitles should contain title of notes. Need to be dynamically loaded.
        //This could for example be just <Note i> for i in [0 ... length of notes list]
        //or maybe the first few words of the corresponding note.

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra("note",  noteList.get(position) );
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
        });
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.mainActivityToolbar));
       /* if(getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_main_burger_button);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        this.menu = menu;
        /*MenuItem searchButton = this.menu.findItem(R.id.searchButton);
        searchButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        });*/
        MenuItem sortByTitleAscButton = this.menu.findItem(R.id.sortByTitleAscButton);
        sortByTitleAscButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortByCreatedDate = false;
                refreshNoteList();
                return true;
            }
        });
        MenuItem sortByCreatedDateDescButton = this.menu.findItem(R.id.sortByCreatedDateDescButton);
        sortByCreatedDateDescButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortByCreatedDate = true;
                refreshNoteList();
                return true;
            }
        });
        /*MenuItem importButton = this.menu.findItem(R.id.importButton);
        importButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        MenuItem exportButton = this.menu.findItem(R.id.exportButton);
        exportButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });*/
        return true;
    }
}
