package DrawingApp;

import javax.swing.*;
import java.awt.*;

public class InstructionPanel extends JPanel {

    public InstructionPanel() {
        setPreferredSize(new Dimension(400, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw a gray box with size 350x350
        int boxWidth = 370;
        int boxHeight = 250;

        g.setColor(new Color(0x3F3C3C)); // Set the box color to gray
        int centerX = (getWidth() - boxWidth) / 2; // Center horizontally
        int centerY = ((getHeight() - boxHeight) / 2) - 100; // Center vertically
        g.fillRect(centerX, centerY, boxWidth, boxHeight);

        // Set the text properties
        g.setColor(Color.WHITE); // Set the text color to white
        Font font = new Font("Roboto", Font.PLAIN, 20); // Set the font
        g.setFont(font);

        // Center the text within the gray box
        String text = "Choose something to draw";
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = centerX + (boxWidth - metrics.stringWidth(text)) / 2;
        int textY = centerY + ((boxHeight - metrics.getHeight()) / 2) + metrics.getAscent();

        // Draw the text
        g.drawString(text, textX, textY);
    }
}
