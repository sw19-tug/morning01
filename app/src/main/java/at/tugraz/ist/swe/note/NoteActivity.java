package at.tugraz.ist.swe.note;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private Menu _menu;
    private Note _note;
    EditText tfTitle;
    EditText tfContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

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

    private  void enableUnpinningButton(boolean enabled){
        _menu.findItem(R.id.action_pinning).setVisible(!enabled);
        _menu.findItem(R.id.action_unpinning).setVisible(enabled);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        _menu = menu;
        enableRemoveButton(false);
        enableUnpinningButton(false);

        MenuItem addButton = _menu.findItem(R.id.action_add);
        addButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.saveNote();
                return true;
            }
        });

        MenuItem removeButton = _menu.findItem(R.id.action_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.deleteNote();
                return true;
            }
        });

        MenuItem pinningButton = _menu.findItem(R.id.action_pinning);
        pinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.pinningNote();
                return true;
            }
        });

        MenuItem unpinningButton = _menu.findItem(R.id.action_unpinning);
        unpinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.unpinningNote();
                return true;
            }
        });

        MenuItem shareButton = _menu.findItem(R.id.action_share);
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
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //remove this
    public void readInputFields(){
        Toast.makeText(getApplicationContext(),"add clicked",Toast.LENGTH_SHORT).show();
        String title = tfTitle.getText().toString();
        String content = tfContent.getText().toString();

        Note note = new Note(title,content,0);
    }

    public void saveNote(){
        Toast.makeText(getApplicationContext(),"save clicked",Toast.LENGTH_SHORT).show();
        enableRemoveButton(true);
        //NoteController.save(_note);
    }

    public void deleteNote(){
        Toast.makeText(getApplicationContext(),"delete clicked",Toast.LENGTH_SHORT).show();
        //todo add dialog
        enableRemoveButton(false);
    }

    public void pinningNote(){
        Toast.makeText(getApplicationContext(),"pinning clicked",Toast.LENGTH_SHORT).show();
        enableUnpinningButton(true);
    }

    public void unpinningNote(){
        Toast.makeText(getApplicationContext(),"unpinning clicked",Toast.LENGTH_SHORT).show();
        enableUnpinningButton(false);
    }

    public void callShare(){
        Toast.makeText(getApplicationContext(),"share clicked",Toast.LENGTH_SHORT).show();
    }
}