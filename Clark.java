import java.util.*;
public class Clark{
    private ArrayList<String> courses = CSVReader.ReadCSV("Clark CS courses.csv");
    private int averageClassSize = 18;
    private double campusSize = 0.29; // in square kilometers (72 acres)
    private int totalStudents = 3700;
    private ArrayList<String> CoA = new ArrayList<>(Arrays.asList("Resident: $77,970", "Commuter: $63,610"));
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 61.9%", "Male: 38.1%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 61.4%", "International: 9.7%", "Latinx: 9.7%", "Asian: 7.1%", "Black: 4.8%", "Unknown: 4.1%", "Multi-ethnic: 3.2%", "Native Hawaiian / Pacific Islander: 0.05%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Resident: 68%", "Commuter: 32%"));
    
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

    public ArrayList<String> getCost() {
        return CoA;
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