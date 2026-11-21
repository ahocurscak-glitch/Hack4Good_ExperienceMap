import java.util.*;
public class WSU{
    static ArrayList<String> courses = CSVReader.ReadCSV("WSU CS courses.csv");
    private int classSize = 21;
    private double airQuality;
    private double campusSize = 0.235; // in square kilometers
    private int totalStudents = 5273;
    private ArrayList<Double> genderPerc;
    private ArrayList<Double> racePerc;
    private ArrayList<Double> lgbtqPerc;
    
    public static void main(String[] args){
        for (String obj : courses){
            System.out.println(obj);
        }
    }
}
