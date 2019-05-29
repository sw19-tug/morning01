package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> noteList = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    ListView noteListView;
    NoteStorage noteStorage;
    private boolean isDarkThemeEnabled = false;
    private Menu menu;
    private boolean sortByCreatedDate = true;
    private boolean removedOnly = false;
    private String pattern = "";


    private static final int NOTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteStorage = new NoteStorage(new DatabaseHelper(this));
        initAddNoteButton();

        initNoteView();
        showNotes();
        createToolbar();
    }

    @VisibleForTesting
    public void setNoteStorage(NoteStorage noteStorage) {
        this.noteStorage = noteStorage;
    }

    public void setNoteList(Note[] newNotes) {
        noteList.clear();
        for (Note node : newNotes) {
            noteList.add(node);
        }
    }

    public void refreshNoteList() {
        Note[] allNotes = noteStorage.getAll(sortByCreatedDate, removedOnly, pattern);
        setNoteList(allNotes);
        customNoteAdapter.notifyDataSetChanged();
    }

    public void initAddNoteButton() {
        FloatingActionButton addNoteButton = findViewById(R.id.createNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra(NoteActivity.NOTE_KEY);
                if (note.getTitle().isEmpty() && note.getContent().isEmpty()) {
                    return;
                }
                OptionFlag flag = (OptionFlag) data.getSerializableExtra(NoteActivity.FLAG_KEY);

                switch (flag) {
                    case SAVE:
                        noteStorage.insert(note);
                        refreshNoteList();
                        break;
                    case EDIT:
                        try {
                            noteStorage.update(note);
                            refreshNoteList();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case SOFT_REMOVE:
                        try {
                            noteStorage.softDelete(note.getId());
                            refreshNoteList();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case REMOVE:
                        try {
                            noteStorage.delete(note.getId());
                            refreshNoteList();
                        }
                        catch (NotFoundException e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        refreshNoteList();
                }
            }
        }
    }

    private void initNoteView() {
        Note[] allNotes = noteStorage.getAll();

        setNoteList(allNotes);

        customNoteAdapter = new NoteAdapter(this, noteList);
        noteListView = findViewById(R.id.notesList);
        noteListView.setAdapter(customNoteAdapter);
    }

    private void showNotes() {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra("note", noteList.get(position));
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
        });
    }

    private void createToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.mainActivityToolbar));
       /* if(getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_main_burger_button);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        this.menu = menu;
        MenuItem sortByTitleAscButton = this.menu.findItem(R.id.sortByTitleAscButton);
        sortByTitleAscButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortByCreatedDate = false;
                refreshNoteList();
                return true;
            }
        });

        MenuItem menuItem = this.menu.findItem(R.id.search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                recreate();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newPattern) {
                pattern = newPattern;
                refreshNoteList();
                return true;
            }
        });

        MenuItem sortByCreatedDateDescButton = this.menu.findItem(R.id.sortByCreatedDateDescButton);
        sortByCreatedDateDescButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortByCreatedDate = true;
                refreshNoteList();
                return true;
            }
        });
        MenuItem importButton = this.menu.findItem(R.id.importButton);
        importButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        MenuItem exportButton = this.menu.findItem(R.id.exportButton);
        exportButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        MenuItem tagsListButton = this.menu.findItem(R.id.tagListButton);
        tagsListButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), TagListActivity.class);
                startActivity(intent);
                return true;
            }
        });

        MenuItem changeThemeButton = this.menu.findItem(R.id.changeTheme);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            changeThemeButton.setTitle(R.string.change_light_theme);
        } else {
            changeThemeButton.setTitle(R.string.change_dark_theme);
        }

        importButton.setVisible(false);
        exportButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeTheme:
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    recreate();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    recreate();
                }
                return true;
            case R.id.action_trash:
                Intent intent = new Intent(getApplicationContext(), TrashActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
