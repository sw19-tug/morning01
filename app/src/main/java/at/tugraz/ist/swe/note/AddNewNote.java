package at.tugraz.ist.swe.note;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Node;

import java.util.Calendar;

public class AddNewNote extends AppCompatActivity {

    private Menu _menu;
    EditText tfTitle;
    EditText tfContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        tfTitle = (EditText)findViewById(R.id.tfTitle);
        tfContent = (EditText)findViewById(R.id.tfContent);

        createToolbar();
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.add_new_note_toolbar));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.text_new_note);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private  void enableRemoveButton(boolean enabled){
        _menu.findItem(R.id.action_add).setVisible(!enabled);
        _menu.findItem(R.id.action_remove).setVisible(enabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        _menu = menu;
        enableRemoveButton(false);

        MenuItem addButton = _menu.findItem(R.id.action_add);
        addButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddNewNote.this.readInputFields();
                return true;
            }
        });

        MenuItem removeButton = _menu.findItem(R.id.action_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddNewNote.this.deleteNote();
                return true;
            }
        });

        MenuItem settingsButton = _menu.findItem(R.id.action_settings);
        settingsButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddNewNote.this.callSettings();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void readInputFields(){
        Toast.makeText(getApplicationContext(),"add clicked",Toast.LENGTH_SHORT).show();
        String title = tfTitle.getText().toString();
        String content = tfContent.getText().toString();

        Note note = new Note(title,content,0);
        note.setCreatedDate(Calendar.getInstance().getTime());
    }

    public void deleteNote(){
        Toast.makeText(getApplicationContext(),"delete clicked",Toast.LENGTH_SHORT).show();
    }

    public void callSettings(){
        Toast.makeText(getApplicationContext(),"settings clicked",Toast.LENGTH_SHORT).show();
    }
}