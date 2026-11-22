import java.util.*;

public class WPI {
    private LinkedHashMap <String, ArrayList<String>> majors = new LinkedHashMap<>();
    private int classSize = 25;
    private double campusSize = 0.38;
    private int totalStudents = 7177;
    private ArrayList<String> CoA = new ArrayList<>(Arrays.asList("Resident: $87,570", "Commuter: $78,358"));
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Male: 71%", "Female: 29%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 57.8%", "International: 15.2%", "Asian: 11.2%", "Latinx: 7.5%", "Multi-ethnic: 3.2%", "Black: 2.6%", "Unknown: 2.5%", "Native Hawiian / Pacific Islander: near 0"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Commuter: 56%", "Resident: 44%"));

    public WPI(){
        majors.put("Computer Science", CSVReader.ReadCSV("WPICourses.csv"));
        majors.put("Business", CSVReader.ReadCSV("WPI Business Courses.csv"));
    }

    public Map<String, ArrayList<String>> getMajors() {
        return majors;
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

    public ArrayList<String> getCost() {
        return CoA;
    }

    public ArrayList<String> getGenderPerc(){
        return genderPerc;
    }

    public ArrayList<String> getRacePerc(){
        return racePerc;
    }

    public ArrayList<String> getLGBTQPerc(){
        return lgbtqPerc;
    }

    public ArrayList<String> getCommuterPerc() {
        return commuterPerc;
    }
}