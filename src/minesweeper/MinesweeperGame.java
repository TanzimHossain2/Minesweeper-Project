import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

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
                buttons[i][j].addActionListener(new CellClickListener(i, j));
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
        // ... (rest of the code)
    }

    private class CellClickListener implements ActionListener {
        private int i;
        private int j;

        public CellClickListener(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void actionPerformed(ActionEvent e) {
            uncoverCell(i, j);
        }
    }

    // Timer listener
    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            secondsPlayed++;

            // Check if the timer exceeds 60 seconds (1 minute) for the Beginner level
            if (secondsPlayed >= 60) {
                timer.stop();
                endGame(false); 
            }
        }
    }

    // Method to end the game
    private void endGame(boolean win) {
        // ... (code to handle game ending, display messages, etc.)
    }

    // ... (rest of the code)
}
