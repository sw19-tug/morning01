package at.tugraz.ist.swe.note;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

public class AddNewNote extends AppCompatActivity {

    private Menu _menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

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
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}