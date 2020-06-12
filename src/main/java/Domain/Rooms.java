package Domain;

public class Rooms {
    private String at1;
    private String at2;

    public Rooms(String at1, String at2) {
        this.at1 = at1;
        this.at2 = at2;
    }

    public Rooms(String[] s) {
        this.at1 = s[0];
        this.at2 = s[1];
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

    @Override
    public String toString() {
        return "Rooms{" +
                "at1='" + at1 + '\'' +
                ", at2='" + at2 + '\'' +
                '}';
    }
}
