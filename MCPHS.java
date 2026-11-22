import java.util.*;
public class MCPHS{
    private ArrayList<String> courses = CSVReader.ReadCSV("MCPHSDataCourses.csv");
    private int averageClassSize = 22;
    private double campusSize = 0.036; // in square kilometers
    private int totalStudents = 5945;
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 76.5%", "Male: 22.5%", "Unknown: 0.9%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 35.4%", "Latinx: 12.1%", "Black: 12.8%", "Multi-ethnic: 1.6%", "International: 14.6%", "Asian: 18.2%", "Unknown: 7.4%", "Native Hawiian / Pacific Islander: 0.02%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Resident: 66%", "Commuter: 34%"));
    
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
