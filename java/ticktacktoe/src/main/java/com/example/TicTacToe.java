package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * TicTacToe is a simple JavaFX application that simulates the classic Tic Tac Toe game.
 */
public class TicTacToe extends Application {

    private static final int BOARD_SIZE = 3;
    private String currentPlayer = "X";
    private String startingPlayer = "X"; // Keeps track of who starts the game
    private boolean gameOver = false;
    private boolean isAIEnabled = false;
    private Text[][] board;
    private Pane linePane; // Separate Pane for the winning line
    private StackPane gameStackPane; // Container for the game grid
    private Line winningLine;
    private static final int CELL_SIZE = 100; // Size of each cell
    private static final int SPACING = 10; // Spacing between cells
    private static final int OFFSET = CELL_SIZE / 2; // Offset to center the line in the cell
    private Button restartButton;
    private Button mainMenuButton;
    private VBox buttonContainer;
    private final Random random = new Random();
    private Scene menuScene;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tic Tac Toe");

        // Main menu layout
        VBox mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);
        Button twoPlayersButton = new Button("2 Players");
        Button onePlayerButton = new Button("1 Player");
        mainMenu.getChildren().addAll(twoPlayersButton, onePlayerButton);

        menuScene = new Scene(mainMenu, 400, 400);
        primaryStage.setScene(menuScene);
        primaryStage.show();

        // Buttons for 1 Player options
        Button playFirstButton = new Button("Play First");
        Button playSecondButton = new Button("Play Second");
        Button goBackButton = new Button("Go Back");

        // VBox for 1 Player buttons
        VBox onePlayerOptions = new VBox(20);
        onePlayerOptions.setAlignment(Pos.CENTER);
        onePlayerOptions.getChildren().addAll(playFirstButton, playSecondButton, goBackButton);

        Scene onePlayerScene = new Scene(onePlayerOptions, 400, 400);

        // Set up action for "1 Player" button
        onePlayerButton.setOnAction(event -> {
            primaryStage.setScene(onePlayerScene);
        });

        // Set up action for "Go Back" button
        goBackButton.setOnAction(event -> {
            primaryStage.setScene(menuScene);
        });

        // Set up actions for "Play First" and "Play Second"
        playFirstButton.setOnAction(event -> {
            isAIEnabled = true;
            startingPlayer = "X";
            currentPlayer = startingPlayer;
            gameOver = false;
            initializeBoard();
            primaryStage.setScene(getGameScene(primaryStage));
        });

        playSecondButton.setOnAction(event -> {
            isAIEnabled = true;
            startingPlayer = "O";
            currentPlayer = startingPlayer;
            gameOver = false;
            initializeBoard();
            primaryStage.setScene(getGameScene(primaryStage));
            makeAIMove(); // AI makes the first move
        });

        // Main menu button actions
        twoPlayersButton.setOnAction(event -> {
            isAIEnabled = false;
            startingPlayer = "X";
            currentPlayer = startingPlayer;
            gameOver = false;
            initializeBoard();
            primaryStage.setScene(getGameScene(primaryStage));
        });
    }

    /**
     * Initializes the game board.
     */
    private void initializeBoard() {
        board = new Text[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new Text("");
                board[row][col].setFont(Font.font(36));
            }
        }
    }
    

   /**
     * Creates and returns the game scene.
     *
     * @param primaryStage The main stage.
     * @return the game scene.
     */
    private Scene getGameScene(Stage primaryStage) {
        // Game layout
        gameStackPane = new StackPane();
        gameStackPane.setAlignment(Pos.CENTER); // Ensure the grid is centered

        GridPane gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        gameGrid.setHgap(SPACING);
        gameGrid.setVgap(SPACING);

        // Create cells for each game position
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);

                StackPane cellContainer = new StackPane();
                cellContainer.setAlignment(Pos.CENTER);
                cellContainer.getChildren().addAll(cell, board[row][col]);

                final int currentRow = row;
                final int currentCol = col;
                cellContainer.setOnMouseClicked(event -> handlePlayerMove(currentRow, currentCol));

                gameGrid.add(cellContainer, col, row);
            }
        }

        linePane = new Pane(); // Pane to handle drawing the winning line independently
        linePane.setPickOnBounds(false); // Ignore mouse events on the Pane to allow grid interaction

        // Restart button
        restartButton = new Button("Restart");
        restartButton.setOnAction(event -> resetBoard());
        restartButton.setVisible(false); // Hide the restart button initially

        // Main menu button
        mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(event -> resetToMainMenu(primaryStage));
        mainMenuButton.setVisible(false); // Hide the main menu button initially

        // VBox containing the restart and main menu buttons
        buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(restartButton, mainMenuButton);
        buttonContainer.setVisible(false); // Hide the button container initially

        // Add game grid, line pane, and button container to the stack pane
        gameStackPane.getChildren().addAll(gameGrid, linePane);

        VBox gameContainer = new VBox(10);
        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.getChildren().addAll(gameStackPane, buttonContainer);

        return new Scene(gameContainer, 400, 500);
    }


    /**
     * Handles a player's move at the specified row and column.
     *
     * @param row The row where the player clicked.
     * @param col The column where the player clicked.
     */
    private void handlePlayerMove(int row, int col) {
        // If the game is over or the cell is not empty, ignore the click
        if (gameOver || !board[row][col].getText().isEmpty()) {
            return;
        }

        // Set the text of the cell to the current player's symbol
        board[row][col].setText(currentPlayer);

        // Check if the current player has won
        if (checkWinner()) {
            gameOver = true;
            // Show the restart and main menu buttons when the game is over
            restartButton.setVisible(true);
            mainMenuButton.setVisible(true);
            buttonContainer.setVisible(true);
            return;
        }

        // Switch to the other player or AI
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
        
        // If AI is enabled and it's the AI's turn, make a move
        if (isAIEnabled && currentPlayer.equals("O")) {
            makeAIMove();
        }
    }

    /**
     * Makes a move for the AI player.
     */
    private void makeAIMove() {
        List<int[]> availableMoves = new ArrayList<>();

        // Find all available moves
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col].getText().isEmpty()) {
                    availableMoves.add(new int[]{row, col});
                }
            }
        }

        // Select a random move
        if (!availableMoves.isEmpty()) {
            int[] move = availableMoves.get(random.nextInt(availableMoves.size()));
            handlePlayerMove(move[0], move[1]);
        }
    }

    /**
     * Checks if there is a winner.
     *
     * @return True if there is a winner, false otherwise.
     */
    private boolean checkWinner() {
        // Check rows for a win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!board[i][0].getText().isEmpty() && board[i][0].getText().equals(board[i][1].getText())
                    && board[i][1].getText().equals(board[i][2].getText())) {
                drawWinningLine(i, 0, i, 2);
                return true;
            }
        }
        // Check columns for a win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!board[0][i].getText().isEmpty() && board[0][i].getText().equals(board[1][i].getText())
                    && board[1][i].getText().equals(board[2][i].getText())) {
                drawWinningLine(0, i, 2, i);
                return true;
            }
        }
        // Check diagonals for a win
        if (!board[0][0].getText().isEmpty() && board[0][0].getText().equals(board[1][1].getText())
                && board[1][1].getText().equals(board[2][2].getText())) {
            drawWinningLine(0, 0, 2, 2);
            return true;
        }
        if (!board[0][2].getText().isEmpty() && board[0][2].getText().equals(board[1][1].getText())
                && board[1][1].getText().equals(board[2][0].getText())) {
            drawWinningLine(0, 2, 2, 0);
            return true;
        }

        // Check for a tie
        boolean isTie = true;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col].getText().isEmpty()) {
                    isTie = false;
                    break;
                }
            }
        }
        return isTie;
    }

    /**
     * Draws a red line to indicate the winning combination.
     *
     * @param startRow The starting row of the winning line.
     * @param startCol The starting column of the winning line.
     * @param endRow   The ending row of the winning line.
     * @param endCol   The ending column of the winning line.
     */
    private void drawWinningLine(int startRow, int startCol, int endRow, int endCol) {
        double gridOffsetX = (400 - (BOARD_SIZE * CELL_SIZE + (BOARD_SIZE - 1) * SPACING)) / 2;
        double gridOffsetY = (325 - (BOARD_SIZE * CELL_SIZE + (BOARD_SIZE - 1) * SPACING)) / 2;

        // Calculate the starting and ending coordinates of the winning line
        double startX = gridOffsetX + startCol * (CELL_SIZE + SPACING) + OFFSET;
        double startY = gridOffsetY + startRow * (CELL_SIZE + SPACING) + OFFSET;
        double endX = gridOffsetX + endCol * (CELL_SIZE + SPACING) + OFFSET;
        double endY = gridOffsetY + endRow * (CELL_SIZE + SPACING) + OFFSET;

        // Create the winning line
        winningLine = new Line(startX, startY, endX, endY);
        winningLine.setStroke(Color.RED);
        winningLine.setStrokeWidth(5);

        // Add the winning line to linePane
        linePane.getChildren().add(winningLine);

        // Show the restart button
        restartButton.setVisible(true);
    }

     /**
     * Resets the game board for a new game.
     */
    private void resetBoard() {
        if (board != null) {
            // Clear all cells on the board
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (board[row][col] != null) {
                        board[row][col].setText("");
                    }
                }
            }
        }

        // Remove the winning line if it exists
        if (winningLine != null) {
            linePane.getChildren().remove(winningLine);
            winningLine = null;
        }

        // Hide the restart button
        if (restartButton != null) {
            restartButton.setVisible(false);
        }

        // Hide the main menu button
        if (mainMenuButton != null) {
            mainMenuButton.setVisible(false);
        }

        // Hide the button container
        if (buttonContainer != null) {
            buttonContainer.setVisible(false);
        }

        // Reset the game state
        gameOver = false;

        // Reset currentPlayer to the starting player
        currentPlayer = startingPlayer;

        // If AI is enabled and the AI starts the game, make a move
        if (isAIEnabled && startingPlayer.equals("O")) {
            makeAIMove();
        }
    }

    /**
     * Resets to the main menu scene.
     */
    private void resetToMainMenu(Stage primaryStage) {
        resetBoard();
        primaryStage.setScene(menuScene); // Set back to main menu scene
    }

    public static void main(String[] args) {
        launch(args);
    }
}
