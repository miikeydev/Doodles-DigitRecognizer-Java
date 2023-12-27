package DrawingApp;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private JProgressBar progressBar;
    protected static Timer timer;
    private int timeLeft;
    private int totalTime;

    private TimerListener timerListener;

    public interface TimerListener {
        void onTimerEnd();
    }

    public void setTimerListener(TimerListener listener) {
        this.timerListener = listener;
    }

    public TimerPanel(int totalTimeInSeconds) {
        this.totalTime = totalTimeInSeconds;
        this.timeLeft = totalTimeInSeconds;
        progressBar = new JProgressBar(0, totalTime);
        progressBar.setValue(totalTime);
        progressBar.setStringPainted(false);

        timer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                progressBar.setValue(timeLeft);
                if (timeLeft <= 0) {
                    timer.stop();
                    if (timerListener != null) {
                        timerListener.onTimerEnd();
                    }
                }
            }
        });
        this.add(progressBar);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
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

    public boolean isRunning() {
        System.out.println("Timer isRunning: " + timer.isRunning() + ", Time left: " + timeLeft);
        return timer.isRunning();
    }


    public String calculateScore() {
        double percentage = (double) timeLeft / totalTime;
        int score = (int) (percentage * 100);
        return Integer.toString(score);
    }

    public String onTimerEnd() {
        return "No more time! SCORE : 0";
    }

    public void resetTimer() {
        timeLeft = totalTime;
        progressBar.setValue(totalTime);
        this.setVisible(false);
    }




}