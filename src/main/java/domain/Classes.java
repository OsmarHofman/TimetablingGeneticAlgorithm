package domain;

public class Classes {
    private int id;
    private String name;
    private String shortName;
    private int teacherId;
    private String timeoff;

    public Classes() {
    }

    public Classes(int id, String name, String shortName, int teacherId, String timeoff) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.teacherId = teacherId;
        this.timeoff = timeoff;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Classes [id=" + id + ", name=" + name + ", shortName=" + shortName + ", teacherId=" + teacherId
                + ", timeoff=" + timeoff + "]";
    }
}
