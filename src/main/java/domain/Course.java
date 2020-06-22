package domain;

public class Course {
    private String id;
    private String name;
    private String shortName;
    private String timeoff;
    private int minWorkingDays;
    private String professorId;

    public Course(String id, String name, String shortName, String timeoff, String professor) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.timeoff = timeoff;
        this.minWorkingDays = calcMinWorkingDays(timeoff);
        this.professorId = professor;
    }

    public Course(String name) {
        this.name = name;
    }

    public Course(String[] s) {
        id = s[0];
        name = s[0];
        shortName = s[0];
        professorId = s[2];
        minWorkingDays = Integer.parseInt(s[3]);
        timeoff = "";
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

    public int getMinWorkingDays() {
        return minWorkingDays;
    }

    public void setMinWorkingDays(int minWorkingDays) {
        this.minWorkingDays = minWorkingDays;
    }

    public String getProfessor() {
        return professorId;
    }

    public void setProfessor(String professor) {
        this.professorId = professor;
    }

    private int calcMinWorkingDays(String timeoff) {
        String[] splitTimeoff = timeoff.split(",");
        int total = 0;
        for (String s : splitTimeoff)
            if (s.contains("1"))
                total++;
        return total;
    }

    @Override
    public String toString() {
        return "Courses{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", timeoff='" + timeoff + '\'' +
                ", minWorkingDays=" + minWorkingDays +
                ", professor=" + professorId +
                '}';
    }
}
