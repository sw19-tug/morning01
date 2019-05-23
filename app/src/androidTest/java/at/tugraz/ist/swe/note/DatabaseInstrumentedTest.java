package at.tugraz.ist.swe.note;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.RunWith;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        Util.resetDatabase(databaseHelper);
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
        assertEquals(NoteStorage.INITIAL_PINNING_NUMBER, storage.getNewPinningNumber());
        int nodePinningNumber = 3;
        Note note = new Note ("title1", "content1", nodePinningNumber);
        storage.insert(note);
        assertEquals(NoteStorage.INITIAL_PINNING_NUMBER + nodePinningNumber, storage.getNewPinningNumber());
    }

    @Test
    public void testNoteInsert() {
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        String title = "title1";
        String content = "content1";
        int pinned = 1;
        Note note = new Note(title, content, pinned);
        assertNull(note.getCreatedDate());
        assertNull(note.getChangedDate());
        storage.insert(note);
        assertNotNull(note.getCreatedDate());
        assertNotNull(note.getChangedDate());
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.NOTE_COLUMN_TITLE + " = ? AND " + DatabaseHelper.NOTE_COLUMN_CONTENT + " = ? AND " + DatabaseHelper.NOTE_COLUMN_PINNED + " = ?";

        String[] selectionArgs = {title, content, String.valueOf(pinned)};

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
        String title = "title1";
        String content = "content1";
        int pinned = 1;
        Note note = new Note (title, content, pinned);
        storage.insert(note);
        Note foundNote = storage.findById(note.getId());
        assertEquals(foundNote.getTitle(),title);
        assertEquals(foundNote.getContent(),content);
        assertEquals(foundNote.getPinned(),pinned);
    }


    @Test
    public void testNoteUpdate() throws NotFoundException{
        NoteStorage storage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        Note note = new Note ("title1", "content1", 1);
        storage.insert(note);
        String title = "title2";
        String content = "content2";
        int pinned = 0;
        note.setTitle(title);
        note.setContent(content);
        note.setPinned(pinned);
        storage.update(note);
        Note foundNote = storage.findById(note.getId());
        assertEquals(foundNote.getTitle(),title);
        assertEquals(foundNote.getContent(),content);
        assertEquals(foundNote.getPinned(),pinned);
    }



    public void testGetAllNotesSortedByTitle() {

        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 1);
        Note note3 = new Note("C_Test_title", "blabla3", 1);
        Note note4 = new Note("D_Test_title", "blabla4", 2);
        boolean sortByCreatedDate = false;
        Note[] notes = {
                note1,
                note3,
                note4,
                note2,
        };
        Note[] expectedNoteArray = {
                note4,
                note1,
                note2,
                note3,
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));

        Util.fillNoteStorage(notes, noteStorage);

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate);

        Util.assertNoteArrayEquals(allStoredNotes, expectedNoteArray);
    }

    @Test
    public void testGetAllNotesSortedByDate() {
        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 1);
        Note note3 = new Note("C_Test_title", "blabla3", 1);
        Note note4 = new Note("D_Test_title", "blabla4", 2);
        boolean sortByCreatedDate = true;
        Note[] notes = {
                note3,
                note2,
                note4,
                note1,
        };
        Note[] expectedNoteArray = {
                note4,
                note1,
                note2,
                note3,
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));

        Util.fillNoteStorage(notes, noteStorage);

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate);

        Util.assertNoteArrayEquals(allStoredNotes, expectedNoteArray);
    }

    @Test
    public void testGetAllRemovedNotesSortedByDate() throws NotFoundException {

        Note note1 = new Note("A_Test_title", "blabla1", 1);
        Note note2 = new Note("B_Test_title", "blabla2", 1);
        Note note3 = new Note("C_Test_title", "blabla3", 1);
        boolean sortByCreatedDate = true;
        boolean removedOnly = true;
        Note[] notes = {
                note1,
                note3,
                note2,
        };
        Note[] expectedArray = {
                note2,
                note1,
        };

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));
        Util.fillNoteStorage(notes, noteStorage);
        noteStorage.softDelete(note1.getId());
        noteStorage.softDelete(note2.getId());

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate, removedOnly);

        Util.assertNoteArrayEquals(allStoredNotes, expectedArray);
    }

    @Test
    public void testGetAllRemovedNotesSortedByTitle() throws NotFoundException {

        Note note1 = new Note("B_Test_title", "blabla1", 1);
        Note note2 = new Note("A_Test_title", "blabla2", 1);
        Note note3 = new Note("C_Test_title", "blabla3", 1);
        boolean sortByCreatedDate = false;
        boolean removedOnly = true;
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
        Util.fillNoteStorage(notes, noteStorage);
        noteStorage.softDelete(note1.getId());
        noteStorage.softDelete(note2.getId());

        Note[] allStoredNotes = noteStorage.getAll(sortByCreatedDate, removedOnly);

        Util.assertNoteArrayEquals(allStoredNotes, expecterArray);
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

    @Test
    public void testSearchNote() throws NotFoundException {
        Note note1 = new Note("Adkdhe", "Ajdnh diekdn ekde eie", 0);
        Note note2 = new Note("Khdhdgrgrg", "Jdkdh dhgnd udef rtr", 0);
        Note note3 = new Note("Odjeuzd", "Kduejd efdf ef dferfef", 0);
        Note note4 = new Note("Ldjehd", "Ldf dfe dgrgrg fgtujtge", 0);

        Note notes[] = {note1, note2, note3, note4};

        NoteStorage noteStorage = new NoteStorage(new DatabaseHelper(InstrumentationRegistry.getTargetContext(), null));
        Util.fillNoteStorage(notes, noteStorage);

        String pattern = "grgrg";
        Note expectedNotes[] = {note2, note4};
        Note foundNotes[] = noteStorage.getAll(true, false, pattern);
        assertTrue(expectedNotes.length == foundNotes.length);
        for (Note eNote: expectedNotes){
            boolean found = false;
            for(Note fNote: foundNotes){
                if (eNote.equals(fNote)){
                    found = true;
                }
            }
            assertTrue(found);
        }
    }


}
