package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> mNoteList = new ArrayList<>();
    NoteAdapter mCustomNoteAdapter;
    ListView mNoteListView;
    NoteStorage mNoteStorage;
    private Menu mMenu;


    private static int NOTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNoteStorage = new NoteStorage(new DatabaseHelper(this));
        initAddNoteButton();

        initNoteView();
        showNotes();
        createToolbar();

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
                boolean editFlag = (boolean) data.getSerializableExtra("editFlag");
                if(editFlag)
                {
                    try {

                        mNoteStorage.update(note);
                        recreate();

                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    mNoteList.add(note);
                    mNoteStorage.insert(note);
                }

            }
            if (resultCode == RESULT_CANCELED) {
               //TODO handle error
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
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra("note",  mNoteList.get(position) );
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
        });
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.mainActivityToolbar));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_main_burger_button);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        mMenu = menu;
        return true;
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        mMenu = menu;


        MenuItem searchButton = mMenu.findItem(R.id.searchButton);
        searchButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        MenuItem sortButton = mMenu.findItem(R.id.sortButton);
        sortButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        MenuItem importButton = mMenu.findItem(R.id.importButton);
        importButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        MenuItem exportButton = mMenu.findItem(R.id.exportButton);
        exportButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        return true;
    }*/


}
