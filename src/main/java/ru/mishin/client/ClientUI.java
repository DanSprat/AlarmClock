package ru.mishin.client;

import ru.mishin.database.models.Event;
import ru.mishin.utils.Time;
import ru.mishin.utils.exceptions.TimeFormatException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientUI extends JFrame {
    private JButton disconnectButton;
    private JButton addAlarmButton;
    private JTextArea descriptionArea;
    private JSpinner hoursSpinner;
    private JSpinner secondsSpinner;
    private JSpinner minutesSpinner;
    private JLabel timeLabel;
    private JTable table1;
    private JPanel panel1;
    private DefaultTableModel defaultTableModel;
    private final ArrayList<Event> tableEvents = new ArrayList<>();
    public Client client;

    private ClientUIListener clientUIListener = new ClientUIListener() {
        @Override
        public void onAddEvent(Event event) {
            synchronized (tableEvents) {
                tableEvents.add(event);
                defaultTableModel.addRow(new String[]{event.getDesc(), Time.timeToFormattedHMS(Time.DEFAULT_TIME_FORMATTER, event.getTime())});
            }
        }

        @Override
        public void onTime(long time) {
            String timeText = Time.timeToFormattedHMS(Time.DEFAULT_TIME_FORMATTER,time);
            timeLabel.setText(timeText);
            timeLabel.invalidate();
        }

        @Override
        public void onAlarm(List<Event> events) {
            new Thread(()->events.forEach(x->JOptionPane.showMessageDialog(null,x.getDesc(),"New Alarm for Client " + client.getClientsID(),1))).start();
            long time = events.get(0).getTime();
            synchronized (tableEvents) {
                for (int i = 0; i< tableEvents.size();i++){
                    if (time == tableEvents.get(i).getTime()) {
                        tableEvents.remove(i);
                        defaultTableModel.removeRow(i);
                    }
                }
            }
        }

        @Override
        public void onSubscribe(long time, List<Event> events) {
            String timeText = Time.timeToFormattedHMS(Time.DEFAULT_TIME_FORMATTER,time);
            timeLabel.setText(timeText);
            timeLabel.invalidate();
            synchronized (tableEvents) {
                for (var event:events) {
                    tableEvents.add(event);
                    defaultTableModel.addRow(new String[]{event.getDesc(), Time.timeToFormattedHMS(Time.DEFAULT_TIME_FORMATTER, event.getTime())});
                }
            }

        }

        @Override
        public void onReset() {
            synchronized (tableEvents) {
                tableEvents.clear();
                defaultTableModel.setRowCount(0);
            }
        }
    };

    private long buildTime() throws TimeFormatException {
        int hours = Integer.parseInt(hoursSpinner.getValue().toString());
        int minutes = Integer.parseInt(minutesSpinner.getValue().toString());
        int seconds = Integer.parseInt(secondsSpinner.getValue().toString());
        return Time.timeToSeconds(hours,minutes,seconds);

    }

    public ClientUI(Client client) {
        super("Client " + client.getClientsID());
        this.client = client;
        setContentPane(panel1);
        setVisible(true);
        setSize(500,250);
        defaultTableModel = (DefaultTableModel) table1.getModel();
        addAlarmButton.addActionListener(e -> {
        try {
            long time = buildTime();
            String description = descriptionArea.getText();
            client.addAlarm(time,description);
        } catch (TimeFormatException exception) {
            exception.printStackTrace();
        }
        });
        disconnectButton.addActionListener(e -> {
            client.disconnect();
            dispose();
        });

        defaultTableModel.addColumn("Event Description");
        defaultTableModel.addColumn("Time");
        table1.getColumnModel().getColumn(0).setCellRenderer(new TextTableCenter());
        table1.getColumnModel().getColumn(1).setCellRenderer(new TextTableCenter());
        table1.setRowHeight(40);
        table1.setFont(new Font("Consolas",Font.BOLD,20));
        client.setClientUIListener(clientUIListener);
        client.start();
    }

    public static void main(String[] args) {
    }
}
