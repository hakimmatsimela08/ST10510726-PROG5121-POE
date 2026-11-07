package hakimmokadimatsimelapartone;

import java.nio.file.*;
import java.util.*;

/** Brief fixtures live only in tests; production arrays are populated from input and JSON. */
public class PartThreeTests {
    private static int checks;
    private static void eq(Object expected, Object actual) {
        checks++;
        if (!Objects.deepEquals(expected, actual)) throw new AssertionError("Expected " + expected + "; got " + actual);
    }
    private static Message fixture(String id, int n, String recipient, String text, int action, Path dir) throws Exception {
        Message m = new Message(id, n, recipient, text); m.setSender("+27811111111");
        m.SentMessage(action, dir); if (action == 2) m.discardMessage(0); return m;
    }
    public static void runAll() throws Exception {
        checks = 0; Message.resetSession();
        Path dir = Files.createTempDirectory("quickchat-part3-");
        try {
            MessageArchive a = new MessageArchive();
            eq(null, a.longest(true)); eq("No messages found.", a.report(true));
            eq("Message not found.", a.deleteByHash("absent"));
            eq(0, a.findByRecipient("+27838884567", false).length);
            String longText = "Where are you? You are late! I have asked you to be on time.";
            Message one = fixture("0012345678", 0, "+27834557896", "Did you get the cake?", 1, dir);
            Message two = fixture("1123456789", 1, "+27838824567", longText, 3, dir);
            Message three = fixture("2234567890", 2, "+27834484567", "Yohoooo, I am at your gate.", 2, dir);
            Message four = fixture("3345678901", 3, "0838884567", "It is dinner time!", 1, dir);
            Message five = fixture("4456789012", 4, "+27838884567", "Ok, I am leaving without you.", 3, dir);
            for (Message m : new Message[]{one,two,three,four,five}) a.add(m);
            eq(new String[]{"Did you get the cake?", "It is dinner time!"}, Arrays.stream(a.getSentMessages()).map(Message::getText).toArray(String[]::new));
            eq(1, a.getDisregardedMessages().length); eq(2, a.getStoredMessages().length);
            eq(5, a.getMessageIDs().length); eq(5, a.getMessageHashes().length);
            for (int i=0;i<5;i++) eq(a.getMessageHashes()[i], a.findByID(a.getMessageIDs()[i]).createMessageHash());
            eq(longText, a.longest(true).getText()); eq(longText, a.longest(false).getText());
            eq("It is dinner time!", a.findByID(four.getMessageID()).getText());
            eq(null, a.findByID("missing"));
            eq("+27838884567", four.getRecipient());
            eq(new String[]{"It is dinner time!", "Ok, I am leaving without you."}, Arrays.stream(a.findByRecipient("0838884567",false)).map(Message::getText).toArray(String[]::new));
            eq(1, a.findByRecipient("+27838884567",true).length);
            eq(true, a.senderRecipientReport().contains("Sender: +27811111111"));
            eq(true, a.report(false).contains(one.createMessageHash()));
            eq(true, a.report(true).contains(longText)); eq(false, a.report(false).contains(longText));
            a.add(one); eq(5, a.getMessageIDs().length);
            String[] snapshot=a.getMessageIDs(); snapshot[0]="mutated"; eq(one, a.findByID(one.getMessageID()));
            MessageArchive loaded = new MessageArchive(); loaded.loadStored(dir);
            eq(2, loaded.getStoredMessages().length);
            eq(longText, loaded.findByID(two.getMessageID()).getText());
            eq("+27811111111",loaded.findByID(two.getMessageID()).getSender());
            loaded.loadStored(dir); eq(2, loaded.getStoredMessages().length);
            eq("Message: \""+longText+"\" successfully deleted.", loaded.deleteByHash(two.createMessageHash()));
            eq(null,loaded.findByID(two.getMessageID())); eq(false,Files.exists(two.getStoredPath()));
            eq(1,loaded.getMessageHashes().length);
            MessageArchive afterDelete=new MessageArchive();afterDelete.loadStored(dir);eq(1,afterDelete.getStoredMessages().length);
            // Round-trip escaping and Unicode, and backward-compatible files without sender.
            Message escape=fixture("5567890123",5,"+27834557896","Café \"quote\" \\ line\nnext\t😀",3,dir);
            eq(escape.getText(),MessageJson.read(escape.getStoredPath()).getText());
            String legacy=Files.readString(escape.getStoredPath()).replace("  \"sender\": \"+27811111111\",\n", "");
            Path old=dir.resolve("legacy.json");Files.writeString(old,legacy);
            eq("Unknown (Part 2 file)",MessageJson.read(old).getSender());
            Files.writeString(dir.resolve("broken.json"),"{invalid}");
            eq(true,afterDelete.loadStored(dir).contains("Skipped broken.json"));
            eq(2,afterDelete.getStoredMessages().length);
            // Hash collisions must never silently delete a different message.
            MessageArchive collision=new MessageArchive();
            collision.add(fixture("9912345678",9,"+27834557896","Same text",1,dir));
            collision.add(fixture("9912345679",9,"+27834557896","Same text",1,dir));
            eq(true,collision.deleteByHash("99:9:SAMETEXT").contains("multiple"));eq(2,collision.getSentMessages().length);
            eq("Message: \"Did you get the cake?\" successfully deleted.",a.deleteByHash(one.createMessageHash()));
            eq(1,a.getSentMessages().length); eq(4,a.getMessageIDs().length);
            // Brief's recipient-search expectation uses a different number for message 2.
            // Explicit variant tests that two stored messages for a shared recipient are returned.
            MessageArchive shared=new MessageArchive();
            shared.add(fixture("6678901234",6,"+27838884567",longText,3,dir)); shared.add(five);
            eq(new String[]{longText,five.getText()},Arrays.stream(shared.findByRecipient("+27838884567",true)).map(Message::getText).toArray(String[]::new));
        } finally {
            Message.resetSession();
            try (java.util.stream.Stream<Path> paths=Files.walk(dir)) {
                for(Path file:(Iterable<Path>)paths.sorted(Comparator.reverseOrder())::iterator)Files.delete(file);
            }
        }
        System.out.println("PASS: " + checks + " Part 3 assertions (arrays, fixtures, search, reports, JSON, deletion and reload).");
    }
    public static void main(String[] args) throws Exception { PartTwoTests.runAll(); runAll(); }
}
