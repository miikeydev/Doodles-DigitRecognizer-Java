package DrawingApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class TimerPanel extends JPanel {
    private JProgressBar progressBar;
    private Timer timer;
    private int timeLeft;
    private int totalTime;
    private static boolean peutDessiner = true;





    public TimerPanel(int totalTimeInSeconds) {
        this.totalTime = totalTimeInSeconds;
        this.timeLeft = totalTimeInSeconds;
        progressBar = new JProgressBar(0, totalTime);
        progressBar.setValue(totalTime);
        progressBar.setStringPainted(true);

        timer = new Timer(300, e -> {
            timeLeft--;
            progressBar.setValue(timeLeft);
            if (timeLeft <= 0) {
                timer.stop();
                onTimerEnd();
            }
        });
        this.add(progressBar);
    }

    public void start() {
        timer.start();
        peutDessiner = false;

    }



    public static void changerCouleurStylet(Graphics g) {
        if (peutDessiner) {
            {g.setColor(Color.WHITE);}
        }
    }

    public void stop() {
        timer.stop();
    }

    private void onTimerEnd() {
        // Actions à effectuer lorsque le timer termine
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public void setProgressBarColor(Color color) {
        progressBar.setForeground(color);
    }

    public void setProgressBarSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        progressBar.setPreferredSize(size);
        progressBar.setMaximumSize(size);
        progressBar.setMinimumSize(size);
    }
}
