package at.tugraz.ist.swe.note;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityImportTest {
    private static final File TMP_DIRECTORY = new File(MainActivity.TMP_DIRECTORY);
    private static final File OUTPUT_DIRECTORY = new File(MainActivity.OUTPUT_DIRECTORY);
    private static final String ZIP_OUTPUT_PATH = OUTPUT_DIRECTORY.toString()  + "/NotesTest.zip";
    private static final File ZIP_OUTPUT_FILE = new File(ZIP_OUTPUT_PATH);

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Util.resetDatabase(new DatabaseHelper(InstrumentationRegistry.getTargetContext()));
        ZIP_OUTPUT_FILE.delete();
    }

    @After
    public void cleanUp() {
        assertTrue(MainActivity.deleteRecursive(TMP_DIRECTORY));
    }

    @Test
    public void checkImportingLogic() throws Exception {
        MainActivity activity = activityActivityTestRule.getActivity();
        Note note1 = new Note("Adkdhe", "Ajdnh diekdn ekde eie", Note.DEFAULT_PINNED);
        Note note2 = new Note("Khdhd", "Jdkdh dhgrgrgnd udef rtr", Note.DEFAULT_PINNED);
        Note note3 = new Note("Odjeuzd", "Kduejd efdf ef dferfef", Note.DEFAULT_PINNED);
        Note note4 = new Note("Ldjehd", "Ldf dfe dgrgrg fgtujtge", Note.DEFAULT_PINNED);
        Note[] notes = {
                note1,
                note2,
                note3,
                note4,
        };
        activity.convertNotesToFiles(TMP_DIRECTORY, notes);
        assertTrue(MainActivity.zipFolder(TMP_DIRECTORY.toString(), ZIP_OUTPUT_PATH));
        assertTrue(ZIP_OUTPUT_FILE.exists());
        Note[] unzipNotes = MainActivity.unzip(ZIP_OUTPUT_PATH);
        Util.assertNoteArrayContains(unzipNotes, notes);
    }
}
