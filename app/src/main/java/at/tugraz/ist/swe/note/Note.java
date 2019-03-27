package at.tugraz.ist.swe.note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import  at.tugraz.ist.swe.note.database.DatabaseHelper;

public class Note {
    private String title;
    private String content;
    private Date createdDate;
    private  boolean removed;
    private int pinned;
    private Date changedDate;
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public Note(){
        this.title = "";
        this.content = "";
        this.createdDate = new Date();
        this.removed = false;
        this.pinned = 0;
        this.changedDate = new Date();
    }

    public Note(String title, String content, int pinned){
        this.title = title;
        this.content = content;
        this.createdDate = new Date();
        this.removed = false;
        this.pinned = pinned;
        this.changedDate = new Date();

    }

    public String getTitle() {
        return title;
    }
    public String getContent(){
        return content;
    }
    public Date getDate(){
        return createdDate;
    }
    public  boolean isRemoved(){
        return removed;
    }
    public int isPinned(){
        return pinned;
    }
    public Date getChangedDate(){
        return changedDate;
    }


    static private String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }

    public boolean save(Context context){
        SQLiteDatabase database = new DatabaseHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_COLUMN_TITLE, title);
        values.put(DatabaseHelper.NOTE_COLUMN_CONTENT, content);
        values.put(DatabaseHelper.NOTE_COLUMN_PINNED, pinned);
        values.put(DatabaseHelper.NOTE_COLUMN_CREATED_DATE, convertDateToString(createdDate));
        values.put(DatabaseHelper.NOTE_COLUMN_CHANGED_DATE, convertDateToString(changedDate));
        values.put(DatabaseHelper.NOTE_COLUMN_REMOVED, removed);
        database.insert(DatabaseHelper.NOTE_TABLE_NAME, null, values);
        return true;
    }

}
