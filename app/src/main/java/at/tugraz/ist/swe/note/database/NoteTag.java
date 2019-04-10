package at.tugraz.ist.swe.note.database;

public class NoteTag {
    public static long ILLEGAL_ID = -1;

    private long id = ILLEGAL_ID;
    private int color = 0;
    private int numberOfUsages = 0;
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
    public int getNumberOfUsages() {
        return numberOfUsages;
    }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
