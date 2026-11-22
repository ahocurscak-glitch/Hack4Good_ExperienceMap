import java.io.*;
import java.util.*;

public class CSVReader {
    public static ArrayList<String> ReadCSV(String filePath) {
        ArrayList<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String toList(ArrayList<String> list) {
        String result = "";
        for (String item : list) {
            result += item + "\n";
        }
        return result;
    }
}