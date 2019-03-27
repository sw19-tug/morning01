package at.tugraz.ist.swe.note;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAddNoteButton();
        initNotesList();

    }

    public void initAddNoteButton(){
        FloatingActionButton addNoteButton = findViewById(R.id.createNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "load the create_note view and remove this box!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initNotesList(){
        //noteTitles should contain title of notes. Need to be dynamically loaded.
        //This could for example be just <Note i> for i in [0 ... length of notes list]
        //or maybe the first few words of the corresponding note.
        String[] noteTitles = new String[]{"Note 1", "Note 2", "Note 3", "Note 4", "Note 5",
                "Note 6", "Note 7", "Note 8", "Note 9", "Note 10", "Note 11", "Note 12"};

        ArrayAdapter<String> noteListViewAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, noteTitles);
        ListView noteListView = findViewById(R.id.notesList);
        noteListView.setAdapter(noteListViewAdapter);
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "load the note view and remove this box!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
