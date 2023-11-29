import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;

public class MinesweeperGame extends JFrame {
    private JButton[][] buttons;
    private boolean[][] mines;
    private int[][] surroundingMines;
    private int uncoveredCells;
    private int totalMines;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private int correctlyMarkedMines;
    private static final int BUTTON_SIZE = 25;
    private boolean gameEnded = false;
    private int[] lastClickedBomb;


    private int lastClickedBombI = -1; // Initialize with an invalid value
    private int lastClickedBombJ = -1;


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
    private final String EXPLOSION_ICONE = "💣";
    private final String INCORRECT_MARK_ICON = "❌"; // Use a red 'X' icon or use "X" for bold text

    private Clip explosionSound;

    public MinesweeperGame(int rows, int cols, int mines) {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Display the board size and number of mines
        JLabel levelLabel = new JLabel(String.format("Level: %s | Size: %dx%d | Mines: %d", getLevelName(), rows, cols, mines));
        add(levelLabel, BorderLayout.WEST);

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

    private String getLevelName() {
        switch (totalMines) {
            case BEGINNER_MINES:
                return "Beginner";
            case INTERMEDIATE_MINES:
                return "Intermediate";
            case ADVANCED_MINES:
                return "Advanced";
            default:
                return "Unknown";
        }
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
    
        if (gameEnded ||!isGameInProgress() || !buttons[i][j].isEnabled() || buttons[i][j].getText().equals("M")) {
            return; 
        }

    
        buttons[i][j].setEnabled(false);
        uncoveredCells++;
    
        if (mines[i][j]) {
            correctlyMarkedMines--; 
            explodeMine(i, j);
        } else {
            if (uncoveredCells == buttons.length * buttons[0].length - totalMines) {
                endGame(true, "You won.");
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
        highlightLastClickedBomb(i, j);
        // Update the score label
        scoreLabel.setText("Score: " + uncoveredCells);
    }

    private boolean isGameInProgress() {
        return uncoveredCells < buttons.length * buttons[0].length - totalMines;
    }
    
    

    private void markMine(int i, int j) {
        if (buttons[i][j].getText().equals("M")) {
            correctlyMarkedMines--;  
            buttons[i][j].setText("");
        } else {
            correctlyMarkedMines++;  
            buttons[i][j].setText("M");
        }

        markIncorrectMines();
        highlightLastClickedBomb(i, j);

        // Update the score label
        scoreLabel.setText("Score: " + uncoveredCells);
    }


    // Mark incorrect mines method
    private void markIncorrectMines() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (buttons[i][j].getText().equals("M") && !mines[i][j]) {
                    buttons[i][j].setText(INCORRECT_MARK_ICON);
                    buttons[i][j].setForeground(Color.RED);
                } else if (buttons[i][j].getText().equals("") && mines[i][j]) {
                    buttons[i][j].setText(EXPLOSION_ICONE);
                }
            }
        }
    }
    
    
    

    private void explodeMine(int i, int j) {
        if (explosionSound != null) {
            explosionSound.start();
        }
        markIncorrectMines();

        buttons[i][j].setText(EXPLOSION_ICONE);

        // Store the coordinates of the last clicked bomb
    lastClickedBombI = i;
    lastClickedBombJ = j;

        revealAllMines();

        disableAllButtons();

        endGame(false , "You lost.");
    }

    

    private void disableAllButtons() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void endGame(boolean win, String message) {
        timer.stop();  // Stop the timer when the game ends

        // Display a message dialog to the user
        JOptionPane.showMessageDialog(this, message);
        gameEnded = true;
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
                endGame(false, "Time's up! You lost.");
            } else {
                updateTimerDisplay(secondsPlayed);
            }
        }

        private int getGameDuration() {
            switch (totalMines) {
                case BEGINNER_MINES:
                    return 5;  // 1 minute for Beginner
                case INTERMEDIATE_MINES:
                    return 180; // 3 minutes for Intermediate
                case ADVANCED_MINES:
                    return 660; // 11 minutes for Advanced
                default:
                    return 0;
            }
        }
    }


    // private void revealAllMines() {
    //     for (int i = 0; i < buttons.length; i++) {
    //         for (int j = 0; j < buttons[0].length; j++) {
    //             if (mines[i][j]) {
    //                 explodeMine(i, j);
    //                 // buttons[i][j].setText(EXPLOSION_ICONE);
    //                 // buttons[i][j].setEnabled(false);
    //             }
    //         }
    //     }
    // }
    


    private void revealAllMines() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (mines[i][j]) {
                    if (explosionSound != null) {
                        explosionSound.start();
                    }
                    if (buttons[i][j].getText().equals("")) {
                        buttons[i][j].setText(EXPLOSION_ICONE);
                        buttons[i][j].setForeground(Color.RED); // Set red X for incorrectly marked mines
                    }
                    buttons[i][j].setEnabled(false);
                } else if (buttons[i][j].isEnabled()) {
                    // If the button is not a mine, set the text indicating the number of surrounding mines
                    int count = surroundingMines[i][j];
                    if (count > 0) {
                        buttons[i][j].setText(Integer.toString(count));
                    }
                    buttons[i][j].setEnabled(false);
                }
            }
        }
        highlightLastClickedBomb( lastClickedBombI, lastClickedBombJ);
    }

    // private void highlightLastClickedBomb() {
    //     if (lastClickedBombI != -1 && lastClickedBombJ != -1) {
    //         buttons[lastClickedBombI][lastClickedBombJ].setBackground(Color.YELLOW); // Set your desired highlight color
    //     }
    // }
    
    private void highlightLastClickedBomb(int i, int j) {
        if (lastClickedBomb != null) {
            int lastClickedI = lastClickedBomb[0];
            int lastClickedJ = lastClickedBomb[1];
    
            if (mines[lastClickedI][lastClickedJ] && !buttons[lastClickedI][lastClickedJ].getText().equals("M")) {
    buttons[lastClickedI][lastClickedJ].setUI(new MetalButtonUI() {
        protected Color getDisabledTextColor() {
            return Color.RED;
        }
    });
    buttons[lastClickedI][lastClickedJ].setText("X");
    buttons[lastClickedI][lastClickedJ].setEnabled(false);
}
        }
    
        // Store the current clicked bomb coordinates
        lastClickedBomb = new int[]{i, j};
    }
    
    
    
    
    
    
    
    
    
    



    
}
