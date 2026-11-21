import java.util.*;

public class WPI {
    private ArrayList<String> courses = CSVReader.ReadCSV("WPI_Courses.csv");
    private int classSize = 25;
    private double campusSize = 0.38;
    private int totalStudents = 7177;
    private ArrayList<Double> genderPerc;
    private ArrayList<Double> racePerc;
    private ArrayList<Double> lgbtqPerc;

    public ArrayList<String> getCourses(){
        return courses;
    }

    public int getClassSize(){
        return classSize;
    }

    public double getCampusSize(){
        return campusSize;
    }

    public int getTotalStudents(){
        return totalStudents;
    }

    public ArrayList<Double> getGenderPerc(){
        return genderPerc;
    }

    public ArrayList<Double> getRacePerc(){
        return racePerc;
    }

    public ArrayList<Double> getLGBTQPerc(){
        return lgbtqPerc;
    }
}