package at.tugraz.ist.swe.note;

import java.util.Date;

public class Note {
    public static long NOT_CREATED_YET = -1;

    private long id;
    private String title;
    private String content;
    private Date createdDate;
    private  boolean removed;
    private int pinned;
    private Date changedDate;

    public Note(){
        this.id = NOT_CREATED_YET;
        this.title = "";
        this.content = "";
        this.createdDate = new Date();
        this.removed = false;
        this.pinned = 0;
        this.changedDate = new Date();
    }

    public Note(String title, String content, int pinned){
        this.id = NOT_CREATED_YET;
        this.title = title;
        this.content = content;
        this.createdDate = new Date();
        this.removed = false;
        this.pinned = pinned;
        this.changedDate = new Date();
    }

    public boolean exists() {
        return id != NOT_CREATED_YET;
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
