//package util;
//
//import domain.Classes;
//import domain.Lesson;
//import domain.Subject;
////import dto.Professor;
////import dto.Room;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class DTOServerData {
//    private List<Classes> classes;
//    private List<Lesson> lessons;
//    private List<Subject> subjects;
////    private List<Professor> professors;
////    private List<Room> rooms;
//
//
//    public DTOServerData() {
//        this.classes = new ArrayList<>();
//        this.lessons = new ArrayList<>();
//        this.subjects = new ArrayList<>();
////        this.professors = new ArrayList<>();
////        this.rooms = new ArrayList<>();
//    }
//
////    public DTOServerData(List<Classes> classes, List<Lesson> lessons, List<Subject> subjects, List<Professor> professors, List<Room> rooms) {
////        this.classes = classes;
////        this.lessons = lessons;
////        this.subjects = subjects;
////        this.professors = professors;
////        this.rooms = rooms;
////    }
//
//    public List<Classes> getClasses() {
//        return classes;
//    }
//
//    public void setClasses(List<Classes> classes) {
//        this.classes = classes;
//    }
//
//    public List<Lesson> getLessons() {
//        return lessons;
//    }
//
//    public void setLessons(List<Lesson> lessons) {
//        this.lessons = lessons;
//    }
//
//    public List<Subject> getSubjects() {
//        return subjects;
//    }
//
//    public void setSubjects(List<Subject> subjects) {
//        this.subjects = subjects;
//    }
//
////    public List<Professor> getProfessors() {
////        return professors;
////    }
////
////    public void setProfessors(List<Professor> professors) {
////        this.professors = professors;
////    }
////
////    public List<Room> getRooms() {
////        return rooms;
////    }
////
////    public void setRooms(List<Room> rooms) {
////        this.rooms = rooms;
////    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DTOServerData that = (DTOServerData) o;
//        return classes.equals(that.classes) &&
//                lessons.equals(that.lessons) &&
//                subjects.equals(that.subjects) &&
//                professors.equals(that.professors) &&
//                rooms.equals(that.rooms);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(classes, lessons, subjects, professors, rooms);
//    }
//
//    @Override
//    public String toString() {
//        return "DTOServerData{" +
//                "classes=" + classes +
//                ", lessons=" + lessons +
//                ", subjects=" + subjects +
//                ", professors=" + professors +
//                ", rooms=" + rooms +
//                '}';
//    }
//}
