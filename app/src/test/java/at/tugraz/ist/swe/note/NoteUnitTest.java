package at.tugraz.ist.swe.note;

import org.junit.Before;
import org.junit.Test;


import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class NoteUnitTest {

    Note note;
    Date today;

    @Before
    public void setUp() {
        note = new Note();
        today = new Date();
    }

    @Test
    public void testInitialState() {
        assertNotNull(note);
        assertEquals("", note.getTitle());
        assertEquals("", note.getContent());
        assertEquals(today, note.getDate());
        assertEquals(0, note.isPinned());
        assertEquals(false, note.isRemoved());
        assertEquals(today, note.getChangedDate());
    }
}