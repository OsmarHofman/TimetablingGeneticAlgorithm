package domain;

public class Curriculas {
    private String at1;
    private String at2;
    private String at3;
    private String at4;
    private String at5;
    private String at6;

    public Curriculas(String at1, String at2, String at3, String at4, String at5, String at6) {
        this.at1 = at1;
        this.at2 = at2;
        this.at3 = at3;
        this.at4 = at4;
        this.at5 = at5;
        this.at6 = at6;
    }

    public Curriculas(String[] s) {
        if (s.length == 2) {
            this.at1 = s[0];
            this.at2 = s[1];
            this.at3 = "";
            this.at4 = "";
            this.at5 = "";
            this.at6 = "";
        } else if (s.length == 3) {
            this.at1 = s[0];
            this.at2 = s[1];
            this.at3 = s[2];
            this.at4 = "";
            this.at5 = "";
            this.at6 = "";
        } else if (s.length == 4) {
            this.at1 = s[0];
            this.at2 = s[1];
            this.at3 = s[2];
            this.at4 = s[3];
            this.at5 = "";
            this.at6 = "";
        } else if (s.length == 5) {
            this.at1 = s[0];
            this.at2 = s[1];
            this.at3 = s[2];
            this.at4 = s[3];
            this.at5 = s[4];
            this.at6 = "";
        } else if (s.length == 6) {
            this.at1 = s[0];
            this.at2 = s[1];
            this.at3 = s[2];
            this.at4 = s[3];
            this.at5 = s[4];
            this.at6 = s[5];
        }
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

    public String getAt6() {
        return at6;
    }

    public void setAt6(String at6) {
        this.at6 = at6;
    }

    @Override
    public String toString() {
        return "Curriculas{" +
                "at1='" + at1 + '\'' +
                ", at2='" + at2 + '\'' +
                ", at3='" + at3 + '\'' +
                ", at4='" + at4 + '\'' +
                ", at5='" + at5 + '\'' +
                ", at6='" + at6 + '\'' +
                '}';
    }
}
