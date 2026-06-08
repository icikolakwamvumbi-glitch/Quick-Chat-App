/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.myapp.chattingapp3;

/**
 *
 * @author iciko
 */
import java.io.*;
import java.util.*;

class User implements Serializable {
    private static final long serialVersionUID = 1L;

    String firstName;
    String lastName;
    String username;
    String password;
    String cellNumber;

    public User(String firstName, String lastName, String username, String password, String cellNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
    }
}

class UserDatabase {
    public static final String FILE_NAME = "users.dat";
    public static Map<String, User> users = new HashMap<>();

    static {
        loadUsers();
    }

    @SuppressWarnings("unchecked")
    public static void loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            users = new HashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            users = new HashMap<>();
        }
    }

    public static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static boolean userExists(String username) {
        return users.containsKey(username);
    }

    public static void saveUser(User user) {
        users.put(user.username, user);
        saveUsers();
    }

    public static User getUser(String username) {
        return users.get(username);
    }
}

class Message {
    String messageID;
    String recipient;
    String messageText;
    String messageHash;

    public Message(String messageID, String recipient, String messageText, String messageHash) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = messageHash;
    }
}

class MessageDatabase {
    public static final String FILE_NAME = "messages.json";

    public static ArrayList<Message> loadMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists() || file.length() == 0) {
            return messages;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String id = null, rcpt = null, text = null, hash = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("{")) {
                    id = rcpt = text = hash = null;
                } else if (line.contains("\"messageID\"")) {
                    id = extractValue(line);
                } else if (line.contains("\"recipient\"")) {
                    rcpt = extractValue(line);
                } else if (line.contains("\"messageText\"")) {
                    text = unescapeJson(extractValue(line));
                } else if (line.contains("\"messageHash\"")) {
                    hash = extractValue(line);
                } else if (line.startsWith("}")) {
                    if (id != null) {
                        messages.add(new Message(id, rcpt, text, hash));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading messages: " + e.getMessage());
        }

        return messages;
    }

    public static void saveMessages(ArrayList<Message> messages) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            bw.write("[\n");
            for (int i = 0; i < messages.size(); i++) {
                Message msg = messages.get(i);
                bw.write("  {\n");
                bw.write("    \"messageID\": \"" + escapeJson(msg.messageID) + "\",\n");
                bw.write("    \"recipient\": \"" + escapeJson(msg.recipient) + "\",\n");
                bw.write("    \"messageText\": \"" + escapeJson(msg.messageText) + "\",\n");
                bw.write("    \"messageHash\": \"" + escapeJson(msg.messageHash) + "\"\n");
                bw.write("  }");
                if (i < messages.size() - 1) {
                    bw.write(",");
                }
                bw.write("\n");
            }
            bw.write("]\n");
        } catch (IOException e) {
            System.out.println("Error saving messages to JSON: " + e.getMessage());
        }
    }

    private static String extractValue(String line) {
        int colon = line.indexOf(':');
        if (colon == -1) return "";

        int firstQuote = line.indexOf('"', colon);
        int lastQuote = line.lastIndexOf('"');
        if (firstQuote == -1 || lastQuote == -1 || firstQuote >= lastQuote) return "";

        return line.substring(firstQuote + 1, lastQuote);
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"");
    }

    private static String unescapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\\"", "\"")
                   .replace("\\\\", "\\");
    }
}

class MessageSystem {
    static Scanner input = new Scanner(System.in);
    int totalMessages;
    int messagesSent = 0;

    ArrayList<Message> sentMessages = MessageDatabase.loadMessages();

    public void startMessaging() {
        System.out.println("\nWelcome to QuickChat.\n");
        System.out.print("How many messages would you like to send? ");

        while (true) {
            try {
                totalMessages = Integer.parseInt(input.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }

        int option = 0;
        do {
            System.out.println("\n========== MENU ==========");
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            try {
                option = Integer.parseInt(input.nextLine().trim());
                switch (option) {
                    case 1 -> sendMessages();
                    case 2 -> showMessages();
                    case 3 -> System.out.println("Exiting QuickChat...");
                    default -> System.out.println("Invalid option. Please choose 1, 2, or 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (option != 3);
    }

    public void sendMessages() {
        if (messagesSent >= totalMessages) {
            System.out.println("\nYou have already reached the maximum number of messages allowed for this session.");
            return;
        }

        while (messagesSent < totalMessages) {
            System.out.println("\n========== NEW MESSAGE ==========");
            String messageID = generateMessageID();

            System.out.print("Enter recipient number (+27xxxxxxxxx): ");
            String recipient = input.nextLine().trim();

            if (!checkRecipientCell(recipient)) {
                System.out.println("Cell number is incorrectly formatted.");
                continue;
            }

            System.out.print("Enter your message: ");
            String message = input.nextLine();

            if (!checkMessageLength(message)) {
                System.out.println("Please enter a message of less than 250 characters.");
                continue;
            } else {
                System.out.println("Message ready to send.");
            }

            String messageHash = createMessageHash(messageID, messagesSent, message);

            System.out.println("\n========== MESSAGE DETAILS ==========");
            System.out.println("Message ID: " + messageID);
            System.out.println("Message Hash: " + messageHash);
            System.out.println("Recipient: " + recipient);
            System.out.println("Message: " + message);

            System.out.println("\nChoose an option:");
            System.out.println("1. Send Message");
            System.out.println("2. Disregard Message");
            System.out.println("3. Store Message to send later");
            System.out.print("Selection: ");

            int choice = 0;
            while (true) {
                try {
                    choice = Integer.parseInt(input.nextLine().trim());
                    if (choice >= 1 && choice <= 3) break;
                    System.out.print("Selection (1-3): ");
                } catch (NumberFormatException e) {
                    System.out.print("Selection (1-3): ");
                }
            }

            switch (choice) {
                case 1 -> {
                    Message msg = new Message(messageID, recipient, message, messageHash);
                    sentMessages.add(msg);
                    MessageDatabase.saveMessages(sentMessages);
                    messagesSent++;
                    System.out.println("Message successfully sent and saved to JSON.");
                }
                case 2 -> System.out.println("Message discarded.");
                case 3 -> {
                    Message msg = new Message(messageID, recipient, message, messageHash);
                    sentMessages.add(msg);
                    MessageDatabase.saveMessages(sentMessages);
                    System.out.println("Message successfully stored to JSON for later.");
                }
            }

            System.out.println("Total messages sent this session: " + messagesSent);

            if (messagesSent == totalMessages) {
                System.out.println("\nYou have reached the maximum number of messages.");
                break;
            }

            System.out.print("\nDo you want to send another message now? (yes/no): ");
            String keepGoing = input.nextLine().trim().toLowerCase();
            if (!keepGoing.equals("yes") && !keepGoing.equals("y")) {
                break;
            }
        }
    }

    public void showMessages() {
        sentMessages = MessageDatabase.loadMessages();
        if (sentMessages.isEmpty()) {
            System.out.println("No messages sent yet.");
            return;
        }

        System.out.println("\n========== SENT MESSAGES HISTORY ==========");
        for (Message msg : sentMessages) {
            System.out.println("Message ID: " + msg.messageID);
            System.out.println("Recipient: " + msg.recipient);
            System.out.println("Message: " + msg.messageText);
            System.out.println("Hash: " + msg.messageHash);
            System.out.println("--------------------------------");
        }
    }

    public static String generateMessageID() {
        Random random = new Random();
        long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }

    public static boolean checkRecipientCell(String recipient) {
        return recipient.startsWith("+27") && recipient.length() == 12;
    }

    public static boolean checkMessageLength(String message) {
        return message.length() <= 250;
    }

    public static String createMessageHash(String messageID, int messageNumber, String message) {
        String[] words = message.trim().isEmpty() ? new String[0] : message.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
        String lastWord = words.length > 0 ? words[words.length - 1].toUpperCase() : "";
        String firstTwoDigits = messageID.length() >= 2 ? messageID.substring(0, 2) : "00";
        return firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord;
    }
}


public class ChattingApp3 {
    public BufferedReader in;
    public String username;

    public ChattingApp3() {
        in = new BufferedReader(new InputStreamReader(System.in));
        username = null;
    }

    public boolean authenticate() throws IOException {
        System.out.println("\n1. Register New Account");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        String choice = in.readLine();
        if (choice == null) return false;

        switch (choice.trim()) {
            case "1" -> { return register(); }
            case "2" -> { return login(); }
            case "3" -> {
                System.out.println("\nGoodbye!");
                System.exit(0);
            }
            default -> {
                System.out.println("Invalid option!");
                return authenticate();
            }
        }
        return false;
    }

    public boolean register() throws IOException {
        System.out.println("\n========== REGISTRATION FORM ==========");
        System.out.print("First Name: ");
        String firstName = in.readLine().trim();

        System.out.print("Last Name: ");
        String lastName = in.readLine().trim();

        String newUsername = null;
        while (newUsername == null) {
            System.out.print("Username (_ and max 5 chars): ");
            String inputLine = in.readLine();
            if (inputLine == null) continue;
            inputLine = inputLine.trim();

            if (inputLine.contains("_") && inputLine.length() <= 5) {
                if (UserDatabase.userExists(inputLine)) {
                    System.out.println("Username already taken!");
                } else {
                    System.out.println("Username successfully captured!");
                    newUsername = inputLine;
                }
            } else {
                System.out.println("Username incorrectly formatted!");
            }
        }

        String newPassword = null;
        while (newPassword == null) {
            System.out.print("Password: ");
            String inputLine = in.readLine();
            if (inputLine == null) continue;

            boolean valid = inputLine.length() >= 8
                    && inputLine.matches(".*[A-Z].*")
                    && inputLine.matches(".*[0-9].*")
                    && inputLine.matches(".*[!@#$%^&*()].*");

            if (valid) {
                newPassword = inputLine;
                System.out.println("Password successfully captured!");
            } else {
                System.out.println("Password incorrectly formatted!");
            }
        }

        String newCellNumber = null;
        while (newCellNumber == null) {
            System.out.print("Cell Number (+27xxxxxxxxx): ");
            String inputLine = in.readLine();
            if (inputLine == null) continue;
            inputLine = inputLine.trim();

            if (inputLine.startsWith("+27") && inputLine.length() == 12) {
                newCellNumber = inputLine;
                System.out.println("Cell phone number successfully added!");
            } else {
                System.out.println("Cell number incorrectly formatted!");
            }
        }

        User newUser = new User(firstName, lastName, newUsername, newPassword, newCellNumber);
        UserDatabase.saveUser(newUser);

        System.out.println("\nRegistration successful!");
        return login();
    }

    public boolean login() throws IOException {
        System.out.println("\n========== LOGIN ==========");
        System.out.print("Username: ");
        String inputUsername = in.readLine();
        if (inputUsername == null) return false;
        inputUsername = inputUsername.trim();

        System.out.print("Password: ");
        String inputPassword = in.readLine();
        if (inputPassword == null) return false;

        User user = UserDatabase.getUser(inputUsername);

        if (user != null && user.password.equals(inputPassword)) {
            this.username = user.username;
            System.out.println("\nLogin successful!");
            System.out.println("Welcome " + user.firstName + " " + user.lastName);
            return true;
        } else {
            System.out.println("Username or password incorrect.");
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        ChattingApp3 client = new ChattingApp3();
        boolean authenticated = false;

        while (!authenticated) {
            authenticated = client.authenticate();
            if (authenticated) {
                System.out.println("\nYou are logged in as: " + client.username);
                MessageSystem system = new MessageSystem();
                system.startMessaging();
            }
        }
    }
}