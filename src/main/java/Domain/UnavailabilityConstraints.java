package Domain;

public class UnavailabilityConstraints {
    private String at1;
    private String at2;
    private String at3;

    public UnavailabilityConstraints(String at1, String at2, String at3) {
        this.at1 = at1;
        this.at2 = at2;
        this.at3 = at3;
    }

    public UnavailabilityConstraints(String[] s) {
        at1 = s[0];
        at2 = s[1];
        at3 = s[2];
    }

    public String getAt1() {
        return at1;
    }

    public void setAt1(String at1) {
        this.at1 = at1;
    }

    public String getAt2() {
        return at2;
    }

    public void setAt2(String at2) {
        this.at2 = at2;
    }

    public String getAt3() {
        return at3;
    }

    public void setAt3(String at3) {
        this.at3 = at3;
    }

    @Override
    public String toString() {
        return "UnavailabilityConstraints{" +
                "at1='" + at1 + '\'' +
                ", at2='" + at2 + '\'' +
                ", at3='" + at3 + '\'' +
                '}';
    }
}
