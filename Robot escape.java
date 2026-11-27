import javax.swing.*;
import java.awt.*;
import java.util.Random;

// Stores information about one robot.
class Robot {
    private String name;
    private int stars;
    private int codeDigit;

    public Robot(String name) {
        this.name = name;
        stars = 0;
        codeDigit = -1;
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

// Stores information and behavior for one escape room.
class EscapeRoom {
    private int roomNumber;
    private String roomName;
    private int timeRemaining;
    private boolean completed;

    public EscapeRoom(int roomNumber, String roomName) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        reset();
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

    private int roomIndex;
    private int currentPlayer;
    private int charge;
    private int sequenceIndex;
    private int[] sequence;
    private int correctAnswer1;
    private int correctAnswer2;
    private int totalTime;
    private int totalStars;
    private int attempts;
    private int[] codeDigits;

    private boolean gravityFlipped;
    private boolean gameOver;
    private boolean gameStarted;
    private boolean challengeActive;
    private boolean sequenceVisible;

    private long roomStartTime;
    private javax.swing.Timer gameTimer;
    private javax.swing.Timer memoryTimer;

    private JLabel timerLabel;
    private JLabel roomTimerLabel;
    private JLabel chargeLabel;
    private JLabel playerLabel;
    private JLabel doorLabel;
    private JLabel scoreLabel;
    private JTextArea display;
    private JPanel buttonPanel;
    private JProgressBar energyBar;
    private JTextField codeInput;

    private final Color DARK = new Color(20, 25, 40);
    private final Color PANEL = new Color(35, 43, 62);
    private final Color BLUE = new Color(65, 150, 255);
    private final Color GREEN = new Color(45, 190, 120);
    private final Color RED = new Color(220, 65, 75);
    private final Color YELLOW = new Color(245, 190, 65);
    private final Random random = new Random();

    public RobotEscapeGame() {
        player1 = new Robot("ROBOT ALPHA");
        player2 = new Robot("ROBOT BETA");
        rooms = new EscapeRoom[] {
            new EscapeRoom(1, "COLOR GRID"),
            new EscapeRoom(2, "MEMORY CIRCUIT"),
            new EscapeRoom(3, "SECURITY TERMINAL")
        };

        roomIndex = 0;
        currentPlayer = 1;
        charge = 0;
        sequenceIndex = 0;
        sequence = new int[5];
        totalTime = 300;
        totalStars = 0;
        attempts = 3;
        codeDigits = new int[3];
        gravityFlipped = false;
        gameOver = false;
        gameStarted = false;
        challengeActive = false;
        sequenceVisible = false;

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
        JLabel titleLabel = new JLabel("THE LAST ESCAPE", SwingConstants.CENTER);
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
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton button = createButton(text, color);
        button.addActionListener(action);
        buttonPanel.add(button);
    }

    private void updateStatus() {
        timerLabel.setText("TOTAL TIME: " + totalTime);
        roomTimerLabel.setText("ROOM TIME: " +
            (currentRoom == null ? 60 : currentRoom.getTimeRemaining()));
        chargeLabel.setText("ENERGY: " + charge + "%");
        playerLabel.setText("PLAYER: " + (currentPlayer == 1 ? "ALPHA" : "BETA"));
        scoreLabel.setText("STARS: " + totalStars);
        energyBar.setValue(charge);
        energyBar.setString(charge + "%");
    }

    private void updateInterface() {
        buttonPanel.revalidate();
        buttonPanel.repaint();
        revalidate();
        repaint();
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
            "PLAYER 1: ROBOT ALPHA\nPLAYER 2: ROBOT BETA\n\n" +
            "GAME RULES:\n" +
            "- Work together to complete every room.\n" +
            "- Each active room challenge has a 60-second limit.\n" +
            "- The entire game has a 5-minute time limit.\n" +
            "- Failed rooms must be restarted.\n" +
            "- Earn up to three stars in each room.\n\n" +
            "Can you escape before midnight?"
        );
        updateStatus();
        addButton("START GAME", GREEN, e -> startGame());
        updateInterface();
    }

    private void startGame() {
        if (gameStarted || gameOver) {
            return;
        }
        gameStarted = true;
        gameTimer = new javax.swing.Timer(1000, e -> {
            if (gameOver) {
                return;
            }
            totalTime--;
            if (challengeActive && currentRoom != null && !currentRoom.isCompleted()) {
                currentRoom.decreaseTime();
            }
            updateStatus();
            if (totalTime <= 0) {
                endGame(false);
            } else if (challengeActive && currentRoom != null &&
                       currentRoom.getTimeRemaining() <= 0) {
                restartRoom("TIME LIMIT REACHED!");
            }
        });
        gameTimer.start();
        startRoom();
    }

    private void startRoom() {
        if (gameOver) {
            return;
        }
        stopMemoryTimer();
        currentRoom = rooms[roomIndex];
        currentRoom.reset();
        charge = 0;
        currentPlayer = 1;
        gravityFlipped = false;
        sequenceIndex = 0;
        sequenceVisible = false;
        challengeActive = false;
        doorLabel.setText("DOOR: RED / LOCKED");
        doorLabel.setForeground(RED);
        showRoomInstructions();
    }

    private void showRoomInstructions() {
        buttonPanel.removeAll();
        updateStatus();
        if (roomIndex == 0) {
            display.setText(
                "ROOM 1: COLOR GRID\n\nThe factory floor is glitching!\n" +
                "Both robots must charge the door lock.\n\n" +
                "ROBOT ALPHA COLORS: BLUE and YELLOW\n" +
                "ROBOT BETA COLORS: GREEN and PURPLE\n" +
                "DECOY COLORS: RED and ORANGE\n\n" +
                "CORRECT COLOR: +25% ENERGY\n" +
                "WRONG ROBOT COLOR: -20% ENERGY\n" +
                "DECOY COLOR: -15% ENERGY\n\n" +
                "Reach 100% energy to unlock the door.\n" +
                "WARNING: Gravity can reverse your selections!"
            );
        } else if (roomIndex == 1) {
            display.setText(
                "ROOM 2: MEMORY CIRCUIT\n\nThe factory memory system has been corrupted.\n\n" +
                "A sequence of five numbers will appear.\n" +
                "Remember the sequence before it disappears.\n\n" +
                "ROBOT ALPHA enters the first two numbers.\n" +
                "ROBOT BETA enters the last three numbers.\n\n" +
                "Every number must be correct to unlock the door."
            );
        } else {
            display.setText(
                "ROOM 3: SECURITY TERMINAL\n\nThe final security system blocks the exit.\n\n" +
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
        if (gameOver || currentRoom == null || currentRoom.isCompleted() || challengeActive) {
            return;
        }
        challengeActive = true;
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
        display.setText("ROOM 1: COLOR GRID\n\nCharge the door by selecting your robot's colors.\nWork together to reach 100%!\n\nNext turn: ROBOT ALPHA");
        String[] colors = {"BLUE", "GREEN", "YELLOW", "PURPLE", "RED", "ORANGE"};
        Color[] colorValues = {
            new Color(45, 110, 230), new Color(40, 160, 90),
            new Color(235, 190, 35), new Color(140, 75, 200),
            new Color(210, 55, 55), new Color(230, 125, 35)
        };
        for (int i = 0; i < colors.length; i++) {
            final int choice = i;
            addButton(colors[i], colorValues[i], e -> {
                if (!canPlayCurrentRoom()) {
                    return;
                }
                processColor(colors[choice], choice);
            });
        }
        updateStatus();
        updateInterface();
    }

    private boolean canPlayCurrentRoom() {
        return !gameOver && challengeActive && currentRoom != null && !currentRoom.isCompleted();
    }

    private void processColor(String selectedColor, int choice) {
        String color = selectedColor;
        if (gravityFlipped) {
            int reversedChoice = 5 - choice;
            String[] colors = {"BLUE", "GREEN", "YELLOW", "PURPLE", "RED", "ORANGE"};
            color = colors[reversedChoice];
        }

        boolean alphaColor = color.equals("BLUE") || color.equals("YELLOW");
        boolean betaColor = color.equals("GREEN") || color.equals("PURPLE");

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
        updateStatus();

        if (charge >= 100) {
            completeRoom();
            return;
        }
        if (random.nextDouble() < 0.25) {
            gravityFlipped = !gravityFlipped;
            display.append(gravityFlipped ? "\nGRAVITY SHIFT! CONTROLS REVERSED!" : "\nGRAVITY RESTORED!");
        }
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        display.append("\n\nNext turn: " + (currentPlayer == 1 ? "ROBOT ALPHA" : "ROBOT BETA"));
        updateStatus();
    }

    private void memoryRoom() {
        buttonPanel.removeAll();
        display.setText("MEMORY CIRCUIT ACTIVATED!\n\nMemorize the sequence.");
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = random.nextInt(4) + 1;
        }
        sequenceVisible = true;
        StringBuilder numbers = new StringBuilder();
        for (int number : sequence) {
            numbers.append(number).append("   ");
        }
        display.append("\n\nSEQUENCE:\n" + numbers);
        updateInterface();

        stopMemoryTimer();
        memoryTimer = new javax.swing.Timer(4000, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            if (!canPlayCurrentRoom() || roomIndex != 1) {
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
        memoryTimer.setRepeats(false);
        memoryTimer.start();
    }

    private void showMemoryButtons() {
        if (!canPlayCurrentRoom()) {
            return;
        }
        buttonPanel.removeAll();
        for (int i = 1; i <= 4; i++) {
            final int number = i;
            addButton(String.valueOf(i), BLUE, e -> {
                if (!canPlayCurrentRoom()) {
                    return;
                }
                if (number == sequence[sequenceIndex]) {
                    sequenceIndex++;
                    charge = sequenceIndex * 20;
                    if (sequenceIndex == 2) {
                        currentPlayer = 2;
                        display.append("\n\nROBOT ALPHA COMPLETE! ROBOT BETA: Enter the last three numbers.");
                    } else if (sequenceIndex == sequence.length) {
                        completeRoom();
                        return;
                    } else {
                        display.append("\nCORRECT! (" + sequenceIndex + "/5)");
                    }
                    updateStatus();
                } else {
                    restartRoom("INCORRECT MEMORY SEQUENCE!");
                }
            });
        }
        updateInterface();
    }

    private void securityRoom() {
        buttonPanel.removeAll();
        int a = random.nextInt(8) + 2;
        int b = random.nextInt(8) + 2;
        while (a == b) {
            b = random.nextInt(8) + 2;
        }
        correctAnswer1 = a * b;
        int c = random.nextInt(25) + 10;
        int d = random.nextInt(25) + 10;
        correctAnswer2 = c + d;

        display.setText(
            "ROOM 3: SECURITY TERMINAL\n\n" +
            "ROBOT ALPHA (Multiplication): " + a + " x " + b + " = ?\n" +
            "ROBOT BETA (Addition): " + c + " + " + d + " = ?\n\n" +
            "Enter both answers separated by spaces (example: 24 45):"
        );
        codeInput = new JTextField(10);
        codeInput.setFont(new Font("Arial", Font.BOLD, 18));
        buttonPanel.add(codeInput);
        addButton("VERIFY ANSWERS", GREEN, e -> verifySecurityAnswers());
        updateInterface();
    }

    private void verifySecurityAnswers() {
        if (!canPlayCurrentRoom()) {
            return;
        }
        String entered = codeInput.getText().trim();
        String[] answers = entered.isEmpty() ? new String[0] : entered.split("\\s+");
        if (answers.length != 2 || !answers[0].matches("\\d+") || !answers[1].matches("\\d+")) {
            display.append("\nPlease enter exactly two whole-number answers separated by spaces.");
            return;
        }
        try {
            int answer1 = Integer.parseInt(answers[0]);
            int answer2 = Integer.parseInt(answers[1]);
            if (answer1 == correctAnswer1 && answer2 == correctAnswer2) {
                charge = 100;
                updateStatus();
                completeRoom();
            } else {
                restartRoom("INCORRECT ANSWERS!");
            }
        } catch (NumberFormatException ex) {
            display.append("\nThose numbers are too large. Please enter reasonable whole-number answers.");
        }
    }

    private void completeRoom() {
        if (!canPlayCurrentRoom()) {
            return;
        }
        challengeActive = false;
        stopMemoryTimer();
        currentRoom.complete();
        int earnedStars = currentRoom.calculateStars();
        int digit = generateCodeDigit(roomIndex);
        codeDigits[roomIndex] = digit;
        if (roomIndex == 0) {
            player1.completeRoom(digit, earnedStars);
        } else {
            player2.completeRoom(digit, earnedStars);
        }
        totalStars += earnedStars;
        doorLabel.setText("DOOR: GREEN / UNLOCKED");
        doorLabel.setForeground(GREEN);
        updateStatus();
        buttonPanel.removeAll();
        long secondsTaken = (System.currentTimeMillis() - roomStartTime) / 1000;
        display.setText(
            "ROOM " + (roomIndex + 1) + " COMPLETE!\n\n" +
            "Door unlocked! Light is now GREEN.\n" +
            "Time taken: " + secondsTaken + " seconds\n" +
            "Stars earned: " + earnedStars + "/3\n" +
            "CODE DIGIT COLLECTED: " + digit + "\n\n" +
            "Walk through the door to continue!"
        );
        if (roomIndex < 2) {
            addButton("WALK THROUGH DOOR", GREEN, e -> {
                if (gameOver) return;
                roomIndex++;
                startRoom();
            });
        } else {
            addButton("PROCEED TO FINAL EXIT", GREEN, e -> finalExit());
        }
        updateInterface();
    }

    // Generates a different random digit for each room (digits can include zero).
    private int generateCodeDigit(int index) {
        boolean alreadyUsed;
        int digit;
        do {
            digit = random.nextInt(10);
            alreadyUsed = false;
            for (int i = 0; i < index; i++) {
                if (codeDigits[i] == digit) {
                    alreadyUsed = true;
                }
            }
        } while (alreadyUsed);
        return digit;
    }

    private void restartRoom(String reason) {
        if (gameOver || currentRoom == null || currentRoom.isCompleted()) {
            return;
        }
        challengeActive = false;
        stopMemoryTimer();
        buttonPanel.removeAll();
        display.setText(
            "SYSTEM FAILURE!\n\n" + reason + "\n\n" +
            "All progress in this room has been reset.\n" +
            "Your previous rooms remain completed.\n\n" +
            "You must try again before midnight."
        );
        currentRoom.reset();
        charge = 0;
        sequenceIndex = 0;
        gravityFlipped = false;
        currentPlayer = 1;
        sequenceVisible = false;
        updateStatus();
        addButton("RESTART ROOM", YELLOW, e -> startRoom());
        updateInterface();
    }

    private void stopMemoryTimer() {
        if (memoryTimer != null) {
            memoryTimer.stop();
            memoryTimer = null;
        }
    }

    private void finalExit() {
        if (gameOver || roomIndex != 2 || !currentRoom.isCompleted()) {
            return;
        }
        challengeActive = false;
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
        addButton("UNLOCK EXIT", GREEN, e -> verifyFinalCode());
        updateInterface();
    }

    private void verifyFinalCode() {
        if (gameOver) {
            return;
        }
        String entered = codeInput.getText().trim();
        if (!entered.matches("\\d{3}")) {
            display.append("\nEnter exactly three digits (0-9). Spaces and other characters are not allowed.");
            return;
        }
        String correctCode = "" + codeDigits[0] + codeDigits[1] + codeDigits[2];
        if (entered.equals(correctCode)) {
            endGame(true);
        } else {
            attempts--;
            display.append("\nINCORRECT CODE!\nAttempts remaining: " + attempts);
            if (attempts <= 0) {
                endGame(false);
            }
        }
    }

    private void endGame(boolean won) {
        if (gameOver) {
            return;
        }
        gameOver = true;
        challengeActive = false;
        stopMemoryTimer();
        if (gameTimer != null) {
            gameTimer.stop();
        }
        buttonPanel.removeAll();
        doorLabel.setText(won ? "DOOR: GREEN / OPEN" : "DOOR: RED / LOCKED");
        doorLabel.setForeground(won ? GREEN : RED);
        if (won) {
            display.setText(
                "ESCAPE SUCCESS!\n\nThe final exit has opened!\n\n" +
                "Both robots escaped the factory!\n\n" +
                "Scrapping procedure cancelled.\n\n" +
                "FINAL RESULTS:\nRooms completed: 3/3\n" +
                "Total stars: " + totalStars + "/9\n" +
                "Time remaining: " + totalTime + " seconds\n\n" +
                "CONGRATULATIONS!"
            );
        } else {
            display.setText(
                "GAME OVER\n\nThe factory clock has reached midnight or all exit attempts were used.\n\n" +
                "The escape mission has failed.\n\n" +
                "ROOMS COMPLETED: " + countCompletedRooms() + "/3\n" +
                "TOTAL STARS: " + totalStars + "/9\n\nMISSION TERMINATED."
            );
        }
        addButton("PLAY AGAIN", BLUE, e -> {
            dispose();
            new RobotEscapeGame();
        });
        updateInterface();
    }

    private int countCompletedRooms() {
        int completedCount = 0;
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].isCompleted()) {
                completedCount++;
            }
        }
        return completedCount;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RobotEscapeGame());
    }
}
