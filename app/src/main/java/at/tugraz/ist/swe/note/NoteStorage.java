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
        int idIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_ID);
        int titleIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_TITLE);
        int contentIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_CONTENT);
        int createDateIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_CREATED_DATE);
        int removedIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_REMOVED);
        int pinnedIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_PINNED);
        int changedDateIndex = noteCursor.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE);

        long id = noteCursor.getLong(idIndex);
        String title = noteCursor.getString(titleIndex);
        String content = noteCursor.getString(contentIndex);
        Date createDate = NoteStorage.convertStringToDate(noteCursor.getString(createDateIndex));
        boolean removed = noteCursor.getInt(removedIndex) == 1;
        int pinned = noteCursor.getInt(pinnedIndex);
        Date changedDate = NoteStorage.convertStringToDate(noteCursor.getString(changedDateIndex));

        return new Note(id, title, content, createDate, removed, pinned, changedDate);
    }

    public void update(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = note.getId();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = getContentValues(note);
        values.put(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE, (String)null); // Forces the database to recalculate changed date.
        database.update(DatabaseHelper.NOTE_TABLE_NAME, getContentValues(note), whereClause, whereArgs);
        Cursor cursor = getCursor(id);
        cursor.moveToNext();
        setValues(note, cursor);
        cursor.close();
    }

    public int getNewPinningNumber(){
        int pinningNumber = 1;
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.NOTE_COLUMN_PINNED +"=(SELECT MAX("+DatabaseHelper.NOTE_COLUMN_PINNED+") FROM  "+DatabaseHelper.NOTE_TABLE_NAME+")" ;

        Cursor cursor =  database.query(DatabaseHelper.NOTE_TABLE_NAME, null, selection, null, null, null, null);
        if(cursor.getCount() == 0) {
            return pinningNumber;
        }
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_PINNED)) + 1;
    }
}
