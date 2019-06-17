package at.tugraz.ist.swe.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import at.tugraz.ist.swe.note.database.NotFoundException;
import at.tugraz.ist.swe.note.database.DatabaseHelper;

public class NoteTagStorage {

    private DatabaseHelper databaseHelper;

    public NoteTagStorage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    static private void assertOneAffectedRow(int affectedRow, long id) throws NotFoundException {
        if (affectedRow != 1) {
            throw new NotFoundException(id);
        }
    }

    private static void setValues(NoteTag noteTag, Cursor cursor) {
        noteTag.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TAG_COLUMN_ID)));
        noteTag.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TAG_COLUMN_NAME)));
        noteTag.setColor(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TAG_COLUMN_COLOR)));
    }

    private Cursor getTagCursor(long id) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.TAG_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return database.query(DatabaseHelper.TAG_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    private Cursor getTagCursor(String name) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.TAG_COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};
        return database.query(DatabaseHelper.TAG_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    private NoteTag convertCursorToNoteTag(Cursor cursor) {
        cursor.moveToFirst();
        NoteTag noteTag = new NoteTag();
        setValues(noteTag, cursor);
        cursor.close();
        return noteTag;
    }

    public NoteTag findTagById(long id) throws NotFoundException {
        Cursor cursor = getTagCursor(id);
        if (cursor.getCount() != 1) {
            throw new NotFoundException(id);
        }
        return convertCursorToNoteTag(cursor);
    }

    public NoteTag findByName(String name) {
        Cursor cursor = getTagCursor(name);
        if (cursor.getCount() != 1) {
            return null;
        }
        return convertCursorToNoteTag(cursor);
    }

    private ContentValues getTagContentValues(NoteTag noteTag) {
        ContentValues tagValues = new ContentValues();
        tagValues.put(DatabaseHelper.TAG_COLUMN_NAME, noteTag.getName());
        tagValues.put(DatabaseHelper.TAG_COLUMN_COLOR, noteTag.getColor());
        return tagValues;
    }

    public void insert(NoteTag noteTag) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = database.insert(DatabaseHelper.TAG_TABLE_NAME, null, getTagContentValues(noteTag));
        Cursor cursor = getTagCursor(id);
        cursor.moveToNext();
        setValues(noteTag, cursor);
        cursor.close();
    }

    public static NoteTag[] getAllTags(Cursor allNoteTagsCursor) {
        NoteTag[] allNoteTags = new NoteTag[allNoteTagsCursor.getCount()];
        int arrayIndex = 0;

        try {
            while (allNoteTagsCursor.moveToNext()) {
                allNoteTags[arrayIndex++] = convertNoteTagCursorToNoteTag(allNoteTagsCursor);
            }
        } finally {
            allNoteTagsCursor.close();
        }
        return allNoteTags;
    }

    public NoteTag[] getAllTags() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor allNoteTagsCursor = database.query(DatabaseHelper.TAG_TABLE_NAME, null, null, null, null, null, DatabaseHelper.TAG_COLUMN_NAME + " ASC");
        return getAllTags(allNoteTagsCursor);
    }

    private static NoteTag convertNoteTagCursorToNoteTag(Cursor noteTagsCursor) {
        NoteTag noteTag = new NoteTag();
        setValues(noteTag, noteTagsCursor);
        return noteTag;
    }

    public void update(NoteTag noteTag) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = noteTag.getId();
        String whereClause = DatabaseHelper.TAG_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = getTagContentValues(noteTag);
        assertOneAffectedRow(database.update(DatabaseHelper.TAG_TABLE_NAME, getTagContentValues(noteTag), whereClause, whereArgs), id);
        Cursor cursor = getTagCursor(id);
        cursor.moveToNext();
        setValues(noteTag, cursor);
        cursor.close();
    }

    public void delete(long id) throws NotFoundException {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.TAG_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        assertOneAffectedRow(database.delete(DatabaseHelper.TAG_TABLE_NAME, whereClause, whereArgs), id);
    }
}

