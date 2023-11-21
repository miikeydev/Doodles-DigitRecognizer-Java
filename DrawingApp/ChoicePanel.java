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
        addTextPanel();
    }

    private void addRectangles() {
        JPanel buttonPanel = new JPanel();

        int topGap = 20;
        int leftGap = 20;
        int bottomGap = 20;
        int rightGap = 20;
        Border borderWithGap = BorderFactory.createEmptyBorder(topGap, leftGap, bottomGap, rightGap);

        int hgap = 10;
        int vgap = 10;
        buttonPanel.setLayout(new GridLayout(5, 2, hgap, vgap));
        buttonPanel.setBorder(borderWithGap);

        String[] rectangleNames = {"Angel", "Apple", "Axe", "Book", "Helicopter", "Moon", "Mushroom", "Octopus", "Pants", "Pencil"};

        for (int i = 0; i < rectangleNames.length; i++) {
            final String word = rectangleNames[i];
            JButton button = new JButton(word);
            button.setPreferredSize(new Dimension(100, 50));

            button.setForeground(Color.WHITE);
            Color buttonBackgroundColor = new Color(0x3F3C3C);
            button.setBackground(buttonBackgroundColor);

            button.setOpaque(true);
            button.setBorderPainted(false);

            button.addActionListener(e -> {
                if (myTimer == null || !myTimer.isRunning()) {
                    if (myTimer != null) {
                        remove(myTimer);
                    }
                    myTimer = new TimerPanel(60);
                    myTimer.setProgressBarColor(new Color(0x147A03));
                    myTimer.setProgressBarSize(300, 30);
                    myTimer.start();
                    ChoicePanel.this.add(myTimer, BorderLayout.NORTH);
                    ChoicePanel.this.revalidate();
                    ChoicePanel.this.repaint();
                }
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

                int boxWidth = 370;
                int boxHeight = 300;

                g.setColor(new Color(0x3F3C3C));
                int centerX = (getWidth() - boxWidth) / 2;
                int centerY = ((getHeight() - boxHeight) / 2);
                g.fillRect(centerX, centerY, boxWidth, boxHeight);

                g.setColor(Color.WHITE);
                Font font = new Font("Roboto", Font.PLAIN, 20);
                g.setFont(font);

                String text = "Choose something to draw";
                FontMetrics metrics = g.getFontMetrics(font);
                int textX = centerX + (boxWidth - metrics.stringWidth(text)) / 2;
                int textY = centerY + ((boxHeight - metrics.getHeight()) / 2) + metrics.getAscent();

                g.drawString(text, textX, textY);
            }
        };

        textPanel.setPreferredSize(new Dimension(400, 300));
        add(textPanel, BorderLayout.CENTER);
    }
}
