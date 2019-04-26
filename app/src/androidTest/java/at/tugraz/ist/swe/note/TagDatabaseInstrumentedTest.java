package at.tugraz.ist.swe.note;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tugraz.ist.swe.note.database.NoteTag;
import at.tugraz.ist.swe.note.database.TagDatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TagDatabaseInstrumentedTest {
    private TagDatabaseHelper tagDatabaseHelper;

    @Before
    public void setUpTagDB() {
        tagDatabaseHelper = new TagDatabaseHelper(InstrumentationRegistry.getTargetContext());
        tagDatabaseHelper.getWritableDatabase().execSQL("DELETE FROM " + tagDatabaseHelper.TAG_TABLE_NAME);
    }

    @Test
    public void testConnectionTagDB() {
        assertEquals(tagDatabaseHelper.getDatabaseName(), "tag");
        assertNotNull(tagDatabaseHelper.getReadableDatabase());
        assertNotNull(tagDatabaseHelper.getWritableDatabase());
    }

    @Test
    public void testTagTable() {
        SQLiteDatabase database = tagDatabaseHelper.getReadableDatabase();
        database.query("tag",null, null, new String[]{}, null, null, null);
    }


    @Test
    public void testTagInsert() {
        NoteTagStorage storage = new NoteTagStorage(new TagDatabaseHelper(InstrumentationRegistry.getTargetContext()));
        NoteTag noteTag = new NoteTag("name", 2);
        storage.insert(noteTag);
        assertNotNull(noteTag.getName());
        assertNotNull(noteTag.getColor());
        assertEquals(0, noteTag.getNumberOfUsages());
        SQLiteDatabase database = tagDatabaseHelper.getReadableDatabase();

        String selection = tagDatabaseHelper.TAG_COLUMN_NAME + " = ? AND " + tagDatabaseHelper.TAG_COLUMN_COLOR + " = ? AND " + tagDatabaseHelper.TAG_COLUMN_NUMBER_OF_USAGES + " = ?";

        String[] selectionArgs = {"name", "2", "0"};

        Cursor cursor = database.query(
                tagDatabaseHelper.TAG_TABLE_NAME,
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
    public void testGetAllTags() {
        boolean found = false;
        NoteTag[] noteTags = {
                new NoteTag("tag1", 1),
                new NoteTag("tag2", 2),
                new NoteTag("tag3", 3)
        };

        NoteTagStorage noteTagStorage = new NoteTagStorage(new TagDatabaseHelper(InstrumentationRegistry.getTargetContext(), null));

        for (int i = 0; i < noteTags.length; ++i){
            noteTagStorage.insert(noteTags[i]);
        }

        NoteTag[] allStoredNoteTags = noteTagStorage.getAllTags();

        assertEquals(3, allStoredNoteTags.length);
        for (int i = 0; i < noteTags.length; i++){
            for (int j = 0; j < allStoredNoteTags.length; j++) {
                if( noteTags[i].equals(allStoredNoteTags[j]))
                {
                    found = true;
                }
            }
            assertTrue(found);
            found = false;
        }
    }

    @Test
    public void testTagUpdate() {
        NoteTagStorage storage = new NoteTagStorage(new TagDatabaseHelper(InstrumentationRegistry.getTargetContext()));
        NoteTag noteTag = new NoteTag("name", 2);

        storage.insert(noteTag);

        noteTag.setName("some other name");
        noteTag.setColor(35);

        try {
            storage.update(noteTag);
            NoteTag fetchedTag = storage.findTagById(noteTag.getId());
            assertEquals(noteTag, fetchedTag);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testTagDelete() {
        NoteTagStorage storage = new NoteTagStorage(new TagDatabaseHelper(InstrumentationRegistry.getTargetContext()));
        NoteTag noteTag = new NoteTag("name", 2);

        storage.insert(noteTag);
        boolean idNotFound = false;
        try {
            storage.delete(noteTag.getId());
            storage.findTagById(noteTag.getId());
        }
        catch (NotFoundException ex) {
            idNotFound = true;
        }
        assertTrue(idNotFound);
    }

}
