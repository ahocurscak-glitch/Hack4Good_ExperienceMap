import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interactive map of Worcester, Massachusetts.
 *
 * Hover over an area to highlight it, click it to see its details.
 *
 * Run with no arguments to use the built-in sample neighborhoods (rough shapes only):
 *     java WorcesterMap
 *
 * Or load real boundaries from a GeoJSON file (neighborhoods, wards, council districts...):
 *     java WorcesterMap worcester_neighborhoods.geojson
 *     java WorcesterMap worcester_wards.geojson WARD_NAME
 * The optional second argument is the property to use as each area's name.
 * Every property in the file is shown in the details panel.
 *
 * No external libraries needed: a small JSON reader is included at the bottom.
 */
public class WorcesterMap2 {

    // ------------------------------------------------------------------
    // Region: one clickable area of the map
    // ------------------------------------------------------------------
    static class Region {
        final String name;
        final List<double[][]> rings;           // each ring = array of {x, y} (lon, lat or projected)
        final Map<String, String> details;      // shown in the details panel
        Path2D.Double screenShape;              // rebuilt whenever the window size changes

        Region(String name, List<double[][]> rings, Map<String, String> details) {
            this.name = name;
            this.rings = rings;
            this.details = details;
        }
    }

    // ------------------------------------------------------------------
    // MapPanel: draws the regions and handles hover + click
    // ------------------------------------------------------------------
    static class MapPanel extends JPanel {
        private static final int PAD = 30;
        private static final int HEADER = 60;

        private final List<Region> regions;
        private final Consumer<Region> onSelect;
        private final String footerNote;

        private Region hovered = null;
        private Region selected = null;
        private int builtWidth = -1, builtHeight = -1;

        // Optional background image and the area it covers: {west, south, east, north}
        private Image backgroundImage = null;
        private double[] imageBounds = null;

        // Current map -> screen transform (set in ensureShapes)
        private double scale = 1, offX = 0, offY = 0;

        // Data bounds
        private double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        private double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        private final double xFactor;           // shrinks longitude so the map isn't stretched

        MapPanel(List<Region> regions, Consumer<Region> onSelect, String footerNote) {
            this.regions = regions;
            this.onSelect = onSelect;
            this.footerNote = footerNote;
            setPreferredSize(new Dimension(620, 620));
            setBackground(new Color(250, 251, 252));

            for (Region r : regions) {
                for (double[][] ring : r.rings) {
                    for (double[] p : ring) {
                        minX = Math.min(minX, p[0]); maxX = Math.max(maxX, p[0]);
                        minY = Math.min(minY, p[1]); maxY = Math.max(maxY, p[1]);
                    }
                }
            }

            // If the data looks like longitude/latitude, correct for the latitude.
            // (Projected data, e.g. Mass State Plane metres, is already to scale.)
            boolean geographic = minX >= -180 && maxX <= 180 && minY >= -90 && maxY <= 90;
            xFactor = geographic ? Math.cos(Math.toRadians((minY + maxY) / 2)) : 1.0;

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Region r = findRegionAt(e.getPoint());
                    setCursor(Cursor.getPredefinedCursor(r == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                    if (r != hovered) {
                        hovered = r;
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
                    selected = findRegionAt(e.getPoint()); // empty space clears the selection
                    onSelect.accept(selected);
                    repaint();
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        /**
         * Sets a picture to draw underneath the areas.
         * bounds = {west, south, east, north} in the same coordinates as the map data
         * (longitude/latitude for most GeoJSON files). If null, the picture is stretched
         * over the whole area covered by the map data.
         */
        void setBackgroundImage(Image image, double[] bounds) {
            this.backgroundImage = image;
            this.imageBounds = bounds != null ? bounds : new double[]{minX, minY, maxX, maxY};
            repaint();
        }

        private double toScreenX(double x) { return offX + (x - minX) * xFactor * scale; }
        private double toScreenY(double y) { return offY + (maxY - y) * scale; }

        private Region findRegionAt(Point p) {
            ensureShapes();
            // Check in reverse draw order so the top-most region wins
            for (int i = regions.size() - 1; i >= 0; i--) {
                Region r = regions.get(i);
                if (r.screenShape != null && r.screenShape.contains(p)) return r;
            }
            return null;
        }

        /** Converts map coordinates to screen pixels, fitted to the current panel size. */
        private void ensureShapes() {
            int w = getWidth(), h = getHeight();
            if (w == builtWidth && h == builtHeight) return;
            builtWidth = w;
            builtHeight = h;

            double dataW = Math.max((maxX - minX) * xFactor, 1e-9);
            double dataH = Math.max(maxY - minY, 1e-9);
            double availW = Math.max(w - 2 * PAD, 1);
            double availH = Math.max(h - HEADER - PAD, 1);
            scale = Math.min(availW / dataW, availH / dataH);   // same scale both ways

            offX = PAD + (availW - dataW * scale) / 2;
            offY = HEADER + (availH - dataH * scale) / 2;

            for (Region r : regions) {
                Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD); // even-odd handles holes
                for (double[][] ring : r.rings) {
                    for (int i = 0; i < ring.length; i++) {
                        double sx = toScreenX(ring[i][0]);
                        double sy = toScreenY(ring[i][1]);                       // screen y goes down
                        if (i == 0) path.moveTo(sx, sy); else path.lineTo(sx, sy);
                    }
                    path.closePath();
                }
                r.screenShape = path;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            ensureShapes();
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Background image, placed at its map coordinates
            boolean hasImage = backgroundImage != null;
            if (hasImage) {
                int x1 = (int) Math.round(toScreenX(imageBounds[0]));
                int y1 = (int) Math.round(toScreenY(imageBounds[3]));
                int x2 = (int) Math.round(toScreenX(imageBounds[2]));
                int y2 = (int) Math.round(toScreenY(imageBounds[1]));
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(backgroundImage, x1, y1, x2 - x1, y2 - y1, null);
            }

            // Regions: selected > hovered > default (see-through when there's an image underneath)
            for (Region r : regions) {
                if (r == selected)      g2d.setColor(new Color(40, 120, 220, hasImage ? 150 : 255));
                else if (r == hovered)  g2d.setColor(new Color(100, 180, 255, hasImage ? 120 : 255));
                else                    g2d.setColor(new Color(210, 220, 230, hasImage ? 40 : 255));
                g2d.fill(r.screenShape);

                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(r == selected ? 2.5f : 1.2f));
                g2d.draw(r.screenShape);
            }

            // Labels at the centre of each region
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            FontMetrics fm = g2d.getFontMetrics();
            for (Region r : regions) {
                Rectangle2D b = r.screenShape.getBounds2D();
                int tw = fm.stringWidth(r.name);
                if (tw > b.getWidth() + 20) continue;   // skip labels that clearly won't fit
                int lx = (int) (b.getCenterX() - tw / 2.0);
                int ly = (int) (b.getCenterY() + fm.getAscent() / 2.0);
                if (hasImage) {
                    // Light outline so labels stay readable on top of the picture
                    g2d.setColor(new Color(255, 255, 255, 220));
                    for (int dx = -1; dx <= 1; dx++)
                        for (int dy = -1; dy <= 1; dy++)
                            if (dx != 0 || dy != 0) g2d.drawString(r.name, lx + dx, ly + dy);
                    g2d.setColor(new Color(20, 20, 20));
                } else {
                    g2d.setColor(r == selected ? Color.WHITE : new Color(40, 40, 40));
                }
                g2d.drawString(r.name, lx, ly);
            }

            // Header
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2d.drawString("Worcester, MA  —  Hovered: " + (hovered == null ? "None" : hovered.name), 20, 28);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2d.setColor(Color.GRAY);
            g2d.drawString("Click an area to see its details", 20, 46);

            if (footerNote != null) {
                g2d.setFont(new Font("SansSerif", Font.ITALIC, 11));
                g2d.drawString(footerNote, 20, getHeight() - 10);
            }
            g2d.dispose();
        }
    }

    // ------------------------------------------------------------------
    // DetailsPanel: shows information about the clicked region
    // ------------------------------------------------------------------
    static class DetailsPanel extends JPanel {
        private final JLabel title = new JLabel();
        private final JTextArea body = new JTextArea();

        DetailsPanel() {
            setLayout(new BorderLayout(0, 12));
            setPreferredSize(new Dimension(280, 620));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            setBackground(new Color(240, 243, 247));

            title.setFont(new Font("SansSerif", Font.BOLD, 18));

            body.setLineWrap(true);
            body.setWrapStyleWord(true);
            body.setEditable(false);
            body.setOpaque(false);
            body.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JScrollPane scroll = new JScrollPane(body);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);

            add(title, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
            showRegion(null);
        }

        void showRegion(Region region) {
            if (region == null) {
                title.setText("No area selected");
                body.setText("Click on an area of the map to view more details.");
                return;
            }
            title.setText(region.name);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> e : region.details.entrySet()) {
                sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n\n");
            }
            body.setText(sb.length() == 0 ? "No extra details for this area." : sb.toString().trim());
            body.setCaretPosition(0);
        }
    }

    // ------------------------------------------------------------------
    // Built-in sample data: ROUGH neighborhood shapes, not real boundaries
    // ------------------------------------------------------------------
    static List<Region> sampleRegions() {
        List<Region> list = new ArrayList<>();
        // Coordinates are {longitude, latitude}
        list.add(sample("Greendale", "Northern neighborhood along West Boylston Street.",
                -71.828, 42.290,  -71.780, 42.290,  -71.776, 42.312,  -71.795, 42.330,  -71.830, 42.322));
        list.add(sample("Burncoat", "North-central residential neighborhood around Burncoat Street.",
                -71.812, 42.270,  -71.776, 42.272,  -71.780, 42.290,  -71.812, 42.290));
        list.add(sample("Tatnuck", "West side neighborhood around Tatnuck Square, toward Worcester Regional Airport.",
                -71.872, 42.255,  -71.835, 42.255,  -71.835, 42.300,  -71.860, 42.305,  -71.878, 42.280));
        list.add(sample("West Side", "Home to Worcester Polytechnic Institute (WPI) and Elm Park.",
                -71.835, 42.255,  -71.812, 42.255,  -71.812, 42.290,  -71.828, 42.290,  -71.835, 42.280));
        list.add(sample("Downtown", "City centre: City Hall, Worcester Common, the DCU Center and Union Station.",
                -71.812, 42.255,  -71.792, 42.255,  -71.790, 42.272,  -71.812, 42.270));
        list.add(sample("East Side", "Includes the Shrewsbury Street restaurant strip; Lake Quinsigamond forms the city's eastern edge.",
                -71.792, 42.255,  -71.758, 42.252,  -71.752, 42.268,  -71.776, 42.272,  -71.790, 42.272));
        list.add(sample("Main South", "Neighborhood around Main Street south of downtown, home to Clark University.",
                -71.828, 42.235,  -71.802, 42.235,  -71.800, 42.255,  -71.835, 42.255));
        list.add(sample("College Hill", "Home to the College of the Holy Cross.",
                -71.802, 42.228,  -71.775, 42.230,  -71.772, 42.250,  -71.792, 42.255,  -71.800, 42.255));
        return list;
    }

    private static Region sample(String name, String about, double... lonLat) {
        double[][] ring = new double[lonLat.length / 2][];
        for (int i = 0; i < ring.length; i++) ring[i] = new double[]{lonLat[2 * i], lonLat[2 * i + 1]};
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Type", "Neighborhood (approximate sample shape)");
        details.put("About", about);
        return new Region(name, Collections.singletonList(ring), details);
    }

    // ------------------------------------------------------------------
    // GeoJSON loading (Polygon and MultiPolygon features)
    // ------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    static List<Region> loadGeoJson(Path file, String nameKey) throws IOException {
        Object root = MiniJson.parse(new String(Files.readAllBytes(file), "UTF-8"));
        if (!(root instanceof Map)) throw new IOException("Not a GeoJSON object");
        Map<String, Object> obj = (Map<String, Object>) root;

        List<Object> features;
        if ("FeatureCollection".equals(obj.get("type"))) features = (List<Object>) obj.get("features");
        else if ("Feature".equals(obj.get("type")))      features = Collections.singletonList(obj);
        else throw new IOException("Expected a FeatureCollection or Feature");

        List<Region> regions = new ArrayList<>();
        int count = 0;
        for (Object fo : features) {
            count++;
            Map<String, Object> feature = (Map<String, Object>) fo;
            Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
            Map<String, Object> props = feature.get("properties") instanceof Map
                    ? (Map<String, Object>) feature.get("properties") : Collections.emptyMap();
            if (geometry == null) continue;

            List<double[][]> rings = new ArrayList<>();
            Object coords = geometry.get("coordinates");
            String type = String.valueOf(geometry.get("type"));
            if ("Polygon".equals(type)) {
                addPolygon((List<Object>) coords, rings);
            } else if ("MultiPolygon".equals(type)) {
                for (Object poly : (List<Object>) coords) addPolygon((List<Object>) poly, rings);
            } else {
                continue; // skip points and lines
            }
            if (rings.isEmpty()) continue;

            Map<String, String> details = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : props.entrySet()) {
                if (e.getValue() != null) details.put(e.getKey(), formatValue(e.getValue()));
            }
            regions.add(new Region(pickName(props, nameKey, count), rings, details));
        }
        if (regions.isEmpty()) throw new IOException("No polygon areas found in " + file);
        return regions;
    }

    @SuppressWarnings("unchecked")
    private static void addPolygon(List<Object> polygon, List<double[][]> rings) {
        for (Object ringObj : polygon) {
            List<Object> pts = (List<Object>) ringObj;
            double[][] ring = new double[pts.size()][];
            for (int i = 0; i < pts.size(); i++) {
                List<Object> p = (List<Object>) pts.get(i);
                ring[i] = new double[]{((Number) p.get(0)).doubleValue(), ((Number) p.get(1)).doubleValue()};
            }
            if (ring.length >= 3) rings.add(ring);
        }
    }

    /** Uses the requested property, or guesses a sensible name property. */
    private static String pickName(Map<String, Object> props, String nameKey, int index) {
        if (nameKey != null && props.get(nameKey) != null) return formatValue(props.get(nameKey));
        for (String key : props.keySet()) {
            if (key.toLowerCase().contains("name") && props.get(key) != null) return formatValue(props.get(key));
        }
        for (String key : props.keySet()) {
            String k = key.toLowerCase();
            if ((k.contains("neighborhood") || k.contains("nbhd") || k.contains("district") || k.contains("ward"))
                    && props.get(key) != null) {
                return (k.contains("ward") ? "Ward " : k.contains("district") ? "District " : "") + formatValue(props.get(key));
            }
        }
        return "Area " + index;
    }

    private static String formatValue(Object v) {
        if (v instanceof Double) {
            double d = (Double) v;
            if (d == Math.rint(d) && Math.abs(d) < 1e15) return String.valueOf((long) d);
        }
        return String.valueOf(v);
    }

    // ------------------------------------------------------------------
    // Minimal JSON reader (objects, arrays, strings, numbers, true/false/null)
    // ------------------------------------------------------------------
    static class MiniJson {
        private final String s;
        private int i = 0;

        private MiniJson(String s) { this.s = s; }

        static Object parse(String text) throws IOException {
            MiniJson p = new MiniJson(text);
            Object v = p.value();
            p.skipWs();
            if (p.i != p.s.length()) throw p.error("Unexpected trailing content");
            return v;
        }

        private Object value() throws IOException {
            skipWs();
            if (i >= s.length()) throw error("Unexpected end of file");
            char c = s.charAt(i);
            switch (c) {
                case '{': return object();
                case '[': return array();
                case '"': return string();
                case 't': expect("true");  return Boolean.TRUE;
                case 'f': expect("false"); return Boolean.FALSE;
                case 'n': expect("null");  return null;
                default:  return number();
            }
        }

        private Map<String, Object> object() throws IOException {
            Map<String, Object> map = new LinkedHashMap<>();
            i++; // {
            skipWs();
            if (peek() == '}') { i++; return map; }
            while (true) {
                skipWs();
                String key = string();
                skipWs();
                if (peek() != ':') throw error("Expected ':'");
                i++;
                map.put(key, value());
                skipWs();
                char c = peek();
                i++;
                if (c == '}') return map;
                if (c != ',') throw error("Expected ',' or '}'");
            }
        }

        private List<Object> array() throws IOException {
            List<Object> list = new ArrayList<>();
            i++; // [
            skipWs();
            if (peek() == ']') { i++; return list; }
            while (true) {
                list.add(value());
                skipWs();
                char c = peek();
                i++;
                if (c == ']') return list;
                if (c != ',') throw error("Expected ',' or ']'");
            }
        }

        private String string() throws IOException {
            if (peek() != '"') throw error("Expected string");
            i++;
            StringBuilder sb = new StringBuilder();
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'u': sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; break;
                        default:  sb.append(e); // \" \\ \/
                    }
                } else {
                    sb.append(c);
                }
            }
            throw error("Unterminated string");
        }

        private Double number() throws IOException {
            int start = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
            if (start == i) throw error("Unexpected character '" + s.charAt(i) + "'");
            return Double.parseDouble(s.substring(start, i));
        }

        private void expect(String word) throws IOException {
            if (!s.startsWith(word, i)) throw error("Expected " + word);
            i += word.length();
        }

        private char peek() throws IOException {
            if (i >= s.length()) throw error("Unexpected end of file");
            return s.charAt(i);
        }

        private void skipWs() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        }

        private IOException error(String msg) {
            return new IOException("JSON error at position " + i + ": " + msg);
        }
    }

    // ------------------------------------------------------------------
    // Program entry point
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Region> regions;
            String note;
            if (args.length > 0) {
                try {
                    regions = loadGeoJson(Paths.get(args[0]), args.length > 1 ? args[1] : null);
                    note = "Data: " + Paths.get(args[0]).getFileName();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Couldn't load " + args[0] + ":\n" + ex.getMessage() + "\n\nShowing sample data instead.",
                            "Load error", JOptionPane.WARNING_MESSAGE);
                    regions = sampleRegions();
                    note = "Sample shapes are approximate — load a GeoJSON file for real boundaries";
                }
            } else {
                regions = sampleRegions();
                note = "Sample shapes are approximate — load a GeoJSON file for real boundaries";
            }

            JFrame frame = new JFrame("Interactive Map of Worcester, MA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DetailsPanel details = new DetailsPanel();
            MapPanel map = new MapPanel(regions, details::showRegion, note);

            frame.setLayout(new BorderLayout());
            frame.add(map, BorderLayout.CENTER);
            frame.add(details, BorderLayout.EAST);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}