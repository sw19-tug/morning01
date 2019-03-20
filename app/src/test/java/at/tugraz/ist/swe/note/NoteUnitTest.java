package at.tugraz.ist.swe.note;

import org.junit.Test;


import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NoteUnitTest {

    Note note;
    Date today;

    @Test
    public void setUp() {
        note = new Note();
        today = new Date();
        System.out.print(today);
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