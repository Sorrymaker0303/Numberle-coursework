package View;// View.NumberleView.java
import Controller.NumberleController;

import Model.INumberleModel;
import Model.Mode;
import Model.NumberleModel;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.ch3_button.__Icon9Factory__;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.*;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);;
    private final StringBuilder input;
    private final JTextField[][] fields = new JTextField[INumberleModel.MAX_ATTEMPTS][7];
    private final Map<String, JButton> buttonMap = new HashMap<>();

    private int remainingAttempts;
    private int currentPosition = 0;

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
        input = controller.getCurrentGuess();
    }

    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(6, 7, 5, 5));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = new Font("Arial", Font.BOLD, 30);
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j] = new JTextField();
                fields[i][j].setEditable(false);
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                fields[i][j].setFont(font);fields[i][j].setFont(font);
                displayPanel.add(fields[i][j]);
            }
        }
        center.add(displayPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(2, INumberleModel.MAX_ATTEMPTS, 5, 5));
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel numberPanel = new JPanel(new GridLayout(1, 10, 5, 5));
        String[] numberKeys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

        for (String key : numberKeys) {
            JButton button = new JButton(key);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(e -> {
                if (currentPosition < 7) {
                    fields[remainingAttempts][currentPosition].setText(key);
                    currentPosition++;
                }
            });
            buttonMap.put(key, button);
            numberPanel.add(button);
        }


        JPanel operationPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        String[] operationKeys = {"Back", "+", "-", "*", "/", "=", "Enter","restart"};
        for (String key : operationKeys) {

            JButton button = new JButton(key);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            if ("restart".equals(key)){

                button.setEnabled(Mode.restart);
            }
            button.addActionListener(e -> {
                if (currentPosition <= 7) {
                    switch (key) {
                        case "restart":
                            currentPosition=0;
                            clearAllContent();
                            remainingAttempts--;
                            model.initialize();
                            Mode.restart=false;
                            buttonMap.get("restart").setEnabled(Mode.restart);
                            break;
                        case "Back":
                            if (currentPosition > 0) {
                                fields[remainingAttempts][currentPosition - 1].setText("");
                                currentPosition--;
                            }
                            break;
                        case "Enter":
                            for (int i = 0; i < currentPosition; i++) {
                                input.append(fields[remainingAttempts][i].getText());
                            }
                            controller.processInput(input.toString());
                            break;
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "=":
                            if (currentPosition < 6) {
                                fields[remainingAttempts][currentPosition].setText(key);
                                currentPosition++;
                            }
                            break;
                    }
                }
            });
            buttonMap.put(key, button);
            operationPanel.add(button);
        }

        frame.setLayout(new BorderLayout());
        frame.add(displayPanel, BorderLayout.CENTER);

        keyboardPanel.add(numberPanel, BorderLayout.NORTH);
        keyboardPanel.add(operationPanel, BorderLayout.SOUTH);
        frame.add(keyboardPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            buttonMap.get("restart").setEnabled(Mode.restart);
            String message = (String) arg;
            switch (message) {
                case "Invalid Input":
                    if (Mode.showTargetEquation){
                        JOptionPane.showMessageDialog(frame, model.getTargetNumber(), "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(frame, message, "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }

                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);

                    break;
                case "Game Won":
                    showColor();
                    JOptionPane.showMessageDialog(frame, "Congratulations! You won the game!", "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    clearAllContent();
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    controller.startNewGame();
                    break;
                case "Game Over":
                    showColor();
                    JOptionPane.showMessageDialog(frame, message + "! No Attempts, The correct equation was: " + controller.getTargetWord(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    clearAllContent();
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    controller.startNewGame();
                    break;
                case "Try Again":
                    buttonMap.get("restart").setEnabled(Mode.restart);
                    showColor();
                    setButtonColors();
                    if (Mode.showTargetEquation){
                        JOptionPane.showMessageDialog(frame, model.getTargetNumber(), "Game Won", JOptionPane.INFORMATION_MESSAGE);

                    }else {
                        JOptionPane.showMessageDialog(frame, message + ", Attempts remaining: " + model.getRemainingAttempts(), "Try Again", JOptionPane.INFORMATION_MESSAGE);

                    }
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "No Equal":
                    if (Mode.showTargetEquation){
                        JOptionPane.showMessageDialog(frame, model.getTargetNumber(), "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(frame,  "No equal '=' sign.", message, JOptionPane.INFORMATION_MESSAGE);
                    }

                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "No Symbols":
                    if (Mode.showTargetEquation){
                        JOptionPane.showMessageDialog(frame, model.getTargetNumber(), "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(frame,  "There must be at least one '+-*/'.", message, JOptionPane.INFORMATION_MESSAGE);

                    }
                     currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Not Equal":
                    if (Mode.showTargetEquation){
                        JOptionPane.showMessageDialog(frame, model.getTargetNumber(), "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(frame,  "The left side is not equal to the right.", message, JOptionPane.INFORMATION_MESSAGE);

                    }
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Consecutive Operators":
                    if (Mode.showTargetEquation){
                        JOptionPane.showMessageDialog(frame, model.getTargetNumber(), "Game Won", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(frame, "Multiple math symbols in a row.", "Input Error", JOptionPane.ERROR_MESSAGE);

                    }
                     currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts();
                    input.setLength(0);
                    break;
            }
        }
    }

    private void showColor() {
        for (int i = 0; i < model.getColors().size(); i++) {
            switch (model.getColors().get(i)) {
                case "0":
                    fields[remainingAttempts][i].setBackground(Color.green);
                    break;
                case "1":
                    fields[remainingAttempts][i].setBackground(Color.orange);
                    break;
                case "2":
                    fields[remainingAttempts][i].setBackground(Color.gray);
                    break;
            }
        }
    }

    private void clearAllContent() {
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j].setText("");
                fields[i][j].setBackground(null);
            }
        }
        for (JButton button : buttonMap.values()) {
            button.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.normal));
        }
    }

    public void setButtonColors() {
        Map<String, Color> colorDefinitions = new HashMap<>();
        colorDefinitions.put("Green", Color.GREEN);
        colorDefinitions.put("Orange", Color.ORANGE);
        colorDefinitions.put("Gray", Color.GRAY);


        for (Map.Entry<String, Set<Character>> entry : model.getMap().entrySet()) {
            String colorName = entry.getKey();
            Set<Character> characters = entry.getValue();

            Color color = colorDefinitions.get(colorName);
            if (color == null) continue;

            for (Character character : characters) {
                JButton button = buttonMap.get(character.toString());
                if (button != null) {
                    if (color==Color.ORANGE){
                        button.setUI(new BasicButtonUI() {
                            @Override
                            public void paint(Graphics g, JComponent c) {
                                __Icon9Factory__.getInstance().getButtonIcon_PressedOrange().draw((Graphics2D)g, 0, 0, c.getWidth(), c.getHeight());
                                super.paint(g, c);
                            }


                        });


                    }else if (color==Color.GREEN){
                        button.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
                    } else if (color ==Color.GRAY) {
                        button.setUI(new BasicButtonUI() {
                            @Override
                            public void paint(Graphics g, JComponent c) {
                                __Icon9Factory__.getInstance().getButtonIcon_DisableGray().draw((Graphics2D)g, 0, 0, c.getWidth(), c.getHeight());
                                super.paint(g, c);
                            }
                        });

                    }


                }
            }
        }
    }


}