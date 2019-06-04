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

import at.tugraz.ist.swe.note.database.DatabaseHelper;


public class NoteActivity extends AppCompatActivity {
    public final static String NOTE_KEY = "note";
    public final static String FLAG_KEY = "flag";
    private Menu menu;
    private Note note;
    NoteStorage storage;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        storage = new NoteStorage(new DatabaseHelper(getApplicationContext()));

        note =  (Note) getIntent().getSerializableExtra(NOTE_KEY);
        if(note == null){
            note = new Note();
        }
        requestCode = getIntent().getIntExtra(TrashActivity.REQUEST_CODE_KEY, 0);

        TextView tfTitle = findViewById(R.id.tfTitle);
        tfTitle.setText(note.getTitle());
        tfTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setTitle(s.toString());
            }
        });

        TextView tfContent = findViewById(R.id.tfContent);
        tfContent.setText(note.getContent());
        tfContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setContent(s.toString());
            }
        });
        if (requestCode == TrashActivity.NOTE_RESTORE_CODE){
            tfTitle.setEnabled(false);
            tfContent.setEnabled(false);
        }
        createToolbar();
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.add_new_note_toolbar));
        if(getSupportActionBar() != null) {
            if(note.getTitle().equals(""))
                getSupportActionBar().setTitle(R.string.text_new_note);
            else
                getSupportActionBar().setTitle(note.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private  void enableUnpinningButton(boolean enabled){
        menu.findItem(R.id.action_pinning).setVisible(!enabled);
        menu.findItem(R.id.action_unpinning).setVisible(enabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        this.menu = menu;
        enableUnpinningButton(false);

        MenuItem removeButton = this.menu.findItem(R.id.action_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.deleteNote();
                return true;
            }
        });

        MenuItem pinningButton = this.menu.findItem(R.id.action_pinning);
        pinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.pinningNote();
                return true;
            }
        });

        MenuItem unpinningButton = this.menu.findItem(R.id.action_unpinning);
        unpinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.unpinningNote();
                return true;
            }
        });

        if (note.getPinned() > 0){
            enableUnpinningButton(true);
        }
        else {
            enableUnpinningButton(false);
        }

        MenuItem shareButton = this.menu.findItem(R.id.action_share);
        shareButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.callShare();
                return true;
            }
        });

        MenuItem restoreButton = this.menu.findItem(R.id.action_restore);
        restoreButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                restoreNote();
                return true;
            }
        });

        if (requestCode == TrashActivity.NOTE_RESTORE_CODE) {
            shareButton.setVisible(false);
            pinningButton.setVisible(false);
        }else{
            restoreButton.setVisible(false);
        }

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
        TextView tfTitle = findViewById(R.id.tfTitle);
        TextView tfContent = findViewById(R.id.tfContent);
        String title = tfTitle.getText().toString();
        String content = tfContent.getText().toString();

        if (note.getId() == Note.ILLEGAL_ID) {
            note.setContent(content);
            note.setTitle(title);
            return OptionFlag.SAVE;
        } else {
            note.setContent(content);
            note.setTitle(title);
            return OptionFlag.EDIT;
        }
    }

    private void deleteNote(){
        String title = "Confirm Delete";
        String message;
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                NoteActivity.this);
        if (requestCode == TrashActivity.NOTE_RESTORE_CODE){
            message = "Are you sure you want permanently delete this note?";
        }else{
            message = "Are you sure you want send this note to trash?";
        }
        confirmDeleteDialog.setTitle(title);
        confirmDeleteDialog.setMessage(message);
        confirmDeleteDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == TrashActivity.NOTE_RESTORE_CODE){
                            startIntentMain(OptionFlag.REMOVE);
                        }else{
                            startIntentMain(OptionFlag.SOFT_REMOVE);
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

    private void restoreNote(){
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                NoteActivity.this);

        confirmDeleteDialog.setTitle("Confirm Restore");
        confirmDeleteDialog.setMessage("Are you sure you want restore this note?");
        confirmDeleteDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIntentMain(OptionFlag.RESTORE);

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

    private void pinningNote(){
        note.setPinned(getCurrentPinningNumber());
        enableUnpinningButton(true);
    }

    private void unpinningNote(){
        note.setPinned(0);
        enableUnpinningButton(false);
    }

    private void callShare(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, note.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, note.getContent());
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }

    private void startIntentMain(OptionFlag flag)
    {
        Intent noteIntent = new Intent();
        noteIntent.putExtra(NOTE_KEY, note);
        noteIntent.putExtra(FLAG_KEY, flag);
        setResult(RESULT_OK, noteIntent);
        finish();
    }

    public Note getNote() {
        return note;
    }

    int getCurrentPinningNumber(){
        return storage.getNewPinningNumber();
    }
}