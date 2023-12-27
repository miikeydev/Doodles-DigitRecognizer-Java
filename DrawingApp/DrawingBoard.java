package DrawingApp;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class DrawingBoard extends JPanel implements MouseListener, MouseMotionListener {

    private Image image;
    private Graphics2D graphics;
    private int prevX, prevY;
    private int paintBrushSize = 50;
    private JButton clearButton;
    private JFrame frame;
    private PredictionHandler predictionHandler;
    private PredictionPanel predictionPanel;
    private Timer predictionDebounceTimer = new Timer();
    private TimerTask predictionDebounceTimerTask;
    private ChoicePanel choicePanel;

    private boolean canDraw = false;

    public DrawingBoard() {
        addMouseListener(this);
        addMouseMotionListener(this);

        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);

        predictionHandler = new PredictionHandler();
        predictionPanel = new PredictionPanel();
        choicePanel = new ChoicePanel(this);


        initializeFrame();
    }

    private void initializeFrame() {
        frame = new JFrame("Drawing Board");
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);

        ImageIcon icon = new ImageIcon("src/icone/logo.png");
        frame.setIconImage(icon.getImage());

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clear());

        clearButton.setFont(new Font("Arial", Font.PLAIN, 16));
        clearButton.setBackground(new Color(0x3F3C3C));
        clearButton.setForeground(Color.WHITE);
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);

        JLabel eraserLabel = new JLabel(" Right click for eraser");
        eraserLabel.setFont(new Font("Arial", Font.BOLD, 16));
        eraserLabel.setForeground(new Color(0x3F3C3C));
        eraserLabel.setOpaque(true);
        eraserLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(eraserLabel);

        buttonPanel.add(Box.createHorizontalStrut(-150));
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(predictionPanel, BorderLayout.EAST);
        frame.add(choicePanel, BorderLayout.WEST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1250, 670);
        frame.setVisible(true);
        frame.setResizable(false);
    }


    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
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
        if (!canDraw) {
            return;
        }
        prevX = e.getX();
        prevY = e.getY();

        if (SwingUtilities.isRightMouseButton(e)) {
            graphics.setPaint(Color.WHITE);
        } else {
            graphics.setPaint(Color.BLACK);
        }
    }


    public void mouseDragged(MouseEvent e) {
        if (!canDraw) {
            return;
        }

        int x = e.getX();
        int y = e.getY();

        graphics.setStroke(new BasicStroke(paintBrushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(prevX, prevY, x, y);

        repaint();

        prevX = x;
        prevY = y;

        schedulePredictionWithDebounce();
    }


    public void mouseReleased(MouseEvent e) {
        if (!canDraw) {
            return;
        }
        schedulePredictionWithDebounce();
    }

    private void schedulePredictionWithDebounce() {
        if (predictionDebounceTimerTask != null) {
            predictionDebounceTimerTask.cancel();
            predictionDebounceTimer.purge();
        }

        predictionDebounceTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    predictionHandler.predict(image, getWidth(), getHeight(), predictionPanel, choicePanel);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        predictionDebounceTimer.schedule(predictionDebounceTimerTask, 500);
    }

    public void mouseExited(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }
}