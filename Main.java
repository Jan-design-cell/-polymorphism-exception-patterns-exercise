package parceldelivery;

import parceldelivery.exception.InputClosedException;
import parceldelivery.ui.ConsoleInput;
import parceldelivery.ui.ConsoleMenu;

public class Main {

    public static void main(String[] args) {
        ConsoleInput input = new ConsoleInput(System.in, System.out);
        ConsoleMenu menu = new ConsoleMenu(input, System.out);
        try {
            menu.run();
        } catch (InputClosedException e) {
            System.out.println();
            System.out.println("Input ended. Goodbye!");
        }
    }
}