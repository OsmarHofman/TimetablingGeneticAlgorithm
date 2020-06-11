package ImportFiles;

import Domain.Courses;
import Domain.Curriculas;
import Domain.Rooms;
import Domain.UnavailabilityConstraints;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RetrieveITCData {

    List<Courses> cursos = new ArrayList<>();
    List<Rooms> salas = new ArrayList<>();
    List<Curriculas> curriculas = new ArrayList<>();
    List<UnavailabilityConstraints> constraints = new ArrayList<>();


        File myObj = new File("ITCFiles/comp01.ctt.txt");
        Scanner myReader;
    {

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
                            Courses curso = new Courses(data.split(" "));
                            cursos.add(curso);
                        }
                    }
                }

                if (myReader.hasNext("ROOMS:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("CURRICULA:")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            Rooms rooms = new Rooms(data.split("\t"));
                            salas.add(rooms);
                        }
                    }
                }

                if (myReader.hasNext("CURRICULA:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("UNAVAILABILITY_CONSTRAINTS:")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            Curriculas cur = new Curriculas(data.split(" "));
                            curriculas.add(cur);
                        }
                    }
                }

                if (myReader.hasNext("UNAVAILABILITY_CONSTRAINTS:")) {
                    myReader.nextLine();
                    myReader.nextLine();
                    while (!myReader.hasNext("END.")) {
                        data = myReader.nextLine();
                        if (!data.equals("")) {
                            UnavailabilityConstraints unavailabilityConstraints = new UnavailabilityConstraints(data.split(" "));
                            constraints.add(unavailabilityConstraints);
                        }
                    }
                }
                myReader.nextLine();
            }
            myReader.close();


        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
//        System.out.println("Cursos:" + cursos.toString());
//        System.out.println("---------------------------------");
//        System.out.println("Rooms : " + salas.toString());
//        System.out.println("---------------------------------");
//        System.out.println("Curricula : " + curriculas.toString());
//        System.out.println("---------------------------------");
//        System.out.println("Constraints : " + constraints.toString());


}
