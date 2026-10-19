 package hakimmokadimatsimelapartone;

import java.io.IOException;
import java.util.Scanner;

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
        while (true) {
            int option = number("1) Send Messages\n2) Show recently sent messages\n3) Quit", 1, 3);
            if (option == 3) { show("Total messages sent: " + new Message(0, phone, "Summary").returnTotalMessages()); return; }
            if (option == 2) { show("Coming Soon."); continue; }
            if (entered == limit) { show("You have reached your message limit."); continue; }
            for (; entered < limit; entered++) {
                String recipient;
                do { recipient = ask("Recipient number (+27 followed by nine digits)");
                    if (!login.checkCellPhoneNumber(recipient)) show(new Message(entered, recipient, "Validation").checkRecipientCell());
                } while (!login.checkCellPhoneNumber(recipient));
                Message message;
                do { message = new Message(entered, recipient, ask("Enter message (1-250 characters)"));
                    show(message.checkMessageLength());
                } while (!"Message ready to send.".equals(message.checkMessageLength()));
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
            }
            show("Total messages sent: " + new Message(0, phone, "Summary").returnTotalMessages());
        }
    }
    public static void main(String[] args) {
        HakimMokadiMatsimelaPartOne.main(args);
    }
}
