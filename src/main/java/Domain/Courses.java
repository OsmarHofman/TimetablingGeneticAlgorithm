package Domain;

public class Courses {
    private String at1;
    private String at2;
    private String at3;
    private String at4;
    private String at5;

    public Courses(String at1, String at2, String at3, String at4, String at5) {
        this.at1 = at1;
        this.at2 = at2;
        this.at3 = at3;
        this.at4 = at4;
        this.at5 = at5;
    }

    public Courses(String[] s) {
        at1 = s[0];
        at2 = s[1];
        at3 = s[2];
        at4 = s[3];
        at5 = s[4];
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

    public String getAt4() {
        return at4;
    }

    public void setAt4(String at4) {
        this.at4 = at4;
    }

    public String getAt5() {
        return at5;
    }

    public void setAt5(String at5) {
        this.at5 = at5;
    }

    @Override
    public String toString() {
        return "Courses{" +
                "at1='" + at1 + '\'' +
                ", at2='" + at2 + '\'' +
                ", at3='" + at3 + '\'' +
                ", at4='" + at4 + '\'' +
                ", at5='" + at5 + '\'' +
                '}';
    }
}
