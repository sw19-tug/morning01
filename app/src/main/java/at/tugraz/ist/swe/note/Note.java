package at.tugraz.ist.swe.note;

import java.util.Date;

public class Note {
    public static long NOT_INSERTED_YET = -1;

    private long id = NOT_INSERTED_YET;
    private String title = "";
    private String content = "";
    private Date createdDate = null;
    private boolean removed = false;
    private int pinned = 0;
    private Date changedDate = null;

    public Note(){
    }

    public Note(String title, String content, int pinned){
        this.title = title;
        this.content = content;
        this.pinned = pinned;
    }

    public boolean wasInserted() {
        return id != NOT_INSERTED_YET;
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
}
