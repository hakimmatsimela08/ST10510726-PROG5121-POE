 package hakimmokadimatsimelapartone;

import java.io.IOException;
import java.util.Scanner;
import java.nio.file.Paths;

/** Console-only QuickChat: registration, login and messaging through System.in/out. */
public class QuickChat {
    private final Scanner scanner;
    private QuickChat(Scanner scanner) { this.scanner = scanner; }

    /** Continue Part 1 with the same input and registered user after login. */
    static void start(Scanner scanner, Login login, String phone, boolean authenticated) {
        if (!authenticated) throw new IllegalStateException("Please log in first.");
        try { new QuickChat(scanner).run(login, phone); }
        catch (Cancelled ex) { System.out.println("QuickChat closed."); }
    }
    private static class Cancelled extends RuntimeException { }
    private String ask(String prompt) {
        System.out.println(prompt);
        String value = scanner.hasNextLine() ? scanner.nextLine() : null;
        if (value == null) throw new Cancelled();
        return value;
    }
    private void show(String text) {
        
        System.out.println(text);
    }
    private int number(String prompt, int min, int max) {
        while (true) {
            try { int n = Integer.parseInt(ask(prompt).trim()); if (n >= min && n <= max) return n; }
            catch (NumberFormatException ignored) { }
            show("Enter a whole number from " + min + " to " + max + ".");
        }
    }
    private void run(Login login, String phone) {
        show("Welcome to QuickChat.");
        int limit = number("How many messages do you wish to enter?", 0, 10000);
        int entered = 0;
        Message.resetSession();
        MessageArchive archive = new MessageArchive();
        try { show(archive.loadStored(Paths.get("stored_messages"))); }
        catch (IOException ex) { show("Could not load stored messages: " + ex.getMessage()); }
        while (true) {
            int option = number("1) Send Messages\n2) Show recently sent messages\n3) Quit\n4) Stored Messages", 1, 4);
            if (option == 3) { show("Total messages sent: " + new Message(0, phone, "Summary").returnTotalMessages()); return; }
            if (option == 4) { storedMenu(archive); continue; }
            if (option == 2) { show("Coming Soon."); continue; }
            if (entered == limit) { show("You have reached your message limit."); continue; }
            for (; entered < limit; entered++) {
                String recipient;
                do { recipient = ask("Recipient number (+27 followed by nine digits)");
                    if (!login.checkCellPhoneNumber(Message.normaliseRecipient(recipient))) show(new Message(entered, recipient, "Validation").checkRecipientCell());
                } while (!login.checkCellPhoneNumber(Message.normaliseRecipient(recipient)));
                Message message;
                do { message = new Message(entered, recipient, ask("Enter message (1-250 characters)"));
                    show(message.checkMessageLength());
                } while (!"Message ready to send.".equals(message.checkMessageLength()));
                message.setSender(phone);
                show("Message ID generated: " + message.getMessageID());
                boolean done = false;
                while (!done) {
                    int action = number("1) Send Message\n2) Disregard Message\n3) Store Message", 1, 3);
                    try {
                        show(message.SentMessage(action));
                        if (action == 2) {
                            int confirm = number("0) Delete message\n1) Go back", 0, 1);
                            if (confirm == 0) { show(message.discardMessage(0)); done = true; }
                        } else { if (action == 1) show(message.details()); done = true; }
                    } catch (IOException ex) { show("Could not store message: " + ex.getMessage() + ". Choose another action or retry."); }
                }
                archive.add(message);
            }
            show("Total messages sent: " + new Message(0, phone, "Summary").returnTotalMessages());
        }
    }
    /** Part 3 submenu; option 0 returns to the original main menu. */
    private void storedMenu(MessageArchive archive) {
        while (true) {
            int option = number("Stored Messages\n1) Display stored senders and recipients\n2) Longest stored message\n3) Search by message ID\n4) Stored messages for recipient\n5) Delete by hash\n6) Full stored message report\n7) Sent message report\n8) Sent or stored messages for recipient\n9) Longest sent or stored message\n10) Reload stored JSON\n0) Back", 0, 10);
            if (option == 0) return;
            try {
                switch (option) {
                    case 1: show(archive.senderRecipientReport()); break;
                    case 2: case 9:
                        Message longest = archive.longest(option == 2);
                        show(longest == null ? "No messages found." : MessageArchive.describe(new Message[]{longest})); break;
                    case 3:
                        Message found = archive.findByID(ask("Enter message ID"));
                        show(found == null ? "Message not found." : MessageArchive.describe(new Message[]{found})); break;
                    case 4: case 8:
                        show(MessageArchive.describe(archive.findByRecipient(ask("Enter recipient number"), option == 4))); break;
                    case 5: show(archive.deleteByHash(ask("Enter message hash"))); break;
                    case 6: show(archive.report(true)); break;
                    case 7: show(archive.report(false)); break;
                    case 10: show(archive.loadStored(Paths.get("stored_messages"))); break;
                    default: break;
                }
            } catch (IOException ex) { show("Stored message operation failed: " + ex.getMessage()); }
        }
    }
    public static void main(String[] args) {
        HakimMokadiMatsimelaPartOne.main(args);
    }
}
