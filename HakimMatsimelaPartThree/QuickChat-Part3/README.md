# Hakim Matsimela — QuickChat Parts 1, 2 and 3 (console)

## Run on Windows
1. Extract the whole ZIP into a NEW folder.
2. Double-click **Run-QuickChat.bat**.
3. Register and log in using the Part 1 prompts.
4. Enter the message count. Use menu **1** for Part 2 messages or **4** for Part 3.

Requires Java 11 or newer. From a terminal: `java -jar dist/QuickChat-Part3.jar`.
In NetBeans, open the folder containing `pom.xml`, select Clean and Build, then Run Project.
Both launch classes begin with Part 1 and continue through the same Scanner to Parts 2/3.

## What is retained
- `HakimMokadiMatsimelaPartOne.java`: your existing Part 1 registration and login loops, with a continuation call after successful login.
- `Login.java`: unchanged from the supplied project.
- `QuickChat.java`: Part 2 message-count/for-loop, validation and send/disregard/store menu, plus Part 3 menu.
- `Message.java`: all existing Part 2 methods; extended with sender, status, restored-file metadata and local-number conversion.
- Original Part 1 tests and Part 2 regression tests remain included.

## Main menu
1. Send Messages — enter the assigned number of messages. Send records a simulated sent message; disregard asks for 0; store writes JSON.
2. Show recently sent messages — retains the Part 2 `Coming Soon.` response. Actual sent report is available under 4 -> 7.
3. Quit.
4. Stored Messages — Part 3 features below.

## Part 3 submenu
1. Display sender and recipient for every stored message.
2. Display the longest stored message and its full details.
3. Search message ID across the populated message arrays, showing recipient and text.
4. Search stored messages for a recipient.
5. Delete a message by hash, updating the arrays and deleting its JSON file if stored.
6. Full stored-message report (ID, hash, recipient, text, sender and status).
7. Full sent-message report.
8. Search sent or stored messages for a recipient.
9. Longest sent or stored message.
10. Reload stored JSON without duplicating IDs.
0. Return to the main menu.

## Arrays and storage
`MessageArchive.java` owns Java arrays for sent, disregarded and stored messages, plus parallel ID/hash arrays matching the all-message array. They grow from user actions and JSON loading. No example messages are inserted into the running app. Example data appears only in tests.

Stored messages are UTF-8 JSON files in `stored_messages/` relative to the working directory. The Windows launcher sets this to the project folder. Stored files are loaded after login and the message-count prompt, and can be reloaded from menu 4 -> 10. Each new file includes the sender's registered phone. Old Part 2 JSON is supported; a missing sender displays `Unknown (Part 2 file)` because the original file did not save it. To retain existing stored messages, copy your old `stored_messages` folder into this new project folder.

Only stored messages persist after restart. Sent/disregarded messages and registration credentials remain session-only, as in Parts 1/2. The count of sent messages excludes stored/disregarded messages. Deleting a sent message removes it from the active sent array/count. The message-entry limit is not reset by deletion.

Malformed JSON files are skipped with a visible filename/reason. Hash collisions are reported without deleting either message. JSON writing retains create-new semantics so existing files are not overwritten. This is a coursework simulation, not an SMS service.

## Brief inconsistencies handled explicitly
- Part 2 uses a 12-character example number despite saying ten characters. International SA format remains +27 followed by nine digits.
- Part 3 message 4 uses `0838884567`. Messaging now also accepts a valid ten-digit local SA number and converts it to +27. Part 1 registration rules remain unchanged.
- The message-ID test supplies `0838884567`, a recipient number. The test searches message 4 by its actual ID; recipient searches use the separate recipient-search methods.
- Message 2's fixture recipient is `+27838824567`; the recipient-search expectation instead groups it under `+27838884567`. Tests preserve the original fixtures and separately verify a variant with a shared recipient. Production searches match actual recipient data.
- Instructions request stored reports, while the test table also requests sent reports. Both are included under separately labelled submenu options.
- Whitespace before punctuation varies in the screenshots. Tests use the expected `It is dinner time!` wording; the app preserves the text users enter.

## Build and tests
JDK 11+ required to build. Windows: `build.bat`. Linux/macOS: `sh build.sh`.
The scripts compile all source, rebuild the three Part 3 JARs, and execute the dependency-free tests.
- `dist/QuickChat-Part3.jar`: complete executable app.
- `dist/QuickChat-Part3-tests.jar`: executable Part 1/2/3 regression suite.
- `dist/QuickChat-Part3-sources.jar`: complete Java source archive (not executable).

Additional console tests (Python 3 standard library):
```
python test_console.py
python test_part3_console.py
```
The first tests both entry points and Part 2 flows. The second tests the Part 3 submenu and storage across three application launches.

`mvn test` runs the included JUnit tests when Maven/dependencies are available. `.github/workflows/TestJava.yml` runs Maven, the standalone tests and both console test scripts. Place the CONTENTS of this project folder at your GitHub repository root. Hosted GitHub Actions and Maven/JUnit execution were not performed in this environment. No repository credentials or repository URL were provided.

## Source and assistance attribution
Part 3 code and tests were written with OpenAI assistance. Review and understand them before submission.
The JSON reader is a small parser specifically for the flat object schema produced by QuickChat; it is not a general-purpose JSON library. It handles quoted strings, escapes, Unicode and integer fields, rejecting unsupported/nested values. JSON encoding/decoding was implemented with reference to:
Bray, T. (2017) *The JavaScript Object Notation (JSON) Data Interchange Format*. RFC 8259, sections 4, 6, 7 and 8.1. Available at: https://www.rfc-editor.org/rfc/rfc8259 (Accessed: 26 October 2026).
