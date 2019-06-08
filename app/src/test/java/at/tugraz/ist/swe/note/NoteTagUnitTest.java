package at.tugraz.ist.swe.note;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoteTagUnitTest {

    private NoteTag noteTag;
    private NoteTag noteTagWithParams;
    String tagName = "Tag name";
    int tagColor = Color.RED;

    @Before
    public void setUp() {
        noteTag = new NoteTag();
        noteTagWithParams = new NoteTag(tagName, tagColor);
    }

    @Test
    public void testInitialState() {
        assertNotNull(noteTag);
        assertEquals("", noteTag.getName());
        assertEquals(Color.BLACK, noteTag.getColor());
        assertEquals(-1, noteTag.getId());
    }

    @Test
    public void testInitialStateWithParams() {
        assertNotNull(noteTagWithParams);
        assertEquals(tagName, noteTagWithParams.getName());
        assertEquals(tagColor, noteTagWithParams.getColor());
        assertEquals(-1, noteTagWithParams.getId());
    }

}
