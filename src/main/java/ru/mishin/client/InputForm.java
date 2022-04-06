package ru.mishin.client;

import javax.swing.*;

public class InputForm extends JFrame {
    private JPanel panel1;
    private JTextField hostText;
    private JTextField portText;
    private JButton connectButton;


    public InputForm() {
        super("Input Form");
        setContentPane(panel1);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500,250);

        connectButton.addActionListener(e -> {
            String host = hostText.getText();
            int port = Integer.parseInt(portText.getText());
            new ClientUI(new Client(host,port));
        });
    }

    public static void main(String[] args) {
        new InputForm();
    }


}
