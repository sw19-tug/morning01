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
                NOTE_COLUMN_PROTECTED + " BOOLEAN DEFAULT 0 NOT NULL" +")");  // BOOLEAN is a shortcut for INTEGER in sqlite3
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int i, int i1) {
        dataBase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        onCreate(dataBase);
    }
}
