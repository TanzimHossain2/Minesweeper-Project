import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Allow the user to choose the level at the start
        String[] options = {"Beginner", "Intermediate", "Advanced"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose the difficulty level:",
                "Minesweeper",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        int rows, cols, mines;

        // Set the board size and number of mines based on the user's choice
        switch (choice) {
            case 0:
                rows = MinesweeperGame.BEGINNER_ROWS;
                cols = MinesweeperGame.BEGINNER_COLS;
                mines = MinesweeperGame.BEGINNER_MINES;
                break;
            case 1:
                rows = MinesweeperGame.INTERMEDIATE_ROWS;
                cols = MinesweeperGame.INTERMEDIATE_COLS;
                mines = MinesweeperGame.INTERMEDIATE_MINES;
                break;
            case 2:
                rows = MinesweeperGame.ADVANCED_ROWS;
                cols = MinesweeperGame.ADVANCED_COLS;
                mines = MinesweeperGame.ADVANCED_MINES;
                break;
            default:
                // Default to Beginner level if an invalid choice is made
                rows = MinesweeperGame.BEGINNER_ROWS;
                cols = MinesweeperGame.BEGINNER_COLS;
                mines = MinesweeperGame.BEGINNER_MINES;
        }

        SwingUtilities.invokeLater(() -> new MinesweeperGame(rows, cols, mines));
    }
}
