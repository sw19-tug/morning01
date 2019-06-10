package at.tugraz.ist.swe.note;


import android.graphics.Color;

import java.io.Serializable;

public class NoteTag implements Serializable {
    public static long ILLEGAL_ID = -1;

    private long id = ILLEGAL_ID;
    private int color = Color.BLACK;
    private String name = "";

    public NoteTag(){
    }

    public NoteTag(String name, int color){
        this.color = color;
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public int getColor() {
        return color;
    }
    public String getName() { return name; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setColor(int color) { this.color = color; }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if (!(o instanceof NoteTag)) {
            return false;
        }

        NoteTag otherNoteTag = (NoteTag)o;

        return this.name.equals(otherNoteTag.name) && this.color == otherNoteTag.color;
    }

    public static String formatAsHtml(String name, int color) {
        String backgroundColor = String.format("#%06X", 0xFFFFFF & color);
        return "<span style=\"background-color: " + backgroundColor + "\">" + name + "</span>";
    }

    @Override
    public String toString() {
        return name + " "; // Additional space for the autocompletion in NoteActivity.
    }
}
