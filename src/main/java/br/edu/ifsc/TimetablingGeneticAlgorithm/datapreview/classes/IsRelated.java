package br.edu.ifsc.TimetablingGeneticAlgorithm.datapreview.classes;

public class IsRelated {

    private String name;
    private boolean hasAdded;

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

    public void setHasAdded(boolean hasAdded) {
        this.hasAdded = hasAdded;
    }
}
