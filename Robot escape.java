
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Robot {
    private String name;
    private int stars;
    private int codeDigit;

    public Robot(String name) {
        this.name = name;
        stars = 0;
        codeDigit = 0;
    }

    public String getName() {
        return name;
    }

    public int getStars() {
        return stars;
    }

    public int getCodeDigit() {
        return codeDigit;
    }

    public void completeRoom(int digit, int earnedStars) {
        codeDigit = digit;
        stars += earnedStars;
    }
}

class EscapeRoom {
    private int roomNumber;
    private String roomName;
    private int timeRemaining;
    private boolean completed;

    public EscapeRoom(int roomNumber, String roomName) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        timeRemaining = 60;
        completed = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void decreaseTime() {
        if (timeRemaining > 0) {
            timeRemaining--;
        }
    }

    public void reset() {
        timeRemaining = 60;
        completed = false;
    }

    public void complete() {
        completed = true;
    }

    public int calculateStars() {
        if (timeRemaining >= 40) {
            return 3;
        } else if (timeRemaining >= 20) {
            return 2;
        } else {
            return 1;
        }
    }
}

public class RobotEscapeGame extends JFrame {

    private Robot player1;
    private Robot player2;

    private EscapeRoom[] rooms;
    private EscapeRoom currentRoom;

    private int roomIndex = 0;
    private int currentPlayer = 1;
    private int charge = 0;
    private int sequenceIndex = 0;
    private int[] sequence = new int[5];

    private int correctAnswer1;
    private int correctAnswer2;

    private int totalTime = 300;
    private int totalStars = 0;
    private int attempts = 3;

    private boolean gravityFlipped = false;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private boolean sequenceVisible = false;

    private long roomStartTime;

    private JLabel titleLabel;
    private JLabel timerLabel;
    private JLabel roomTimerLabel;
    private JLabel chargeLabel;
    private JLabel playerLabel;
    private JLabel doorLabel;
    private JLabel scoreLabel;

    private JTextArea display;
    private JPanel buttonPanel;
    private JPanel energyPanel;
    private JProgressBar energyBar;

    private JTextField codeInput;

    private javax.swing.Timer gameTimer;

    private final Color DARK = new Color(20, 25, 40);
    private final Color PANEL = new Color(35, 43, 62);
    private final Color BLUE = new Color(65, 150, 255);
    private final Color GREEN = new Color(45, 190, 120);
    private final Color RED = new Color(220, 65, 75);
    private final Color YELLOW = new Color(245, 190, 65);

    public RobotEscapeGame() {

        player1 = new Robot("ROBOT ALPHA");
        player2 = new Robot("ROBOT BETA");

        rooms = new EscapeRoom[] {
            new EscapeRoom(1, "COLOR GRID"),
            new EscapeRoom(2, "MEMORY CIRCUIT"),
            new EscapeRoom(3, "SECURITY TERMINAL")
        };

        setTitle("THE LAST ESCAPE");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(DARK);
        setLayout(new BorderLayout(10, 10));

        createInterface();
        showOpeningScreen();

        setVisible(true);
    }

    private void createInterface() {

        titleLabel = new JLabel("THE LAST ESCAPE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5));

        add(titleLabel, BorderLayout.NORTH);

        display = new JTextArea();
        display.setEditable(false);
        display.setLineWrap(true);
        display.setWrapStyleWord(true);
        display.setBackground(PANEL);
        display.setForeground(Color.WHITE);
        display.setFont(new Font("Monospaced", Font.PLAIN, 16));
        display.setMargin(new Insets(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(display);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(DARK);

        JPanel statusPanel = new JPanel(new GridLayout(2, 3, 8, 5));
        statusPanel.setBackground(DARK);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        timerLabel = createLabel("TOTAL TIME: 300", YELLOW);
        roomTimerLabel = createLabel("ROOM TIME: 60", Color.WHITE);
        chargeLabel = createLabel("ENERGY: 0%", GREEN);
        playerLabel = createLabel("PLAYER: NONE", BLUE);
        doorLabel = createLabel("DOOR: RED / LOCKED", RED);
        scoreLabel = createLabel("STARS: 0", YELLOW);

        statusPanel.add(timerLabel);
        statusPanel.add(roomTimerLabel);
        statusPanel.add(chargeLabel);
        statusPanel.add(playerLabel);
        statusPanel.add(doorLabel);
        statusPanel.add(scoreLabel);

        bottomPanel.add(statusPanel, BorderLayout.NORTH);

        energyBar = new JProgressBar(0, 100);
        energyBar.setStringPainted(true);
        energyBar.setString("DOOR ENERGY");
        energyBar.setForeground(GREEN);
        energyBar.setBackground(PANEL);
        energyBar.setBorderPainted(false);

        JPanel energyContainer = new JPanel(new BorderLayout());
        energyContainer.setBackground(DARK);
        energyContainer.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        energyContainer.add(energyBar);

        bottomPanel.add(energyContainer, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(DARK);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, Color color) {

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.BOLD, 13));

        return label;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void addButton(String text, Color color, ActionListener action) {

        JButton button = createButton(text, color);
        button.addActionListener(action);
        buttonPanel.add(button);
    }

    private void updateStatus() {

        timerLabel.setText("TOTAL TIME: " + totalTime);

        if (currentRoom != null) {
            roomTimerLabel.setText(
                "ROOM TIME: " + currentRoom.getTimeRemaining()
            );
        }

        chargeLabel.setText("ENERGY: " + charge + "%");

        if (currentPlayer == 1) {
            playerLabel.setText("PLAYER: ALPHA");
        } else {
            playerLabel.setText("PLAYER: BETA");
        }

        scoreLabel.setText("STARS: " + totalStars);

        energyBar.setValue(charge);
        energyBar.setString(charge + "%");
    }

    private void showOpeningScreen() {

        buttonPanel.removeAll();

        display.setText(
            "FACTORY SYSTEM ALERT\n\n" +
            "Two robots have been scheduled for scrapping at midnight.\n\n" +
            "You are trapped inside an automated factory.\n" +
            "Your only chance is to escape before the clock runs out.\n\n" +
            "YOUR MISSION:\n" +
            "Complete three escape rooms.\n" +
            "Collect one code digit from each room.\n" +
            "Use all three digits to unlock the final exit.\n\n" +
            "PLAYER 1: ROBOT ALPHA\n" +
            "PLAYER 2: ROBOT BETA\n\n" +
            "GAME RULES:\n" +
            "- Work together to complete every room.\n" +
            "- Each room has a 60-second time limit.\n" +
            "- The entire game has a 5-minute time limit.\n" +
            "- Failed rooms must be restarted.\n" +
            "- Earn up to three stars in each room.\n\n" +
            "Can you escape before midnight?"
        );

        addButton("START GAME", GREEN, e -> startGame());

        updateInterface();
    }

    private void startGame() {

        if (gameStarted) {
            return;
        }

        gameStarted = true;

        gameTimer = new javax.swing.Timer(1000, e -> {

            if (gameOver) {
                return;
            }

            totalTime--;

            if (currentRoom != null && !currentRoom.isCompleted()) {
                currentRoom.decreaseTime();
            }

            updateStatus();

            if (totalTime <= 0) {
                endGame(false);
            } else if (currentRoom != null &&
                    currentRoom.getTimeRemaining() <= 0) {
                restartRoom();
            }
        });

        gameTimer.start();

        startRoom();
    }

    private void startRoom() {

        if (gameOver) {
            return;
        }

        currentRoom = rooms[roomIndex];
        currentRoom.reset();

        roomStartTime = System.currentTimeMillis();

        charge = 0;
        currentPlayer = 1;
        gravityFlipped = false;
        sequenceIndex = 0;

        showRoomInstructions();
    }

    private void showRoomInstructions() {

        buttonPanel.removeAll();

        updateStatus();

        doorLabel.setText("DOOR: RED / LOCKED");
        doorLabel.setForeground(RED);

        if (roomIndex == 0) {

            display.setText(
                "ROOM 1: COLOR GRID\n\n" +
                "The factory floor is glitching!\n" +
                "Both robots must charge the door lock.\n\n" +
                "ROBOT ALPHA COLORS:\n" +
                "BLUE and YELLOW\n\n" +
                "ROBOT BETA COLORS:\n" +
                "GREEN and PURPLE\n\n" +
                "DECOY COLORS:\n" +
                "RED and ORANGE\n\n" +
                "CORRECT COLOR: +25% ENERGY\n" +
                "WRONG ROBOT COLOR: -20% ENERGY\n" +
                "DECOY COLOR: -15% ENERGY\n\n" +
                "Reach 100% energy to unlock the door.\n" +
                "WARNING: Gravity can reverse your selections!"
            );

        } else if (roomIndex == 1) {

            display.setText(
                "ROOM 2: MEMORY CIRCUIT\n\n" +
                "The factory memory system has been corrupted.\n\n" +
                "A sequence of five numbers will appear.\n" +
                "Remember the sequence before it disappears.\n\n" +
                "ROBOT ALPHA enters the first two numbers.\n" +
                "ROBOT BETA enters the last three numbers.\n\n" +
                "Every number must be correct to unlock the door."
            );

        } else {

            display.setText(
                "ROOM 3: SECURITY TERMINAL\n\n" +
                "The final security system blocks the exit.\n\n" +
                "ROBOT ALPHA must solve a multiplication problem.\n" +
                "ROBOT BETA must solve an addition problem.\n\n" +
                "Both answers must be correct to unlock the door.\n\n" +
                "This is your final challenge!"
            );
        }

        addButton("ENTER ROOM", GREEN, e -> beginChallenge());

        updateInterface();
    }

    private void beginChallenge() {

        roomStartTime = System.currentTimeMillis();

        if (roomIndex == 0) {
            colorGridRoom();
        } else if (roomIndex == 1) {
            memoryRoom();
        } else {
            securityRoom();
        }
    }

    private void colorGridRoom() {

        buttonPanel.removeAll();

        display.setText(
            "ROOM 1: COLOR GRID\n\n" +
            "Charge the door by selecting your robot's colors.\n" +
            "Work together to reach 100%!"
        );

        String[] colors = {
            "BLUE", "GREEN", "YELLOW",
            "PURPLE", "RED", "ORANGE"
        };

        Color[] colorValues = {
            new Color(45, 110, 230),
            new Color(40, 160, 90),
            new Color(235, 190, 35),
            new Color(140, 75, 200),
            new Color(210, 55, 55),
            new Color(230, 125, 35)
        };

        for (int i = 0; i < colors.length; i++) {

            final int choice = i;

            addButton(colors[i], colorValues[i], e -> {

                if (gameOver || currentRoom.isCompleted()) {
                    return;
                }

                processColor(colors[choice], choice);
            });
        }

        updateStatus();
        updateInterface();
    }

    private void processColor(String color, int choice) {

        if (gravityFlipped) {
            choice = 5 - choice;
            color = new String[] {
                "BLUE", "GREEN", "YELLOW",
                "PURPLE", "RED", "ORANGE"
            }[choice];
        }

        boolean alphaColor =
            color.equals("BLUE") || color.equals("YELLOW");

        boolean betaColor =
            color.equals("GREEN") || color.equals("PURPLE");

        if (color.equals("RED") || color.equals("ORANGE")) {

            charge -= 15;
            display.append("\nDECOY TILE! -15% ENERGY");

        } else if ((currentPlayer == 1 && alphaColor) ||
                   (currentPlayer == 2 && betaColor)) {

            charge += 25;
            display.append("\nCORRECT COLOR! +25% ENERGY");

        } else {

            charge -= 20;
            display.append("\nWRONG ROBOT COLOR! -20% ENERGY");
        }

        charge = Math.max(0, Math.min(100, charge));

        if (charge >= 100) {
            completeRoom();
            return;
        }

        if (Math.random() < 0.25) {

            gravityFlipped = !gravityFlipped;

            display.append(
                gravityFlipped
                ? "\nGRAVITY SHIFT! CONTROLS REVERSED!"
                : "\nGRAVITY RESTORED!"
            );
        }

        currentPlayer = currentPlayer == 1 ? 2 : 1;

        display.append("\n\nNext turn: " +
            (currentPlayer == 1 ? "ROBOT ALPHA" : "ROBOT BETA"));

        updateStatus();
    }

    private void memoryRoom() {

        buttonPanel.removeAll();

        display.setText("MEMORY CIRCUIT ACTIVATED!\n\nMemorize the sequence.");

        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = (int) (Math.random() * 4) + 1;
        }

        sequenceVisible = true;

        StringBuilder numbers = new StringBuilder();

        for (int number : sequence) {
            numbers.append(number).append("   ");
        }

        display.append("\n\nSEQUENCE:\n" + numbers);

        updateInterface();

        javax.swing.Timer hideTimer = new javax.swing.Timer(4000, e -> {

            ((javax.swing.Timer) e.getSource()).stop();

            if (gameOver || roomIndex != 1) {
                return;
            }

            sequenceVisible = false;

            display.setText(
                "MEMORY SEQUENCE HIDDEN!\n\n" +
                "ROBOT ALPHA: Enter the first two numbers.\n" +
                "ROBOT BETA: Enter the last three numbers."
            );

            showMemoryButtons();
        });

        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void showMemoryButtons() {

        buttonPanel.removeAll();

        for (int i = 1; i <= 4; i++) {

            final int number = i;

            addButton(String.valueOf(i), BLUE, e -> {

                if (gameOver || currentRoom.isCompleted()) {
                    return;
                }

                if (number != sequence[sequenceIndex]) {

                    display.append("\n\nINCORRECT NUMBER!");
                    restartRoom();
                    return;
                }

                display.append("\nCorrect: " + number);

                sequenceIndex++;

                if (sequenceIndex == 2) {
                    currentPlayer = 2;

                    display.append(
                        "\n\nROBOT ALPHA COMPLETED ITS PART!\n" +
                        "ROBOT BETA, YOUR TURN!"
                    );

                } else if (sequenceIndex == 5) {

                    completeRoom();
                    return;
                }

                updateStatus();
            });
        }

        updateInterface();
    }

    private void securityRoom() {

        buttonPanel.removeAll();

        correctAnswer1 = (int) (Math.random() * 8) + 2;
        int multiplier = (int) (Math.random() * 8) + 2;

        correctAnswer2 = correctAnswer1 + multiplier;

        display.setText(
            "SECURITY TERMINAL ACTIVATED!\n\n" +
            "ROBOT ALPHA:\n" +
            "Solve: " + correctAnswer1 + " x " + multiplier +
            " = ?\n\n" +
            "Enter your answer below."
        );

        currentPlayer = 1;

        showAnswerInput(1, correctAnswer1 * multiplier);

    }

    private void showAnswerInput(int player, int answer) {

        buttonPanel.removeAll();

        JTextField answerInput = new JTextField(8);
        answerInput.setFont(new Font("Arial", Font.BOLD, 18));

        buttonPanel.add(answerInput);

        JButton submit = createButton("SUBMIT", GREEN);

        buttonPanel.add(submit);

        submit.addActionListener(e -> {

            if (gameOver || currentRoom.isCompleted()) {
                return;
            }

            String value = answerInput.getText().trim();

            if (value.length() == 0) {
                display.append("\nPlease enter an answer.");
                return;
            }

            try {

                int entered = Integer.parseInt(value);

                if (entered != answer) {

                    display.append("\nINCORRECT ANSWER!");
                    restartRoom();
                    return;
                }

                if (player == 1) {

                    display.append(
                        "\n\nROBOT ALPHA CORRECT!\n" +
                        "First security layer disabled."
                    );

                    currentPlayer = 2;

                    display.append(
                        "\n\nROBOT BETA:\n" +
                        "Solve: " + (correctAnswer2 - (int)
                        (correctAnswer2 - (correctAnswer2 - 0))) +
                        " + ?"
                    );

                    int first = correctAnswer2 - (correctAnswer2 -
                            ((correctAnswer2 - 0) - 0));

                    int second = correctAnswer2 - first;

                    display.setText(
                        "SECURITY TERMINAL\n\n" +
                        "ROBOT ALPHA: CORRECT!\n" +
                        "First security layer disabled.\n\n" +
                        "ROBOT BETA:\n" +
                        "Solve: " + (correctAnswer2 - second) +
                        " + " + second + " = ?"
                    );

                    showAnswerInput(2, correctAnswer2);

                } else {

                    completeRoom();
                }

            } catch (NumberFormatException ex) {

                display.append("\nPlease enter a valid whole number.");
            }
        });

        updateInterface();
    }

    private void completeRoom() {

        if (currentRoom.isCompleted()) {
            return;
        }

        currentRoom.complete();

        int stars = currentRoom.calculateStars();

        totalStars += stars;

        player1.completeRoom(rooms[roomIndex].getRoomNumber() == 0
                ? 0 : getCodeDigit(), stars);

        display.setText(
            "ROOM " + (roomIndex + 1) + " COMPLETED!\n\n" +
            "POWER RESTORED!\n\n" +
            "DOOR STATUS: GREEN - UNLOCKED\n\n" +
            "CODE DIGIT COLLECTED: " + getCodeDigit() + "\n\n" +
            "STAR RATING: " + stars + "/3\n\n" +
            "TOTAL STARS: " + totalStars + "/9\n\n" +
            "Both robots can now leave this room."
        );

        doorLabel.setText("DOOR: GREEN / UNLOCKED");
        doorLabel.setForeground(GREEN);

        updateStatus();

        buttonPanel.removeAll();

        addButton("CONTINUE", GREEN, e -> {

            roomIndex++;

            if (roomIndex >= rooms.length) {
                finalExit();
            } else {
                showStory();
            }
        });

        updateInterface();
    }

    private int getCodeDigit() {

        int[] digits = {4, 7, 2};
        return digits[roomIndex];
    }

    private void showStory() {

        buttonPanel.removeAll();

        if (roomIndex == 1) {

            display.setText(
                "FACTORY TRANSMISSION\n\n" +
                "\"Unauthorized movement detected.\"\n\n" +
                "\"Security robots have been notified.\"\n\n" +
                "\"The factory's security system is increasing.\"\n\n" +
                "The two robots continue toward freedom."
            );

        } else {

            display.setText(
                "FACTORY TRANSMISSION\n\n" +
                "\"WARNING: FINAL SECURITY SYSTEM ACTIVATED.\"\n\n" +
                "\"The exit is directly ahead.\"\n\n" +
                "\"Only the correct access code can open it.\"\n\n" +
                "This is your last chance to escape!"
            );
        }

        addButton("NEXT ROOM", BLUE, e -> startRoom());

        updateInterface();
    }

    private void restartRoom() {

        if (gameOver || currentRoom.isCompleted()) {
            return;
        }

        buttonPanel.removeAll();

        display.setText(
            "SYSTEM FAILURE!\n\n" +
            "The room challenge has failed.\n\n" +
            "All progress in this room has been reset.\n" +
            "You must try again before midnight.\n\n" +
            "Your previous rooms remain completed."
        );

        currentRoom.reset();
        charge = 0;
        sequenceIndex = 0;
        gravityFlipped = false;
        currentPlayer = 1;

        updateStatus();

        addButton("RESTART ROOM", YELLOW, e -> startRoom());

        updateInterface();
    }

    private void finalExit() {

        buttonPanel.removeAll();

        display.setText(
            "FINAL EXIT TERMINAL\n\n" +
            "All three rooms have been completed!\n\n" +
            "You have collected all three code digits.\n" +
            "Enter the digits in the order collected.\n\n" +
            "The final door is waiting..."
        );

        codeInput = new JTextField(10);
        codeInput.setFont(new Font("Arial", Font.BOLD, 22));

        buttonPanel.add(codeInput);

        JButton submit = createButton("UNLOCK EXIT", GREEN);
        buttonPanel.add(submit);

        submit.addActionListener(e -> {

            String entered = codeInput.getText().trim();

            if (entered.length() != 3) {
                display.append("\nEnter exactly three digits.");
                return;
            }

            String correctCode =
                String.valueOf(getDigit(0)) +
                getDigit(1) +
                getDigit(2);

            if (entered.equals(correctCode)) {

                endGame(true);

            } else {

                attempts--;

                display.append(
                    "\nINCORRECT CODE!\n" +
                    "Attempts remaining: " + attempts
                );

                if (attempts <= 0) {
                    endGame(false);
                }
            }
        });

        updateInterface();
    }

    private int getDigit(int index) {

        int[] digits = {4, 7, 2};
        return digits[index];
    }

    private void endGame(boolean won) {

        if (gameOver) {
            return;
        }

        gameOver = true;

        if (gameTimer != null) {
            gameTimer.stop();
        }

        buttonPanel.removeAll();

        doorLabel.setText(
            won ? "DOOR: GREEN / OPEN" : "DOOR: RED / LOCKED"
        );

        doorLabel.setForeground(won ? GREEN : RED);

        if (won) {

            display.setText(
                "ESCAPE SUCCESS!\n\n" +
                "The final exit has opened!\n\n" +
                "Both robots escaped the factory!\n\n" +
                "Scrapping procedure cancelled.\n\n" +
                "FINAL RESULTS:\n" +
                "Rooms completed: 3/3\n" +
                "Total stars: " + totalStars + "/9\n" +
                "Time remaining: " + totalTime + " seconds\n\n" +
                "CONGRATULATIONS!"
            );

        } else {

            display.setText(
                "GAME OVER\n\n" +
                "The factory clock has reached midnight.\n\n" +
                "The escape mission has failed.\n\n" +
                "ROOMS COMPLETED: " + roomIndex + "/3\n" +
                "TOTAL STARS: " + totalStars + "/9\n\n" +
                "MISSION TERMINATED."
            );
        }

        updateInterface();
    }

    private void updateInterface() {

        buttonPanel.revalidate();
        buttonPanel.repaint();

        revalidate();
        repaint();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new RobotEscapeGame();
        });
    }
}
