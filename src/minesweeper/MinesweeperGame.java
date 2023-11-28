
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Random;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseEvent;
import java.io.File;






public class MinesweeperGame extends JFrame {
    private JButton[][] buttons;
    private boolean[][] mines;
    private int[][] surroundingMines;
    private int uncoveredCells;

    // Adjust the board size for the Beginner level
    private final int ROWS = 6;
    private final int COLS = 9;
    private final int MINES = 11;

    // Timer variables
    private Timer timer;
    private int secondsPlayed;

    // Sound effect for exploding mines
    private final String EXPLOSION_SOUND_PATH = "../resources/explosion_sound.wav";
    private Clip explosionSound;

    public MinesweeperGame() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Adjust the layout based on the Beginner level board size
        setLayout(new GridLayout(ROWS, COLS));

        buttons = new JButton[ROWS][COLS];
        mines = new boolean[ROWS][COLS];
        surroundingMines = new int[ROWS][COLS];
        uncoveredCells = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addMouseListener(new CellClickListener(i, j));
                add(buttons[i][j]);
            }
        }

        placeMines();
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
        setVisible(true);
    }

    private void placeMines() {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < MINES) {
            int i = random.nextInt(ROWS);
            int j = random.nextInt(COLS);

            if (!mines[i][j]) {
                mines[i][j] = true;
                placedMines++;
            }
        }
    }

    private void countSurroundingMines() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!mines[i][j]) {
                    int count = 0;
                    if (i > 0 && mines[i - 1][j]) count++;
                    if (i < ROWS - 1 && mines[i + 1][j]) count++;
                    if (j > 0 && mines[i][j - 1]) count++;
                    if (j < COLS - 1 && mines[i][j + 1]) count++;
                    if (i > 0 && j > 0 && mines[i - 1][j - 1]) count++;
                    if (i < ROWS - 1 && j < COLS - 1 && mines[i + 1][j + 1]) count++;
                    if (i > 0 && j < COLS - 1 && mines[i - 1][j + 1]) count++;
                    if (i < ROWS - 1 && j > 0 && mines[i + 1][j - 1]) count++;

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
            explodeMine(i, j);
        } else {
            if (uncoveredCells == ROWS * COLS - MINES) {
                endGame(true);  // All non-mine cells uncovered, player wins
            } else if (surroundingMines[i][j] == 0) {
                // Auto-uncover adjacent cells if the current cell has no surrounding mines
                for (int x = Math.max(0, i - 1); x <= Math.min(ROWS - 1, i + 1); x++) {
                    for (int y = Math.max(0, j - 1); y <= Math.min(COLS - 1, j + 1); y++) {
                        if (x != i || y != j) {  // skip the current cell
                            uncoverCell(x, y);
                        }
                    }
                }
            } else {
                buttons[i][j].setText(Integer.toString(surroundingMines[i][j]));
            }
        }
    }
    
    
    
    
    

    private void markMine(int i, int j) {
        if (buttons[i][j].getText().equals("M")) {
            // Erase mine marking
            buttons[i][j].setText("");
        } else {
            // Mark as mine
            buttons[i][j].setText("M");
        }
    }

    private void explodeMine(int i, int j) {
        // Play explosion sound
        if (explosionSound != null) {
            explosionSound.start();
        }

        // Display explosion animation (you can add your animation logic here)

        // Mark exploded mine with a special symbol
        buttons[i][j].setText("*");

        // Disable all buttons
        disableAllButtons();

        // End the game
        endGame(false);
    }

    private void disableAllButtons() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void endGame(boolean win) {
        // ... (code to handle game ending, display messages, etc.)
        // You can customize this method based on your needs.
    }

    // private class CellClickListener extends MouseAdapter {
    //     private int i;
    //     private int j;
    
    //     public CellClickListener(int i, int j) {
    //         this.i = i;
    //         this.j = j;
    //     }
    
    //     @Override
    //     public void mousePressed(java.awt.event.MouseEvent e) {
    //         if (SwingUtilities.isRightMouseButton(e)) {
    //             markMine(i, j);
    //         } else {
    //             uncoverCell(i, j);
    //         }
    //     }
    // }

    // private class CellClickListener implements ActionListener {
    //     private int i;
    //     private int j;
    
    //     public CellClickListener(int i, int j) {
    //         this.i = i;
    //         this.j = j;
    //     }
    
    //     @Override
    //     public void actionPerformed(ActionEvent e) {
    //         if (SwingUtilities.isRightMouseButton((MouseEvent) e)) {
    //             markMine(i, j);
    //         } else {
    //             uncoverCell(i, j);
    //         }
    //     }
    // }

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
            } else {
                uncoverCell(i, j);
            }
        }
    }
    
    


    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            secondsPlayed++;

            if (secondsPlayed >= 60) {
                timer.stop();
                endGame(false);
            }
        }
    }
}
