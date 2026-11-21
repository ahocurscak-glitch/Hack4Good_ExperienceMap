import java.util.*;
public class WSU{
    private ArrayList<String> courses = CSVReader.ReadCSV("WSU CS courses.csv");
    private int averageClassSize = 21;
    private double campusSize = 0.235; // in square kilometers
    private int totalStudents = 5273;
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 61.5%", "Male: 38.5%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 62.4%", "Latinx: 16.7%", "Black: 8.5%", "Asian: 5.3%", "Multi-ethnic: 3.0%", "Unknown: 2.3%", "International: 1.9%", "Native Hawiian / Pacific Islander: <0.1%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Commuter: 66.7%", "Resident: 33.3%"));
    
    public ArrayList<String> getCourses() {
        return courses;
    }

    public int getClassSize() {
        return averageClassSize;
    }

    public double getCampusSize() {
        return campusSize;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public ArrayList<String> getGenderPerc() {
        return genderPerc;
    }

    public ArrayList<String> getRacePerc() {
        return racePerc;
    }

    public ArrayList<String> getLgbtqPerc() {
        return lgbtqPerc;
    }

    public ArrayList<String> getCommuterPerc() {
        return commuterPerc;
    }
}
