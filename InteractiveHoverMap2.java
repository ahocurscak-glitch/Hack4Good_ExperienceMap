import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class InteractiveHoverMap2 extends JPanel {

    // Holds everything we know about a territory
    static class Region {
        final String name;
        final Polygon shape;
        final String capital;
        final String population;
        final String area;
        final String description;

        Region(String name, Polygon shape, String capital,
               String population, String area, String description) {
            this.name = name;
            this.shape = shape;
            this.capital = capital;
            this.population = population;
            this.area = area;
            this.description = description;
        }
    }

    private final Map<String, Region> mapSections = new LinkedHashMap<>();
    private String hoveredSection = "None";
    private String selectedSection = null;

    private final DetailsPanel detailsPanel;

    public InteractiveHoverMap2(DetailsPanel detailsPanel) {
        this.detailsPanel = detailsPanel;
        setPreferredSize(new Dimension(420, 380));
        setBackground(Color.WHITE);

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

        addRegion(new Region("Northern Territory", northRegion,
                "Frostport", "1.2 million", "48,000 km²",
                "A rugged highland region known for its mountain passes, "
                + "pine forests and cold winters. Mining and timber drive the economy."));

        addRegion(new Region("Southern Valley", southRegion,
                "Riverbend", "3.4 million", "62,500 km²",
                "Fertile lowlands along the great river. The valley is the "
                + "agricultural heart of the land, famous for its vineyards and orchards."));

        // 2. Mouse handling: hover + click
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                String currentHover = findRegionAt(e.getPoint());
                if (currentHover == null) currentHover = "None";

                // Hand cursor over a clickable region
                setCursor(Cursor.getPredefinedCursor(
                        currentHover.equals("None") ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));

                // Repaint only when the hovered section changes
                if (!currentHover.equals(hoveredSection)) {
                    hoveredSection = currentHover;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredSection = "None";
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                String clicked = findRegionAt(e.getPoint());
                selectedSection = clicked; // clicking empty space clears selection
                detailsPanel.showRegion(clicked == null ? null : mapSections.get(clicked));
                repaint();
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private void addRegion(Region region) {
        mapSections.put(region.name, region);
    }

    // Returns the name of the region under the point, or null
    private String findRegionAt(Point p) {
        for (Region region : mapSections.values()) {
            if (region.shape.contains(p)) {
                return region.name;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 3. Draw each map section: selected > hovered > default
        for (Region region : mapSections.values()) {
            if (region.name.equals(selectedSection)) {
                g2d.setColor(new Color(40, 120, 220));   // Selected color
            } else if (region.name.equals(hoveredSection)) {
                g2d.setColor(new Color(100, 180, 255));  // Hover color
            } else {
                g2d.setColor(new Color(210, 220, 230));  // Default color
            }
            g2d.fillPolygon(region.shape);

            // Thicker border on the selected region
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(region.name.equals(selectedSection) ? 3 : 2));
            g2d.drawPolygon(region.shape);
        }

        // 4. Display hovered section info
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.drawString("Hovered Section: " + hoveredSection, 20, 30);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.setColor(Color.GRAY);
        g2d.drawString("Click a territory to see its details", 20, 50);
    }

    // Side panel that shows information about the clicked region
    static class DetailsPanel extends JPanel {
        private final JLabel title = new JLabel();
        private final JLabel capital = new JLabel();
        private final JLabel population = new JLabel();
        private final JLabel area = new JLabel();
        private final JTextArea description = new JTextArea();

        DetailsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(240, 380));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            setBackground(new Color(245, 247, 250));

            title.setFont(new Font("SansSerif", Font.BOLD, 18));

            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            description.setEditable(false);
            description.setOpaque(false);
            description.setFont(new Font("SansSerif", Font.PLAIN, 13));
            description.setAlignmentX(LEFT_ALIGNMENT);

            add(title);
            add(Box.createVerticalStrut(12));
            add(capital);
            add(Box.createVerticalStrut(4));
            add(population);
            add(Box.createVerticalStrut(4));
            add(area);
            add(Box.createVerticalStrut(12));
            add(description);

            showRegion(null);
        }

        void showRegion(Region region) {
            if (region == null) {
                title.setText("No territory selected");
                capital.setText(" ");
                population.setText(" ");
                area.setText(" ");
                description.setText("Click on a territory on the map to view more details.");
            } else {
                title.setText(region.name);
                capital.setText("Capital: " + region.capital);
                population.setText("Population: " + region.population);
                area.setText("Area: " + region.area);
                description.setText(region.description);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Interactive Java Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DetailsPanel details = new DetailsPanel();
            InteractiveHoverMap2 map = new InteractiveHoverMap2(details);

            frame.setLayout(new BorderLayout());
            frame.add(map, BorderLayout.CENTER);
            frame.add(details, BorderLayout.EAST);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}