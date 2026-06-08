/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */


/**
 *
 * @author iciko
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class Poepart3JUnitTest {

    private ArrayList<Message> mockStoredMessages;
    private ArrayList<String> sentMessagesArray;
    private ArrayList<String> disregardedMessagesArray;
    private ArrayList<String> messageIDsArray;
    private ArrayList<String> messageHashesArray;

    //  Message class 
    private static class Message {
        String messageID;
        String recipient;
        String messageText;
        String messageHash;

        public Message(String messageID, String recipient, String messageText, String messageHash) {
            this.messageID   = messageID;
            this.recipient   = recipient;
            this.messageText = messageText;
            this.messageHash = messageHash;
        }
    }

    //  Test data setup
    @BeforeEach
    public void setUp() {
        mockStoredMessages      = new ArrayList<>();
        sentMessagesArray       = new ArrayList<>();
        disregardedMessagesArray = new ArrayList<>();
        messageIDsArray         = new ArrayList<>();
        messageHashesArray      = new ArrayList<>();

        // Message 1 — sent 
        Message m1 = new Message("1000000001", "+27834557896",
                "Did you get the cake?", "20:0:DIDCAKE");
        sentMessagesArray.add(m1.messageText);
        messageIDsArray.add(m1.messageID);
        messageHashesArray.add(m1.messageHash);

        // Message 2 — stored
        Message m2 = new Message("1000000002", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", "20:1:WHERETIME");
        mockStoredMessages.add(m2);
        messageIDsArray.add(m2.messageID);
        messageHashesArray.add(m2.messageHash);

        // Message 3 — disregarded 
        Message m3 = new Message("1000000003", "+27834484567",
                "Yohoooo, I am at your gate.", "20:2:YOHOOOO GATE.");
        disregardedMessagesArray.add(m3.messageText);
        messageIDsArray.add(m3.messageID);
        messageHashesArray.add(m3.messageHash);

        // Message 4 — sent AND stored
        Message m4 = new Message("0838884567", "+27838884567",
                "It is dinner time !", "08:3:IT!");
        sentMessagesArray.add(m4.messageText);
        mockStoredMessages.add(m4);
        messageIDsArray.add(m4.messageID);
        messageHashesArray.add(m4.messageHash);

        // Message 5 — stored
        Message m5 = new Message("1000000005", "+27838884567",
                "Ok, I am leaving without you.", "10:4:OKYOU.");
        mockStoredMessages.add(m5);
        messageIDsArray.add(m5.messageID);
        messageHashesArray.add(m5.messageHash);
    }

   
    
    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        assertEquals(2, sentMessagesArray.size());
        assertEquals("Did you get the cake?",  sentMessagesArray.get(0));
        assertEquals("It is dinner time !",    sentMessagesArray.get(1));
    }

    
    @Test
    public void testDisplayLongestMessage() {
        assertFalse(mockStoredMessages.isEmpty(), "Stored messages should not be empty");

        Message longest = mockStoredMessages.get(0);
        for (Message msg : mockStoredMessages) {
            if (msg.messageText.length() > longest.messageText.length()) {
                longest = msg;
            }
        }

        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            longest.messageText
        );
    }

    
    @Test
    public void testSearchForMessageID() {
        String targetID = "0838884567";
        int index = messageIDsArray.indexOf(targetID);

        assertNotEquals(-1, index, "Message ID should exist in tracking records");

        Message foundMessage = null;
        for (Message msg : mockStoredMessages) {
            if (msg.messageID.equals(targetID)) {
                foundMessage = msg;
                break;
            }
        }

        assertNotNull(foundMessage, "Message with target ID should be in storedMessages");
        assertEquals("It is dinner time !", foundMessage.messageText);
    }

    
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void testSearchAllMessagesRegardingParticularRecipient() {
        String targetRecipient = "+27838884567";
        ArrayList<String> matchingTextResults = new ArrayList<>();

        for (Message msg : mockStoredMessages) {
            if (msg.recipient.equals(targetRecipient)) {
                matchingTextResults.add(msg.messageText);
            }
        }

        assertEquals(3, matchingTextResults.size(),
                "Expected 3 messages for recipient " + targetRecipient);
        assertTrue(matchingTextResults.contains(
                "Where are you? You are late! I have asked you to be on time."));
        assertTrue(matchingTextResults.contains("It is dinner time !"));
        assertTrue(matchingTextResults.contains("Ok, I am leaving without you."));
    }

    
    @Test
    public void testDeleteMessageUsingMessageHash() {
        String targetHash = "20:1:WHERETIME";
        int initialSize = mockStoredMessages.size();

        int indexToRemove = -1;
        for (int i = 0; i < mockStoredMessages.size(); i++) {
            if (mockStoredMessages.get(i).messageHash.equals(targetHash)) {
                indexToRemove = i;
                break;
            }
        }

        assertNotEquals(-1, indexToRemove, "Hash should be found in stored messages");

        Message removedMessage = mockStoredMessages.remove(indexToRemove);

        assertEquals(initialSize - 1, mockStoredMessages.size());
        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            removedMessage.messageText
        );
    }

    
    @Test
    public void testDisplayReport() {
        assertFalse(messageHashesArray.isEmpty());
        assertFalse(messageIDsArray.isEmpty());
        assertEquals(5, messageIDsArray.size());

        for (int i = 0; i < messageIDsArray.size(); i++) {
            assertNotNull(messageIDsArray.get(i),   "ID at index "   + i + " should not be null");
            assertNotNull(messageHashesArray.get(i), "Hash at index " + i + " should not be null");
        }
    }
}