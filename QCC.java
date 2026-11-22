import java.util.ArrayList;
import java.util.Arrays;

public class QCC {
    private ArrayList<String> courses = CSVReader.ReadCSV("QCCCSCourses.csv");
    private int classSize = 17;
    private double campusSize = 0.23;
    private int totalStudents = 8715;
    private ArrayList<String> genderPerc = new ArrayList<>(Arrays.asList("Female: 62.2%", "Male: 37.8%"));
    private ArrayList<String> racePerc = new ArrayList<>(Arrays.asList("White: 41.0%", "Latinx: 25.2%", "Black: 16.5%", "Unknown: 6.2%", "Asian: 5.6%", "Multi-ethnic: 3.2%", "Other: 1.1%", "International: 0.6%", "American Indian / Alaska Native: 0.3%", "Native Hawiian / Pacific Islander: 0.2%"));
    private ArrayList<String> lgbtqPerc = new ArrayList<>(Arrays.asList("Not Listed"));
    private ArrayList<String> commuterPerc = new ArrayList<>(Arrays.asList("Commuter: 100%"));

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
