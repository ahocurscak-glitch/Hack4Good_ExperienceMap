import java.util.*;

public class WPI {
    static ArrayList<String> courses = CSVReader.ReadCSV("WPI_Courses.csv");
    static int classSize = 25;
    static double campusSize = 0.38;
    static int totalStudents = 7177;
    static ArrayList<Double> genderPerc;
    static ArrayList<Double> racePerc;
    static ArrayList<Double> lgbtqPerc;

    public static ArrayList<String> getCourses(){
        return courses;
    }

    public static int getClassSize(){
        return classSize;
    }

    public static double getCampusSize(){
        return campusSize;
    }

    public static int getTotalStudents(){
        return totalStudents;
    }

    public static ArrayList<Double> getGenderPerc(){
        return genderPerc;
    }

    public static ArrayList<Double> getRacePerc(){
        return racePerc;
    }

    public static ArrayList<Double> getLGBTQPerc(){
        return lgbtqPerc;
    }
}