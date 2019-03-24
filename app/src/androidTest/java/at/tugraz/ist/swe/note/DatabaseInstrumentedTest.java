package at.tugraz.ist.swe.note;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {
    private SqlDatabaseHelper database;

    @Before
    public void setUp() {
        database = new SqlDatabaseHelper(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testConnection() {
        assertEquals(database.getDatabaseName(), "note");
        assertNotNull(database.getReadableDatabase());
        assertNotNull(database.getWritableDatabase());
    }
}
