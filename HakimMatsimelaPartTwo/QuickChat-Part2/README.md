# ST10510726 — PROG5121 POE Part 2: QuickChat (Console Only)

Built from the supplied Part 1 project for Hakim Matsimela. The original package and Login.java are retained. The original entry-point class now launches the integrated Part 2 application. The original LoginTest.java is retained under test/.

## Run on Windows

1. Extract this ZIP completely.
2. Install Java 11 or newer if Java is not installed.
3. Double-click Run-QuickChat.bat, or run:

   java -jar dist/QuickChat-Part2.jar

All prompts and output use the terminal with Scanner and System.out. No Swing, AWT or graphical dialogs are used. End-of-input closes the application. Registration and login are required each time you launch it; credentials are kept only in memory. This coursework implementation does not send SMS or internet messages. 'Send' records a sent message for this session.

To launch directly from a terminal (no extra flags required):

   java -jar dist/QuickChat-Part2.jar

## Included JAR files

- dist/QuickChat-Part2.jar: runnable integrated registration, login and messaging application.
- dist/QuickChat-Part2-tests.jar: runnable regression tests, no additional libraries required.
- dist/QuickChat-Part2-sources.jar: Java source archive; open/extract it to view the code. This JAR is not executable.

Run the tests:

   java -jar dist/QuickChat-Part2-tests.jar

## Using QuickChat

Choose the total number of messages to enter immediately after successful login. Choose menu 1 to enter that many messages using a for loop. Each completed send, store or discard uses one slot. Message numbers start at zero and increment per completed message. Sent totals include only sent messages, not stored or discarded messages. Re-entering menu 1 after the limit displays a limit warning. Menu 2 deliberately displays 'Coming Soon.' as instructed in Part 2. Menu 3 quits.

For each message, enter an international South African recipient number and 1–250 characters. Choose send, disregard (confirm with 0), or store. Sent-message details appear in order: ID, hash, recipient, message. Stored messages are saved individually as valid UTF-8 JSON in stored_messages/ under the working directory. Run-QuickChat.bat sets that directory to the project directory. Existing JSON files are not overwritten. Stored messages remain on disk after exit; Part 2 does not reload them or implement sending stored messages.

## Brief inconsistencies resolved

- The brief says recipient numbers should not exceed ten characters but supplies +27718693002, which has twelve characters. This project follows that example and the Part 1 validation: +27 plus exactly nine digits. 08575975889 is rejected in the application. It is retained as the invalid/discard unit-test input.
- The example hash is 00:0:HITONIGHT, although its text ends in 'tonight?'. Hashes remove punctuation from the first and last words and uppercase them. A fixed 0012345678 ID is injected only in the example test. Real messages receive random ten-digit IDs, including possible leading zeroes; IDs are checked for uniqueness within the session.
- Exactly 250 characters are accepted, following 'should not exceed 250'. The detailed excess-character error follows the unit-test table.

## Build or open in NetBeans

Requires a JDK 11 or newer. Open this directory as a Maven project in NetBeans (pom.xml). The old machine-specific NetBeans private files and stale compiled classes are not reused.

Windows: build.bat
Linux/macOS: sh build.sh

These scripts compile sources, create all three JARs and run the standalone tests using JDK compiler/JAR modules. No external libraries are required for the application or standalone tests.

To run the original JUnit login tests and the JUnit bridge for the Part 2 suite, use Maven with internet access:

   mvn test

## GitHub Actions

.github/workflows/TestJava.yml is supplied. Upload the CONTENTS of QuickChat-Part2 as the repository root, including the hidden .github folder. The workflow runs Maven/JUnit tests and the standalone build on push and pull requests. No repository was supplied or accessed, and the workflow has not been executed on GitHub. Signing up for a student developer pack is an account task you must do yourself.

## Validation performed

See TEST-RESULTS.txt. The application was tested directly in its console interface. Maven/JUnit dependency execution was not performed here; the dependency-free regression JAR and application console flows were executed successfully.

## Assistance attribution

Part 2 implementation and tests were generated with OpenAI assistance and should be reviewed and understood before submission. Original Login.java and LoginTest.java came from the supplied Part 1 archive. JSON string escaping is implemented locally without third-party JARs; the source includes an RFC 8259 section 7 reference. No fabricated research or GitHub execution evidence is included.
