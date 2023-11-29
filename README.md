# Minesweeper Project

Minesweeper is a classic single-player puzzle game implemented in Java, providing a graphical user interface for users to play the game.

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [How to Play](#how-to-play)
- [Bonus Feature](#bonus-feature)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features
- Classic Minesweeper gameplay with three difficulty levels: Beginner, Intermediate, Advanced.
- Customizable board size and number of mines.
- Timer to track the time spent on the game.
- Sound effects for mine explosions.
- Mark mines with flags to aid in gameplay.
- Score display indicating the number of uncovered cells.
- Responsive design for different difficulty levels.

## Project Structure
The project follows the following file structure:

```
📦src
 ┣ 📂minesweeper
 ┃ ┣ 📜Main.java
 ┃ ┗ 📜MinesweeperGame.java
 ┗ 📂resources
 ┃ ┣ 📜bomb.png
 ┃ ┗ 📜explosion_sound.wav
```

- `Main.java`: [Brief description of Main.java]
- `MinesweeperGame.java`: [Brief description of MinesweeperGame.java]

## How to Play
1. Run the `Main.java` file to start the game.
2. Choose the difficulty level: Beginner, Intermediate, or Advanced.
3. Click on cells to uncover them.
4. Use the right mouse button to mark cells with flags.
5. The game is won when all non-mine cells are uncovered.
6. The game is lost if a mine is uncovered.

## Bonus Feature
The project includes a bonus feature:
- **Automatic Uncovering**: A second click on a revealed location with the correct number of adjacent marked mines will automatically uncover all adjacent cells without mines.

## Installation
1. Clone the repository: `git clone https://github.com/TanzimHossain2/Minesweeper-Project.git`
2. Navigate to the project directory: `cd Minesweeper-Project`

## Usage
1. Compile and run the `Main.java` file using your preferred Java IDE.
2. Follow the on-screen instructions to choose the difficulty level and play the game.

## Contributing
Contributions are welcome! If you'd like to contribute to the project, please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bug fix: `git checkout -b feature-name`
3. Make changes and commit them: `git commit -m 'Description of changes'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request.

## License
This project is licensed under the [MIT License](LICENSE).
