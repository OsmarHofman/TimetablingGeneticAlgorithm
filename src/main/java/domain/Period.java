package domain;

import java.util.Date;
import java.util.Objects;

public class Period {
    private int id;
    private Date startTime;
    private Date endTime;
    private String shortName;
    private String name;

    public Period(int id, Date startTime, Date endTime, String shortName, String name) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shortName = shortName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return id == period.id &&
                startTime.equals(period.startTime) &&
                endTime.equals(period.endTime) &&
                shortName.equals(period.shortName) &&
                name.equals(period.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, shortName, name);
    }

    @Override
    public String toString() {
        return "Period{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
