import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interactive Worcester colleges campus map.
 *
 * Hover over a colored campus (or its label) to highlight it, click it to open a
 * scrollable page with that school's details. The details come from each school's
 * Java class (AU, Clark, HolyCross, QCC, WPI, WSU, ...). Schools without a class
 * show a "no data yet" page.
 *
 * Compile and run from this folder (so the CSV files and the PNG are found):
 *     javac *.java
 *     java CampusMap
 */
public class CampusMap {

    static final String IMAGE_FILE = "Worcester Colleges Campus Map.png";

    // Shapes below were traced on the image at this size; they are scaled to the real image size.
    static final double TRACE_W = 1536, TRACE_H = 1024;

    // ------------------------------------------------------------------
    // Campus: one clickable school on the map
    // ------------------------------------------------------------------
    static class Campus {
        final String name;
        final String className;     // Java class holding this school's data (null if none)
        final Color color;
        final Path2D.Double shape;  // in traced-image coordinates (campus outline + label box)

        Campus(String name, String className, Color color, double[] outline, double[] labelBox) {
            this.name = name;
            this.className = className;
            this.color = color;
            this.shape = new Path2D.Double(Path2D.WIND_NON_ZERO);
            for (int i = 0; i < outline.length; i += 2) {
                if (i == 0) shape.moveTo(outline[i], outline[i + 1]);
                else shape.lineTo(outline[i], outline[i + 1]);
            }
            shape.closePath();
            // label box = {x1, y1, x2, y2}
            shape.append(new Rectangle.Double(labelBox[0], labelBox[1],
                    labelBox[2] - labelBox[0], labelBox[3] - labelBox[1]), false);
        }
    }

    static List<Campus> campuses() {
        List<Campus> list = new ArrayList<>();
        list.add(new Campus("Quinsigamond Community College (QCC)", "QCC", new Color(0, 180, 195),
                new double[]{835, 85, 905, 55, 945, 60, 945, 145, 930, 190, 845, 165},
                new double[]{940, 60, 1157, 147}));
        list.add(new Campus("Assumption University", "AU", new Color(40, 100, 230),
                new double[]{430, 250, 470, 215, 520, 195, 545, 215, 548, 295, 520, 300, 465, 315, 440, 300},
                new double[]{535, 205, 684, 267}));
        list.add(new Campus("Worcester Polytechnic Institute (WPI)", "WPI", new Color(90, 170, 40),
                new double[]{825, 355, 870, 345, 915, 335, 925, 350, 940, 395, 925, 445, 870, 435, 835, 420, 822, 385},
                new double[]{925, 342, 1160, 402}));
        list.add(new Campus("Worcester State University", "WSU", new Color(240, 150, 20),
                new double[]{295, 420, 340, 405, 390, 402, 410, 440, 400, 510, 355, 500, 285, 480},
                new double[]{392, 404, 581, 465}));
        list.add(new Campus("Clark University", "Clark", new Color(220, 50, 50),
                new double[]{365, 630, 430, 600, 460, 610, 470, 650, 500, 690, 490, 700, 410, 718, 375, 700},
                new double[]{462, 615, 652, 666}));
        list.add(new Campus("College of the Holy Cross", "HolyCross", new Color(140, 60, 220),
                new double[]{670, 715, 720, 690, 800, 675, 810, 690, 820, 740, 845, 795, 750, 858, 720, 800, 690, 745},
                new double[]{806, 713, 965, 775}));
        list.add(new Campus("MCPHS University", "MCPHS", new Color(220, 40, 130),
                new double[]{1090, 600, 1110, 560, 1150, 545, 1190, 560, 1180, 620, 1160, 655, 1110, 625},
                new double[]{1175, 558, 1466, 638}));
        list.add(new Campus("Nichols College", "Nichols", new Color(30, 160, 110),
                new double[]{1290, 885, 1340, 850, 1375, 850, 1425, 915, 1400, 945, 1355, 965, 1315, 930},
                new double[]{1365, 850, 1524, 913}));
        return list;
    }

    // ------------------------------------------------------------------
    // MapPanel: draws the picture and handles hover + click
    // ------------------------------------------------------------------
    static class MapPanel extends JPanel {
        private static final int HEADER = 56;

        private final BufferedImage image;
        private final List<Campus> campuses;
        private final Consumer<Campus> onClick;
        private Campus hovered = null;

        // traced-image coordinates -> screen
        private double scale = 1, offX = 0, offY = HEADER;

        MapPanel(BufferedImage image, List<Campus> campuses, Consumer<Campus> onClick) {
            this.image = image;
            this.campuses = campuses;
            this.onClick = onClick;
            setPreferredSize(new Dimension(1100, 790));
            setBackground(new Color(250, 251, 252));

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Campus c = campusAt(e.getPoint());
                    setCursor(Cursor.getPredefinedCursor(c == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                    if (c != hovered) {
                        hovered = c;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = null;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    Campus c = campusAt(e.getPoint());
                    if (c != null) onClick.accept(c);
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        private void updateTransform() {
            double availW = getWidth(), availH = getHeight() - HEADER;
            scale = Math.min(availW / TRACE_W, availH / TRACE_H);
            offX = (availW - TRACE_W * scale) / 2;
            offY = HEADER + (availH - TRACE_H * scale) / 2;
        }

        private Campus campusAt(Point p) {
            updateTransform();
            double x = (p.x - offX) / scale, y = (p.y - offY) / scale;
            for (Campus c : campuses) if (c.shape.contains(x, y)) return c;
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            updateTransform();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.drawImage(image, (int) offX, (int) offY,
                    (int) Math.round(TRACE_W * scale), (int) Math.round(TRACE_H * scale), null);

            if (hovered != null) {
                AffineTransform at = new AffineTransform(scale, 0, 0, scale, offX, offY);
                Shape s = at.createTransformedShape(hovered.shape);
                Color c = hovered.color;
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
                g2.fill(s);
                g2.setColor(c.darker());
                g2.setStroke(new BasicStroke(3f));
                g2.draw(s);
            }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Worcester Colleges  —  " + (hovered == null ? "Hover over a campus" : hovered.name), 20, 26);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(Color.GRAY);
            g2.drawString("Click a colored campus (or its label) to see details", 20, 44);
            g2.dispose();
        }
    }

    // ------------------------------------------------------------------
    // DetailsPage: scrollable page about one school
    // ------------------------------------------------------------------
    static class DetailsPage extends JPanel {
        private final JLabel title = new JLabel();
        private final JPanel header = new JPanel(new BorderLayout(12, 0));
        private final JEditorPane body = new JEditorPane("text/html", "");
        private final JScrollPane scroll;

        DetailsPage(Runnable onBack) {
            setLayout(new BorderLayout());

            JButton back = new JButton("← Back to map");
            back.addActionListener(e -> onBack.run());
            back.setFocusPainted(false);

            title.setFont(new Font("SansSerif", Font.BOLD, 22));
            title.setForeground(Color.WHITE);

            header.setBorder(new EmptyBorder(14, 16, 14, 16));
            header.add(back, BorderLayout.WEST);
            header.add(title, BorderLayout.CENTER);

            body.setEditable(false);
            body.setBorder(new EmptyBorder(12, 24, 24, 24));
            scroll = new JScrollPane(body);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            add(header, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }

        void show(Campus campus) {
            header.setBackground(campus.color.darker());
            title.setText(campus.name);
            body.setText(UniversityData.toHtml(campus));
            SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(0));
            body.setCaretPosition(0);
        }
    }

    // ------------------------------------------------------------------
    // UniversityData: reads each school's Java class with reflection, so it works
    // even though the classes name their getters slightly differently
    // (getLgbtqPerc vs getLGBTQPerc, getAirQuality only on some, ...).
    // ------------------------------------------------------------------
    static class UniversityData {
        // Friendly labels for known getters (keys are lower-case, without "get")
        private static final Map<String, String> LABELS = new LinkedHashMap<>();
        static {
            LABELS.put("totalstudents", "Total Students");
            LABELS.put("classsize", "Average Class Size");
            LABELS.put("campussize", "Campus Size");
            LABELS.put("airquality", "Air Quality");
            LABELS.put("genderperc", "Gender Breakdown (%)");
            LABELS.put("raceperc", "Race / Ethnicity Breakdown (%)");
            LABELS.put("lgbtqperc", "LGBTQ+ Students (%)");
            LABELS.put("courses", "Computer Science Courses");
        }

        static String toHtml(Campus campus) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='font-family:SansSerif; font-size:13pt; color:#222'>");

            Object data = load(campus.className);
            if (data == null) {
                sb.append("<h2>No information yet</h2>")
                  .append("<p>There is no data file for this school yet. Add a <b>")
                  .append(esc(campus.className == null ? "(class)" : campus.className))
                  .append(".java</b> class with getters like <i>getTotalStudents()</i> and ")
                  .append("<i>getCourses()</i> and it will show up here.</p>");
                return sb.append("</body></html>").toString();
            }

            // Collect public no-argument getters
            Map<String, Method> getters = new TreeMap<>();
            for (Method m : data.getClass().getMethods()) {
                if (m.getDeclaringClass() == Object.class || m.getParameterCount() != 0) continue;
                if (Modifier.isStatic(m.getModifiers()) || !m.getName().startsWith("get")) continue;
                getters.put(m.getName().substring(3).toLowerCase(), m);
            }

            // Known fields first, in a sensible order, then anything else
            List<String> order = new ArrayList<>();
            for (String k : LABELS.keySet()) if (getters.containsKey(k)) order.add(k);
            for (String k : getters.keySet()) if (!order.contains(k)) order.add(k);

            for (String key : order) {
                Method m = getters.get(key);
                Object value;
                try {
                    value = m.invoke(data);
                } catch (Exception ex) {
                    value = null;
                }
                String label = LABELS.getOrDefault(key, splitCamel(m.getName().substring(3)));
                sb.append("<h3 style='margin-bottom:4px'>").append(esc(label)).append("</h3>");
                sb.append(formatValue(key, value));
            }
            return sb.append("</body></html>").toString();
        }

        /** Creates an instance of the school's class, or null if it doesn't exist. */
        private static Object load(String className) {
            if (className == null) return null;
            try {
                return Class.forName(className).getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                return null;
            } catch (Exception e) {
                System.err.println("Couldn't load " + className + ": " + e);
                return null;
            }
        }

        private static String formatValue(String key, Object value) {
            String na = "<p style='color:#888; margin-top:0'><i>Not available yet</i></p>";
            if (value == null) return na;
            if (value instanceof Collection) {
                Collection<?> c = (Collection<?>) value;
                List<String> items = new ArrayList<>();
                for (Object o : c) if (o != null && !o.toString().isBlank()) items.add(o.toString().trim());
                if (items.isEmpty()) return "<p style='color:#888; margin-top:0'><i>None listed yet</i></p>";
                StringBuilder sb = new StringBuilder();
                if (key.equals("courses")) sb.append("<p style='margin:0; color:#555'>").append(items.size()).append(" courses</p>");
                sb.append("<ul style='margin-top:4px'>");
                for (String s : items) sb.append("<li>").append(esc(s)).append("</li>");
                return sb.append("</ul>").toString();
            }
            if (value instanceof Number) {
                double d = ((Number) value).doubleValue();
                if (d == 0) return na;  // unset primitive fields default to 0
                String text = d == Math.rint(d) ? String.format("%,d", (long) d) : String.valueOf(d);
                if (key.equals("campussize")) text += " km²";
                return "<p style='margin-top:0; font-size:15pt'><b>" + text + "</b></p>";
            }
            return "<p style='margin-top:0'>" + esc(value.toString()) + "</p>";
        }

        private static String splitCamel(String s) {
            return s.replaceAll("([a-z])([A-Z])", "$1 $2");
        }

        private static String esc(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }

    // ------------------------------------------------------------------
    // Program entry point
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BufferedImage image;
            try {
                image = ImageIO.read(new File(IMAGE_FILE));
                if (image == null) throw new Exception("not a readable image");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Couldn't load \"" + IMAGE_FILE + "\": " + ex.getMessage()
                                + "\nRun this program from the folder that contains the image.",
                        "Load error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFrame frame = new JFrame("Worcester Colleges Campus Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CardLayout cards = new CardLayout();
            JPanel root = new JPanel(cards);

            DetailsPage details = new DetailsPage(() -> cards.show(root, "map"));
            MapPanel map = new MapPanel(image, campuses(), campus -> {
                details.show(campus);
                cards.show(root, "details");
            });

            root.add(map, "map");
            root.add(details, "details");
            frame.setContentPane(root);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
