import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class InteractiveHoverMap extends JPanel {

    private final Map<String, Polygon> mapSections = new LinkedHashMap<>();
    private String hoveredSection = "None";

    public InteractiveHoverMap() {
        // 1. Define map sections using (x, y) polygon coordinates
        Polygon northRegion = new Polygon(
            new int[]{100, 300, 280, 120},
            new int[]{80,  80,  200, 180},
            4
        );

        Polygon southRegion = new Polygon(
            new int[]{120, 280, 320, 90},
            new int[]{180, 200, 320, 300},
            4
        );

        mapSections.put("Northern Territory", northRegion);
        mapSections.put("Southern Valley", southRegion);

        // 2. Track mouse movement to detect which polygon contains the cursor
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                String currentHover = "None";
                for (Map.Entry<String, Polygon> entry : mapSections.entrySet()) {
                    if (entry.getValue().contains(e.getPoint())) {
                        currentHover = entry.getKey();
                        break;
                    }
                }

                // Repaint only when the hovered section changes
                if (!currentHover.equals(hoveredSection)) {
                    hoveredSection = currentHover;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 3. Draw each map section, highlighting the hovered one
        for (Map.Entry<String, Polygon> entry : mapSections.entrySet()) {
            String name = entry.getKey();
            Polygon poly = entry.getValue();

            if (name.equals(hoveredSection)) {
                g2d.setColor(new Color(100, 180, 255)); // Highlight color
            } else {
                g2d.setColor(new Color(210, 220, 230)); // Default color
            }
            g2d.fillPolygon(poly);

            // Draw border
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawPolygon(poly);
        }

        // 4. Display hovered section info
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.drawString("Hovered Section: " + hoveredSection, 20, 30);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Interactive Java Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(450, 420);
            frame.add(new InteractiveHoverMap());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
