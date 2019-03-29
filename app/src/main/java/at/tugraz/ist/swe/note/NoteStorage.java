package at.tugraz.ist.swe.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

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

    static private Date convertStringToDate(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch(java.text.ParseException e) {
            return null;
        }
    }

    /**
     * @return Returns true if note was inserted, false otherwise.
     */
    public boolean save(Note note) {
        if(note.wasInserted()) {
            update(note);
            return false;
        } else {
            insert(note);
            return true;

        }
    }

    private Note getNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CONTENT)));
        note.setCreatedDate(convertStringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CREATED_DATE))));
        note.setChangedDate(convertStringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE))));
        note.setPinned(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_PINNED)));
        note.setRemoved(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_REMOVED)) > 0);
        return note;
    }

    public Note findById(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = database.query(DatabaseHelper.NOTE_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if(cursor.getCount() != 1) {
            throw new NotFoundException(id);
        }
        cursor.moveToFirst();
        Note note = getNote(cursor);
        cursor.close();
        return note;
    }

    private ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.NOTE_COLUMN_CONTENT, note.getContent());
        values.put(DatabaseHelper.NOTE_COLUMN_PINNED, note.getPinned());
        values.put(DatabaseHelper.NOTE_COLUMN_CREATED_DATE, convertDateToString(note.getCreatedDate()));
        values.put(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE, convertDateToString(note.getChangedDate()));
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, note.isRemoved());
        return values;
    }

    private void insert(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = database.insert(DatabaseHelper.NOTE_TABLE_NAME, null, getContentValues(note));
        note.setId(id);
    }

    private void update(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(note.getId())};
        note.setChangedDate(new Date());
        database.update(DatabaseHelper.NOTE_TABLE_NAME, getContentValues(note), whereClause, whereArgs);
    }
}
