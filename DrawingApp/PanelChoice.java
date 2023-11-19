package DrawingApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;

public class PanelChoice extends JPanel {

    Color myCustomColorGreen = new Color(0x147A03);
    Color myCustomColorBlack = new Color(0x147A03);

    private MyTimer myTimer; // Ajout d'un attribut pour le timer


    public PanelChoice() {
        setLayout(new BorderLayout()); // Utiliser BorderLayout

        addRectangles();
    }

    private void addRectangles() {
        JPanel buttonPanel = new JPanel();

        // Specify the horizontal and vertical gaps in the GridLayout constructor
        int hgap = 10; // horizontal gap in pixels
        int vgap = 5; // vertical gap in pixels
        buttonPanel.setLayout(new GridLayout(5, 2, hgap, vgap));

        for (int i = 1; i <= 10; i++) {
            final String word = "Mot " + i;
            JButton button = new JButton(word);
            button.setPreferredSize(new Dimension(100, 50));

            // Set the button colors as desired
            button.setForeground(Color.WHITE);
            Color buttonBackgroundColor = new Color(0x3F3C3C); // Crée une nouvelle couleur avec le code hexadécimal
            button.setBackground(buttonBackgroundColor); // Utilise cette couleur pour l'arrière-plan du bouton


            // Make the button colors visible
            button.setOpaque(true);
            button.setBorderPainted(false);

            button.addActionListener(e -> {
                if (myTimer != null) {
                    myTimer.stop(); // Arrête le timer précédent s'il est en cours
                }
                myTimer = new MyTimer(60); // Crée un nouveau MyTimer de 60 secondes
                myTimer.setProgressBarColor(new Color(0x147A03));
                myTimer.setProgressBarSize(300, 30); // Définir la taille de la barre de progression
                myTimer.start(); // Démarrer le timer
                PanelChoice.this.add(myTimer, BorderLayout.NORTH); // Ajoute myTimer au PanelChoice
                PanelChoice.this.revalidate();
                PanelChoice.this.repaint();
            });
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Exemple BorderLayout avec Rectangle en bas à gauche");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new PanelChoice());
        frame.pack();
        frame.setVisible(true);
    }
}
