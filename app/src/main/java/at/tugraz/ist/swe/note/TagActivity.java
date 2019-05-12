package at.tugraz.ist.swe.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import at.tugraz.ist.swe.note.NoteTag;
import at.tugraz.ist.swe.note.NoteTagStorage;
import at.tugraz.ist.swe.note.OptionFlag;
import at.tugraz.ist.swe.note.R;

public class TagActivity extends AppCompatActivity {
    public final static String TAG_KEY = "tag";
    public final static String FLAG_KEY = "tag_flag";
    private Menu menu;
    private NoteTag tag;
    boolean editFlag = false;
    NoteTagStorage storage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tag = (NoteTag) getIntent().getSerializableExtra(TAG_KEY);
        if(tag == null){
            tag = new NoteTag();
        }

        TextView tagNameTextView = findViewById(R.id.tagNameEditText);
        tagNameTextView.setText(tag.getName());
        tagNameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tag.setName(s.toString());
            }
        });

        createToolbar();
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.add_new_tag_toolbar));
        if(getSupportActionBar() != null) {
            if(tag.getName().equals(""))
                getSupportActionBar().setTitle(R.string.text_new_tag);
            else
                getSupportActionBar().setTitle(R.string.text_edit_tag);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_tag, menu);
        this.menu = menu;

        MenuItem removeButton = this.menu.findItem(R.id.action_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        OptionFlag flag = saveNoteTag();
        startIntentMain(flag);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private OptionFlag saveNoteTag(){
        TextView tagNameEditText = (TextView)findViewById(R.id.tagNameEditText);
        String name = tagNameEditText.getText().toString();

        if (tag.getId() == NoteTag.ILLEGAL_ID) {
            tag = new NoteTag(name, Color.BLACK);
            return OptionFlag.SAVE;
        }
        return OptionFlag.SAVE;
    }

    private void startIntentMain(OptionFlag flag)
    {
        Intent tagIntent = new Intent();
        tagIntent.putExtra(TAG_KEY, tag);
        tagIntent.putExtra(FLAG_KEY, flag);
        setResult(RESULT_OK, tagIntent);
        finish();
    }
}
