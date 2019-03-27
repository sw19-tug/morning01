package at.tugraz.ist.swe.note;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

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
        Note note = new Note("title1", "content1", 1);
        note.save();
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
    }
}
