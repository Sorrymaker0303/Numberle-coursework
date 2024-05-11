package View;

import Controller.NumberleController;
import Model.INumberleModel;
import Model.Mode;
import Model.NumberleModel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyGUI extends JFrame {
    private JCheckBox checkBox1, checkBox2, checkBox3;
    private JButton button;

    public MyGUI() {
        setTitle("game mode");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建复选框
        checkBox1 = new JCheckBox("mode1");
        checkBox2 = new JCheckBox("mode2");
        checkBox3 = new JCheckBox("mode3");


        button = new JButton("start game");


        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


                if (checkBox1.isSelected()) {
                    Mode.showErrorOnInvalidInput = true;
                } else {
                    Mode.showErrorOnInvalidInput = false;
                }
                if (checkBox2.isSelected()) {
                    Mode.showTargetEquation = true;
                } else {
                    Mode.showTargetEquation = false;
                }
                if (checkBox3.isSelected()) {
                    Mode.randomizeEquation = true;
                } else {
                    Mode.randomizeEquation = false;
                }

                dispose();
                createAndShowGUI();
            }
        });


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        panel.add(checkBox1);
        panel.add(checkBox2);
        panel.add(checkBox3);
        panel.add(button);


        add(panel);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public static void createAndShowGUI() {

        INumberleModel model = new NumberleModel();
        NumberleController controller = new NumberleController(model);
        NumberleView view = new NumberleView(model, controller);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MyGUI();
            }
        });
    }
}
