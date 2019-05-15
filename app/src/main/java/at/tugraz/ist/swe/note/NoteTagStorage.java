package at.tugraz.ist.swe.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import at.tugraz.ist.swe.note.database.NotFoundException;
import at.tugraz.ist.swe.note.database.TagDatabaseHelper;

public class NoteTagStorage {

    private TagDatabaseHelper tagDatabaseHelper;

    public NoteTagStorage(TagDatabaseHelper tagDatabaseHelper) {
        this.tagDatabaseHelper = tagDatabaseHelper;
    }


    static private void assertOneAffectedRow(int affectedRow, long id) throws NotFoundException {
        if(affectedRow != 1)  {
            throw new NotFoundException(id);
        }
    }

    private void setValues(NoteTag noteTag, Cursor cursor) {
        noteTag.setId(cursor.getLong(cursor.getColumnIndex(tagDatabaseHelper.TAG_COLUMN_ID)));
        noteTag.setName(cursor.getString(cursor.getColumnIndex(tagDatabaseHelper.TAG_COLUMN_NAME)));
        noteTag.setColor(cursor.getInt(cursor.getColumnIndex(tagDatabaseHelper.TAG_COLUMN_COLOR)));
    }

    private Cursor getTagCursor(long id) {
        SQLiteDatabase database = tagDatabaseHelper.getReadableDatabase();
        String selection = TagDatabaseHelper.TAG_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return database.query(TagDatabaseHelper.TAG_TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    public NoteTag findTagById(long id) throws NotFoundException {
        Cursor cursor = getTagCursor(id);
        if(cursor.getCount() != 1) {
            throw new NotFoundException(id);
        }
        cursor.moveToFirst();
        NoteTag noteTag = new NoteTag();
        setValues(noteTag, cursor);
        cursor.close();
        return noteTag;
    }

    private ContentValues getTagContentValues(NoteTag noteTag) {
        ContentValues tagValues = new ContentValues();
        tagValues.put(TagDatabaseHelper.TAG_COLUMN_NAME, noteTag.getName());
        tagValues.put(TagDatabaseHelper.TAG_COLUMN_COLOR, noteTag.getColor());
        tagValues.put(TagDatabaseHelper.TAG_COLUMN_NUMBER_OF_USAGES, noteTag.getNumberOfUsages());
        return tagValues;
    }

    public void insert(NoteTag noteTag) {
        SQLiteDatabase database = tagDatabaseHelper.getWritableDatabase();
        long id = database.insert(TagDatabaseHelper.TAG_TABLE_NAME, null, getTagContentValues(noteTag));
        Cursor cursor = getTagCursor(id);
        cursor.moveToNext();
        setValues(noteTag, cursor);
        cursor.close();
    }

    public NoteTag[] getAllTags() {
        SQLiteDatabase database = tagDatabaseHelper.getReadableDatabase();
        Cursor allNoteTagsCursor = database.query(tagDatabaseHelper.TAG_TABLE_NAME, null, null, null, null ,null, null);

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

    private NoteTag convertNoteTagCursorToNoteTag(Cursor noteTagsCursor){
        NoteTag noteTag = new NoteTag();
        setValues(noteTag, noteTagsCursor);
        return noteTag;
    }

    public void update(NoteTag noteTag) throws NotFoundException {
        SQLiteDatabase database = tagDatabaseHelper.getWritableDatabase();
        long id = noteTag.getId();
        String whereClause = TagDatabaseHelper.TAG_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        ContentValues values = getTagContentValues(noteTag);
        assertOneAffectedRow(database.update(tagDatabaseHelper.TAG_TABLE_NAME, getTagContentValues(noteTag), whereClause, whereArgs), id);
        Cursor cursor = getTagCursor(id);
        cursor.moveToNext();
        setValues(noteTag, cursor);
        cursor.close();
    }

    public void delete(long id) throws NotFoundException {
        SQLiteDatabase database = tagDatabaseHelper.getWritableDatabase();
        String whereClause = tagDatabaseHelper.TAG_COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        assertOneAffectedRow(database.delete(tagDatabaseHelper.TAG_TABLE_NAME, whereClause, whereArgs), id);
    }
}

