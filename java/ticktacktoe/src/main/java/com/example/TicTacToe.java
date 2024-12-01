package com.example;

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
    private boolean gameOver = false;
    private final Text[][] board = new Text[BOARD_SIZE][BOARD_SIZE];
    private Pane linePane; // Separate Pane for the winning line
    private StackPane gameStackPane; // Container for the game grid
    private Line winningLine;
    private static final int CELL_SIZE = 100; // Size of each cell
    private static final int SPACING = 10; // Spacing between cells
    private static final int OFFSET = CELL_SIZE / 2; // Offset to center the line in the cell
    private Button restartButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tic Tac Toe");

        // Main menu layout
        VBox mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);
        Button twoPlayersButton = new Button("2 Players");
        Button onePlayerButton = new Button("1 Player");
        mainMenu.getChildren().addAll(twoPlayersButton, onePlayerButton);

        Scene menuScene = new Scene(mainMenu, 400, 400);
        primaryStage.setScene(menuScene);
        primaryStage.show();

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

                Text text = new Text("");
                text.setFont(Font.font(36));
                board[row][col] = text;

                StackPane cellContainer = new StackPane();
                cellContainer.setAlignment(Pos.CENTER);
                cellContainer.getChildren().addAll(cell, text);

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

        // VBox containing the game grid and restart button
        VBox gameContainer = new VBox(10);
        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.getChildren().addAll(gameStackPane, restartButton);

        gameStackPane.getChildren().addAll(gameGrid, linePane); // Add both grid and line pane to the StackPane

        Scene gameScene = new Scene(gameContainer, 400, 500);


        // Main menu button actions
        twoPlayersButton.setOnAction(event -> {
            currentPlayer = "X";
            gameOver = false;
            resetBoard();
            primaryStage.setScene(gameScene);
        });
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
            return;
        }

        // Switch to the other player
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
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
        return false;
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
        // Clear all cells on the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col].setText("");
            }
        }
        // Remove the winning line if it exists
        if (winningLine != null) {
            linePane.getChildren().remove(winningLine);
            winningLine = null;
        }
        // Hide the restart button
        restartButton.setVisible(false);
        // Reset the game state
        gameOver = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
