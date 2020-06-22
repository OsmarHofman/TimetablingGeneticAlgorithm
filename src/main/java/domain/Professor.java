package domain;

public class Professor {
    private String id;
    private String name;
    private String timeoff;

    public Professor(String name) {
        this.id = name;
        this.name = name;
    }

    public Professor(String id, String name, String timeoff) {
        this.id = id;
        this.name = name;
        this.timeoff = timeoff;
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

    public String getTimeoff() {
        return timeoff;
    }

    public void setTimeoff(String timeoff) {
        this.timeoff = timeoff;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeoff='" + timeoff + '\'' +
                '}';
    }
}
