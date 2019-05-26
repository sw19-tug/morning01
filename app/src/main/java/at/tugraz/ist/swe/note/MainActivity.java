package at.tugraz.ist.swe.note;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> noteList = new ArrayList<>();
    NoteAdapter customNoteAdapter;
    CheckBoxAdapter checkBoxAdapter;
    ListView noteListView;
    NoteStorage noteStorage;
    private Menu menu;
    private boolean sortByCreatedDate = true;
    Context context = this;
    ArrayList<Note> notesListForExport = new ArrayList<>();
    Activity mainActivity = this;



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

    public void setNoteList(Note[] newNotes) {
        noteList.clear();
        for (Note node : newNotes) {
            noteList.add(node);
        }
    }

    public void refreshNoteList() {
        Note[] allNotes = noteStorage.getAll(sortByCreatedDate);
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
                    case REMOVE:
                        try {
                            noteStorage.delete(note.getId());
                            refreshNoteList();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                if (resultCode == RESULT_CANCELED) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        this.menu = menu;
        MenuItem searchButton = this.menu.findItem(R.id.searchButton);
        searchButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        MenuItem sortByTitleAscButton = this.menu.findItem(R.id.sortByTitleAscButton);
        sortByTitleAscButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortByCreatedDate = false;
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

                //TODO: title and content in view is false
                //TODO: Clicklistener is on item instead of checkbox

                checkBoxAdapter = new CheckBoxAdapter(context, noteList);
                noteListView = findViewById(R.id.notesList);
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
                        Log.d("note",note.getTitle());
                        notesListForExport.add(note);
                        noteListView.getChildAt(position).setBackgroundColor(Color.GRAY);
                    }
                });

                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                confirmExportButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                       for (Note note : notesListForExport) {
                            convertNoteToFile(note);
                        }
                        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File dir1 = new File (root.getAbsolutePath() + "/Notes");
                        File dir = new File (root.getAbsolutePath() + "/Notes.zip");
                        String zipOutputDir = root.getAbsolutePath() + "/Notes.zip";
                        int counter = 1;
                        while(dir.exists())
                        {
                            dir = new File (root.getAbsolutePath() + "/Notes" + counter + ".zip");
                            zipOutputDir = root.getAbsolutePath() + "/Notes" + counter + ".zip";
                            counter++;
                        }
                        String zipDir = root.getAbsolutePath() + "/Notes";

                        zipFolder(zipDir, zipOutputDir);
                        notesListForExport.clear();
                        deleteRecursive(dir1);
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

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
        searchButton.setVisible(false);
        importButton.setVisible(false);
        return true;
    }


    public void convertNoteToFile(Note note) {
        String title = note.getTitle();
        String content = note.getContent();
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (checkPermission()) {
                File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dir = new File (root.getAbsolutePath() + "/Notes");
                dir.mkdirs();
                File file = new File(dir, title + ".txt");

                try {
                    FileOutputStream f = new FileOutputStream(file);
                    PrintWriter pw = new PrintWriter(f);
                    pw.println(content);
                    pw.flush();
                    pw.close();
                    f.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private static void zipFolder(String inputFolderPath, String outZipPath) {

        try {
            Log.d("Zip", "zipFolder function");
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("Zip", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("Zip", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("Zip", ioe.getMessage());
        }
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

        // The directory is now empty so delete it
        return dir.delete();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}