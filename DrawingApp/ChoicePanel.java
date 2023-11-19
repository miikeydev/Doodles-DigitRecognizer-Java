package DrawingApp;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class ChoicePanel extends JPanel {

    private TimerPanel myTimer;
    private JPanel textPanel;


    public ChoicePanel() {
        setLayout(new BorderLayout());
        addRectangles();
        addTextPanel(); // Ajouter la méthode pour le nouveau panel
    }

    private void addRectangles() {
        JPanel buttonPanel = new JPanel();

        // Create an EmptyBorder with top, left, bottom, and right gaps
        int topGap = 20;
        int leftGap = 20;
        int bottomGap = 20;
        int rightGap = 20;
        Border borderWithGap = BorderFactory.createEmptyBorder(topGap, leftGap, bottomGap, rightGap);

        // Specify the horizontal and vertical gaps in the GridLayout constructor
        int hgap = 10;
        int vgap = 10;
        buttonPanel.setLayout(new GridLayout(5, 2, hgap, vgap));
        buttonPanel.setBorder(borderWithGap); // Add the border with gaps to buttonPanel

        // Names for the rectangles
        String[] rectangleNames = {"Angel", "Apple", "Axe", "Book", "Helicopter", "Moon", "Mushroom", "Octopus", "Pants", "Pencil"};

        for (int i = 0; i < rectangleNames.length; i++) {
            final String word = rectangleNames[i];
            JButton button = new JButton(word);
            button.setPreferredSize(new Dimension(100, 50));

            // Set the button colors as desired
            button.setForeground(Color.WHITE);
            Color buttonBackgroundColor = new Color(0x3F3C3C);
            button.setBackground(buttonBackgroundColor);

            button.setOpaque(true);
            button.setBorderPainted(false);

            button.addActionListener(e -> {
                if (myTimer != null) {
                    myTimer.stop();
                }
                myTimer = new TimerPanel(60);
                myTimer.setProgressBarColor(new Color(0x147A03));
                myTimer.setProgressBarSize(300, 30);
                myTimer.start();
                ChoicePanel.this.add(myTimer, BorderLayout.NORTH);
                ChoicePanel.this.revalidate();
                ChoicePanel.this.repaint();
            });
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.SOUTH);
    }



    private void addTextPanel() {
        textPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw a gray box with size 350x350
                int boxWidth = 370;
                int boxHeight = 300;

                g.setColor(new Color(0x3F3C3C)); // Set the box color to gray
                int centerX = (getWidth() - boxWidth) / 2; // Center horizontally
                int centerY = ((getHeight() - boxHeight) / 2) ; // Center vertically
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
        };

        // Set the preferred size for the textPanel
        textPanel.setPreferredSize(new Dimension(400, 300)); // Adjust the size as needed

        add(textPanel, BorderLayout.CENTER); // Add the panel to the center of ChoicePanel
    }

}
