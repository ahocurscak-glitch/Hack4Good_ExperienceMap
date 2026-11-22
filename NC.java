import java.util.*;
public class NC {
    private ArrayList<String> courses = CSVReader.ReadCSV("NC CS courses.csv");
    private String averageClassSize = "Not Listed";
    private double campusSize = 0.81; // in square kilometers
    private int totalStudents = 1313;
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 34.7%", "Male: 65.3%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 69.0%", "Latinx: 12.0%", "Black: 7.0%", "Asian: 2.0%", "Multi-ethnic: 4.0%", "Unknown: 2.3%", "International: 4%", "Native Hawiian / Pacific Islander: <1.0%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Commuter: 27.0%", "Resident: 73.0%"));
    
    public ArrayList<String> getCourses() {
        return courses;
    }

    public String getClassSize() {
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
