package ru.mishin.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerStarter extends JFrame {
    private JTextField textField1;
    private JButton createServerButton;
    private JPanel panel1;


    public ServerStarter() {
        super("Server Starter");
        setContentPane(panel1);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500,250);

        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ServerUI(new Server(Integer.parseInt(textField1.getText())));
            }
        });
    }

    public static void main(String[] args) {
        new ServerStarter();
    }
}
