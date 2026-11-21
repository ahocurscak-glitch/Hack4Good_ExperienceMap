import java.util.*;
public class WSU{
    static ArrayList<String> courses = CSVReader.ReadCSV("WSU CS courses.csv");
    private int averageClassSize = 21;
    private double airQuality;
    private double campusSize = 0.235; // in square kilometers
    private int totalStudents = 5273;
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
