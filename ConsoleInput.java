package parceldelivery.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import parceldelivery.exception.InputClosedException;
import parceldelivery.exception.ParcelException;

public class ConsoleInput {

    /** Converts raw text to a value or throws a ParcelException explaining what is wrong. */
    @FunctionalInterface
    public interface Parser<T> {
        T parse(String raw) throws ParcelException;
    }

    private final BufferedReader reader;
    private final PrintStream out;

    public ConsoleInput(InputStream in, PrintStream out) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.out = out;
    }

    /** Prints the prompt and returns the next line. Throws InputClosedException at end of input. */
    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new InputClosedException();
            }
            return line;
        } catch (IOException e) {
            throw new InputClosedException();
        }
    }

    /** Asks repeatedly until the parser accepts the answer. */
    public <T> T ask(String prompt, Parser<T> parser) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return parser.parse(raw);
            } catch (ParcelException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }
}