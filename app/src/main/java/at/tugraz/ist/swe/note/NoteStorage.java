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
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
    public static final int INITIAL_PINNING_NUMBER = 1;

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

    static private void assertOneAffectedRow(int affectedRow, long id) throws NotFoundException {
        if(affectedRow != 1)  {
            throw new NotFoundException(id);
        }
    }

    private void setValues(Note note, Cursor cursor) {
        note.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CONTENT)));
        note.setCreatedDate(convertStringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CREATED_DATE))));
        note.setChangedDate(convertStringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE))));
        note.setPinned(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_PINNED)));
        note.setRemoved(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_REMOVED)) > 0);
    }

    private Cursor getCursor(long id) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return database.query(DatabaseHelper.NOTE_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    public Note findById(long id) throws NotFoundException {
        Cursor cursor = getCursor(id);
        if(cursor.getCount() != 1) {
            throw new NotFoundException(id);
        }
        cursor.moveToFirst();
        Note note = new Note();
        setValues(note, cursor);
        cursor.close();
        return note;
    }

    private ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.NOTE_COLUMN_CONTENT, note.getContent());
        values.put(DatabaseHelper.NOTE_COLUMN_PINNED, note.getPinned());
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, note.isRemoved());
        return values;
    }

    public void insert(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = database.insert(DatabaseHelper.NOTE_TABLE_NAME, null, getContentValues(note));
        Cursor cursor = getCursor(id);
        cursor.moveToNext();
        setValues(note, cursor);
        cursor.close();
    }

    public Note[] getAll() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor allNotesCursor = database.query(DatabaseHelper.NOTE_TABLE_NAME, null, null, null, null ,null, null);

        Note[] allNotes = new Note[allNotesCursor.getCount()];
        int arrayIndex = 0;

        try {
            while (allNotesCursor.moveToNext()) {
                allNotes[arrayIndex++] = convertNoteCursorToNote(allNotesCursor);
            }
        } finally {
            allNotesCursor.close();
        }

        return allNotes;
    }

    private Note convertNoteCursorToNote(Cursor noteCursor){
        Note note = new Note();
        setValues(note, noteCursor);
        return note;
    }

    public void update(Note note) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = note.getId();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = getContentValues(note);
        values.put(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE, (String)null); // Forces the database to recalculate changed date.
        assertOneAffectedRow(database.update(DatabaseHelper.NOTE_TABLE_NAME, getContentValues(note), whereClause, whereArgs), id);
        Cursor cursor = getCursor(id);
        cursor.moveToNext();
        setValues(note, cursor);
        cursor.close();
    }

    public int getNewPinningNumber(){
        int pinningNumber = INITIAL_PINNING_NUMBER;
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.NOTE_COLUMN_PINNED +"=(SELECT MAX("+DatabaseHelper.NOTE_COLUMN_PINNED+") FROM  "+DatabaseHelper.NOTE_TABLE_NAME+")" ;

        Cursor cursor =  database.query(DatabaseHelper.NOTE_TABLE_NAME, null, selection, null, null, null, null);
        if(cursor.getCount() == 0) {
            return pinningNumber;
        }
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_PINNED)) + 1;
    }

    public void delete(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        assertOneAffectedRow(database.delete(DatabaseHelper.NOTE_TABLE_NAME, whereClause, whereArgs), id);
    }

    public void softDelete(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, true);
        assertOneAffectedRow(database.update(DatabaseHelper.NOTE_TABLE_NAME, values, whereClause, whereArgs), id);
    }
}
