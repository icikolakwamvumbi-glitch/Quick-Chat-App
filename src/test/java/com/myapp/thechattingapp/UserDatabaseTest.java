/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.myapp.thechattingapp;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author iciko
 */
public class UserDatabaseTest {
    
    private User testUser;
    private User anotherUser;
    private User thirdUser;
    
    @BeforeEach
    public void setUp() {
        System.out.println("Setting up before each test...");
        
        // Create test users
        testUser = new User("John", "Doe", "te_st", "Test@123", "+27721234567");
        anotherUser = new User("Jane", "Smith", "ja_ne", "Pass@456", "+27821234567");
        thirdUser = new User("Bob", "Johnson", "bo_b", "Secure@789", "+27921234567");
        
        // Clear database before each test
        UserDatabase.users.clear();
        
        // Delete existing test file
        File file = new File(UserDatabase.FILE_NAME);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("Deleted existing file: " + deleted);
        }
        
        System.out.println("Setup complete. Database size: " + UserDatabase.users.size());
    }
    
    @AfterEach
    public void tearDown() {
        System.out.println("Cleaning up after test...");
        
        // Clean up test file
        File file = new File(UserDatabase.FILE_NAME);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("Deleted file after test: " + deleted);
        }
        
        // Clear database
        UserDatabase.users.clear();
        
        System.out.println("Cleanup complete.\n");
    }
    
    
    // LOAD USERS TESTS
    
    
    @Test
    public void testLoadUsersWhenFileDoesNotExist() {
        System.out.println("Test 1: Loading users when file doesn't exist");
        
        // Ensure file doesn't exist
        File file = new File(UserDatabase.FILE_NAME);
        if (file.exists()) {
            assertTrue(file.delete(), "Should be able to delete existing file");
        }
        assertFalse(file.exists(), "File should not exist before test");
        
        // Load users - should create empty HashMap without error
        try {
            UserDatabase.loadUsers();
        } catch (Exception e) {
            fail("loadUsers() should not throw exception when file doesn't exist: " + e.getMessage());
        }
        
        // Verify database is empty but not null
        assertNotNull(UserDatabase.users, "Users map should not be null");
        assertTrue(UserDatabase.users.isEmpty(), "Users map should be empty when no file exists");
        
        System.out.println("✓ Load users with no file test passed");
    }
    
    @Test
    public void testLoadUsersWhenFileExists() {
        System.out.println("Test 2: Loading users when file exists");
        
        // First save a user to create file
        UserDatabase.saveUser(testUser);
        
        // Force a save to ensure file is written
        UserDatabase.saveUsers();
        
        // Verify file was created
        File file = new File(UserDatabase.FILE_NAME);
        assertTrue(file.exists(), "File should exist after saving");
        assertTrue(file.length() > 0, "File should not be empty");
        
        // Clear the in-memory map to simulate fresh start
        UserDatabase.users.clear();
        assertTrue(UserDatabase.users.isEmpty(), "Map should be empty before loading");
        
        // Now load users from file
        try {
            UserDatabase.loadUsers();
        } catch (Exception e) {
            fail("loadUsers() should not throw exception when file exists: " + e.getMessage());
        }
        
        // Verify user was loaded
        assertFalse(UserDatabase.users.isEmpty(), "Users map should not be empty after loading");
        assertTrue(UserDatabase.userExists("te_st"), "User should exist after loading");
        
        // Verify data integrity
        User loadedUser = UserDatabase.getUser("te_st");
        assertNotNull(loadedUser, "Loaded user should not be null");
        assertEquals("John", loadedUser.firstName, "First name should match");
        assertEquals("Doe", loadedUser.lastName, "Last name should match");
        assertEquals("Test@123", loadedUser.password, "Password should match");
        assertEquals("+27721234567", loadedUser.cellNumber, "Cell number should match");
        
        System.out.println("✓ Load users with existing file test passed");
    }
    
   
    // SAVE USERS TESTS
    // 
    
    @Test
    public void testSaveUsers() {
        System.out.println("Test 3: Saving users to file");
        
        // Add a user to database
        try {
            UserDatabase.saveUser(testUser);
        } catch (Exception e) {
            fail("saveUser() should not throw exception: " + e.getMessage());
        }
        
        // Force a save to ensure file is written
        UserDatabase.saveUsers();
        
        // Check if file was created
        File file = new File(UserDatabase.FILE_NAME);
        assertTrue(file.exists(), "Users.dat file should be created");
        assertTrue(file.length() > 0, "File should not be empty");
        
        // Verify user is still in memory
        assertTrue(UserDatabase.userExists("te_st"), "User should exist in memory");
        
        System.out.println("✓ Save users test passed");
    }
    
    @Test
    public void testSaveUsersWithMultipleUsers() {
        System.out.println("Test 4: Saving multiple users to file");
        
        // Save multiple users
        UserDatabase.saveUser(testUser);
        UserDatabase.saveUser(anotherUser);
        UserDatabase.saveUser(thirdUser);
        
        // Force a save to ensure file is written
        UserDatabase.saveUsers();
        
        // Verify file exists
        File file = new File(UserDatabase.FILE_NAME);
        assertTrue(file.exists(), "File should be created");
        assertTrue(file.length() > 0, "File should not be empty");
        
        // Verify all users exist
        assertEquals(3, UserDatabase.users.size(), "Should have 3 users in database");
        assertTrue(UserDatabase.userExists("te_st"), "First user should exist");
        assertTrue(UserDatabase.userExists("ja_ne"), "Second user should exist");
        assertTrue(UserDatabase.userExists("bo_b"), "Third user should exist");
        
        System.out.println("✓ Save multiple users test passed");
    }
    
    @Test
    public void testSaveUsersWithEmptyDatabase() {
        System.out.println("Test 4b: Saving users with empty database");
        
        // Don't add any users - database is empty
        assertEquals(0, UserDatabase.users.size(), "Database should be empty");
        
        // Save empty database
        try {
            UserDatabase.saveUsers();
        } catch (Exception e) {
            fail("saveUsers() should not throw exception when database is empty: " + e.getMessage());
        }
        
        // File should still be created (empty or not)
        File file = new File(UserDatabase.FILE_NAME);
        // File may or may not exist depending on implementation
        // This test just ensures no exception is thrown
        
        System.out.println("✓ Save empty database test passed");
    }
    
    
    // USER EXISTS TESTS
   
    
    @Test
    public void testUserExistsForExistingUser() {
        System.out.println("Test 5: User exists for existing user");
        
        // Save a user
        UserDatabase.saveUser(testUser);
        
        // Check if user exists
        boolean exists = UserDatabase.userExists("te_st");
        assertTrue(exists, "User should exist after being saved");
        
        System.out.println("✓ User exists for existing user test passed");
    }
    
    @Test
    public void testUserExistsForNonExistingUser() {
        System.out.println("Test 6: User exists for non-existing user");
        
        // Check for user that doesn't exist
        boolean exists = UserDatabase.userExists("nonexistent");
        assertFalse(exists, "User should not exist");
        
        System.out.println("✓ User exists for non-existing user test passed");
    }
    
    @Test
    public void testUserExistsWithEmptyUsername() {
        System.out.println("Test 7: User exists with empty username");
        
        boolean exists = UserDatabase.userExists("");
        assertFalse(exists, "User with empty username should not exist");
        
        System.out.println("✓ User exists with empty username test passed");
    }
    
    @Test
    public void testUserExistsWithNullUsername() {
        System.out.println("Test 8: User exists with null username");
        
        boolean exists = UserDatabase.userExists(null);
        assertFalse(exists, "User with null username should not exist");
        
        System.out.println("✓ User exists with null username test passed");
    }
    
    
    // SAVE USER TESTS
    
    
    @Test
    public void testSaveUser() {
        System.out.println("Test 9: Saving a single user");
        
        // Save user
        UserDatabase.saveUser(testUser);
        
        // Verify user was added to map
        assertEquals(1, UserDatabase.users.size(), "Map should have 1 user");
        assertTrue(UserDatabase.users.containsKey("te_st"), "Map should contain the username");
        
        // Verify user data is correct
        User retrieved = UserDatabase.users.get("te_st");
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("John", retrieved.firstName);
        assertEquals("Doe", retrieved.lastName);
        
        System.out.println("✓ Save user test passed");
    }
    
    @Test
    public void testSaveUserWithNullUser() {
        System.out.println("Test 10: Saving null user");
        
        // Save null user - should handle gracefully
        try {
            UserDatabase.saveUser(null);
        } catch (NullPointerException e) {
            // This is acceptable - some implementations throw NPE
            System.out.println("saveUser(null) threw NPE - acceptable behavior");
        } catch (Exception e) {
            fail("saveUser(null) threw unexpected exception: " + e.getClass().getSimpleName());
        }
        
        // Database should still be empty
        assertEquals(0, UserDatabase.users.size(), "Database should remain empty after saving null");
        
        System.out.println("✓ Save null user test passed");
    }
    
    @Test
    public void testSaveUserWithDuplicateUsername() {
        System.out.println("Test 11: Saving user with duplicate username (overwrite)");
        
        // Save first user
        UserDatabase.saveUser(testUser);
        
        // Create another user with SAME username but different data
        User duplicateUser = new User("Johnny", "Smith", "te_st", "NewPass@123", "+27729876543");
        UserDatabase.saveUser(duplicateUser);
        
        // Should still have only 1 user
        assertEquals(1, UserDatabase.users.size(), "Map should still have 1 user");
        
        // Verify the user was overwritten with new data
        User retrieved = UserDatabase.getUser("te_st");
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("Johnny", retrieved.firstName, "First name should be updated");
        assertEquals("Smith", retrieved.lastName, "Last name should be updated");
        assertEquals("NewPass@123", retrieved.password, "Password should be updated");
        assertEquals("+27729876543", retrieved.cellNumber, "Cell number should be updated");
        
        System.out.println("✓ Save duplicate user (overwrite) test passed");
    }
    
   
    // GET USER TESTS
    
    
    @Test
    public void testGetUserForExistingUser() {
        System.out.println("Test 12: Getting existing user");
        
        // Save user
        UserDatabase.saveUser(testUser);
        
        // Retrieve user
        User retrieved = UserDatabase.getUser("te_st");
        
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("John", retrieved.firstName);
        assertEquals("Doe", retrieved.lastName);
        assertEquals("Test@123", retrieved.password);
        assertEquals("+27721234567", retrieved.cellNumber);
        
        System.out.println("✓ Get existing user test passed");
    }
    
    @Test
    public void testGetUserForNonExistingUser() {
        System.out.println("Test 13: Getting non-existing user");
        
        User retrieved = UserDatabase.getUser("nonexistent");
        assertNull(retrieved, "Retrieved user should be null for non-existing user");
        
        System.out.println("✓ Get non-existing user test passed");
    }
    
    @Test
    public void testGetUserWithEmptyUsername() {
        System.out.println("Test 14: Getting user with empty username");
        
        User retrieved = UserDatabase.getUser("");
        assertNull(retrieved, "Getting user with empty username should return null");
        
        System.out.println("✓ Get user with empty username test passed");
    }
    
    @Test
    public void testGetUserWithNullUsername() {
        System.out.println("Test 15: Getting user with null username");
        
        User retrieved = UserDatabase.getUser(null);
        assertNull(retrieved, "Getting user with null username should return null");
        
        System.out.println("✓ Get user with null username test passed");
    }
    
    
    // UPDATE USER TESTS
    
    
    @Test
    public void testUpdateUser() {
        System.out.println("Test 16: Updating existing user");
        
        // Save original user
        UserDatabase.saveUser(testUser);
        
        // Create updated user with same username
        User updatedUser = new User("Johnny", "Smith", "te_st", "NewPass@123", "+27729876543");
        UserDatabase.updateUser(updatedUser);
        
        // Retrieve and verify
        User retrieved = UserDatabase.getUser("te_st");
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("Johnny", retrieved.firstName, "First name should be updated");
        assertEquals("Smith", retrieved.lastName, "Last name should be updated");
        assertEquals("NewPass@123", retrieved.password, "Password should be updated");
        assertEquals("+27729876543", retrieved.cellNumber, "Cell number should be updated");
        
        System.out.println("✓ Update user test passed");
    }
    
    @Test
    public void testUpdateUserThatDoesNotExist() {
        System.out.println("Test 17: Updating user that doesn't exist");
        
        // Update user that wasn't saved first
        try {
            UserDatabase.updateUser(testUser);
        } catch (Exception e) {
            fail("updateUser() should not throw exception for non-existent user: " + e.getMessage());
        }
        
        // User should now exist because updateUser saves if doesn't exist
        assertTrue(UserDatabase.userExists("te_st"), "User should be created if it doesn't exist");
        
        // Verify data is correct
        User retrieved = UserDatabase.getUser("te_st");
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("John", retrieved.firstName);
        assertEquals("Doe", retrieved.lastName);
        
        System.out.println("✓ Update non-existing user test passed (user gets created)");
    }
    
    @Test
    public void testUpdateUserWithNullUser() {
        System.out.println("Test 18: Updating null user");
        
        // Update null user - should handle gracefully
        try {
            UserDatabase.updateUser(null);
        } catch (NullPointerException e) {
            // This is acceptable
            System.out.println("updateUser(null) threw NPE - acceptable behavior");
        } catch (Exception e) {
            fail("updateUser(null) threw unexpected exception: " + e.getClass().getSimpleName());
        }
        
        System.out.println("✓ Update null user test passed");
    }
    
    @Test
    public void testMultipleUpdates() {
        System.out.println("Test 19: Multiple updates on same user");
        
        // Save original
        UserDatabase.saveUser(testUser);
        
        // First update
        User updatedUser1 = new User("Johnny", "Smith", "te_st", "Pass1@123", "+27721111111");
        UserDatabase.updateUser(updatedUser1);
        
        // Verify first update
        User retrieved1 = UserDatabase.getUser("te_st");
        assertNotNull(retrieved1);
        assertEquals("Johnny", retrieved1.firstName, "First update: first name");
        assertEquals("Smith", retrieved1.lastName, "First update: last name");
        
        // Second update
        User updatedUser2 = new User("Jonathan", "Brown", "te_st", "Pass2@456", "+27722222222");
        UserDatabase.updateUser(updatedUser2);
        
        // Verify second update
        User retrieved2 = UserDatabase.getUser("te_st");
        assertNotNull(retrieved2);
        assertEquals("Jonathan", retrieved2.firstName, "Second update: first name should reflect last update");
        assertEquals("Brown", retrieved2.lastName, "Second update: last name should reflect last update");
        assertEquals("Pass2@456", retrieved2.password, "Password should reflect last update");
        assertEquals("+27722222222", retrieved2.cellNumber, "Cell number should reflect last update");
        
        System.out.println("✓ Multiple updates test passed");
    }
    
    
    // PERSISTENCE TESTS (Save and Load)
    
    
    @Test
    public void testDataPersistenceAfterSaveAndLoad() {
        System.out.println("Test 20: Data persistence after save and load");
        
        // Save a user
        UserDatabase.saveUser(testUser);
        UserDatabase.saveUsers(); // Force save to file
        
        // Verify file exists
        File file = new File(UserDatabase.FILE_NAME);
        assertTrue(file.exists(), "File should exist after save");
        
        // Clear memory
        UserDatabase.users.clear();
        assertTrue(UserDatabase.users.isEmpty(), "Memory should be empty");
        
        // Load from file
        UserDatabase.loadUsers();
        
        // Verify user was restored
        assertFalse(UserDatabase.users.isEmpty(), "Users should be restored from file");
        assertTrue(UserDatabase.userExists("te_st"), "User should exist after load");
        
        User retrieved = UserDatabase.getUser("te_st");
        assertNotNull(retrieved, "Retrieved user should not be null");
        assertEquals("John", retrieved.firstName);
        assertEquals("Doe", retrieved.lastName);
        assertEquals("Test@123", retrieved.password);
        assertEquals("+27721234567", retrieved.cellNumber);
        
        System.out.println("✓ Data persistence test passed");
    }
    
    @Test
    public void testMultipleUsersPersistence() {
        System.out.println("Test 21: Multiple users persistence");
        
        // Save multiple users
        UserDatabase.saveUser(testUser);
        UserDatabase.saveUser(anotherUser);
        UserDatabase.saveUser(thirdUser);
        UserDatabase.saveUsers(); // Force save to file
        
        // Clear memory
        UserDatabase.users.clear();
        assertEquals(0, UserDatabase.users.size(), "Memory should be empty after clear");
        
        // Load from file
        UserDatabase.loadUsers();
        
        // Verify all users exist
        assertEquals(3, UserDatabase.users.size(), "All three users should be restored");
        assertTrue(UserDatabase.userExists("te_st"), "First user should exist");
        assertTrue(UserDatabase.userExists("ja_ne"), "Second user should exist");
        assertTrue(UserDatabase.userExists("bo_b"), "Third user should exist");
        
        // Verify data integrity
        User retrieved1 = UserDatabase.getUser("te_st");
        User retrieved2 = UserDatabase.getUser("ja_ne");
        User retrieved3 = UserDatabase.getUser("bo_b");
        
        assertNotNull(retrieved1, "First user should not be null");
        assertNotNull(retrieved2, "Second user should not be null");
        assertNotNull(retrieved3, "Third user should not be null");
        
        assertEquals("John", retrieved1.firstName);
        assertEquals("Jane", retrieved2.firstName);
        assertEquals("Bob", retrieved3.firstName);
        
        System.out.println("✓ Multiple users persistence test passed");
    }
    
    
    // DATABASE SIZE TESTS
    
    
    @Test
    public void testDatabaseSizeAfterAddingUsers() {
        System.out.println("Test 22: Database size after adding users");
        
        assertEquals(0, UserDatabase.users.size(), "Database should start empty");
        
        UserDatabase.saveUser(testUser);
        assertEquals(1, UserDatabase.users.size(), "Database should have 1 user after first save");
        
        UserDatabase.saveUser(anotherUser);
        assertEquals(2, UserDatabase.users.size(), "Database should have 2 users after second save");
        
        UserDatabase.saveUser(thirdUser);
        assertEquals(3, UserDatabase.users.size(), "Database should have 3 users after third save");
        
        System.out.println("✓ Database size test passed");
    }
    
    @Test
    public void testDatabaseSizeAfterDeletingFile() {
        System.out.println("Test 23: Database size after file deletion and reload");
        
        // Add users
        UserDatabase.saveUser(testUser);
        UserDatabase.saveUser(anotherUser);
        UserDatabase.saveUsers(); // Force save to file
        assertEquals(2, UserDatabase.users.size(), "Should have 2 users in memory");
        
        // Delete file
        File file = new File(UserDatabase.FILE_NAME);
        assertTrue(file.exists(), "File should exist before deletion");
        boolean deleted = file.delete();
        assertTrue(deleted, "Should be able to delete file");
        
        // Clear memory and reload
        UserDatabase.users.clear();
        UserDatabase.loadUsers();
        
        // Should be empty since file was deleted
        assertEquals(0, UserDatabase.users.size(), "Database should be empty after loading deleted file");
        
        System.out.println("✓ Database size after deletion test passed");
    }
    
    
    // EDGE CASE TESTS
    
    
    @Test
    public void testUserWithSpecialCharacters() {
        System.out.println("Test 24: User with special characters in fields");
        
        User specialUser = new User("John-O'Neil", "McDonald-Smith", "j_o", "Test@123!", "+27721234567");
        
        try {
            UserDatabase.saveUser(specialUser);
        } catch (Exception e) {
            fail("Saving user with special characters should not throw exception: " + e.getMessage());
        }
        
        assertTrue(UserDatabase.userExists("j_o"), "User with special characters should exist");
        
        User retrieved = UserDatabase.getUser("j_o");
        assertNotNull(retrieved);
        assertEquals("John-O'Neil", retrieved.firstName, "First name with special chars should match");
        assertEquals("McDonald-Smith", retrieved.lastName, "Last name with special chars should match");
        
        System.out.println("✓ Special characters test passed");
    }
    
    @Test
    public void testVeryLongCellNumber() {
        System.out.println("Test 25: Very long cell number handling");
        
        User longCellUser = new User("Test", "User", "te_st2", "Test@123", "+27721234567890");
        
        try {
            UserDatabase.saveUser(longCellUser);
        } catch (Exception e) {
            fail("Saving user with long cell number should not throw exception: " + e.getMessage());
        }
        
        // Verify it saved (even if validation might reject it, the database should handle it)
        assertTrue(UserDatabase.userExists("te_st2"), "User should be saved regardless of validation");
        
        System.out.println("✓ Long cell number test passed");
    }
    
    
    // ADDITIONAL BOUNDARY TESTS FOR 100%
    
    
    @Test
    public void testSaveAndLoadWithEmptyDatabase() {
        System.out.println("Test 26: Save and load with empty database");
        
        // Ensure database is empty
        UserDatabase.users.clear();
        assertEquals(0, UserDatabase.users.size(), "Database should be empty");
        
        // Save empty database
        try {
            UserDatabase.saveUsers();
        } catch (Exception e) {
            fail("saveUsers() with empty database should not throw exception: " + e.getMessage());
        }
        
        // Load back
        UserDatabase.users.clear();
        UserDatabase.loadUsers();
        
        // Should still be empty
        assertEquals(0, UserDatabase.users.size(), "Database should remain empty after load");
        
        System.out.println("✓ Save and load with empty database test passed");
    }
    
    @Test
    public void testConsecutiveSaveAndLoad() {
        System.out.println("Test 27: Consecutive save and load operations");
        
        // First save
        UserDatabase.saveUser(testUser);
        UserDatabase.saveUsers();
        
        // Load and verify
        UserDatabase.users.clear();
        UserDatabase.loadUsers();
        assertTrue(UserDatabase.userExists("te_st"), "First user should exist");
        
        // Add second user and save again
        UserDatabase.saveUser(anotherUser);
        UserDatabase.saveUsers();
        
        // Load and verify both
        UserDatabase.users.clear();
        UserDatabase.loadUsers();
        assertTrue(UserDatabase.userExists("te_st"), "First user should still exist");
        assertTrue(UserDatabase.userExists("ja_ne"), "Second user should exist");
        
        // Add third user
        UserDatabase.saveUser(thirdUser);
        UserDatabase.saveUsers();
        
        // Load and verify all three
        UserDatabase.users.clear();
        UserDatabase.loadUsers();
        assertEquals(3, UserDatabase.users.size(), "All three users should exist");
        
        System.out.println("✓ Consecutive save and load test passed");
    }
}