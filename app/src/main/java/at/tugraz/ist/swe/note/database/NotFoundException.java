package at.tugraz.ist.swe.note.database;

public class NotFoundException extends Exception {
    private long id;

    public NotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
