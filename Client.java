package HW1_QuizGame;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    // Define server details (address and port)
    private static final String SERVER_ADDRESS = "localhost"; 
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        // Try-with-resources to ensure proper resource management
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); // Establish connection to server
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream for receiving server messages
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Output stream for sending messages to server
             Scanner scanner = new Scanner(System.in)) { // Scanner for user input

            System.out.println("Connected to the server!");

            while (true) {
                // Receive the question from the server
                String question = in.readLine();
                if (question == null || question.startsWith("Quiz finished")) {
                    // If quiz ends, display final score and exit
                    System.out.println(question);
                    break;
                }

                // Display the question to the client
                System.out.println("Questtion: " + question);
                System.out.print("Your answer: ");
                String answer = scanner.nextLine(); // Capture user's answer
                out.println(answer); // Send answer to the server

                // Receive feedback from the server
                String feedback = in.readLine();
                System.out.println("Server: " + feedback); // Display feedback
            }

        } catch (IOException e) {
            // Handle exceptions such as server unavailability
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}