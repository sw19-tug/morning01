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
    public  void testGetNewPinningNumber() throws NotFoundException{
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        assertEquals(1, storage.getNewPinningNumber());
        Note note = new Note ("title1", "content1", 3);
        storage.insert(note);
        assertEquals(4, storage.getNewPinningNumber());
    }

    @Test
    public void testNoteInsert() {
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note("title1", "content1", 1);
        assertNull(note.getCreatedDate());
        assertNull(note.getChangedDate());
        storage.insert(note);
        assertNotNull(note.getCreatedDate());
        assertNotNull(note.getChangedDate());
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
    public void testDeleteNote() throws NotFoundException{
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));

        Note note = new Note ("some title", "some content", 1);
        storage.insert(note);

        storage.delete(note.getId());

        boolean idNotFound = false;
        try {
            storage.findById(note.getId());
        }
        catch (NotFoundException ex) {
            idNotFound = true;
        }

        assertTrue(idNotFound);
    }


    @Test
    public void testFindNoteById() throws NotFoundException {
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note ("title1", "content1", 1);
        storage.insert(note);
        Note foundNote = storage.findById(note.getId());
        assertEquals(foundNote.getTitle(),"title1");
        assertEquals(foundNote.getContent(),"content1");
        assertEquals(foundNote.getPinned(),1);
    }


    @Test
    public void testNoteUpdate() throws NotFoundException{
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note ("title1", "content1", 1);
        storage.insert(note);
        note.setTitle("title2");
        note.setContent("content2");
        note.setPinned(0);
        storage.update(note);
        Note foundNote = storage.findById(note.getId());
        assertEquals(foundNote.getTitle(),"title2");
        assertEquals(foundNote.getContent(),"content2");
        assertEquals(foundNote.getPinned(),0);
    }

    public void assertNoteArrayEquals(int lengthOfArray, Note[] allStoredNotes, Note[] expectedArray){

        assertEquals(lengthOfArray, allStoredNotes.length);
        for (int i = 0; i < allStoredNotes.length; ++i){
            assertTrue(expectedArray[i].equals(allStoredNotes[i]));
        }

    }
    public void fillNoteStorage(Note[] notes, NoteStorage noteStorage) {
        for (int i = 0; i < notes.length; ++i) {
            noteStorage.insert(notes[i]);
        }
    }


    @Test
    public void testGetAllNotesSortedByTitle() {

        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 2);
        Note note3 = new Note("C_Test_title", "blabla3", 3);
        boolean sortByCreatedDate = false;
        Note[] notes = {
                note1,
                note3,
                note2
        };
        Note[] expectedNoteArray = {
                note1,
                note2,
                note3
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));

        fillNoteStorage(notes, noteStorage);

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate);

        assertNoteArrayEquals(3, allStoredNotes, expectedNoteArray);
    }

    @Test
    public void testGetAllNotesSortedByDate() {

        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 2);
        Note note3 = new Note("C_Test_title", "blabla3", 3);
        boolean sortByCreatedDate = true;
        Note[] notes = {
                note1,
                note3,
                note2
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));

        fillNoteStorage(notes, noteStorage);

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate);

        assertNoteArrayEquals(3, allStoredNotes, notes);
    }

    @Test
    public void testGetAllRemovedNotesSortedByDate() throws NotFoundException {

        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 2);
        Note note3 = new Note("C_Test_title", "blabla3", 3);
        boolean sortByCreatedDate = true;
        boolean removedOnly = false;
        Note[] notes = {
                note1,
                note3,
                note2
        };
        Note[] expecterArray = {
                note1,
                note2,
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));
        fillNoteStorage(notes, noteStorage);
        noteStorage.softDelete(note1.getId());
        noteStorage.softDelete(note2.getId());

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate, removedOnly);

        assertNoteArrayEquals(2, allStoredNotes, expecterArray);
    }

    @Test
    public void testGetAllRemovedNotesSortedByTitle() throws NotFoundException {

        Note note1 = new Note("B_Test_title", "blabla1", 1);
        Note note2 = new Note("A_Test_title", "blabla2", 2);
        Note note3 = new Note("C_Test_title", "blabla3", 3);
        boolean sortByCreatedDate = false;
        boolean removedOnly = false;
        Note[] notes = {
                note1,
                note3,
                note2
        };
        Note[] expecterArray = {
                note2,
                note1,
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));
        fillNoteStorage(notes, noteStorage);
        noteStorage.softDelete(note1.getId());
        noteStorage.softDelete(note2.getId());

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate, removedOnly);

        assertNoteArrayEquals(2, allStoredNotes, expecterArray);
    }


    @Test
    public void testSoftDeleteNote() throws NotFoundException {
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));

        Note note = new Note ("some title", "some content", 1);
        storage.insert(note);
        storage.softDelete(note.getId());
        note = storage.findById(note.getId());
        assertTrue(note.isRemoved());
    }
}
