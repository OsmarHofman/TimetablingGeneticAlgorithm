package br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc;

/**
 * Classe que representa as restrições de horário dos professores
 */
public class UnavailabilityConstraint {
    private int id;
    private int day;
    private int dayPeriod;

    public UnavailabilityConstraint(int id, int day, int dayPeriod) {
        this.id = id;
        this.day = day;
        this.dayPeriod = dayPeriod;
    }

    public UnavailabilityConstraint(String[] parameters) {
        this.id = Integer.parseInt(parameters[0]);
        this.day = Integer.parseInt(parameters[1]);
        this.dayPeriod = Integer.parseInt(parameters[2]);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "UnavailabilityConstraint{" +
                "id='" + id + '\'' +
                ", day=" + day +
                ", dayPeriod=" + dayPeriod +
                '}';
    }
}
