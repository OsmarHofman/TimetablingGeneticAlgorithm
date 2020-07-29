package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.dataaccess;


import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Room;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;
import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOITC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RetrieveITCData {

    private DTOITC dtoitc;

    List<Lesson> lessons = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    List<Course> courses = new ArrayList<>();
    List<UnavailabilityConstraint> constraints = new ArrayList<>();


    public DTOITC getFromITC() {
        File myObj = new File("src/Datasets/ITCFiles/comp01.ctt.txt");
        Scanner myReader;

        try {
            myReader = new Scanner(myObj);
            String data;
            while (myReader.hasNextLine()) {

                //cursos
                if (myReader.hasNext("COURSES:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("ROOMS:")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            //Lesson lesson = new Lesson(data.split(" "));
                            //lessons.add(lesson);
                        }
                    }
                }

                if (myReader.hasNext("ROOMS:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("CURRICULA:")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            Room room = new Room(data.split("\t"));
                            rooms.add(room);
                        }
                    }
                }

                if (myReader.hasNext("CURRICULA:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("UNAVAILABILITY_CONSTRAINTS:")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            Course course = new Course(data.split(" "));
                            courses.add(course);
                        }
                    }
                }

                if (myReader.hasNext("UNAVAILABILITY_CONSTRAINTS:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("END.")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            UnavailabilityConstraint unavailabilityConstraint = new UnavailabilityConstraint(data.split(" "));
                            constraints.add(unavailabilityConstraint);
                        }
                    }
                }
                myReader.nextLine();
            }
            myReader.close();
            for (Lesson iterationLesson : lessons) {
                iterationLesson.retrieveConstraints(constraints);
            }
            dtoitc = new DTOITC(courses, lessons, rooms, constraints);


        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return dtoitc;
    }
}



