package DrawingApp;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.TimerTask;
import javax.swing.*;
import java.util.Timer;


public class DrawingBoard extends JPanel implements MouseListener, MouseMotionListener {

    private Image image;
    private Graphics2D graphics;
    private int prevX, prevY;
    private int paintBrushSize = 25;
    private JButton clearButton;
    private JFrame frame;
    private JTextArea predictionDisplay = new JTextArea(20, 20);
    private final Timer predictionDebounceTimer = new Timer();
    private TimerTask predictionDebounceTimerTask;
    private long debounceDelay = 1; // 1 second debounce delay


    public DrawingBoard() {
        addMouseListener(this);
        addMouseMotionListener(this);

        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);

        // Adjusting the predictionDisplay JTextArea
        predictionDisplay = new JTextArea(20, 15);
        Font newFont = new Font("Arial", Font.BOLD, 16);
        predictionDisplay.setFont(newFont);

        initializeFrame();
    }


    private void initializeFrame() {
        frame = new JFrame("Drawing Board");
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clear());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        predictionDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(predictionDisplay);


        JPanel predictionPanel = new JPanel();
        predictionPanel.setLayout(new BorderLayout());
        predictionPanel.add(scrollPane, BorderLayout.CENTER);
        predictionPanel.setPreferredSize(new Dimension(250, 600));

        frame.add(predictionPanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setVisible(true);
    }


    protected void paintComponent(Graphics g) {
        if (image == null) {
            image = createImage(getWidth(), getHeight());
            graphics = (Graphics2D) image.getGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image, 0, 0, null);
    }

    public void clear() {
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setPaint(Color.BLACK);
        repaint();
    }


    public void mousePressed(MouseEvent e) {
        prevX = e.getX();
        prevY = e.getY();
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        graphics.setStroke(new BasicStroke(paintBrushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(prevX, prevY, x, y);
        repaint();
        prevX = x;
        prevY = y;

        // If the timer task is already scheduled, cancel it
        if (predictionDebounceTimerTask != null) {
            predictionDebounceTimerTask.cancel();
        }

        predictionDebounceTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    // Pass the width and height to the predict method
                    PredictionHandler Predict = new PredictionHandler();
                    Predict.predict(image, getWidth(), getHeight(), predictionDisplay);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        predictionDebounceTimer.schedule(predictionDebounceTimerTask, debounceDelay);
    }


    public void mouseReleased(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }
}