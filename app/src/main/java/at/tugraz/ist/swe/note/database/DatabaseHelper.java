package at.tugraz.ist.swe.note.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import at.tugraz.ist.swe.note.Note;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String NOTE_DATABASE_NAME = "note";
    public static final String NOTE_TABLE_NAME = "note";
    public static final String NOTE_COLUMN_TITLE = "title";
    public static final String NOTE_COLUMN_ID = "id";
    public static final String NOTE_COLUMN_CONTENT = "content";
    public static final String NOTE_COLUMN_CREATED_DATE = "created_date";
    public static final String NOTE_COLUMN_CHANGED_DATE = "changed_at";
    public static final String NOTE_COLUMN_REMOVED = "removed";
    public static final String NOTE_COLUMN_PINNED = "pinned";
    public static final String NOTE_COLUMN_PROTECTED = "protected";

    public static final String TAG_TABLE_NAME = "tag";
    public static final String TAG_COLUMN_NAME = "name";
    public static final String TAG_COLUMN_COLOR = "color";
    public static final String TAG_COLUMN_ID = "id";

    public static final String NOTE_TAG_TABLE_NAME = "note_tag";
    public static final String NOTE_TAG_COLUMN_NOTE_ID = "note_id";
    public static final String NOTE_TAG_COLUMN_TAG_ID = "tag_id";

    public DatabaseHelper(@Nullable Context context) {
        super(context, NOTE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @VisibleForTesting
    public DatabaseHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL("CREATE TABLE " + NOTE_TABLE_NAME + " (" +
                NOTE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTE_COLUMN_TITLE + " TEXT NOT NULL, " +
                NOTE_COLUMN_CONTENT + " TEXT NOT NULL, " +
                NOTE_COLUMN_CREATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON CONFLICT REPLACE, " +
                NOTE_COLUMN_CHANGED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON CONFLICT REPLACE, " +
                NOTE_COLUMN_PINNED + " INTEGER DEFAULT " + Note.DEFAULT_PINNED + " NOT NULL, " +
                NOTE_COLUMN_REMOVED + " BOOLEAN DEFAULT 0 NOT NULL," +
                NOTE_COLUMN_PROTECTED + " BOOLEAN DEFAULT 0 NOT NULL" + ")");  // BOOLEAN is a shortcut for INTEGER in sqlite3

        dataBase.execSQL("CREATE TABLE " + TAG_TABLE_NAME + " (" +
                TAG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TAG_COLUMN_COLOR + " INTEGER NOT NULL, " +
                TAG_COLUMN_NAME + " TEXT NOT NULL," +
                "CONSTRAINT unique_name UNIQUE( " + TAG_COLUMN_NAME + "))");

        dataBase.execSQL("CREATE TABLE " + NOTE_TAG_TABLE_NAME + " (" +
                NOTE_TAG_COLUMN_NOTE_ID + " INTEGER NOT NULL, " +
                NOTE_TAG_COLUMN_TAG_ID + " INTEGER NOT NULL, " +
                "PRIMARY KEY(" + NOTE_TAG_COLUMN_NOTE_ID + "," + NOTE_TAG_COLUMN_TAG_ID + ")," +
                "FOREIGN KEY(" + NOTE_TAG_COLUMN_NOTE_ID + ") REFERENCES " + NOTE_TABLE_NAME + "(" + NOTE_COLUMN_ID + ")," +
                "FOREIGN KEY(" + NOTE_TAG_COLUMN_TAG_ID + ") REFERENCES " + TAG_TABLE_NAME + "(" + TAG_COLUMN_ID + ")" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int i, int i1) {
        dataBase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        dataBase.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE_NAME);
        dataBase.execSQL("DROP TABLE IF EXISTS " + NOTE_TAG_TABLE_NAME);
        onCreate(dataBase);
    }
}
