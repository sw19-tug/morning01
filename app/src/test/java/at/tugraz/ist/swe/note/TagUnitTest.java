package at.tugraz.ist.swe.note;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import at.tugraz.ist.swe.note.database.NoteTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TagUnitTest {

    private NoteTag mTag;
    private NoteTag mTagWithParams;
    String tagName = "Tag name";
    int tagColor = Color.RED;

    @Before
    public void setUp() {
        mTag = new NoteTag();
        mTagWithParams = new NoteTag(tagName, tagColor);
    }

    @Test
    public void testInitialState() {
        assertNotNull(mTag);
        assertEquals("", mTag.getName());
        assertNull(mTag.getColor());
        assertEquals(0, mTag.getNumberOfUsages);
        assertEquals(0, mTag.getId());
    }

    @Test
    public void testInitialStateWithParams() {
        assertNotNull(mTagWithParams);
        assertEquals(tagName, mTag.getName());
        assertEquals(tagColor, mTag.getColor());
        assertEquals(0, mTag.getNumberOfUsages);
        assertEquals(0, mTag.getId());
    }

}
