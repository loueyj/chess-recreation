package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Action implements ActionListener {

    public JTextField field;

    public Action(JTextField theField) {
        field = theField;
    }

    public void actionPerformed(ActionEvent e) {
        field.setText(e.getActionCommand());

        JButton theButton = (JButton) e.getSource();
        theButton.setBackground(Color.white);
    }

}
