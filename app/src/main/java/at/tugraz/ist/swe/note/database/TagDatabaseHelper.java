package at.tugraz.ist.swe.note.database;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.support.annotation.Nullable;
        import android.support.annotation.VisibleForTesting;

public class TagDatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 2;
    public static final String TAG_DATABASE_NAME = "tag";
    public static final String TAG_TABLE_NAME = "tag";
    public static final String TAG_COLUMN_NAME = "name";
    public static final String TAG_COLUMN_COLOR = "color";
    public static final String TAG_COLUMN_ID = "id";
    public static final String TAG_COLUMN_NUMBER_OF_USAGES = "numberOfUsages";

    public TagDatabaseHelper(@Nullable Context context) {
        super(context, TAG_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @VisibleForTesting
    public TagDatabaseHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL("CREATE TABLE " + TAG_TABLE_NAME + " (" +
                TAG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TAG_COLUMN_COLOR + " INTEGER NOT NULL, " +
                TAG_COLUMN_NAME + " TEXT NOT NULL, " +
                TAG_COLUMN_NUMBER_OF_USAGES + " INTEGER NOT NULL"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int i, int i1) {
        dataBase.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE_NAME);
        onCreate(dataBase);
    }
}