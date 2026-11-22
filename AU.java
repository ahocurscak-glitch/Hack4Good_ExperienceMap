import java.util.*;
public class AU{
    private LinkedHashMap <String, ArrayList<String>> majors = new LinkedHashMap<>();
    private int averageClassSize = 18;
    private double campusSize = 0.75; // in square kilometers
    private int totalStudents = 2038;
    private ArrayList<String> CoA = new ArrayList<>(Arrays.asList("Resident: $75,850", "Commuter: $58,800"));
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 55.7%", "Male: 44.3%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 72.0%", "Latinx: 11.4%", "Black: 6.0%", "Multi-ethnic: 3.6%", "International: 2.7%", "Asian: 2.7%", "Unknown: 1.6%", "Native Hawiian / Pacific Islander: 0.1%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Resident: 83%", "Commuter: 17%"));
    
   public AU(){
        majors.put("Computer Science", CSVReader.ReadCSV("AU CS Courses.csv"));
        majors.put("Business", CSVReader.ReadCSV("AU Business Courses.csv"));

    }

    public Map<String, ArrayList<String>> getMajors() {
        return majors;
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