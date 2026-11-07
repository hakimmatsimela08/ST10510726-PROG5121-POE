package hakimmokadimatsimelapartone;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Flat JSON-object reader for QuickChat's Part 2/3 schema, written with OpenAI assistance.
 * Reference: Bray (2017), RFC 8259, sections 4, 6, 7 and 8.1.
 * https://www.rfc-editor.org/rfc/rfc8259
 * Supports escaped strings and integer values; rejects unsupported/nested values.
 */
final class MessageJson {
    private final String text;
    private int pos;
    private MessageJson(String text) { this.text = text; }
    private void whitespace() { while (pos < text.length() && " \t\r\n".indexOf(text.charAt(pos)) >= 0) pos++; }
    private char take() throws IOException {
        if (pos >= text.length()) throw new IOException("Unexpected end of JSON.");
        return text.charAt(pos++);
    }
    private void expect(char c) throws IOException { whitespace(); if (take() != c) throw new IOException("Expected " + c); }
    private String string() throws IOException {
        expect('"'); StringBuilder out = new StringBuilder();
        while (true) {
            char c = take();
            if (c == '"') return out.toString();
            if (c < 32) throw new IOException("Unescaped control character.");
            if (c != '\\') { out.append(c); continue; }
            c = take();
            switch (c) {
                case '"': case '\\': case '/': out.append(c); break;
                case 'b': out.append('\b'); break;
                case 'f': out.append('\f'); break;
                case 'n': out.append('\n'); break;
                case 'r': out.append('\r'); break;
                case 't': out.append('\t'); break;
                case 'u':
                    if (pos + 4 > text.length()) throw new IOException("Incomplete Unicode escape.");
                    String hex = text.substring(pos, pos + 4);
                    if (!hex.matches("[0-9a-fA-F]{4}")) throw new IOException("Invalid Unicode escape.");
                    out.append((char)Integer.parseInt(hex, 16)); pos += 4; break;
                default: throw new IOException("Invalid escape.");
            }
        }
    }
    private Map<String,String> object() throws IOException {
        Map<String,String> fields = new HashMap<>(); expect('{'); whitespace();
        if (pos < text.length() && text.charAt(pos) == '}') { pos++; return fields; }
        while (true) {
            String key = string(); expect(':'); whitespace();
            String value;
            if (pos < text.length() && text.charAt(pos) == '"') value = string();
            else {
                int start = pos;
                while (pos < text.length() && "-0123456789".indexOf(text.charAt(pos)) >= 0) pos++;
                value = text.substring(start, pos);
                if (!value.matches("-?(0|[1-9][0-9]*)")) throw new IOException("Expected string or integer.");
            }
            if (fields.put(key, value) != null) throw new IOException("Duplicate key: " + key);
            whitespace(); char c = take();
            if (c == '}') break;
            if (c != ',') throw new IOException("Expected comma.");
        }
        whitespace(); if (pos != text.length()) throw new IOException("Trailing JSON data.");
        return fields;
    }
    private static String required(Map<String,String> map, String key) throws IOException {
        String value = map.get(key); if (value == null) throw new IOException("Missing " + key); return value;
    }
    static Message read(Path file) throws IOException {
        Map<String,String> f = new MessageJson(Files.readString(file)).object();
        try {
            String id = required(f, "messageID");
            int number = Integer.parseInt(required(f, "messageNumber"));
            Message m = new Message(id, number, required(f, "recipient"), required(f, "message"));
            if (!m.checkMessageID() || number < 0 || !m.isRecipientValid() || !m.checkMessageLength().equals("Message ready to send."))
                throw new IOException("Invalid message fields.");
            if (!"STORED".equals(required(f, "status"))) throw new IOException("Expected STORED status.");
            if (!m.createMessageHash().equals(required(f, "messageHash"))) throw new IOException("Message hash does not match.");
            m.setSender(f.getOrDefault("sender", "Unknown (Part 2 file)"));
            m.restoreStored(file); return m;
        } catch (NumberFormatException ex) { throw new IOException("Invalid message number.", ex); }
    }
}
