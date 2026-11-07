package hakimmokadimatsimelapartone;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

/** Part 3: dynamically populated Java arrays; IDs and hashes stay parallel to allMessages. */
public class MessageArchive {
    private Message[] allMessages = new Message[0];
    private Message[] sentMessages = new Message[0];
    private Message[] disregardedMessages = new Message[0];
    private Message[] storedMessages = new Message[0];
    private String[] messageIDs = new String[0];
    private String[] messageHashes = new String[0];

    private static Message[] append(Message[] array, Message message) {
        Message[] grown = Arrays.copyOf(array, array.length + 1);
        grown[array.length] = message;
        return grown;
    }
    public void add(Message message) {
        if (findByID(message.getMessageID()) != null) return;
        if (!Arrays.asList("SENT", "STORED", "DISCARDED").contains(message.getStatus()))
            throw new IllegalArgumentException("Complete the message action first.");
        allMessages = append(allMessages, message);
        rebuild();
    }
    private void rebuild() {
        int sent = 0, stored = 0, disregarded = 0;
        for (Message m : allMessages) {
            if (m.getStatus().equals("SENT")) sent++;
            if (m.getStatus().equals("STORED")) stored++;
            if (m.getStatus().equals("DISCARDED")) disregarded++;
        }
        sentMessages = new Message[sent]; storedMessages = new Message[stored];
        disregardedMessages = new Message[disregarded];
        messageIDs = new String[allMessages.length]; messageHashes = new String[allMessages.length];
        sent = 0; stored = 0; disregarded = 0;
        for (int i = 0; i < allMessages.length; i++) {
            Message m = allMessages[i];
            messageIDs[i] = m.getMessageID(); messageHashes[i] = m.createMessageHash();
            if (m.getStatus().equals("SENT")) sentMessages[sent++] = m;
            if (m.getStatus().equals("STORED")) storedMessages[stored++] = m;
            if (m.getStatus().equals("DISCARDED")) disregardedMessages[disregarded++] = m;
        }
    }
    public Message[] getSentMessages() { return sentMessages.clone(); }
    public Message[] getDisregardedMessages() { return disregardedMessages.clone(); }
    public Message[] getStoredMessages() { return storedMessages.clone(); }
    public String[] getMessageIDs() { return messageIDs.clone(); }
    public String[] getMessageHashes() { return messageHashes.clone(); }
    public Message findByID(String id) {
        for (int i = 0; i < messageIDs.length; i++)
            if (messageIDs[i].equals(id)) return allMessages[i];
        return null;
    }
    public Message[] findByRecipient(String recipient, boolean storedOnly) {
        Message[] result = new Message[0];
        String number = Message.normaliseRecipient(recipient);
        for (Message m : storedOnly ? storedMessages : allMessages)
            if (!m.getStatus().equals("DISCARDED") && m.getRecipient().equals(number)) result = append(result, m);
        return result;
    }
    public Message longest(boolean storedOnly) {
        Message longest = null;
        for (Message m : storedOnly ? storedMessages : allMessages)
            if (!m.getStatus().equals("DISCARDED") && (longest == null || m.getText().length() > longest.getText().length())) longest = m;
        return longest;
    }
    public String senderRecipientReport() {
        StringBuilder out = new StringBuilder();
        for (Message m : storedMessages) out.append("Sender: ").append(m.getSender()).append("\nRecipient: ").append(m.getRecipient()).append("\n\n");
        return out.length() == 0 ? "No stored messages." : out.toString();
    }
    public String report(boolean storedOnly) {
        return describe(storedOnly ? storedMessages : sentMessages);
    }
    public static String describe(Message[] messages) {
        if (messages.length == 0) return "No messages found.";
        StringBuilder out = new StringBuilder();
        for (Message m : messages) out.append(m.details()).append("\nSender: ").append(m.getSender())
            .append("\nStatus: ").append(m.getStatus()).append("\n\n");
        return out.toString();
    }
    public String deleteByHash(String hash) throws IOException {
        int found = -1, count = 0;
        for (int i = 0; i < messageHashes.length; i++) if (messageHashes[i].equalsIgnoreCase(hash)) { found = i; count++; }
        if (count == 0) return "Message not found.";
        if (count > 1) return "Hash matches multiple messages; no message deleted. Use a unique hash.";
        Message m = allMessages[found];
        // Delete disk bytes before changing arrays, so a failed deletion preserves state.
        if (m.getStoredPath() != null) Files.deleteIfExists(m.getStoredPath());
        Message[] remaining = new Message[allMessages.length - 1];
        System.arraycopy(allMessages, 0, remaining, 0, found);
        System.arraycopy(allMessages, found + 1, remaining, found, remaining.length - found);
        allMessages = remaining;
        Message.removeSent(m);
        rebuild();
        return "Message: \"" + m.getText() + "\" successfully deleted.";
    }
    /** Read Part 2 one-object-per-file JSON into storedMessages; skip invalid files visibly. */
    public String loadStored(Path directory) throws IOException {
        if (!Files.exists(directory)) return "No stored message files found.";
        int loaded = 0;
        StringBuilder warnings = new StringBuilder();
        try (java.util.stream.Stream<Path> paths = Files.list(directory)) {
            Path[] files = paths.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().toArray(Path[]::new);
            for (Path file : files) {
                try {
                    Message m = MessageJson.read(file);
                    if (findByID(m.getMessageID()) == null) { add(m); loaded++; }
                } catch (IOException | IllegalArgumentException ex) {
                    warnings.append("Skipped ").append(file.getFileName()).append(": ").append(ex.getMessage()).append('\n');
                }
            }
        }
        return "Loaded " + loaded + " stored messages.\n" + warnings;
    }
}
