package DrawingApp;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

public class DrawingBoard extends JPanel implements MouseListener, MouseMotionListener {

    private List<Integer> predictions = new ArrayList<>();
    private List<Integer> clickedElements = new ArrayList<>();

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
    private JLabel timerLabel;

    public DrawingBoard() {
        addMouseListener(this);
        addMouseMotionListener(this);

        Color lightGray = new Color(240, 240, 240);

        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);



        predictionHandler = new PredictionHandler();
        predictionPanel = new PredictionPanel();

        initializeFrame();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenSize);



        // Mettez la fenêtre en plein écran
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setVisible(true);

        // Créez un panneau pour le côté gauche
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(400, screenSize.height));

        // Ajoutez l'étiquette "Choose Your Label" dans le panneau latéral
        JLabel chooseLabel = new JLabel("Choose Your Label");
        chooseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Créez un nouvel objet Font avec la taille désirée (par exemple, 50 points)
        Font labelFont = new Font(chooseLabel.getFont().getName(), Font.PLAIN, 30);
        // Définissez la couleur de fond de l'étiquette
        chooseLabel.setBackground(Color.LIGHT_GRAY); // Vous pouvez choisir la couleur de votre choix
        // Assurez-vous que l'étiquette est opaque pour que la couleur de fond soit visible
        chooseLabel.setOpaque(true);
        // Alignez le texte au centre de l'étiquette
        chooseLabel.setHorizontalAlignment(SwingConstants.CENTER);

        chooseLabel.setFont(labelFont);

        // Créez un panneau pour les objets numérotés
        JPanel objectPanel = new JPanel();
        objectPanel.setLayout(new GridLayout(2, 5)); // 2 rangées de 5 colonnes

        Dimension objectSize = new Dimension(100, 30);

        for (int i = 0; i < 10; i++) {
            final int clickedIndex = i; //
            JLabel objectLabel = new JLabel(getObjectName(i));
            objectLabel.setHorizontalAlignment(JLabel.CENTER); // Pour centrer le texte
            objectLabel.setBackground(Color.LIGHT_GRAY); // Couleur de fond
            objectLabel.setOpaque(true); // Assurez-vous que le fond est opaque

            // Définissez une bordure autour de l'étiquette
            objectLabel.setBorder(BorderFactory.createLineBorder(lightGray, 5)); // Couleur de la bordure et épaisseur

            objectPanel.add(objectLabel);

            // Ajoutez un clic sur l'étiquette "Angel"
            objectLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    clickedElements.add(clickedIndex);
                    // Gérez l'événement du clic ici
                    startDrawingPhase(clickedIndex);
                }

                private void startDrawingPhase(int clickedIndex) {
                    // JLabel pour afficher le chronomètre
                    timerLabel = new JLabel("0 secondes");
                    timerLabel.setFont(new Font("Serif", Font.BOLD, 20)); // Personnalisez la police
                    sidePanel.add(timerLabel);
                    // Démarrez le chronomètre
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        int seconds = 0;

                        @Override
                        public void run() {
                            // Vérifiez si le dessin correspond à "Angel" ici
                            if (dessinCorrespond()) {
                                timer.cancel(); // Arrêtez le chronomètre
                                System.out.println("Bravo, votre dessin a été trouvé !");
                                JOptionPane.showMessageDialog(null, "Bravo, votre dessin a été trouvé !", "Résultat", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                seconds++;
                                SwingUtilities.invokeLater(() -> timerLabel.setText(seconds + " "));
                            }
                        }
                    };

                    timer.scheduleAtFixedRate(task, 1000, 1000);


                    // Planifiez la tâche pour s'exécuter pendant une période de temps (par exemple, 10 secondes)
                    timer.schedule(task, 10000); // 10 000 millisecondes (10 secondes)

                    // Lorsque vous cliquez sur un élément, ajoutez l'index de cet élément à la liste des éléments cliqués
                    clickedElements.add(clickedIndex);


                    // Comparer les listes de prédictions et d'éléments cliqués
                    if (predictions.equals(clickedElements)) {
                        // Les listes sont égales, fermez la fenêtre
                        closeWindow();
                    }
                }


                private boolean predictionsEqual() {
                    return predictions.equals(clickedElements);
                }

                private void closeWindow() {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(DrawingBoard.this);
                    if (topFrame != null) {
                        topFrame.dispose();
                    }
                }

                private boolean dessinCorrespond() {

                    return false;
                }


            });


            objectLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String objectName = ((JLabel) e.getSource()).getText();
                    showObjectNameDialog(objectName);
                }
            });

            objectPanel.add(objectLabel);

        }


        sidePanel.add(chooseLabel);
        sidePanel.add(objectPanel);

        // Créez une bordure à droite de la partie gauche
        int borderSize = 20; // Taille de la bordure (ajustez-la selon vos besoins)
        sidePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, borderSize, lightGray)); // Couleur de la bordure (noir ici)


        // Ajoutez le panneau latéral à la fenêtre principale
        setLayout(new BorderLayout());
        add(sidePanel, BorderLayout.WEST);

    }

    private void showObjectNameDialog(String objectName) {
        JFrame dialogFrame = new JFrame("Object Name");
        dialogFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel nameLabel = new JLabel(objectName);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        dialogFrame.add(nameLabel);
        dialogFrame.pack();
        dialogFrame.setLocationRelativeTo(this);
        dialogFrame.setVisible(true);
    }


    private String getObjectName(int index) {
        String[] objectNames = {"Angel", "Apple", "Axe", "Book", "Helicopter", "Moon", "Mushroom", "Octopus", "Pants", "Pencil"};
        if (index >= 0 && index < objectNames.length) {
            return objectNames[index];
        } else {
            return "Unknown";
        }
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

        frame.add(predictionPanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
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

        // Check if the right mouse button was pressed
        if (SwingUtilities.isRightMouseButton(e)) {
            graphics.setPaint(Color.WHITE); // Set the paint color to white for the eraser
        } else {
            graphics.setPaint(Color.BLACK); // Set the paint color to black for drawing
        }
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Check which button is being dragged
        if (SwingUtilities.isRightMouseButton(e)) {
            graphics.setPaint(Color.WHITE); // Use white color to erase
        } else {
            graphics.setPaint(Color.BLACK); // Use black color to draw
        }

        graphics.setStroke(new BasicStroke(paintBrushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(prevX, prevY, x, y);
        repaint();
        prevX = x;
        prevY = y;

        schedulePredictionWithDebounce();
    }

    public void mouseReleased(MouseEvent e) {
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
                    double[] predictions = predictionHandler.predict(image, getWidth(), getHeight(), predictionPanel);
                    predictionPanel.setPredictions(predictions); // Ajoutez cette ligne pour mettre à jour les prédictions
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        predictionDebounceTimer.schedule(predictionDebounceTimerTask, 500);
    }

    // The remaining mouse event methods are unchanged
    public void mouseExited(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }
}
