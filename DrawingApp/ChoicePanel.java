package DrawingApp;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class ChoicePanel extends JPanel {

    private TimerPanel myTimer;
    private static JPanel textPanel;
    private static String chosenValue;

    private static int intChosenValue;

    private static String message = "Choose something to draw";



    public ChoicePanel() {
        setLayout(new BorderLayout());
        addRectangles();
        addAndUpdateTextPanel(); // Ajouter la méthode pour le nouveau panel
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
            final int chosenIndex = i; // Capture the index of the button clicked

            JButton button = new JButton(rectangleNames[i]);
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

                // Update the chosenValue variable with the selected word
                chosenValue = rectangleNames[chosenIndex];
                intChosenValue = chosenIndex + 1; // Update intChosenValue with the index
                updateMessage("You should draw " + chosenValue); // Mettre à jour le message
            });
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.SOUTH);
    }




    private void addAndUpdateTextPanel() {
        textPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw a gray box with size 350x350
                int boxWidth = 370;
                int boxHeight = 300;

                g.setColor(new Color(0x3F3C3C)); // Set the box color to gray
                int centerX = (getWidth() - boxWidth) / 2; // Center horizontally
                int centerY = ((getHeight() - boxHeight) / 2); // Center vertically
                g.fillRect(centerX, centerY, boxWidth, boxHeight);

                // Set the text properties
                g.setColor(Color.WHITE); // Set the text color to white
                Font font = new Font("Roboto", Font.PLAIN, 20); // Set the font
                g.setFont(font);

                FontMetrics metrics = g.getFontMetrics(font);
                int textX = (getWidth() - metrics.stringWidth(message)) / 2;
                int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

                g.drawString(message, textX, textY);
            }
        };


        // Set the preferred size for the textPanel
        textPanel.setPreferredSize(new Dimension(400, 300)); // Adjust the size as needed

        add(textPanel, BorderLayout.CENTER); // Add the panel to the center of ChoicePanel
    }

    private static void updateMessage(String newMessage) {
        System.out.println("Mise à jour du message: " + newMessage); // Ajouter pour le débogage
        message = newMessage;
        textPanel.repaint();
    }

    public static void updateInstructionPanel(double[] predictionsPercentages) {
        // Trouver l'index de la valeur maximale dans predictionsPercentages
        int maxIndex = 0;
        for (int i = 1; i < predictionsPercentages.length; i++) {
            if (predictionsPercentages[i] > predictionsPercentages[maxIndex]) {
                maxIndex = i;
            }
        }

        System.out.println("updateInstructionPanel appelée");

        String newMessage;
        if (intChosenValue - 1 == maxIndex) {
            newMessage = "Congratulations! You got it right!";
        } else {
            newMessage = "Oops! I couldn't guess what you drew.";
        }

        updateMessage(newMessage);
    }

    public boolean isTimerActive() {
        return myTimer != null && myTimer.isRunning();
    }

    public TimerPanel getMyTimer() {
        return myTimer;

}}