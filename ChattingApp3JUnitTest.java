/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */



/**
 *
 * @author iciko
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ChattingApp3JUnitTest {

    private MessageSystem messageSystem;

    @BeforeEach
    public void setUp() {
        messageSystem = new MessageSystem();
        messageSystem.messagesSent = 0;
        messageSystem.totalMessages = 2;
    }

    @Test
    public void testMessage1_HiMike_SuccessFlow() {
        String recipient = "+27718693002";
        String messageText = "Hi Mike, can you join us for dinner tonight?";
        String mockID = "0012345678";

        String cellResult = MessageSystem.checkRecipientCell(recipient);
        assertEquals("Cell phone number successfully captured.", cellResult);
     
        String lengthResult = MessageSystem.checkMessageLength(messageText);
        assertEquals("Message ready to send.", lengthResult);
  
        String actualHash = MessageSystem.createMessageHash(mockID, 0, messageText);
        assertEquals("00:0:HITONIGHT", actualHash);

        String actionResult = messageSystem.handleUserSelection(1);
        assertEquals("Message successfully sent.", actionResult);
        assertEquals(1, messageSystem.messagesSent, "Total sent counter should increment by 1.");
    }

    @Test
    public void testMessage2_HiKeegan_Flow() {
        String recipient = "08575975889";
        String messageText = "Hi Keegan, did you receive the payment?";
        String mockID = "0098765432";

        String cellResult = MessageSystem.checkRecipientCell(recipient);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", cellResult);

        String lengthResult = MessageSystem.checkMessageLength(messageText);
        assertEquals("Message ready to send.", lengthResult);

        String actionResult = messageSystem.handleUserSelection(2);
        assertEquals("Message discarded.", actionResult);
        assertEquals(0, messageSystem.messagesSent, "Sent counter should remain 0 upon selecting discard.");
    }
   @Test
    public void testCheckMessageLength_Failure() {
        String longMessage = "A".repeat(265);
        int expectedOverflow = 265 - 250;
        String expectedMessage = "Message exceeds 250 characters by " + expectedOverflow + "; please reduce the size.";

        String actualResult = MessageSystem.checkMessageLength(longMessage);
        assertEquals(expectedMessage, actualResult);
    }

    @Test
    public void testHandleUserSelection_StoreMessage() {
        String actionResult = messageSystem.handleUserSelection(3);
        assertEquals("Message successfully stored.", actionResult);
    }

    @Test
    public void testDisplayMessageIDStatus() {
        String testID = "44123987";
        String expected = "Message ID generated: 44123987";
        assertEquals(expected, MessageSystem.displayMessageIDStatus(testID));
    }

    @Test
    public void testRemainderOfMessageHashes_InLoop() {
        HashTestData[] loopData = {
            new HashTestData("00123456", 0, "Hi Mike, can you join us for dinner tonight?", "00:0:HITONIGHT"),
            new HashTestData("00987654", 1, "Hi Keegan, did you receive the payment?",      "00:1:HIPAYMENT")
        };

        for (HashTestData data : loopData) {
            String generated = MessageSystem.createMessageHash(data.id, data.index, data.text);
            assertEquals(data.expectedHash, generated, "Hash mismatch detected inside automated loop sequence.");
        }
    }
    private static class HashTestData {
        String id;
        int index;
        String text;
        String expectedHash;

        HashTestData(String id, int index, String text, String expectedHash) {
            this.id = id;
            this.index = index;
            this.text = text;
            this.expectedHash = expectedHash;
        }
    }

    public static class MessageSystem {

        public int messagesSent;
        public int totalMessages;

        public MessageSystem() {}

        
        public static String checkRecipientCell(String recipient) {
            if (recipient != null && recipient.startsWith("+")) {
                return "Cell phone number successfully captured.";
            }
            return "Cell phone number is incorrectly formatted or does not contain an international code. "
                 + "Please correct the number and try again.";
        }

        
        public static String checkMessageLength(String messageText) {
            if (messageText == null || messageText.length() <= 250) {
                return "Message ready to send.";
            }
            int overflow = messageText.length() - 250;
            return "Message exceeds 250 characters by " + overflow + "; please reduce the size.";
        }

       
        public static String createMessageHash(String id, int index, String text) {
            String prefix = id.substring(0, 2).toUpperCase();

            String[] words = text.trim().split("\\s+");

            String firstWord = words[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
            String lastWord  = words[words.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();

            return prefix + ":" + index + ":" + firstWord + lastWord;
        }

                public static String displayMessageIDStatus(String messageID) {
            return "Message ID generated: " + messageID;
        }

        
        public String handleUserSelection(int selection) {
            switch (selection) {
                case 1 -> {
                    messagesSent++;
                    return "Message successfully sent.";
                }
                case 2 -> {
                    return "Message discarded.";
                }
                case 3 -> {
                    return "Message successfully stored.";
                }
                default -> {
                    return "Invalid selection.";
                }
            }
        }
    }
}