package Model;


import View.NumberleView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private final ArrayList<String> colors = new ArrayList<>();
    private final Map<String, Set<Character>> map = new HashMap<>();



    private String generateTargetEquation() {

        List<String> equations = new ArrayList<>();
        String fileName = "equations.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                equations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!equations.isEmpty()) {
            if (Mode.randomizeEquation){
                Random rand = new Random();
                return equations.get(rand.nextInt(equations.size()));
            }else {
                return "3+4*1=7";
            }

        } else {
            return "3+4*1=7";
        }
    }

    @Override
    public void initialize() {
        Random rand = new Random();
        targetNumber = Integer.toString(rand.nextInt(10000000));
        currentGuess = new StringBuilder("");
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        setChanged();
        notifyObservers();
        targetNumber = generateTargetEquation();
        System.out.println(targetNumber);
    }

    @Override
    public void processInput(String input) {
        if (Mode.showTargetEquation){
            System.out.println(targetNumber);

        }
        Mode.restart=false;
        colors.clear();
        if (input == null || input.length() != 7) {

            if (!Mode.showErrorOnInvalidInput){
                remainingAttempts--;
                setChanged();
                notifyObservers("Try Again");
            }else {
                setChanged();
                notifyObservers("Invalid Input");
                System.out.println("Invalid Input");

            }
            if (isGameOver()) {
                remainingAttempts = INumberleModel.MAX_ATTEMPTS;
                setChanged();
                notifyObservers(gameWon ? "Game Won" : "Game Over");
                map.clear();
            }
            return;
        }

        if (!evaluateExpression(input)) {
//            System.out.println(input);
            if (!Mode.showErrorOnInvalidInput){
                remainingAttempts--;
                setChanged();
                notifyObservers("Try Again");

            }
            if (isGameOver()) {
                remainingAttempts = INumberleModel.MAX_ATTEMPTS;
                setChanged();
                notifyObservers(gameWon ? "Game Won" : "Game Over");
                map.clear();
            }

            return;
        }

        remainingAttempts--;

        if (input.equals(targetNumber)) {
            gameWon = true;
            for(int i = 0; i < input.length(); i++) {
                System.out.print("Green ");
                colors.add("0");
            }
            System.out.println();
        } else {
            if (remainingAttempts==5){
                Mode.restart=true;
            }

            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (i < targetNumber.length() && c == targetNumber.charAt(i)) {
                    System.out.print("Green ");
                    colors.add("0");
                    map.computeIfAbsent("Green", k -> new HashSet<>()).add(c);
                } else if (targetNumber.contains(String.valueOf(c))) {
                    System.out.print("Orange ");
                    colors.add("1");
                    map.computeIfAbsent("Orange", k -> new HashSet<>()).add(c);
                } else {
                    System.out.print("Gray ");
                    colors.add("2");
                    map.computeIfAbsent("Gray", k -> new HashSet<>()).add(c);
                }
            }
        }
        System.out.println();
        if (isGameOver()) {
            remainingAttempts = INumberleModel.MAX_ATTEMPTS;
            setChanged();
            notifyObservers(gameWon ? "Game Won" : "Game Over");
            map.clear();
        } else {
            setChanged();
            notifyObservers("Try Again");
        }
    }


    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    @Override
    public ArrayList<String> getColors(){
        return colors;
    }

    @Override
    public Map<String, Set<Character>> getMap() {
        return map;
    }

    @Override
    public boolean evaluateExpression(String expression) {
        String[] parts = expression.split("=");
        if (parts.length != 2) {
            if (Mode.showErrorOnInvalidInput){
                setChanged();
                notifyObservers("No Equal");

                System.out.println("No equal '=' sign.");

            }

            return false;
        }

        if (!isExpressionValid(expression)) {
            if (Mode.showErrorOnInvalidInput){
                setChanged();
                notifyObservers("No Symbols");
                System.out.println("There must be at least one '+-*/'.");
            }


            return false;
        }
        // 检查是否存在连续的运算符
        if (expression.matches(".*[+\\-*/]{2,}.*")) {
            if (Mode.showErrorOnInvalidInput){
                setChanged();
                notifyObservers("Consecutive Operators");  // 连续的运算符
                System.out.println("Consecutive Operators");
            }

            return false;
        }

        if (Math.abs( evaluateSide(parts[0]) - evaluateSide(parts[1]) ) < 0.0001) {
            return true;
        } else {
            if (Mode.showErrorOnInvalidInput){
                setChanged();
                notifyObservers("Not Equal");
                System.out.println("The left side is not equal to the right.");
            }


            return false;
        }
//        try {
//            double leftResult = evaluateSide(parts[0]);
//            double rightResult = evaluateSide(parts[1]);
//
//            return Math.abs(leftResult - rightResult) < 0.0001;
//        } catch (Exception e) {
//            System.out.println("Error calculating expression: " + e.getMessage());
//            return false;
//        }
    }


    private static boolean isExpressionValid(String expression) {
        String regex = "^[0-9=]*[+\\-*/][0-9=+\\-*/]*$";
        return Pattern.matches(regex, expression);
    }


    private static double evaluateSide(String side) {
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        // Extract numbers and operators with placeholders for intermediate results
        String tempNum = "";
        for (char ch : side.toCharArray()) {
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                numbers.add(Double.parseDouble(tempNum));
                tempNum = "";
                operators.add(ch);
            } else {
                tempNum += ch;
            }
        }
        numbers.add(Double.parseDouble(tempNum)); // Add last number

        // First pass: Evaluate * and /
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '*' || operators.get(i) == '/') {
                double result = operators.get(i) == '*' ? numbers.get(i) * numbers.get(i + 1) : numbers.get(i) / numbers.get(i + 1);
                numbers.set(i, result); // Store intermediate result
                numbers.remove(i + 1); // Remove used number
                operators.remove(i); // Remove processed operator
                i--; // Adjust index after removal
            }
        }

        // Second pass: Evaluate + and -
        double result = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            double number = numbers.get(i + 1);
            switch (operators.get(i)) {
                case '+':
                    result += number;
                    break;
                case '-':
                    result -= number;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected operator: " + operators.get(i));
            }
        }

        return result;
    }
}

