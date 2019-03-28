package at.tugraz.ist.swe.note;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {
    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() {
        databaseHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        databaseHelper.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.NOTE_TABLE_NAME);
    }

    @Test
    public void testConnection() {
        assertEquals(databaseHelper.getDatabaseName(), "note");
        assertNotNull(databaseHelper.getReadableDatabase());
        assertNotNull(databaseHelper.getWritableDatabase());
    }

    @Test
    public void testNoteTable() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        database.query("note", null, null, new String[]{}, null, null, null);
    }

    @Test
    public void testNoteInsert() {
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note("title1", "content1", 1);
        assertTrue(storage.save(note));
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.NOTE_COLUMN_TITLE + " = ? AND " + DatabaseHelper.NOTE_COLUMN_CONTENT + " = ? AND " + DatabaseHelper.NOTE_COLUMN_PINNED + " = ?";

        String[] selectionArgs = {"title1", "content1", "1"};

        Cursor cursor = database.query(
                DatabaseHelper.NOTE_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }
    @Test
    public void testFindNoteById() throws NotFoundException {
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note ("title1", "content1", 1);
        assertTrue(storage.save(note));
        Note foundNote = storage.findById(note.getId());
        assertEquals(foundNote.getTitle(),"title1");
        assertEquals(foundNote.getContent(),"content1");
        assertEquals(foundNote.isPinned(),1);
    }


    @Test
    public  void testNoteUpdade() throws NotFoundException{
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note ("title1", "content1", 1);
        assertTrue(storage.save(note));
        note.setTitle("title2");
        note.setContent("content2");
        note.setPinned(0);
        assertFalse(storage.update(note));
        Note foundNote = storage.findById(note.getId());
        assertEquals(foundNote.getTitle(),"title2");
        assertEquals(foundNote.getContent(),"content2");
        assertEquals(foundNote.isPinned(),0);

    }
}
