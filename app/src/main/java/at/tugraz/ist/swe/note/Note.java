package at.tugraz.ist.swe.note;

import java.util.Date;
import java.util.List;

public class Note {
    private String title;
    private String content;
    private Date createdDate;
    private  boolean removed;
    private int pinned;
    private Date changedDate;

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

}
