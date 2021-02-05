package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities;


//TODO Refatorar para tentar retirar esta classe (método checkListIntersection da CourseRelation)
public class IsRelated {
    private String name;
    private final boolean hasAdded;

    public IsRelated(String name, boolean hasAdded) {
        this.name = name;
        this.hasAdded = hasAdded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasAdded() {
        return hasAdded;
    }

}
