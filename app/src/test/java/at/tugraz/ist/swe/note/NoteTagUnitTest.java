package at.tugraz.ist.swe.note;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NoteTagUnitTest {

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
        assertEquals(0, mTag.getColor());
        assertEquals(0, mTag.getNumberOfUsages());
        assertEquals(-1, mTag.getId());
    }

    @Test
    public void testInitialStateWithParams() {
        assertNotNull(mTagWithParams);
        assertEquals(tagName, mTagWithParams.getName());
        assertEquals(tagColor, mTagWithParams.getColor());
        assertEquals(0, mTagWithParams.getNumberOfUsages());
        assertEquals(-1, mTagWithParams.getId());
    }

}
