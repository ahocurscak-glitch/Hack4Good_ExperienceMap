import java.util.*;

public class WPI {
    static ArrayList<String> courses = CSVReader.ReadCSV("WPI_Courses.csv");
    
    public static void main(String[] args){
        for (String obj : courses){
            System.out.println(obj);
        }
    }
}