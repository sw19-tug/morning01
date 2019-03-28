package at.tugraz.ist.swe.note;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import at.tugraz.ist.swe.note.database.DatabaseHelper;

public class NoteStorage {
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());

    private DatabaseHelper databaseHelper;

    public NoteStorage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    static private String convertDateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * @return Returns true if note was inserted, false otherwise.
     */
    public boolean save(Note note) {
        if(note.exists()) {
            //update(note); // TODO
            return false;
        } else {
            insert(note);
            return true;

        }
    }

    private ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.NOTE_COLUMN_CONTENT, note.getContent());
        values.put(DatabaseHelper.NOTE_COLUMN_PINNED, note.isPinned());
        values.put(DatabaseHelper.NOTE_COLUMN_CREATED_DATE, convertDateToString(note.getDate()));
        values.put(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE, convertDateToString(note.getChangedDate()));
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, note.isRemoved());
        return values;
    }

    private void insert(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = database.insert(DatabaseHelper.NOTE_TABLE_NAME, null, getContentValues(note));
        note.setId(id);
    }
}
