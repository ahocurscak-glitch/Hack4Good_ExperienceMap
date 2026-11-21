import java.util.*;
public class AU{
    static ArrayList<String> courses = CSVReader.ReadCSV("AU CS courses.csv");
    private int averageClassSize = 18;
    private double airQuality;
    private double campusSize = 0.75; // in square kilometers
    private int totalStudents = 2038;
    private ArrayList<Double> genderPerc;
    private ArrayList<Double> racePerc;
    private ArrayList<Double> lgbtqPerc;
    
    public static void main(String[] args){
        for (String obj : courses){
            System.out.println(obj);
        }
    }
}