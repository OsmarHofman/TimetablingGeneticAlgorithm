package domain;

public class Courses {
    private String id;
    private String name;
    private String shortName;
    private String timeoff;
    private String minWorkingDays;

    public Courses(String id, String name, String shortName, String timeoff, String minWorkingDays) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.timeoff = timeoff;
        this.minWorkingDays = minWorkingDays;
    }

    public Courses(String[] s) {
        id = s[0];
        name = s[1];
        shortName = s[2];
        timeoff = s[3];
        minWorkingDays = s[4];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTimeoff() {
        return timeoff;
    }

    public void setTimeoff(String timeoff) {
        this.timeoff = timeoff;
    }

    public String getMinWorkingDays() {
        return minWorkingDays;
    }

    public void setMinWorkingDays(String minWorkingDays) {
        this.minWorkingDays = minWorkingDays;
    }

    @Override
    public String toString() {
        return "Courses{" +
                "at1='" + id + '\'' +
                ", at2='" + name + '\'' +
                ", at3='" + shortName + '\'' +
                ", at4='" + timeoff + '\'' +
                ", at5='" + minWorkingDays + '\'' +
                '}';
    }
}
