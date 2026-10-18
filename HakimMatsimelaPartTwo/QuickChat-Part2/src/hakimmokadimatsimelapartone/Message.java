package hakimmokadimatsimelapartone;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

/** Part 2 message validation, hashing, session totals and JSON storage. */
public class Message {
    private static final Set<String> IDS = new HashSet<>();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> SENT = new ArrayList<>();
    private final String id, recipient, text;
    private final int number;
    private String state = "NEW";
    private Path storedPath;

    public Message(int number, String recipient, String text) {
        this(generateID(), number, recipient, text);
    }
    // Explicit ID supports reproducible tests; the application always generates IDs.
    public Message(String id, int number, String recipient, String text) {
        this.id = Objects.requireNonNull(id);
        this.number = number;
        this.recipient = recipient;
        this.text = text;
    }
    private static synchronized String generateID() {
        String id;
        do { id = String.format(Locale.ROOT, "%010d", Math.floorMod(RANDOM.nextLong(), 10000000000L)); }
        while (!IDS.add(id));
        return id;
    }
    public String getMessageID() { return id; }
    public String getText() { return text; }
    public boolean checkMessageID() { return id.matches("[0-9]{10}"); }
    public boolean isRecipientValid() { return new Login().checkCellPhoneNumber(recipient); }
    public String checkRecipientCell() {
        return isRecipientValid() ? "Cell phone number successfully captured."
            : "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }
    public String checkMessageLength() {
        if (text == null || text.trim().isEmpty()) return "Please enter a message.";
        return text.length() <= 250 ? "Message ready to send."
            : "Message exceeds 250 characters by " + (text.length() - 250) + "; please reduce the size.";
    }
    public String createMessageHash() {
        String[] words = text == null ? new String[]{""} : text.trim().split("\\s+");
        // Remove punctuation so 'tonight?' produces TONIGHT as in the brief.
        String first = words[0].replaceAll("[^\\p{L}\\p{N}]", "");
        String last = words[words.length - 1].replaceAll("[^\\p{L}\\p{N}]", "");
        return (id.substring(0, 2) + ":" + number + ":" + first + last).toUpperCase(Locale.ROOT);
    }
    private void validate() {
        if (!checkMessageID()) throw new IllegalArgumentException("Invalid message ID.");
        if (!isRecipientValid()) throw new IllegalArgumentException(checkRecipientCell());
        if (!"Message ready to send.".equals(checkMessageLength())) throw new IllegalArgumentException(checkMessageLength());
    }
    public String SentMessage() throws IOException {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("1) Send Message\n2) Disregard Message\n3) Store Message");
            if (!input.hasNextLine()) return "Message left unchanged.";
            try {
                int choice = Integer.parseInt(input.nextLine().trim());
                if (choice >= 1 && choice <= 3) return SentMessage(choice);
            } catch (NumberFormatException ignored) { }
            System.out.println("Choose 1, 2 or 3.");
        }
    }

    public String SentMessage(int choice) throws IOException { return SentMessage(choice, Paths.get("stored_messages")); }
    public String SentMessage(int choice, Path directory) throws IOException {
        if (!state.equals("NEW")) return "Message already processed.";
        switch (choice) {
            case 1: validate(); SENT.add(this); state = "SENT"; return "Message successfully sent.";
            case 2: return "Press 0 to delete the message.";
            case 3: storeMessage(directory); return "Message successfully stored.";
            default: throw new IllegalArgumentException("Choose 1, 2 or 3.");
        }
    }
    public String discardMessage(int confirmation) {
        if (confirmation != 0) return "Message left unchanged.";
        if (!state.equals("NEW")) return "Message already processed.";
        state = "DISCARDED";
        return "Message discarded.";
    }
    public Path storeMessage() throws IOException { return storeMessage(Paths.get("stored_messages")); }
    public Path storeMessage(Path directory) throws IOException {
        if (storedPath != null) return storedPath;
        if (!state.equals("NEW")) throw new IllegalStateException("Message already processed.");
        validate();
        Files.createDirectories(directory);
        Path target = directory.resolve("message-" + id + ".json");
        String json = "{\n  \"messageID\": " + quote(id) + ",\n  \"messageNumber\": " + number
            + ",\n  \"messageHash\": " + quote(createMessageHash()) + ",\n  \"recipient\": " + quote(recipient)
            + ",\n  \"message\": " + quote(text) + ",\n  \"status\": \"STORED\"\n}\n";
        Files.write(target, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        storedPath = target; state = "STORED";
        return target;
    }
    // JSON escaping written with OpenAI assistance. Implements JSON string control-character escaping.
    // Specification reference: RFC 8259 section 7 (https://www.rfc-editor.org/rfc/rfc8259#section-7).
    private static String quote(String value) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            if (c == '"' || c == '\\') out.append('\\').append(c);
            else if (c < 32) out.append(String.format(Locale.ROOT, "\\u%04x", (int)c));
            else out.append(c);
        }
        return out.append('"').toString();
    }
    public String details() {
        return "Message ID: " + id + "\nMessage Hash: " + createMessageHash()
            + "\nRecipient: " + recipient + "\nMessage: " + text;
    }
    public String printMessages() {
        StringBuilder out = new StringBuilder();
        for (Message message : SENT) out.append(message.details()).append("\n\n");
        return out.toString();
    }
    public int returnTotalMessages() { return SENT.size(); }
    static void resetSession() { SENT.clear(); }
}
