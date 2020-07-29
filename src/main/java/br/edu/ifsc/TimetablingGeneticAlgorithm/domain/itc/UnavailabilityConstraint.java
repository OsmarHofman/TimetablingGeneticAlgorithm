package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

public class UnavailabilityConstraint {
    private String id;
    private int day;
    private int dayPeriod;

    public UnavailabilityConstraint() {
    }

    public UnavailabilityConstraint(String id, int day, int dayPeriod) {
        this.id = id;
        this.day = day;
        this.dayPeriod = dayPeriod;
    }

    public UnavailabilityConstraint(String[]parameters) {
        this.id = parameters[0];
        this.day = Integer.parseInt(parameters[1]);
        this.dayPeriod = Integer.parseInt(parameters[2]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", day=" + day +
                ", dayPeriod=" + dayPeriod +
                '}';
    }
}
