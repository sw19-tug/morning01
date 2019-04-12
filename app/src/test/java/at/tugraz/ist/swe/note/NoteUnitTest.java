package at.tugraz.ist.swe.note;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NoteUnitTest {

    private Note note;
    private Note noteWithParams;

    @Before
    public void setUp() {
        note = new Note();
        noteWithParams = new Note("School", "Lorum Ipsum", 0);
    }

    @Test
    public void testInitialState() {
        assertNotNull(note);
        assertEquals("", note.getTitle());
        assertEquals("", note.getContent());
        assertNull(note.getCreatedDate());
        assertEquals(Note.DEFAULT_PINNED, note.getPinned());
        assertFalse(note.isRemoved());
        assertNull(note.getChangedDate());
    }

    @Test
    public void testInitialStatewithparams(){
        assertNotNull(noteWithParams);
        assertEquals("School", noteWithParams.getTitle());
        assertEquals("Lorum Ipsum", noteWithParams.getContent());
        assertNull(noteWithParams.getCreatedDate());
        assertEquals(Note.DEFAULT_PINNED, noteWithParams.getPinned());
        assertFalse(noteWithParams.isRemoved());
        assertNull(noteWithParams.getChangedDate());
    }
}