package at.tugraz.ist.swe.note;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import at.tugraz.ist.swe.note.database.DatabaseHelper;

public class Note implements Serializable {
    public static final long ILLEGAL_ID = -1;
    public static final int DEFAULT_PINNED = 0;

    private long id = ILLEGAL_ID;
    private String title = "";
    private String content = "";
    private Date createdDate = null;
    private boolean removed = false;
    private int pinned = DEFAULT_PINNED;
    private Date changedDate = null;
    private boolean isProtected = false;
    private ArrayList<NoteTag> tags = new ArrayList<>();

    public Note(){
    }

    public Note(String title, String content, int pinned) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
    }

    public Note(String title, String content){
        this.title = title;
        this.content = content;
    }

    public Note(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.id = jsonObject.getLong(DatabaseHelper.NOTE_COLUMN_ID);
        this.title = jsonObject.getString(DatabaseHelper.NOTE_COLUMN_TITLE);
        this.content = jsonObject.getString(DatabaseHelper.NOTE_COLUMN_CONTENT);
        this.pinned = jsonObject.getInt(DatabaseHelper.NOTE_COLUMN_PINNED);
    }

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent(){
        return content;
    }
    public Date getCreatedDate(){
        return createdDate;
    }
    public boolean isRemoved(){
        return removed;
    }
    public int getPinned(){
        return pinned;
    }
    public Date getChangedDate(){
        return changedDate;
    }
    public ArrayList<NoteTag> getTags(){
        return tags;
    }

    public boolean getIsProtected() { return isProtected; }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    public void setProtected(boolean isProtected) { this.isProtected = isProtected; }

    public void addTag(NoteTag tag) {
        this.tags.add(tag);
    }

    @Override
    public boolean equals(Object other){
        if (other == this) {
            return true;
        }

        if (!(other instanceof Note)) {
            return false;
        }

        Note otherNote = (Note)other;

        return this.title.equals(otherNote.title) && this.content.equals(otherNote.content) &&
                (this.pinned == otherNote.pinned);
    }

    public String getJsonString() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DatabaseHelper.NOTE_COLUMN_ID, this.id);
        jsonObject.put(DatabaseHelper.NOTE_COLUMN_TITLE, this.title);
        jsonObject.put(DatabaseHelper.NOTE_COLUMN_CONTENT, this.content);
        jsonObject.put(DatabaseHelper.NOTE_COLUMN_PINNED, this.pinned);
        return jsonObject.toString();
    }
}
