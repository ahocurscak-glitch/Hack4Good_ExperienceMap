import java.util.ArrayList;
import java.util.Arrays;

public class HolyCross {
    private ArrayList<String> courses = CSVReader.ReadCSV("HolyCrossCSCourses.csv");
    private int classSize = 18;
    private double campusSize = 0.7;
    private int totalStudents = 3106;
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 56%", "Male: 44%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 71%", "Latinx: 14%", "Black: 5%", "Multi-ethnic: 4%", "International: 3%", "Asian: 3%", "American Indian / Alaska Native & Native Hawaiian / Pacific Islander: <1%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Resident: 87%", "Commuter: 13%"));

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
