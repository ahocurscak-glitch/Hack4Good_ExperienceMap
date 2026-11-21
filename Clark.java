import java.util.*;
public class Clark {
    static ArrayList<String> courses = CSVReader.ReadCSV("Clark CS courses.csv");
    private int averageClassSize = 18;
    private double airQuality;
    private double campusSize = 0.75; // in square kilometers
    private int totalStudents = 2038;
    private ArrayList<Double> genderPerc;
    private ArrayList<Double> racePerc;
    private ArrayList<Double> lgbtqPerc;
    
    public ArrayList<String> getCourses() {
        return courses;
    }

    public int getClassSize() {
        return averageClassSize;
    }

    public double getAirQuality() {
        return airQuality;
    }

    public double getCampusSize() {
        return campusSize;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public ArrayList<Double> getGenderPerc() {
        return genderPerc;
    }

    public ArrayList<Double> getRacePerc() {
        return racePerc;
    }

    public ArrayList<Double> getLgbtqPerc() {
        return lgbtqPerc;
    }
}