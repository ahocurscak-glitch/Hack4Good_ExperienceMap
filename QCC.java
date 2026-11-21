import java.util.ArrayList;

public class QCC {
    private ArrayList<String> courses = CSVReader.ReadCSV("QCCCSCourses.csv");
    private int classSize = 17;
    private double campusSize = 0.23;
    private int totalStudents = 8715;
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
