
import java.awt.*;
import javax.swing.*;

public class TuitionChart extends JPanel {

    private final String[] colleges = {
        "Worcester State", "Clark", "WPI",
        "MCPHS", "Holy Cross", "Assumption"
    };

    // Temporary sample values — replace with your verified tuition data.
    private final double[] tuition = {
        10000, 55000, 60000, 39000, 62000, 49000
    };

    public TuitionChart() {
        setPreferredSize(new Dimension(800, 500));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString("Annual Commuter Tuition", 280, 40);

        int chartBottom = 400;
        int chartTop = 100;
        int chartHeight = chartBottom - chartTop;
        int barWidth = 65;
        int gap = 45;
        int startX = 70;

        double maxTuition = 70000;

        g2.setColor(Color.BLACK);
        g2.drawLine(50, chartTop, 50, chartBottom);
        g2.drawLine(50, chartBottom, 760, chartBottom);

        for (int i = 0; i < colleges.length; i++) {
            int barHeight = (int) (
                (tuition[i] / maxTuition) * chartHeight
            );

            int x = startX + i * (barWidth + gap);
            int y = chartBottom - barHeight;

            g2.setColor(new Color(70, 130, 180));
            g2.fillRect(x, y, barWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));

            g2.drawString(
                "$" + String.format("%,.0f", tuition[i]),
                x - 5, y - 8
            );

            g2.drawString(
                colleges[i],
                x - 10, chartBottom + 20
            );
        }
    }
}
