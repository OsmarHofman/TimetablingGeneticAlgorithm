package domain;

public class UnavailabilityConstraints {
    private int courseId;
    private int day;
    private int dayPeriod;

    public UnavailabilityConstraints(int courseId, int day, int dayPeriod) {
        this.courseId = courseId;
        this.day = day;
        this.dayPeriod = dayPeriod;
    }

    public UnavailabilityConstraints(String[] s) {
        courseId = Integer.parseInt(s[0]);
        day = Integer.parseInt(s[1]);
        dayPeriod = Integer.parseInt(s[2]);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDayPeriod() {
        return dayPeriod;
    }

    public void setDayPeriod(int dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    @Override
    public String toString() {
        return "UnavailabilityConstraints{" +
                "at1='" + courseId + '\'' +
                ", at2='" + day + '\'' +
                ", at3='" + dayPeriod + '\'' +
                '}';
    }
}
