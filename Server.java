package HW1_QuizGame;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 1234; // Port number for the server to listen on
    private static final List<Question> QUESTIONS = Arrays.asList(
        new Question("The java.net package supports socket programming in Java. (T/F)", "T"),
        new Question("TCP is a connectionless protocol. (T/F)", "F"),
        new Question("127.0.0.1 is known as the _______________ address.", "localhost"),
        new Question("The class used to create a client socket in Java is _______________.", "Socket"),
        new Question("UDP guarantees the order of packets. (T/F)", "F")
    );

    public static void main(String[] args) {
        System.out.println("Quiz Server is running...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Create server socket
            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                // Create a new thread to handle the client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            // Handle exceptions such as port binding errors
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream for client communication
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { // Output stream for client communication

                int score = 0;

                // Send each question to the client
                for (Question question : QUESTIONS) {
                    out.println(question.getQuestion()); // Send the question
                    String answer = in.readLine(); // Receive the client's answer
                    if (answer == null) break; // Check for client disconnection

                    if (question.checkAnswer(answer)) {
                        out.println("Correct!"); // Provide positive feedback
                        score++;
                    } else {
                        out.println("Incorrect!"); // Provide negative feedback
                    }
                }

                // Send final score to the client
                out.println("Quiz finished! You scored " + score + "/" + QUESTIONS.size());
                System.out.println("Client disconnected. Final Score: " + score);

            } catch (IOException e) {
                // Handle exceptions during client communication
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close(); // Ensure socket is closed after communication
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    // Inner class to represent a quiz question
    private static class Question {
        private final String question;
        private final String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer.toLowerCase(); // Normalize answer to lowercase for comparison
        }

        public String getQuestion() {
            return question;
        }

        public boolean checkAnswer(String userAnswer) {
            // Validate user's answer, ignoring case and trimming whitespace
            return userAnswer != null && userAnswer.trim().toLowerCase().equals(answer);
        }
    }
}