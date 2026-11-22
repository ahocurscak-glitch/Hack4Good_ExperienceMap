import javax.swing.*;
import java.awt.*;

public class Dropdown {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JComboBox Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        String[] options = {"Option 1", "Option 2", "Option 3"};
        JComboBox<String> dropdown = new JComboBox<>(options);
        JLabel label = new JLabel("Selected: Option 1");

        // Listen for selection changes
        dropdown.addActionListener(e -> {
            String selected = (String) dropdown.getSelectedItem();
            label.setText("Selected: " + selected);
        });

        frame.add(dropdown);
        frame.add(label);
        frame.setVisible(true);
    }
}
