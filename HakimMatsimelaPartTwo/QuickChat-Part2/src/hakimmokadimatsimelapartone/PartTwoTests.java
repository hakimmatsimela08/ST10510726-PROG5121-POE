package hakimmokadimatsimelapartone;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Dependency-free regression runner; also called by the JUnit test bridge. */
public class PartTwoTests {
    private static int checks;
    private static void eq(Object expected, Object actual) {
        checks++;
        if (!Objects.equals(expected, actual)) throw new AssertionError("Expected " + expected + "; got " + actual);
    }
    public static void runAll() throws Exception {
        checks = 0;
        Message.resetSession();
        Login login = new Login();
        eq(false, login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        eq(true, login.checkUserName("kyl_1"));
        eq(false, login.checkUserName("kyle!!!!!!!"));
        eq(false, login.checkUserName(null));
        eq(true, login.checkPasswordComplexity("Ch&&sec@ke99!"));
        eq(false, login.checkPasswordComplexity("password"));
        eq(true, login.checkCellPhoneNumber("+27838968976"));
        eq(false, login.checkCellPhoneNumber("08966553"));
        eq("The user has been registered successfully.", login.registerUser("Hakim", "Matsimela", "kyl_1", "Ch&&sec@ke99!", "+27838968976"));
        eq(true, login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        eq(false, login.loginUser("kyl_1", "wrong"));
        eq("Welcome Hakim Matsimela, it is great to see you again.", login.returnLoginStatus(true));
        eq("Username or password incorrect, please try again.", login.returnLoginStatus(false));
        Message first = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        eq(true, first.checkMessageID());
        eq("00:0:HITONIGHT", first.createMessageHash());
        eq("Message ready to send.", first.checkMessageLength());
        eq("Cell phone number successfully captured.", first.checkRecipientCell());
        eq(false, new Message("12345678901", 0, "+27718693002", "Hi").checkMessageID());
        eq(false, new Message("abcdef1234", 0, "+27718693002", "Hi").checkMessageID());
        eq("Message ready to send.", new Message(0, "+27718693002", "a".repeat(250)).checkMessageLength());
        eq("Message exceeds 250 characters by 1; please reduce the size.", new Message(0, "+27718693002", "a".repeat(251)).checkMessageLength());
        eq("Please enter a message.", new Message(0, "+27718693002", "  ").checkMessageLength());
        Message second = new Message(1, "08575975889", "Hi Keegan, did you receive the payment?");
        eq(false, second.isRecipientValid());
        eq("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", second.checkRecipientCell());
        eq("Message successfully sent.", first.SentMessage(1));
        eq(1, first.returnTotalMessages());
        eq("Message already processed.", first.SentMessage(1));
        eq(1, first.returnTotalMessages());
        eq("Press 0 to delete the message.", second.SentMessage(2));
        eq("Message left unchanged.", second.discardMessage(1));
        eq("Message discarded.", second.discardMessage(0));
        eq(1, second.returnTotalMessages());
        eq(true, first.printMessages().contains(first.details()));
        eq(false, first.printMessages().contains("Keegan"));
        for (int i = 0; i < 12; i++) {
            Message m = new Message("1234567890", i, "+27718693002", "  Hello   person " + i + "!  ");
            eq("12:" + i + ":HELLO" + i, m.createMessageHash());
        }
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            Message m = new Message(i, "+27718693002", "Hi");
            eq(true, m.checkMessageID()); eq(true, ids.add(m.getMessageID()));
        }
        Path dir = Files.createTempDirectory("quickchat-test-");
        try {
            Message stored = new Message("9876543210", 2, "+27718693002", "Hello \"Mike\"\npath \\ end\t");
            eq("Message successfully stored.", stored.SentMessage(3, dir));
            String json = Files.readString(dir.resolve("message-9876543210.json"), StandardCharsets.UTF_8);
            eq(true, json.contains("\\\"Mike\\\""));
            eq(true, json.contains("\\u000a"));
            eq(true, json.contains("\\\\ end\\u0009"));
            eq(true, json.contains("\"status\": \"STORED\""));
            eq(1, stored.returnTotalMessages());
            eq("Message already processed.", stored.SentMessage(1));
            boolean rejected = false;
            try { new Message(3, "08575975889", "Invalid").SentMessage(1); }
            catch (IllegalArgumentException expected) { rejected = true; }
            eq(true, rejected);
            eq(1, stored.returnTotalMessages());
            Path file = dir.resolve("not-a-folder"); Files.writeString(file, "block");
            Message retry = new Message(3, "+27718693002", "Retry save");
            boolean failed = false;
            try { retry.SentMessage(3, file); } catch (java.io.IOException expected) { failed = true; }
            eq(true, failed);
            eq("Message successfully sent.", retry.SentMessage(1));
            eq(2, retry.returnTotalMessages());
        } finally {
            try (java.util.stream.Stream<Path> paths = Files.walk(dir)) {
                for (Path path : (Iterable<Path>) paths.sorted(Comparator.reverseOrder())::iterator) Files.delete(path);
            }
        }
        Message.resetSession();
        System.out.println("PASS: " + checks + " assertions (Part 1 login + Part 2 messaging).");
    }
    public static void main(String[] args) throws Exception { runAll(); }
}
