package ru.mishin.server;

import ru.mishin.utils.Time;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerUI  extends JFrame{
    private JButton playPauseButton;
    private JButton stopButton;
    private JLabel numberOfClientsLabel;
    private JButton shutdownServerButton;
    private JPanel panel1;
    private JLabel timerLabel;
    private Server server;
    private ServerUIListener serverUIListener = new ServerUIListener() {
        @Override
        public void onChangeNumberClients(int count) {
            numberOfClientsLabel.setText("Count of clients: " + count);
            numberOfClientsLabel.invalidate();
        }

        @Override
        public void onUpdateTime(long time) {
            String timeText = Time.timeToFormattedHMS(Time.DEFAULT_TIME_FORMATTER,time);
            timerLabel.setText(timeText);
            timerLabel.invalidate();
        }
    };

    public ServerUI(Server server){
        super("Alarm Server");
        this.server = server;
        setContentPane(panel1);
        setVisible(true);

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                playPauseButton.setText("Start");
                server.stopTimer();
            }
        });
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = playPauseButton.getText();
                if (text.equals("Start")) {
                    playPauseButton.setText("Pause");
                    stopButton.setEnabled(true);
                    server.playTimer();
                } else {
                    playPauseButton.setText("Start");
                    server.pauseTimer();
                }
            }
        });

        server.setServerUIListener(serverUIListener);
        server.start();
    }



}
