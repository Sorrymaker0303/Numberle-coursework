import Controller.*;
import Model.*;

import java.util.Scanner;


public class CLIApp {
    public static void main(String[] args) {
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("enabe mode1?(0/1)");
        String mode1 = scanner2.nextLine();
        if (mode1.equals("1")){
            Mode.showErrorOnInvalidInput=true;
        }
        System.out.println("enabe mode2?(0/1)");
        String mode2 = scanner2.nextLine();
        if (mode2.equals("1")){
            Mode.showTargetEquation=true;
        }

        System.out.println("enabe mode3?(0/1)");
        String mode3 = scanner2.nextLine();
        if (mode3.equals("1")){
            Mode.randomizeEquation=true;
        }

        INumberleModel model = new NumberleModel();
        NumberleController controller = new NumberleController(model);


        try (Scanner scanner = new Scanner(System.in)) {
            controller.startNewGame();
            System.out.println("Welcome to Numberle - CLI Version");
            System.out.println("You have " + controller.getRemainingAttempts() + " attempts to guess the right equation. Good luck!");

            while (!controller.isGameOver()) {
                System.out.println("\nEnter your guess (7 characters long equation): ");
                String input = scanner.nextLine();

                controller.processInput(input);

                if (controller.isGameOver()) {
                    if (controller.isGameWon()) {
                        System.out.println("Congratulations! You've guessed the equation correctly.");
                    } else {
                        System.out.println("Game Over! The correct equation was: " + controller.getTargetWord());
                    }
                } else {
                    System.out.println("Try again. You have " + controller.getRemainingAttempts() + " attempts left.");
                }
            }
        }
    }
}
