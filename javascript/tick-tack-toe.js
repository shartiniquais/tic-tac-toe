// HTML Elements
const menu = document.getElementById("menu");
const game = document.getElementById("game");
const endMenu = document.getElementById("endMenu");
const onePlayerMenu = document.getElementById("onePlayerMenu");
const twoPlayersButton = document.getElementById("twoPlayers");
const onePlayerButton = document.getElementById("onePlayer");
const playFirstButton = document.getElementById("playFirst");
const playSecondButton = document.getElementById("playSecond");
const mainMenuButton = document.getElementById("mainMenu");
const restartGameButton = document.getElementById("restartGame");
const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

// Game Variables
let board = Array.from(Array(3), () => Array(3).fill(""));
let currentPlayer = "X";
let gameOver = false;
let onePlayerMode = false;
let playerStarts = true;

// Event Listeners
twoPlayersButton.addEventListener("click", () => startGame(false));
onePlayerButton.addEventListener("click", () => {
    menu.classList.remove("active");
    onePlayerMenu.classList.add("active");
});
playFirstButton.addEventListener("click", () => startGame(true, true));
playSecondButton.addEventListener("click", () => startGame(true, false));
mainMenuButton.addEventListener("click", () => switchToMenu());
restartGameButton.addEventListener("click", () => {
    resetGame();
    if (!playerStarts && onePlayerMode) {
        aiMove();
        drawBoard();
        currentPlayer = "X";
    }
    game.classList.add("active");
    endMenu.classList.remove("active");
});
canvas.addEventListener("click", handleClick);

// Start Game Function
function startGame(isOnePlayer, startsFirst = true) {
    onePlayerMode = isOnePlayer;
    playerStarts = startsFirst;
    menu.classList.remove("active");
    onePlayerMenu.classList.remove("active");
    game.classList.add("active");
    resetGame();
    drawBoard();
    if (!playerStarts && onePlayerMode) {
        aiMove();
        drawBoard();
        currentPlayer = "X";
    }
}

// Switch to Main Menu
function switchToMenu() {
    endMenu.classList.remove("active");
    menu.classList.add("active");
    game.classList.remove("active");
    onePlayerMenu.classList.remove("active");
    resetGame();
}

// Draw the Game Board
function drawBoard() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.lineWidth = 5;
    ctx.strokeStyle = "#179187";

    for (let i = 1; i < 3; i++) {
        ctx.beginPath();
        ctx.moveTo(i * 200, 0);
        ctx.lineTo(i * 200, canvas.height);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(0, i * 200);
        ctx.lineTo(canvas.width, i * 200);
        ctx.stroke();
    }
    drawFigures();
}

// Draw Xs and Os
function drawFigures() {
    for (let row = 0; row < 3; row++) {
        for (let col = 0; col < 3; col++) {
            if (board[row][col] === "X") {
                drawX(col, row);
            } else if (board[row][col] === "O") {
                drawO(col, row);
            }
        }
    }
}

function drawX(col, row) {
    const padding = 50;
    ctx.strokeStyle = "#424242";
    ctx.beginPath();
    ctx.moveTo(col * 200 + padding, row * 200 + padding);
    ctx.lineTo((col + 1) * 200 - padding, (row + 1) * 200 - padding);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo((col + 1) * 200 - padding, row * 200 + padding);
    ctx.lineTo(col * 200 + padding, (row + 1) * 200 - padding);
    ctx.stroke();
}

function drawO(col, row) {
    const centerX = col * 200 + 100;
    const centerY = row * 200 + 100;
    const radius = 70;
    ctx.strokeStyle = "#efe7c8";
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
    ctx.stroke();
}

// Handle Player Clicks
function handleClick(event) {
    if (gameOver) return;
    const rect = canvas.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;

    const col = Math.floor(mouseX / 200);
    const row = Math.floor(mouseY / 200);

    if (board[row][col] === "") {
        board[row][col] = currentPlayer;
        currentPlayer = currentPlayer === "X" ? "O" : "X";
        drawBoard();
        if (checkWinner() || checkTie()) {
            gameOver = true;
            endMenu.classList.add("active");
        } else if (onePlayerMode && currentPlayer === "O") {
            aiMove();
            drawBoard();
            if (checkWinner() || checkTie()) {
                gameOver = true;
                endMenu.classList.add("active");
            }
            currentPlayer = "X";
        }
    }
}

// AI Move (Random Empty Spot)
function aiMove() {
    const emptyCells = [];
    for (let row = 0; row < 3; row++) {
        for (let col = 0; col < 3; col++) {
            if (board[row][col] === "") emptyCells.push({ row, col });
        }
    }
    if (emptyCells.length > 0) {
        const { row, col } = emptyCells[Math.floor(Math.random() * emptyCells.length)];
        board[row][col] = "O";
    }
}

// Check for Winner
function checkWinner() {
    for (let i = 0; i < 3; i++) {
        if (board[i][0] && board[i][0] === board[i][1] && board[i][1] === board[i][2]) {
            drawWinningLine(i, 0, i, 2);
            return true;
        }
        if (board[0][i] && board[0][i] === board[1][i] && board[1][i] === board[2][i]) {
            drawWinningLine(0, i, 2, i);
            return true;
        }
    }
    if (board[0][0] && board[0][0] === board[1][1] && board[1][1] === board[2][2]) {
        drawWinningLine(0, 0, 2, 2);
        return true;
    }
    if (board[0][2] && board[0][2] === board[1][1] && board[1][1] === board[2][0]) {
        drawWinningLine(0, 2, 2, 0);
        return true;
    }
    return false;
}

// Check for Tie
function checkTie() {
    return board.flat().every(cell => cell !== "");
}

// Draw Winning Line
function drawWinningLine(row1, col1, row2, col2) {
    ctx.strokeStyle = "red";
    ctx.lineWidth = 10;
    ctx.beginPath();
    ctx.moveTo(col1 * 200 + 100, row1 * 200 + 100);
    ctx.lineTo(col2 * 200 + 100, row2 * 200 + 100);
    ctx.stroke();
}

// Reset Game
function resetGame() {
    board = Array.from(Array(3), () => Array(3).fill(""));
    currentPlayer = playerStarts ? "X" : "O";
    gameOver = false;
    drawBoard();
}
