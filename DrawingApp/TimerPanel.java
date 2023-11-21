package DrawingApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private JProgressBar progressBar;
    private Timer timer; // javax.swing.Timer
    private int timeLeft;
    private int totalTime;

    public TimerPanel(int totalTimeInSeconds) {
        this.totalTime = totalTimeInSeconds;
        this.timeLeft = totalTimeInSeconds;
        progressBar = new JProgressBar(0, totalTime);
        progressBar.setValue(totalTime);
        progressBar.setStringPainted(true); // Pour afficher le temps restant sur la barre de progression

        // Configure le Timer
        timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                progressBar.setValue(timeLeft);
                if (timeLeft <= 0) {
                    timer.stop(); // Arrête le javax.swing.Timer
                    onTimerEnd();
                }
            }
        });
        this.add(progressBar); // Ajoute la barre de progression au JPanel
    }

    // Méthode pour démarrer le Timer
    public void start() {
        timer.start();
    }

    // Méthode pour arrêter le Timer
    public void stop() {
        timer.stop();
    }

    // Méthode appelée lorsque le Timer atteint zéro
    private void onTimerEnd() {
        // Actions à effectuer lorsque le timer termine
        JOptionPane.showMessageDialog(this, "Temps écoulé !");
    }

    // Méthode pour modifier la couleur de la barre de progression
    public void setProgressBarColor(Color color) {
        progressBar.setForeground(color);
    }

    // Méthode pour modifier la taille de la barre de progression
    public void setProgressBarSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        progressBar.setPreferredSize(size);
        progressBar.setMaximumSize(size);
        progressBar.setMinimumSize(size);
    }

    public boolean isRunning() {
        return timer.isRunning();
    }
}