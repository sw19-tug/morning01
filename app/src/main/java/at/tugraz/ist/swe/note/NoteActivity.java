package at.tugraz.ist.swe.note;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class NoteActivity extends AppCompatActivity {

    private Menu menu;
    private Note note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

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
        menu.findItem(R.id.action_add).setVisible(!enabled);
        menu.findItem(R.id.action_remove).setVisible(enabled);
    }

    private  void enableUnpinningButton(boolean enabled){
        menu.findItem(R.id.action_pinning).setVisible(!enabled);
        menu.findItem(R.id.action_unpinning).setVisible(enabled);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        this.menu = menu;
        enableRemoveButton(false);
        enableUnpinningButton(false);

        MenuItem addButton = this.menu.findItem(R.id.action_add);
        addButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.saveNote();
                return true;
            }
        });

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

        MenuItem shareButton = this.menu.findItem(R.id.action_share);
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
        Intent intent = new Intent();
        intent.putExtra("note", note);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void saveNote(){
        enableRemoveButton(true);
        if (note == null) {
            TextView tfTitle = findViewById(R.id.tfTitle);
            TextView tfContent = findViewById(R.id.tfContent);
            String title = tfTitle.getText().toString();
            String content = tfContent.getText().toString();
            note = new Note(title, content, 0);
        }
    }

    private void deleteNote(){
        Toast.makeText(getApplicationContext(),"delete clicked",Toast.LENGTH_SHORT).show();
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