package DrawingApp;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ChoicePanel extends JPanel implements TimerPanel.TimerListener {

    private static DrawingBoard drawingBoard;

    private static TimerPanel myTimerPanel = null;

    private static JPanel textPanel;
    private static String chosenValue;

    private static int intChosenValue;

    private static String message = "Choose something to draw";

    private JButton selectedButton = null;

    public ChoicePanel(DrawingBoard drawingBoard) {
        this.drawingBoard = drawingBoard;
        setLayout(new BorderLayout());
        addRectangles();
        addAndUpdateTextPanel();
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
        buttonPanel.setBorder(borderWithGap);

        // Names for the rectangles
        String[] rectangleNames = {"Angel", "Apple", "Axe", "Book", "Helicopter", "Moon", "Mushroom", "Octopus", "Pants", "Pencil"};

        for (int i = 0; i < rectangleNames.length; i++) {
            final int chosenIndex = i;

            JButton button = new JButton(rectangleNames[i]);
            button.setPreferredSize(new Dimension(100, 50));


            button.setForeground(Color.WHITE);
            Color buttonBackgroundColor = new Color(0x3F3C3C);
            button.setBackground(buttonBackgroundColor);

            button.setOpaque(true);
            button.setBorderPainted(false);

            button.addActionListener(e -> {
                if (selectedButton != null) {

                    selectedButton.setBackground(new Color(0x3F3C3C));
                }
                selectedButton = button;
                selectedButton.setBackground(new Color(0x147A03));

                if (myTimerPanel != null) {
                    myTimerPanel.stop();
                }
                myTimerPanel = new TimerPanel(60);
                myTimerPanel.setTimerListener((TimerPanel.TimerListener) this);
                myTimerPanel.setProgressBarColor(new Color(0x147A03));
                myTimerPanel.setProgressBarSize(300, 30);
                myTimerPanel.start();
                ChoicePanel.this.add(myTimerPanel, BorderLayout.NORTH);
                ChoicePanel.this.revalidate();
                ChoicePanel.this.repaint();

                drawingBoard.setCanDraw(true);

                // Update the chosenValue variable with the selected word
                chosenValue = rectangleNames[chosenIndex];
                intChosenValue = chosenIndex + 1;
                updateMessage("You should draw " + chosenValue);
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

                int boxWidth = 370;
                int boxHeight = 300;

                g.setColor(new Color(0x3F3C3C));
                int centerX = (getWidth() - boxWidth) / 2;
                int centerY = ((getHeight() - boxHeight) / 2);
                g.fillRect(centerX, centerY, boxWidth, boxHeight);

                g.setColor(Color.WHITE);
                Font font = new Font("Roboto", Font.PLAIN, 20);
                g.setFont(font);

                FontMetrics metrics = g.getFontMetrics(font);
                int textX = (getWidth() - metrics.stringWidth(message)) / 2;
                int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

                g.drawString(message, textX, textY);
            }
        };

        // Set the preferred size for the textPanel
        textPanel.setPreferredSize(new Dimension(400, 300));
        add(textPanel, BorderLayout.CENTER);
    }

    private static void updateMessage(String newMessage) {
        message = newMessage;
        textPanel.repaint();
    }

    public static void updateInstructionPanel(double[] predictionsPercentages) {
        int maxIndex = 0;
        for (int i = 1; i < predictionsPercentages.length; i++) {
            if (predictionsPercentages[i] > predictionsPercentages[maxIndex]) {
                maxIndex = i;
            }
        }


        String newMessage;
        if (intChosenValue - 1 == maxIndex) {
            newMessage = "Congratulation ! SCORE : ";
            drawingBoard.setCanDraw(false);
            if (myTimerPanel != null) {
                myTimerPanel.stop();
                String score = myTimerPanel.calculateScore();
                newMessage +=  score;

                Timer delayTimer = new Timer();
                delayTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        drawingBoard.clear();
                        updateMessage("Choose something to draw");
                    }
                }, 3000);
            }
        }

        else {
            newMessage = "Oops! I couldn't guess what you drew.";
            if (!myTimerPanel.isRunning()) {
                newMessage = myTimerPanel.onTimerEnd();
            }
        }

        updateMessage(newMessage);
    }

    public void onTimerEnd() {
        drawingBoard.setCanDraw(false);
        drawingBoard.clear();
        String message = "No more time! SCORE : 0";
        updateMessage(message);
    }
}
