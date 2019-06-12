package at.tugraz.ist.swe.note;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> noteList = new ArrayList<>();
    ArrayList<Note> notesListForExport = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    CheckBoxAdapter checkBoxAdapter;
    ListView noteListView;
    NoteStorage noteStorage;
    private Menu menu;
    private boolean sortByCreatedDate = true;
    private boolean removedOnly = false;
    private String pattern = "";
    Context context = this;
    Activity mainActivity = this;
    boolean exporting = false;
    String userPassword;
    String inputPassword;



    private static final int NOTE_REQUEST_CODE = 1;
    public final String TMP_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmpNotes";
    public final String OUTPUT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes";

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
        Note[] allNotes = noteStorage.getAll(sortByCreatedDate, removedOnly,false, pattern);
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
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case PROTECT:
                        try {
                            noteStorage.protectNote(note.getId());
                            refreshNoteList();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        refreshNoteList();
                        break;
                }
            }
        }
    }

    private void initNoteView() {
        Note[] allNotes = noteStorage.getAll();

        setNoteList(allNotes);

        customNoteAdapter = new NoteAdapter(this, noteList);
        final FloatingActionButton confirmExportButton = findViewById(R.id.confirmExportButton);
        confirmExportButton.setEnabled(false);

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

    private boolean onExport() {
        setTitle(getString(R.string.main_export));
        exporting = true;
        noteListView = findViewById(R.id.notesList);
        checkBoxAdapter = new CheckBoxAdapter(context, noteList);
        noteListView.setAdapter(checkBoxAdapter);
        final FloatingActionButton addNoteButton = findViewById(R.id.createNoteButton);
        final FloatingActionButton confirmExportButton = findViewById(R.id.confirmExportButton);
        confirmExportButton.setEnabled(true);
        addNoteButton.setEnabled(false);
        noteListView.setItemsCanFocus(true);
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View item,
                                     int position, long id) {
                Note note = checkBoxAdapter.getItem( position );
                Log.d("added note", note.getTitle());
                if(notesListForExport.contains(note)){
                    Log.d("removed note", note.getTitle());
                    notesListForExport.remove(note);
                    noteListView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                } else {
                    notesListForExport.add(note);
                    noteListView.getChildAt(position).setBackgroundColor(isNightModeEnabled() ? Color.LTGRAY : Color.GRAY);
                }
            }
        });
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(mainActivity, permissions, 0);
        confirmExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File tmpDirectory = new File (TMP_DIRECTORY);
                File outputDirectory = new File (OUTPUT_DIRECTORY);
                outputDirectory.mkdirs();
                File zipFile = new File (outputDirectory.toString() + "/Notes.zip");
                String zipOutputPath = zipFile.toString();
                for (Note note : notesListForExport) {
                    convertNoteToFile(note, tmpDirectory);
                }
                int counter = 1;
                while(zipFile.exists()) {
                    zipOutputPath = outputDirectory.toString()  + "/Notes" + counter + ".zip";
                    zipFile = new File (zipOutputPath);
                    counter++;
                }
                boolean zipped = zipFolder(tmpDirectory.toString(), zipOutputPath);
                notesListForExport.clear();
                deleteRecursive(tmpDirectory);
                if (zipped) {
                    Toast.makeText(getApplicationContext(), getString(R.string.export_successfully) + " " + zipOutputPath , Toast.LENGTH_SHORT).show();
                    exporting = false;
                    recreate();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_notes_selected) , Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
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
        final MenuItem importButton = this.menu.findItem(R.id.importButton);
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
                return onExport();
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

        MenuItem protectedNotesButton = this.menu.findItem(R.id.protectedNotesButton);
        protectedNotesButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showProtectedNotes();
                return true;
            }
        });

        MenuItem changeThemeButton = this.menu.findItem(R.id.changeTheme);
        if(isNightModeEnabled()){
            changeThemeButton.setTitle(R.string.change_light_theme);
        } else {
            changeThemeButton.setTitle(R.string.change_dark_theme);
        }

        importButton.setVisible(false);
        return true;
    }

    private boolean isNightModeEnabled() {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeTheme:
                if (!isNightModeEnabled()) {
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

    @Override
    public void onBackPressed() {
        if(exporting){
            recreate();
            return;
        }
        super.onBackPressed();
    }

    public void convertNoteToFile(Note note, File outPutDirectory) {
        String title = note.getTitle();
        String content = note.getContent();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (checkPermission()) {
                outPutDirectory.mkdirs();
                File file = new File(outPutDirectory, title + ".txt");
                try (PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
                    pw.println(title);
                    pw.println(content);
                    pw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private static boolean zipFolder(String inputFolderPath, String outZipPath) {
        Log.d("Zip", "zipFolder function");
        File srcFile = new File(inputFolderPath);
        File[] files = srcFile.listFiles();
        if (files == null) {
            return false;
        }
        Log.d("Zip", "Zip directory: " + srcFile.getName());
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZipPath))) {
            for (File file : files) {
                Log.d("Zip", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        } catch (IOException ioe) {
            Log.e("Zip", ioe.getMessage());
        }
        return true;
    }

    public static boolean deleteRecursive(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteRecursive(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void showProtectedNotes(){
        SharedPreferences settings = getSharedPreferences(getString(R.string.protected_notes), MODE_PRIVATE);
        userPassword = settings.getString(getString(R.string.protected_notes_password),"");
        if(userPassword.isEmpty()){
            showPasswordDialog(getString(R.string.new_password_dialog_title), true);
        }else {
            showPasswordDialog(getString(R.string.password_dialog_title), false);
        }
    }

    private void showPasswordDialog(String dialogTitle, final boolean newPassword){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dialogTitle);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputPassword = input.getText().toString();

                if(!newPassword) {
                    if (userPassword.equals(inputPassword)) {
                        dialogInterface.cancel();
                        Toast.makeText(getApplicationContext(), R.string.logged_in, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ProtectedActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    userPassword = input.getText().toString();
                    SharedPreferences settings = getSharedPreferences(getString(R.string.protected_notes), MODE_PRIVATE);
                    settings.edit().putString(getString(R.string.protected_notes_password),userPassword).apply();
                    dialogInterface.cancel();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

}
