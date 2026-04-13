/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.myapp.thechattingapp;

/**
 *
 * @author student
 */
import java.util.*;
import java.io.*;

// User class declarations
class User implements Serializable {
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

// USER DATABASE CLASS 
class UserDatabase {
    public static final String FILE_NAME = "users.dat";
    public static Map<String, User> users = new HashMap<>();
    
    static {
        loadUsers();
    }
    
    @SuppressWarnings("unchecked")
    public static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // First time running, no file yet
            users = new HashMap<>();
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
    public static void updateUser(User user) {
        users.put(user.username, user);
        saveUsers();
    }
}

// MAIN CLIENT CLASS
public class TheChattingApp {
    public BufferedReader in;
    public String username;
    
    public TheChattingApp() {
        in = new BufferedReader(new InputStreamReader(System.in));
        username = null;
    }
    
    public boolean authenticate() throws IOException {
        System.out.println("1. Register New Account");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        
        String choice = in.readLine();
        
        switch (choice) {
            case "1" -> {
                return register();
            }
            case "2" -> {
                return login();
            }
            case "3" -> {
                System.out.println("\nGoodbye!");
                return false;
            }
            default -> {
                System.out.println("Invalid option! Please try again.");
                return authenticate();
            }
        }
    }
    
    public boolean register() throws IOException {
        // Display requirements
        System.out.println("\n REGISTRATION FORM ");
        System.out.println("\nPLEASE READ REQUIREMENTS CAREFULLY:");
        System.out.println("\nUSERNAME Requirements:");
        System.out.println("  * Must contain an underscore (_)");
        System.out.println("  * Must be no more than 5 characters in length");
        System.out.println("  * Example: jo_hn, a_b_c, us_er");
        System.out.println("\nPASSWORD Requirements:");
        System.out.println("  * At least eight (8) characters long");
        System.out.println("  * Contains a capital letter (A-Z)");
        System.out.println("  * Contains a number (0-9)");
        System.out.println("  * Contains a special character (!@#$%^&*)");
        System.out.println("  * Example: Test@123");
        System.out.println("\nCELL NUMBER Requirements:");
        System.out.println("  * Must contain international country code (+27)");
        System.out.println("  * Followed by 9-10 digits");
        System.out.println("  * Example: +27721234567");
        
        // Get first name
        System.out.print("\nFirst Name: ");
        String firstName = in.readLine().trim();
        while (firstName.isEmpty()) {
            System.out.print("First Name cannot be empty. Please enter: ");
            firstName = in.readLine().trim();
        }
        
        // Get last name
        System.out.print("Last Name: ");
        String lastName = in.readLine().trim();
        while (lastName.isEmpty()) {
            System.out.print("Last Name cannot be empty. Please enter: ");
            lastName = in.readLine().trim();
        }
       // Username validation
        String newUsername = null;
        while (newUsername == null) {
            System.out.print("\nUsername (must contain _ and be ≤5 chars): ");
            String input = in.readLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Username cannot be empty!");
                continue;
            }
            
            if (input.contains("_") && input.length() <= 5) {
                if (UserDatabase.userExists(input)) {
                    System.out.println("Username already taken! Please choose another.");
                    continue;
                }
                System.out.println("Username successfully captured!");
                newUsername = input;
            } else {
                System.out.println("Username is not correctly formatted!please ensure that your username  contains  an underscore (_) and is no more than 5 characters in length.");
            }
        }
        
        // Password validation
        String newPassword = null;
        while (newPassword == null) {
            System.out.print("\nPassword (min 8 chars, 1 capital, 1 number, 1 special): ");
            String input = in.readLine();
            
            if (input.isEmpty()) {
                System.out.println("Password cannot be empty!");
                continue;
            }
            
            boolean hasMinLength = input.length() >= 8;
            boolean hasCapital = input.matches(".*[A-Z].*");
            boolean hasNumber = input.matches(".*[0-9].*");
            boolean hasSpecial = input.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
            
            if (hasMinLength && hasCapital && hasNumber && hasSpecial) {
                System.out.print("Confirm Password: ");
                String confirmPassword = in.readLine();
                
                if (input.equals(confirmPassword)) {
                    System.out.println("Password successfully captured!");
                    newPassword = input;
                } else {
                    System.out.println("Passwords do not match! Please try again.");
                }
            } else {
                System.out.println("Password is not correctly formatted!please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.");
            }
        }
        
        // Cellnumber validation
        String newCellNumber = null;
        while (newCellNumber == null) {
            System.out.print("\nCell Number (+27 followed by 9-10 digits): ");
            String input = in.readLine().trim().replaceAll("\\s+", "");
            
            if (input.isEmpty()) {
                System.out.println("Cell number cannot be empty!");
                continue;
            }
            
            // Check if the number starts with +27
            if (!input.startsWith("+27")) {
                System.out.println("Cell phone number incorrectly formatted! Must start with +27");
                continue;
            }
            
            String numberPart = input.substring(3);
            
            // Check if number part contains only digits and that the length is valid
            if (!numberPart.matches("\\d+")) {
                System.out.println("Cell phone number incorrectly formatted! Must contain only digits after +27");
                continue;
            }
            
            if (numberPart.length() > 10 || numberPart.length() < 9) {
                System.out.println("Cell phone number incorrectly formatted! Must have 9-10 digits after +27");
                continue;
            }
            
            // Checking first digit of number part (must be 6,7,8,9 for SA)
            char firstDigit = numberPart.charAt(0);
            if (firstDigit != '6' && firstDigit != '7' && firstDigit != '8' && firstDigit != '9') {
                System.out.println("Cell phone number incorrectly formatted or does not contain international code");
                continue;
            }
            
            System.out.println("Cell phone number successfully added!");
            newCellNumber = input;
        }
        
        // Creating and saving user
        User newUser = new User(firstName, lastName, newUsername, newPassword, newCellNumber);
        UserDatabase.saveUser(newUser);
        
        System.out.println("\n✅ REGISTRATION SUCCESSFUL!");
        System.out.println("Welcome " + firstName + " " + lastName + "!");
        System.out.println("\nPlease login to continue...\n");
        
        return login();
    }
    
    public boolean login() throws IOException {
        System.out.println("\n=== LOGIN PORTAL ===");
        
        System.out.print("\nUsername: ");
        String inputUsername = in.readLine().trim();
        
        System.out.print("Password: ");
        String inputPassword = in.readLine();
        
        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            System.out.println("Username and password cannot be empty!");
            return false;
        }
        
        User user = UserDatabase.getUser(inputUsername);
        
        if (user != null && user.password.equals(inputPassword)) {
            this.username = user.username;
            System.out.println("\n✅ LOGIN SUCCESSFUL!");
            System.out.println("Welcome " + user.firstName + " " + user.lastName + ", it is great to see you again!");
            return true;
        } else {
            System.out.println("❌ Username or password is incorrect,please try again");
            return false;
        }
    }
    public static void main(String[] args) {
        TheChattingApp client = new TheChattingApp();
        try {
            boolean authenticated = false;
            while (!authenticated) {
                authenticated = client.authenticate();
                if (authenticated) {
                    System.out.println("\nYou are now logged in as: " + client.username);
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}

   

