package at.tugraz.ist.swe.note;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import at.tugraz.ist.swe.note.database.DatabaseHelper;


public class NoteActivity extends AppCompatActivity {

    private Menu mMenu;
    private Note mNote;
    boolean editFlag = false;
    NoteStorage mStorage;


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



    private  void enableUnpinningButton(boolean enabled){
        mMenu.findItem(R.id.action_pinning).setVisible(!enabled);
        mMenu.findItem(R.id.action_unpinning).setVisible(enabled);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        mMenu = menu;
        enableUnpinningButton(false);


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
        OptionFlag flag = saveNote();
        startIntentMain(flag);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private OptionFlag saveNote(){
        TextView tfTitle = (TextView)findViewById(R.id.tfTitle);
        TextView tfContent = (TextView)findViewById(R.id.tfContent);
        String title = tfTitle.getText().toString();
        String content = tfContent.getText().toString();

        if (mNote.getId() == Note.ILLEGAL_ID) {
            //TODO: pinning
            mNote = new Note(title, content, 0);
            return OptionFlag.SAVE;
        } else {
                mNote.setContent(content);
                mNote.setTitle(title);
                return  OptionFlag.EDIT;


        }
    }

    private void deleteNote(){
        Toast.makeText(getApplicationContext(),"delete clicked",Toast.LENGTH_SHORT).show();
        //todo add dialog
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                NoteActivity.this);

        confirmDeleteDialog.setTitle("Confirm Delete");
        confirmDeleteDialog.setMessage("Are you sure you want delete this note?");
        confirmDeleteDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIntentMain(OptionFlag.REMOVE);

                    }
                    //onBackPressed();
                });

        confirmDeleteDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmDeleteDialog.show();
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

    private void startIntentMain(OptionFlag flag)
    {
        Intent noteIntent = new Intent();
        noteIntent.putExtra("note", mNote);
        noteIntent.putExtra("flag",flag);
        setResult(RESULT_OK, noteIntent);
        finish();
    }
}