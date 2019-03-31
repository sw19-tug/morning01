package at.tugraz.ist.swe.note;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

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
        assertEquals(null, note.getCreatedDate());
        assertEquals(0, note.getPinned());
        assertEquals(false, note.isRemoved());
        assertEquals(null, note.getChangedDate());
    }

    @Test
    public void testInitialStatewithparams(){
        assertNotNull(noteWithParams);
        assertEquals("School", noteWithParams.getTitle());
        assertEquals("Lorum Ipsum", noteWithParams.getContent());
        assertEquals(null, noteWithParams.getCreatedDate());
        assertEquals(0, noteWithParams.getPinned());
        assertEquals(false, noteWithParams.isRemoved());
        assertEquals(null, noteWithParams.getChangedDate());
    }
}