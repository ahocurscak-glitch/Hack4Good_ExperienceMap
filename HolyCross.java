import java.util.ArrayList;

public class HolyCross {
    private ArrayList<String> courses = CSVReader.ReadCSV("HolyCrossCSCourses.csv");
    private int classSize = 18;
    private double campusSize = 0.7;
    private int totalStudents = 3106;
    private ArrayList<Double> genderPerc;
    private ArrayList<Double> racePerc;
    private ArrayList<Double> lgbtqPerc;
    private ArrayList<Double> commuterPerc;

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

    public ArrayList<Double> getGenderPerc(){
        return genderPerc;
    }

    public ArrayList<Double> getRacePerc(){
        return racePerc;
    }

    public ArrayList<Double> getLGBTQPerc(){
        return lgbtqPerc;
    }

    public ArrayList<Double> getCommuterPerc() {
        return commuterPerc;
    }
}
