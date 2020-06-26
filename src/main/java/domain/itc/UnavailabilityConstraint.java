package domain.itc;

public class UnavailabilityConstraint {
    private String lessonId;
    private int day;
    private int dayPeriod;

    public UnavailabilityConstraint() {
    }

    public UnavailabilityConstraint(String lessonId, int day, int dayPeriod) {
        this.lessonId = lessonId;
        this.day = day;
        this.dayPeriod = dayPeriod;
    }

    public UnavailabilityConstraint(String[]parameters) {
        this.lessonId = parameters[0];
        this.day = Integer.parseInt(parameters[1]);
        this.dayPeriod = Integer.parseInt(parameters[2]);
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
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
                "courseId='" + lessonId + '\'' +
                ", day=" + day +
                ", dayPeriod=" + dayPeriod +
                '}';
    }
}
