/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.myapp.thechattingapp;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author student
 */


public class UserTest {
    
    private User testUser;
    
    @BeforeEach
    public void setUp() {
        // This runs before each test - creates a fresh test user
        testUser = new User("John", "Doe", "te_st", "Test@123", "+27721234567");
        System.out.println("Setting up test...");
    }
    
    
    // CONSTRUCTOR TESTS
   
    
    @Test
    public void testUserConstructor() {
        System.out.println("Test 1: Testing User Constructor");
        
        assertNotNull(testUser, "User should not be null");
        assertEquals("John", testUser.firstName, "First name should match");
        assertEquals("Doe", testUser.lastName, "Last name should match");
        assertEquals("te_st", testUser.username, "Username should match");
        assertEquals("Test@123", testUser.password, "Password should match");
        assertEquals("+27721234567", testUser.cellNumber, "Cell number should match");
        
        System.out.println("✓ Constructor test passed");
    }
    
    @Test
    public void testUserConstructorWithDifferentValues() {
        System.out.println("Test 2: Testing User Constructor with different values");
        
        User anotherUser = new User("Jane", "Smith", "ja_ne", "Pass@456", "+27821234567");
        
        assertNotNull(anotherUser, "User should not be null");
        assertEquals("Jane", anotherUser.firstName);
        assertEquals("Smith", anotherUser.lastName);
        assertEquals("ja_ne", anotherUser.username);
        assertEquals("Pass@456", anotherUser.password);
        assertEquals("+27821234567", anotherUser.cellNumber);
        
        System.out.println("✓ Different values test passed");
    }
    
    
    // FIRST NAME TESTS
    
    
    @Test
    public void testFirstNameNotNull() {
        System.out.println("Test 3: Testing first name is not null");
        assertNotNull(testUser.firstName, "First name should not be null");
        System.out.println("✓ First name not null test passed");
    }
    
    @Test
    public void testFirstNameNotEmpty() {
        System.out.println("Test 4: Testing first name is not empty");
        assertFalse(testUser.firstName.isEmpty(), "First name should not be empty");
        System.out.println("✓ First name not empty test passed");
    }
    
    
    // LAST NAME TESTS
    
    
    @Test
    public void testLastNameNotNull() {
        System.out.println("Test 5: Testing last name is not null");
        assertNotNull(testUser.lastName, "Last name should not be null");
        System.out.println("✓ Last name not null test passed");
    }
    
    @Test
    public void testLastNameNotEmpty() {
        System.out.println("Test 6: Testing last name is not empty");
        assertFalse(testUser.lastName.isEmpty(), "Last name should not be empty");
        System.out.println("✓ Last name not empty test passed");
    }
    
    
    // USERNAME TESTS
    
    
    @Test
    public void testUsernameNotNull() {
        System.out.println("Test 7: Testing username is not null");
        assertNotNull(testUser.username, "Username should not be null");
        System.out.println("✓ Username not null test passed");
    }
    
    @Test
    public void testUsernameNotEmpty() {
        System.out.println("Test 8: Testing username is not empty");
        assertFalse(testUser.username.isEmpty(), "Username should not be empty");
        System.out.println("✓ Username not empty test passed");
    }
    
    @Test
    public void testUsernameContainsUnderscore() {
        System.out.println("Test 9: Testing username contains underscore");
        assertTrue(testUser.username.contains("_"), "Username should contain underscore (_)");
        System.out.println("✓ Username contains underscore test passed");
    }
    
    @Test
    public void testUsernameMaxLength() {
        System.out.println("Test 10: Testing username max length (≤5 characters)");
        assertTrue(testUser.username.length() <= 5, "Username should be 5 characters or less");
        System.out.println("✓ Username max length test passed");
    }
    
    
    // PASSWORD TESTS
    
    
    @Test
    public void testPasswordNotNull() {
        System.out.println("Test 11: Testing password is not null");
        assertNotNull(testUser.password, "Password should not be null");
        System.out.println("✓ Password not null test passed");
    }
    
    @Test
    public void testPasswordNotEmpty() {
        System.out.println("Test 12: Testing password is not empty");
        assertFalse(testUser.password.isEmpty(), "Password should not be empty");
        System.out.println("✓ Password not empty test passed");
    }
    
    @Test
    public void testPasswordMinLength() {
        System.out.println("Test 13: Testing password minimum length (≥8 characters)");
        assertTrue(testUser.password.length() >= 8, "Password should be at least 8 characters");
        System.out.println("✓ Password min length test passed");
    }
    
    @Test
    public void testPasswordHasCapital() {
        System.out.println("Test 14: Testing password has capital letter");
        boolean hasCapital = testUser.password.matches(".*[A-Z].*");
        assertTrue(hasCapital, "Password should contain at least one capital letter");
        System.out.println("✓ Password has capital test passed");
    }
    
    @Test
    public void testPasswordHasNumber() {
        System.out.println("Test 15: Testing password has number");
        boolean hasNumber = testUser.password.matches(".*[0-9].*");
        assertTrue(hasNumber, "Password should contain at least one number");
        System.out.println("✓ Password has number test passed");
    }
    
    @Test
    public void testPasswordHasSpecial() {
        System.out.println("Test 16: Testing password has special character");
        boolean hasSpecial = testUser.password.matches(".*[!@#$%^&*()].*");
        assertTrue(hasSpecial, "Password should contain at least one special character");
        System.out.println("✓ Password has special character test passed");
    }
    
    
    // CELL NUMBER TESTS
    
    
    @Test
    public void testCellNumberNotNull() {
        System.out.println("Test 17: Testing cell number is not null");
        assertNotNull(testUser.cellNumber, "Cell number should not be null");
        System.out.println("✓ Cell number not null test passed");
    }
    
    @Test
    public void testCellNumberNotEmpty() {
        System.out.println("Test 18: Testing cell number is not empty");
        assertFalse(testUser.cellNumber.isEmpty(), "Cell number should not be empty");
        System.out.println("✓ Cell number not empty test passed");
    }
    
    @Test
    public void testCellNumberStartsWithPlus27() {
        System.out.println("Test 19: Testing cell number starts with +27");
        assertTrue(testUser.cellNumber.startsWith("+27"), "Cell number should start with +27");
        System.out.println("✓ Cell number starts with +27 test passed");
    }
    
    @Test
    public void testCellNumberHasCorrectLength() {
        System.out.println("Test 20: Testing cell number length");
        int totalLength = testUser.cellNumber.length();
        assertTrue(totalLength >= 12 && totalLength <= 13, 
                   "Cell number should be 12-13 characters total, but was " + totalLength);
        System.out.println("✓ Cell number length test passed");
    }
    
    @Test
    public void testCellNumberDigitsOnlyAfterPrefix() {
        System.out.println("Test 21: Testing cell number has only digits after +27");
        String numberPart = testUser.cellNumber.substring(3);
        boolean onlyDigits = numberPart.matches("\\d+");
        assertTrue(onlyDigits, "After +27 should only contain digits");
        System.out.println("✓ Cell number digits only test passed");
    }
    
    @Test
    public void testCellNumberDigitCount() {
        System.out.println("Test 22: Testing cell number has 9-10 digits after +27");
        String numberPart = testUser.cellNumber.substring(3);
        int digitCount = numberPart.length();
        assertTrue(digitCount >= 9 && digitCount <= 10, 
                   "Should have 9-10 digits, but has " + digitCount);
        System.out.println("✓ Cell number digit count test passed");
    }
    
    @Test
    public void testCellNumberFirstDigitValid() {
        System.out.println("Test 23: Testing cell number first digit (6,7,8,9)");
        String numberPart = testUser.cellNumber.substring(3);
        char firstDigit = numberPart.charAt(0);
        boolean isValidFirstDigit = (firstDigit == '6' || firstDigit == '7' || 
                                      firstDigit == '8' || firstDigit == '9');
        assertTrue(isValidFirstDigit, "First digit after +27 should be 6,7,8, or 9, but was " + firstDigit);
        System.out.println("✓ Cell number first digit test passed");
    }
    
    
    // ADDITIONAL TESTS
    
    
    @Test
    public void testMultipleUsersAreDifferent() {
        System.out.println("Test 24: Testing multiple users are different objects");
        
        User user1 = new User("Alice", "Brown", "al_ice", "Pass@123", "+27721234567");
        User user2 = new User("Bob", "White", "bo_b", "Pass@456", "+27821234567");
        
        assertNotSame(user1, user2, "Users should be different objects");
        assertNotEquals(user1.username, user2.username, "Usernames should be different");
        System.out.println("✓ Multiple users test passed");
    }
    
    @Test
    public void testUserFieldsAreMutable() {
        System.out.println("Test 25: Testing user fields can be changed");
        
        User user = new User("Original", "User", "ori_g", "Pass@123", "+27721234567");
        
        user.firstName = "Changed";
        user.lastName = "Name";
        user.username = "ch_ng";
        user.password = "New@Pass123";
        user.cellNumber = "+27987654321";
        
        assertEquals("Changed", user.firstName, "First name should be changed");
        assertEquals("Name", user.lastName, "Last name should be changed");
        assertEquals("ch_ng", user.username, "Username should be changed");
        assertEquals("New@Pass123", user.password, "Password should be changed");
        assertEquals("+27987654321", user.cellNumber, "Cell number should be changed");
        System.out.println("✓ Field mutability test passed");
    }
    
   
}