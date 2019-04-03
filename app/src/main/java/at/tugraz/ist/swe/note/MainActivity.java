package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAddNoteButton();
        showNotes();

    }

    public void initAddNoteButton(){
        FloatingActionButton addNoteButton = findViewById(R.id.createNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void showNotes(){
        //noteTitles should contain title of notes. Need to be dynamically loaded.
        //This could for example be just <Note i> for i in [0 ... length of notes list]
        //or maybe the first few words of the corresponding note.
        noteList.add(new Note("title1", "content1", 1));

        ArrayAdapter<String> noteListViewAdapter = new ArrayAdapter<>(this, R.layout.note_list_item, getArrayOfTitles());
        ListView noteListView = findViewById(R.id.notesList);
        noteListView.setAdapter(noteListViewAdapter);
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "load the note view and remove this box!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private String[] getArrayOfTitles(){
        String[] titles = new String[noteList.size()];
        for (int i = 0; i < noteList.size(); ++i){
            titles[i] = noteList.get(i).getTitle();
        }
        return titles;
    }

}
