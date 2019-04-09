package at.tugraz.ist.swe.note;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;


public class NoteActivity extends AppCompatActivity {

    private Menu mMenu;
    private Note mNote;
    private NoteStorage mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mStorage = new NoteStorage(new DatabaseHelper(getApplicationContext()));
        mNote =  (Note) getIntent().getSerializableExtra("note");
        if(mNote == null){
            mNote = new Note();
        }

        TextView tfTitle = findViewById(R.id.tfTitle);
        tfTitle.setText(mNote.getTitle());
        tfTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableRemoveButton(false);
                mNote.setTitle(s.toString());
            }
        });

        TextView tfContent = findViewById(R.id.tfContent);
        tfContent.setText(mNote.getContent());
        tfContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableRemoveButton(false);
                mNote.setContent(s.toString());
            }
        });

        createToolbar();
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.add_new_note_toolbar));
        if(getSupportActionBar() != null) {
            if(mNote.getTitle().equals(""))
                getSupportActionBar().setTitle(R.string.text_new_note);
            else
                getSupportActionBar().setTitle(mNote.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private  void enableRemoveButton(boolean enabled){
        mMenu.findItem(R.id.action_add).setVisible(!enabled);
        mMenu.findItem(R.id.action_remove).setVisible(enabled);
    }

    private  void enableUnpinningButton(boolean enabled){
        mMenu.findItem(R.id.action_pinning).setVisible(!enabled);
        mMenu.findItem(R.id.action_unpinning).setVisible(enabled);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        mMenu = menu;
        enableRemoveButton(false);
        enableUnpinningButton(false);

        MenuItem addButton = mMenu.findItem(R.id.action_add);
        addButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.saveNote();
                return true;
            }
        });

        MenuItem removeButton = mMenu.findItem(R.id.action_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.deleteNote();
                return true;
            }
        });

        MenuItem pinningButton = mMenu.findItem(R.id.action_pinning);
        pinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.pinningNote();
                return true;
            }
        });

        MenuItem unpinningButton = mMenu.findItem(R.id.action_unpinning);
        unpinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.unpinningNote();
                return true;
            }
        });

        MenuItem shareButton = mMenu.findItem(R.id.action_share);
        shareButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.callShare();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        saveNote();
        Intent noteIntent = new Intent();
        noteIntent.putExtra("note", mNote);
        setResult(RESULT_OK, noteIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void saveNote(){
        //Toast.makeText(getApplicationContext(),"save clicked",Toast.LENGTH_SHORT).show();
        enableRemoveButton(true);

        if (mNote.getId() == Note.ILLEGAL_ID) {
            Log.d("NoteActivity", "insert");
            mStorage.insert(mNote);
        } else {

            try {
                mStorage.update(mNote);
                Log.d("NoteActivity", "update");
            }
            catch (NotFoundException e) {

            }
        }
        this.finish();
    }

    private void deleteNote(){
        Toast.makeText(getApplicationContext(),"delete clicked",Toast.LENGTH_SHORT).show();
        //todo add dialog
        enableRemoveButton(false);
    }

    private void pinningNote(){
        Toast.makeText(getApplicationContext(),"pinning clicked",Toast.LENGTH_SHORT).show();
        enableUnpinningButton(true);
    }

    private void unpinningNote(){
        Toast.makeText(getApplicationContext(),"unpinning clicked",Toast.LENGTH_SHORT).show();
        enableUnpinningButton(false);
    }

    private void callShare(){
        Toast.makeText(getApplicationContext(),"share clicked",Toast.LENGTH_SHORT).show();
    }
}