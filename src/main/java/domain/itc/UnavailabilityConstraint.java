package domain.itc;

public class UnavailabilityConstraint {
    private String courseId;
    private int day;
    private int dayPeriod;

    public UnavailabilityConstraint() {
    }

    public UnavailabilityConstraint(String[]parameters) {
        this.courseId = parameters[0];
        this.day = Integer.parseInt(parameters[1]);
        this.dayPeriod = Integer.parseInt(parameters[2]);
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
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
        return "UnavailabilityConstraint{" +
                "courseId='" + courseId + '\'' +
                ", day=" + day +
                ", dayPeriod=" + dayPeriod +
                '}';
    }
}
