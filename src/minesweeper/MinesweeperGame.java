import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Random;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.File;

public class MinesweeperGame extends JFrame {
    private JButton[][] buttons;
    private boolean[][] mines;
    private int[][] surroundingMines;
    private int uncoveredCells;
    private int totalMines;
    private JLabel timerLabel;
    private JLabel scoreLabel;  // Added score label
    private int correctlyMarkedMines;
    private static final int BUTTON_SIZE = 30; 

    // Board sizes and number of mines for each level
    public static final int BEGINNER_ROWS = 6;
    public static final int BEGINNER_COLS = 9;
    public static final int BEGINNER_MINES = 11;

    public static final int INTERMEDIATE_ROWS = 12;
    public static final int INTERMEDIATE_COLS = 18;
    public static final int INTERMEDIATE_MINES = 36;

    public static final int ADVANCED_ROWS = 21;
    public static final int ADVANCED_COLS = 26;
    public static final int ADVANCED_MINES = 92;

    // Timer variables
    private Timer timer;
    private int secondsPlayed;

    // Sound effect for exploding mines
    private final String EXPLOSION_SOUND_PATH = "../resources/explosion_sound.wav";
    private Clip explosionSound;

    public MinesweeperGame(int rows, int cols, int mines) {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Adjust the layout based on the selected board size
        setLayout(new BorderLayout());

        // Create the timerLabel and add it to the North position
        timerLabel = new JLabel("Time: 00:00");
        add(timerLabel, BorderLayout.NORTH);

        // Create a panel for the game grid
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(rows, cols));

        buttons = new JButton[rows][cols];
        this.mines = new boolean[rows][cols];
        surroundingMines = new int[rows][cols];
        uncoveredCells = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                buttons[i][j].addMouseListener(new CellClickListener(i, j));
                gamePanel.add(buttons[i][j]);
            }
        }

        add(gamePanel, BorderLayout.CENTER);

                // Create the scoreLabel and add it below the timerLabel
        scoreLabel = new JLabel("Score: 0");
        add(scoreLabel, BorderLayout.SOUTH);

        placeMines(mines);
        countSurroundingMines();

        // Initialize timer
        timer = new Timer(1000, new TimerListener());
        secondsPlayed = 0;

        // Start the timer
        timer.start();

        // Load the audio clip
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(EXPLOSION_SOUND_PATH));
            explosionSound = AudioSystem.getClip();
            explosionSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pack();
        setLocationRelativeTo(null); 
        setVisible(true);

        // Store the total number of mines
        totalMines = mines;

        // Initialize correctlyMarkedMines
        correctlyMarkedMines = 0;
    }

    private void placeMines(int mines) {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < mines) {
            int i = random.nextInt(buttons.length);
            int j = random.nextInt(buttons[0].length);

            if (!this.mines[i][j]) {
                this.mines[i][j] = true;
                placedMines++;
            }
        }
    }

    private void countSurroundingMines() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (!mines[i][j]) {
                    int count = 0;
                    for (int x = Math.max(0, i - 1); x <= Math.min(buttons.length - 1, i + 1); x++) {
                        for (int y = Math.max(0, j - 1); y <= Math.min(buttons[0].length - 1, j + 1); y++) {
                            if (mines[x][y]) {
                                count++;
                            }
                        }
                    }
                    surroundingMines[i][j] = count;
                }
            }
        }
    }

    private void uncoverCell(int i, int j) {
        if (!buttons[i][j].isEnabled()) {
            return;  // cell already uncovered or flagged
        }

        buttons[i][j].setEnabled(false);
        uncoveredCells++;

        if (mines[i][j]) {
            correctlyMarkedMines--;  // Decrement the count of correctly marked mines
            explodeMine(i, j);
        } else {
            if (uncoveredCells == buttons.length * buttons[0].length - totalMines) {
                endGame(true);  // All non-mine cells uncovered, player wins
            } else if (surroundingMines[i][j] == 0) {
                for (int x = Math.max(0, i - 1); x <= Math.min(buttons.length - 1, i + 1); x++) {
                    for (int y = Math.max(0, j - 1); y <= Math.min(buttons[0].length - 1, j + 1); y++) {
                        if (x != i || y != j) {
                            uncoverCell(x, y);
                        }
                    }
                }
            } else {
                buttons[i][j].setText(Integer.toString(surroundingMines[i][j]));
            }
        }

        // Update the score label
        scoreLabel.setText("Score: " + uncoveredCells);
    }

    private void markMine(int i, int j) {
        if (buttons[i][j].getText().equals("M")) {
            correctlyMarkedMines--;  // Decrement the count of correctly marked mines
            buttons[i][j].setText("");
        } else {
            correctlyMarkedMines++;  // Increment the count of correctly marked mines
            buttons[i][j].setText("M");
        }

        // Update the score label
        scoreLabel.setText("Score: " + uncoveredCells);
    }

    private void explodeMine(int i, int j) {
        if (explosionSound != null) {
            explosionSound.start();
        }

        buttons[i][j].setText("*");

        disableAllButtons();

        endGame(false);
    }

    private void disableAllButtons() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void endGame(boolean win) {
        timer.stop();  // Stop the timer when the game ends
        // ... (existing code remains unchanged)
    }

    private class CellClickListener extends MouseAdapter {
        private int i;
        private int j;

        public CellClickListener(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                markMine(i, j);
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                uncoverCell(i, j);
            }
        }
    }

    private void updateTimerDisplay(int seconds) {
        timerLabel.setText(String.format("Time: %02d:%02d", seconds / 60, seconds % 60));
    }

    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            secondsPlayed++;

            if (secondsPlayed >= getGameDuration()) {
                timer.stop();
                revealAllMines();
                endGame(false);
            } else {
                updateTimerDisplay(secondsPlayed);
            }
        }

        private int getGameDuration() {
            switch (totalMines) {
                case BEGINNER_MINES:
                    return 10;  // 1 minute for Beginner
                case INTERMEDIATE_MINES:
                    return 180; // 3 minutes for Intermediate
                case ADVANCED_MINES:
                    return 660; // 11 minutes for Advanced
                default:
                    return 0;
            }
        }
    }

    private void revealAllMines() {
        for (int i = 0; i < mines.length; i++) {
            for (int j = 0; j < mines[0].length; j++) {
                if (mines[i][j]) {
                    buttons[i][j].setText("*");  
                }
            }
        }
    }

    
}
