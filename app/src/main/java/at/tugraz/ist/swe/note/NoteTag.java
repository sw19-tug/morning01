package at.tugraz.ist.swe.note;


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

        return this.name.equals(otherNoteTag.name) && this.color == otherNoteTag.color &&
                (this.numberOfUsages == otherNoteTag.numberOfUsages);
    }
}
