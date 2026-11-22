
import java.awt.*;
import javax.swing.*;

public class residentTuitionChart extends JPanel {

    private final String[] colleges = {
        "Assumption", "WPI", "MCPHS", "Nichols",
        "Holy Cross", "Clark", "Worcester State", "QCC"
    };

    private final double[] tuition = {
        75850, 87570, 75100, 67726,
        91740, 77970, 31363, 0
    };

    public residentTuitionChart() {
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

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString("Annual Resident Tuition", 250, 40);

        int chartBottom = 400;
        int chartTop = 100;
        int chartHeight = chartBottom - chartTop;
        int barWidth = 55;
        int gap = 30;
        int startX = 65;
        double maxTuition = 100000;

        g2.drawLine(50, chartTop, 50, chartBottom);
        g2.drawLine(50, chartBottom, 780, chartBottom);

        for (int i = 0; i < colleges.length; i++) {
            int barHeight = (int) ((tuition[i] / maxTuition) * chartHeight);
            int x = startX + i * (barWidth + gap);
            int y = chartBottom - barHeight;

            g2.setColor(new Color(70, 130, 180));
            g2.fillRect(x, y, barWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 11));

            if (i == 2) {
                g2.drawString("$75,100*", x - 8, y - 8);
            } else if (i == 7) {
                g2.drawString("N/A", x + 12, chartBottom - 10);
            } else {
                g2.drawString(
                    "$" + String.format("%,.0f", tuition[i]),
                    x - 8,
                    y - 8
                );
            }

            g2.drawString(colleges[i], x - 8, chartBottom + 20);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.drawString("* MCPHS range: $75,100-$86,840", 65, 455);
        g2.drawString("QCC: No resident option", 65, 475);
    }
}
