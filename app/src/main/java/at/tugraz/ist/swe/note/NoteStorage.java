package at.tugraz.ist.swe.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        if(date == null) {
            return null;
        }
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
        note.setProtected(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN_PROTECTED)) > 0);
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
        values.put(DatabaseHelper.NOTE_COLUMN_CREATED_DATE, convertDateToString(note.getCreatedDate()));
        values.put(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE, convertDateToString(note.getChangedDate()));
        values.put(DatabaseHelper.NOTE_COLUMN_CONTENT, note.getContent());
        values.put(DatabaseHelper.NOTE_COLUMN_PINNED, note.getPinned());
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, note.isRemoved());
        values.put(DatabaseHelper.NOTE_COLUMN_PROTECTED, note.getIsProtected());
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
        return getAll(false);
    }

    public Note[] getAll(boolean sortByCreatedDate) {
        return getAll(sortByCreatedDate, false, false);
    }

    public Note[] getAll(boolean sortByCreatedDate, boolean removedOnly) {
        return getAll(sortByCreatedDate, removedOnly, "");
    }

    public Note[] getAll(boolean sortByCreatedDate, boolean removedOnly, boolean protectedOnly) {
        return getAll(sortByCreatedDate, removedOnly, protectedOnly, "");
    }


    public Note[] getAll(boolean sortByCreatedDate, boolean removedOnly, String pattern) {
        return getAll(sortByCreatedDate, removedOnly, pattern, null);
    }

    private static String[] arrayListToArray(ArrayList<String> arrayList) {
        String[] array = new String[arrayList.size()];
        for(int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }
        return array;
    }

    private static final String TAG_SUB_QUERY = "SELECT count(*) FROM " +
            DatabaseHelper.TAG_TABLE_NAME + "," + DatabaseHelper.NOTE_TAG_TABLE_NAME +
            " WHERE " + DatabaseHelper.TAG_TABLE_NAME + "." + DatabaseHelper.TAG_COLUMN_ID  +
            "=" + DatabaseHelper.NOTE_TAG_TABLE_NAME + "." + DatabaseHelper.NOTE_TAG_COLUMN_TAG_ID +
            " AND " + DatabaseHelper.NOTE_TABLE_NAME + "." + DatabaseHelper.NOTE_COLUMN_ID + " = " + DatabaseHelper.NOTE_TAG_TABLE_NAME + "." + DatabaseHelper.NOTE_TAG_COLUMN_NOTE_ID +
            " AND " + DatabaseHelper.TAG_TABLE_NAME + "." + DatabaseHelper.TAG_COLUMN_NAME;

    public Note[] getAll(boolean sortByCreatedDate, boolean removedOnly, String pattern, NoteTag noteTag) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String orderBy = DatabaseHelper.NOTE_COLUMN_PINNED + " DESC, ";
        if(sortByCreatedDate) {
            orderBy += DatabaseHelper.NOTE_COLUMN_CREATED_DATE + " DESC";
        } else {
            orderBy += DatabaseHelper.NOTE_COLUMN_TITLE + " ASC";
        }
        StringBuilder whereClause = new StringBuilder();
        if(removedOnly) {
            whereClause.append(DatabaseHelper.NOTE_COLUMN_REMOVED + " = 1");
        } else {
            whereClause.append(DatabaseHelper.NOTE_COLUMN_REMOVED + " = 0");
        }
        ArrayList<String> selectionArgs = new ArrayList<>();
        String[] patternParts = pattern.split("\\s+");
        for(String patternPart : patternParts) {
            if(patternPart.startsWith("#")) {
                whereClause.append(" AND (" + TAG_SUB_QUERY + " LIKE ? ) > 0");
                selectionArgs.add("%" + patternPart.substring(1) + "%");
            } else {
                whereClause.append(" AND (" + DatabaseHelper.NOTE_COLUMN_TITLE + " LIKE ? OR " + DatabaseHelper.NOTE_COLUMN_CONTENT + " LIKE ?)");
                selectionArgs.add("%" + patternPart + "%");
                selectionArgs.add("%" + patternPart + "%");
            }
        }
        if(noteTag != null) {
            whereClause.append(" AND (" + TAG_SUB_QUERY + "= ?) > 0");
            selectionArgs.add(noteTag.getName());
        }
        Cursor allNotesCursor = database.query(DatabaseHelper.NOTE_TABLE_NAME, null, whereClause.toString(), arrayListToArray(selectionArgs), null ,null, orderBy);

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

    public Note[] getAll(boolean sortByCreatedDate, boolean removedOnly, boolean protectedOnly, String patten) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String orderBy = DatabaseHelper.NOTE_COLUMN_PINNED + " DESC, ";
        if(sortByCreatedDate) {
            orderBy += DatabaseHelper.NOTE_COLUMN_CREATED_DATE + " DESC";
        } else {
            orderBy += DatabaseHelper.NOTE_COLUMN_TITLE + " ASC";
        }
        String whereClause = "";
        if(protectedOnly) {
            whereClause = DatabaseHelper.NOTE_COLUMN_PROTECTED + " = 1";
        } else {
            whereClause = DatabaseHelper.NOTE_COLUMN_PROTECTED + " = 0";
        }
        if(removedOnly) {
            whereClause = whereClause +" and "+ DatabaseHelper.NOTE_COLUMN_REMOVED + " = 1";
        } else {
            whereClause =  whereClause +" and "+ DatabaseHelper.NOTE_COLUMN_REMOVED + " = 0";
        }
        String[] selectionArgs = {"%" + patten + "%", "%" + patten + "%"};
        whereClause += " AND (title LIKE ? OR content LIKE ?)";
        Cursor allNotesCursor = database.query(DatabaseHelper.NOTE_TABLE_NAME, null, whereClause, selectionArgs, null ,null, orderBy);

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

    public void protectNote(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_PROTECTED, true);
        assertOneAffectedRow(database.update(DatabaseHelper.NOTE_TABLE_NAME, values, whereClause, whereArgs), id);
    }

    public void unprotect(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_PROTECTED, false);
        assertOneAffectedRow(database.update(DatabaseHelper.NOTE_TABLE_NAME, values, whereClause, whereArgs), id);
    }

    public boolean associate(Note note, NoteTag tag){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_TAG_COLUMN_NOTE_ID, note.getId());
        values.put(DatabaseHelper.NOTE_TAG_COLUMN_TAG_ID, tag.getId());

        try {
            SQLiteDatabase database = databaseHelper.getWritableDatabase();
            database.insertOrThrow(DatabaseHelper.NOTE_TAG_TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            return false;
        }
        return true;
    }

    public NoteTag[] getAssociatedTags(Note note){
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String whereClause = DatabaseHelper.TAG_TABLE_NAME + "." + DatabaseHelper.TAG_COLUMN_ID
                + "=" + DatabaseHelper.NOTE_TAG_TABLE_NAME + "." + DatabaseHelper.NOTE_TAG_COLUMN_TAG_ID
                + " AND " + DatabaseHelper.NOTE_TAG_TABLE_NAME + "." + DatabaseHelper.NOTE_TAG_COLUMN_NOTE_ID + "=?";
        String[] selectionArgs = {String.valueOf(note.getId())};
        Cursor cursor = database.query(DatabaseHelper.TAG_TABLE_NAME + "," + DatabaseHelper.NOTE_TAG_TABLE_NAME,
                null, whereClause, selectionArgs, null ,null, DatabaseHelper.TAG_TABLE_NAME + "." + DatabaseHelper.TAG_COLUMN_NAME + " ASC");
        return NoteTagStorage.getAllTags(cursor);
    }

    public boolean dissociate(Note note, NoteTag tag){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_TAG_COLUMN_NOTE_ID + " = ? AND " + DatabaseHelper.NOTE_TAG_COLUMN_TAG_ID + " = ?";
        String[] whereArgs = {String.valueOf(note.getId()), String.valueOf(tag.getId())};
        return database.delete(DatabaseHelper.NOTE_TAG_TABLE_NAME, whereClause, whereArgs) == 1;
    }

    public void dissociateAll(Note note) {
        for (NoteTag noteTag : getAssociatedTags(note)) {
            dissociate(note, noteTag);
        }
    }

    public void associateAll(Note note) {
        // note has to be already inserted to database.
        for (NoteTag noteTag : note.getTags()) {
            associate(note, noteTag);
        }
    }

    public void restore(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.NOTE_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, false);
        assertOneAffectedRow(database.update(DatabaseHelper.NOTE_TABLE_NAME, values, whereClause, whereArgs), id);
    }
}
