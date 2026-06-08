/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.myapp.poepart3;

/**
 *
 * @author iciko
 */
import java.io.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

    // --- Part 3 Requirement 1: Parallel Storage Structures ---
    ArrayList<String> sentMessages = new ArrayList<>();
    ArrayList<String> disregardedMessages = new ArrayList<>();
    ArrayList<Message> storedMessages = new ArrayList<>(); // Loaded from JSON file array
    ArrayList<String> messageHashes = new ArrayList<>();
    ArrayList<String> messageIDs = new ArrayList<>();

    public MessageSystem() {
        // Automatically populate Stored Messages from the online JSON local data source context
        refreshStoredMessagesData();
    }

    // Synchronizes tracking arrays with JSON data source
    private void refreshStoredMessagesData() {
        storedMessages = MessageDatabase.loadMessages();
        messageHashes.clear();
        messageIDs.clear();
        for (Message msg : storedMessages) {
            messageHashes.add(msg.messageHash);
            messageIDs.add(msg.messageID);
        }
    }

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
            System.out.println("3. Stored Messages Menu ");
            System.out.println("4. Quit");
            System.out.print("Choose an option: ");

            try {
                option = Integer.parseInt(input.nextLine().trim());
                switch (option) {
                    case 1 -> sendMessages();
                    case 2 -> showMessages();
                    case 3 -> manageStoredMessagesMenu();
                    case 4 -> System.out.println("Exiting QuickChat...");
                    default -> System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (option != 4);
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
                    
                    // Track into status arrays dynamically
                    sentMessages.add(message);
                    
                    // Persist to JSON
                    ArrayList<Message> globalMessages = MessageDatabase.loadMessages();
                    globalMessages.add(msg);
                    MessageDatabase.saveMessages(globalMessages);
                    
                    messagesSent++;
                    refreshStoredMessagesData();
                    System.out.println("Message successfully sent.");
                }
                case 2 -> {
                    disregardedMessages.add(message);
                    System.out.println("Message discarded.");
                }
                case 3 -> {
                    Message msg = new Message(messageID, recipient, message, messageHash);
                    
                    ArrayList<Message> globalMessages = MessageDatabase.loadMessages();
                    globalMessages.add(msg);
                    MessageDatabase.saveMessages(globalMessages);
                    
                    refreshStoredMessagesData();
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
        ArrayList<Message> historicalMessages = MessageDatabase.loadMessages();
        if (historicalMessages.isEmpty()) {
            System.out.println("No messages sent yet.");
            return;
        }

        System.out.println("\n========== SENT MESSAGES HISTORY ==========");
        for (Message msg : historicalMessages) {
            System.out.println("Message ID: " + msg.messageID);
            System.out.println("Recipient: " + msg.recipient);
            System.out.println("Message: " + msg.messageText);
            System.out.println("Hash: " + msg.messageHash);
            System.out.println("--------------------------------");
        }
    }

    // --- Part 3 Requirement 2: Fourth Main Menu Item Options Handler ---
    public void manageStoredMessagesMenu() {
        refreshStoredMessagesData(); // Make sure data arrays are up-to-date
        
        System.out.println("\n========== STORED MESSAGES SUB-MENU ==========");
        System.out.println("a. Display sender and recipient of all stored messages");
        System.out.println("b. Display the longest stored message");
        System.out.println("c. Search for a message ID and display the corresponding recipient and message");
        System.out.println("d. Search for all the messages stored for a particular recipient");
        System.out.println("e. Delete a message using the message hash");
        System.out.println("f. Display a report that lists the full details of all the stored messages");
        System.out.print("Choose sub-option (a-f): ");
        
        String subChoice = input.nextLine().trim().toLowerCase();
        
        switch (subChoice) {
            case "a" -> displayRecipients();
            case "b" -> displayLongestMessage();
            case "c" -> searchByMessageID();
            case "d" -> searchByRecipient();
            case "e" -> deleteByHash();
            case "f" -> displayFullReport();
            default -> System.out.println("Invalid sub-option select! Returning to Main Menu.");
        }
    }

    // a. Display the sender (current logged-in context) and recipient of all stored messages
    private void displayRecipients() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        System.out.println("\n--- Recipients List ---");
        for (Message msg : storedMessages) {
            System.out.println("Recipient: " + msg.recipient);
        }
    }

    // b. Display the longest stored message
    private void displayLongestMessage() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        Message longest = storedMessages.get(0);
        for (Message msg : storedMessages) {
            if (msg.messageText.length() > longest.messageText.length()) {
                longest = msg;
            }
        }
        System.out.println("\n--- Longest Stored Message ---");
        System.out.println("Length: " + longest.messageText.length() + " characters");
        System.out.println("Text: " + longest.messageText);
    }

    // c. Search for a message ID and display the corresponding recipient and message
    private void searchByMessageID() {
        System.out.print("Enter Message ID to search: ");
        String targetID = input.nextLine().trim();
        
        int index = messageIDs.indexOf(targetID);
        if (index != -1) {
            Message msg = storedMessages.get(index);
            System.out.println("\n--- Message Found ---");
            System.out.println("Recipient: " + msg.recipient);
            System.out.println("Message: " + msg.messageText);
        } else {
            System.out.println("Message ID not found.");
        }
    }

    // d. Search for all the messages stored for a particular recipient
    private void searchByRecipient() {
        System.out.print("Enter Recipient Cell Number (+27xxxxxxxxx): ");
        String targetRecipient = input.nextLine().trim();
        
        boolean found = false;
        System.out.println("\n--- Match Results for Recipient: " + targetRecipient + " ---");
        for (Message msg : storedMessages) {
            if (msg.recipient.equalsIgnoreCase(targetRecipient)) {
                System.out.println("ID: " + msg.messageID + " | Message: " + msg.messageText);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No messages stored for this recipient.");
        }
    }

    // e. Delete a message using the message hash
    private void deleteByHash() {
        System.out.print("Enter Message Hash to delete: ");
        String targetHash = input.nextLine().trim();
        
        int index = messageHashes.indexOf(targetHash);
        if (index != -1) {
            storedMessages.remove(index);
            // Re-save modified lists data layer
            MessageDatabase.saveMessages(storedMessages);
            refreshStoredMessagesData();
            System.out.println("Message matching hash successfully deleted from system store.");
        } else {
            System.out.println("No matching message hash found.");
        }
    }

    // f. Display a report that lists the full details of all the stored messages
    private void displayFullReport() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored data reports available.");
            return;
        }
        System.out.println("\n=======================================================");
        System.out.println("               STORED MESSAGES TASK REPORT             ");
        System.out.println("=======================================================");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message msg = storedMessages.get(i);
            System.out.printf("Record #%d\n", (i + 1));
            System.out.println("  Message ID:   " + msg.messageID);
            System.out.println("  Hash string:  " + msg.messageHash);
            System.out.println("  Recipient:    " + msg.recipient);
            System.out.println("  Text Payload: " + msg.messageText);
            System.out.println("-------------------------------------------------------");
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

public class Poepart3 {
    public BufferedReader in;
    public String username;

    public Poepart3() {
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
        System.out.println("\n=== REGISTRATION FORM ===");
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
        System.out.println("\n=== LOGIN ===");
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
        Poepart3 client = new Poepart3();
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