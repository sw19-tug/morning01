package at.tugraz.ist.swe.note;

import org.junit.Before;
import org.junit.Test;


import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class NoteUnitTest {

    Note note, notewithparams;
    Date today;

    @Before
    public void setUp() {
        note = new Note();
        today = new Date();
        notewithparams = new Note("School", "Lorum Ipsum", 0);
    }

    @Test
    public void testInitialState() {
        assertNotNull(note);
        assertEquals("", note.getTitle());
        assertEquals("", note.getContent());
        assertEquals(today, note.getCreatedDate());
        assertEquals(0, note.getPinned());
        assertEquals(false, note.isRemoved());
        assertEquals(today, note.getChangedDate());
    }

    @Test
    public void testInitialStatewithparams(){
        assertNotNull(notewithparams);
        assertEquals("School", notewithparams.getTitle());
        assertEquals("Lorum Ipsum", notewithparams.getContent());
        assertEquals(today, notewithparams.getCreatedDate());
        assertEquals(0, notewithparams.getPinned());
        assertEquals(false, notewithparams.isRemoved());
        assertEquals(today, notewithparams.getChangedDate());

    }

}