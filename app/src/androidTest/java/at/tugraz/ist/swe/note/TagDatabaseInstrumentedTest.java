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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 * In this class we have to use the real database, otherwise tests will fail.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TagDatabaseInstrumentedTest {
    private DatabaseHelper databaseHelper;

    @Before
    public void setUpTagDB() {
        databaseHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        Util.resetDatabase(databaseHelper);
    }

    @Test
    public void testConnectionTagDB() {
        assertEquals(databaseHelper.getDatabaseName(), DatabaseHelper.NOTE_DATABASE_NAME);
        assertNotNull(databaseHelper.getReadableDatabase());
        assertNotNull(databaseHelper.getWritableDatabase());
    }

    @Test
    public void testTagTable() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        database.query("tag",null, null, new String[]{}, null, null, null);
    }


    @Test
    public void testTagInsert() {
        NoteTagStorage storage = new NoteTagStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        NoteTag noteTag = new NoteTag("name", 2);
        storage.insert(noteTag);
        assertNotNull(noteTag.getName());
        assertNotNull(noteTag.getColor());
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = databaseHelper.TAG_COLUMN_NAME + " = ? AND " + databaseHelper.TAG_COLUMN_COLOR + " = ?";

        String[] selectionArgs = {"name", "2"};

        Cursor cursor = database.query(
                databaseHelper.TAG_TABLE_NAME,
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

        NoteTagStorage noteTagStorage = new NoteTagStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));
        Util.fillNoteTagStorage(noteTags, noteTagStorage);

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
        NoteTagStorage storage = new NoteTagStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
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
        NoteTagStorage storage = new NoteTagStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
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


    private interface ApplyNotesTags {
        void apply(Note note, NoteTag noteTag);
    }

    private void applyNotesTags(NoteTag[][] notesTags, Note[] notes, ApplyNotesTags callback) {
        for(int noteIndex = 0; noteIndex < notesTags.length; noteIndex++) {
            for(NoteTag tag : notesTags[noteIndex]) {
                callback.apply(notes[noteIndex], tag);
            }
        }
    }

    @Test
    public void testTagNote() {
        Note note1 = new Note("Adkdhe", "Ajdnh diekdn ekde eie", 0);
        Note note2 = new Note("Khdhdgrgrg", "Jdkdh dhgnd udef rtr", 0);
        Note note3 = new Note("Odjeuzd", "Kduejd efdf ef dferfef", 0);
        Note note4 = new Note("Ldjehd", "Ldf dfe dgrgrg fgtujtge", 0);
        Note[] notes = {note1, note2, note3, note4};
        NoteTag tag1 = new NoteTag("tag1", 1);
        NoteTag tag2 = new NoteTag("tag2", 2);
        NoteTag tag3 = new NoteTag("tag3", 3);
        NoteTag[] noteTags = {tag1, tag2, tag3};
        final NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        NoteTagStorage noteTagStorage = new NoteTagStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Util.fillNoteStorage(notes, noteStorage);
        Util.fillNoteTagStorage(noteTags, noteTagStorage);

        assertTrue(noteStorage.getAll().length > 0);
        assertTrue(noteTagStorage.getAllTags().length > 0);

        NoteTag[] note1Tags = {tag1, tag2};
        NoteTag[] note2Tags = {tag2, tag3};
        NoteTag[] note3Tags = {};
        NoteTag[] note4Tags = {tag1};
        NoteTag[][] notesTags = {note1Tags, note2Tags, note3Tags, note4Tags};

        // Associate first time
        applyNotesTags(notesTags, notes, new ApplyNotesTags() {
            @Override
            public void apply(Note note, NoteTag tag) {
                assertTrue(noteStorage.associate(note, tag));
            }
        });

        assertTrue(noteStorage.getAll().length > 0);
        assertTrue(noteTagStorage.getAllTags().length > 0);

        // Associate twice
        applyNotesTags(notesTags, notes, new ApplyNotesTags() {
            @Override
            public void apply(Note note, NoteTag tag) {
                assertFalse(noteStorage.associate(note, tag));
            }
        });

        assertTrue(noteStorage.getAll().length > 0);
        assertTrue(noteTagStorage.getAllTags().length > 0);

        for(int noteIndex = 0; noteIndex < notesTags.length; noteIndex++) {
            NoteTag[] fetchedNoteTags = noteStorage.getAssociatedTags(notes[noteIndex]);
            NoteTag[] expectedNoteTags = notesTags[noteIndex];
            Util.assertNoteTagsContains(fetchedNoteTags, expectedNoteTags);
        }

        // Dissociate first time
        applyNotesTags(notesTags, notes, new ApplyNotesTags() {
            @Override
            public void apply(Note note, NoteTag tag) {
                assertTrue(noteStorage.dissociate(note, tag));
            }
        });
        // Dissociate twice
        applyNotesTags(notesTags, notes, new ApplyNotesTags() {
            @Override
            public void apply(Note note, NoteTag tag) {
                assertFalse(noteStorage.dissociate(note, tag));
            }
        });
    }
}
