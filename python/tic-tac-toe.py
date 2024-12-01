import random
import pygame
import sys

# Constants
WIDTH, HEIGHT = 600, 600
LINE_WIDTH = 15
BOARD_ROWS, BOARD_COLS = 3, 3
SQUARE_SIZE = WIDTH // BOARD_COLS
CIRCLE_RADIUS = SQUARE_SIZE // 3
CIRCLE_WIDTH = 15
CROSS_WIDTH = 25
SPACE = SQUARE_SIZE // 4

# Colors
BG_COLOR = (28, 170, 156)
LINE_COLOR = (23, 145, 135)
CIRCLE_COLOR = (239, 231, 200)
CROSS_COLOR = (66, 66, 66)
BUTTON_COLOR = (50, 50, 200)
BUTTON_TEXT_COLOR = (255, 255, 255)

# Initialize pygame
pygame.init()

# Set up the screen
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption('Tic Tac Toe')
screen.fill(BG_COLOR)

# Fonts
font = pygame.font.Font(None, 60)
button_font = pygame.font.Font(None, 40)

# Board
board = [[" " for _ in range(BOARD_COLS)] for _ in range(BOARD_ROWS)]

def draw_lines():
    # Horizontal lines
    pygame.draw.line(screen, LINE_COLOR, (0, SQUARE_SIZE), (WIDTH, SQUARE_SIZE), LINE_WIDTH)
    pygame.draw.line(screen, LINE_COLOR, (0, 2 * SQUARE_SIZE), (WIDTH, 2 * SQUARE_SIZE), LINE_WIDTH)
    # Vertical lines
    pygame.draw.line(screen, LINE_COLOR, (SQUARE_SIZE, 0), (SQUARE_SIZE, HEIGHT), LINE_WIDTH)
    pygame.draw.line(screen, LINE_COLOR, (2 * SQUARE_SIZE, 0), (2 * SQUARE_SIZE, HEIGHT), LINE_WIDTH)

def draw_figures():
    for row in range(BOARD_ROWS):
        for col in range(BOARD_COLS):
            if board[row][col] == 'O':
                pygame.draw.circle(screen, CIRCLE_COLOR, (int(col * SQUARE_SIZE + SQUARE_SIZE // 2), int(row * SQUARE_SIZE + SQUARE_SIZE // 2)), CIRCLE_RADIUS, CIRCLE_WIDTH)
            elif board[row][col] == 'X':
                pygame.draw.line(screen, CROSS_COLOR, (col * SQUARE_SIZE + SPACE, row * SQUARE_SIZE + SQUARE_SIZE - SPACE), (col * SQUARE_SIZE + SQUARE_SIZE - SPACE, row * SQUARE_SIZE + SPACE), CROSS_WIDTH)
                pygame.draw.line(screen, CROSS_COLOR, (col * SQUARE_SIZE + SPACE, row * SQUARE_SIZE + SPACE), (col * SQUARE_SIZE + SQUARE_SIZE - SPACE, row * SQUARE_SIZE + SQUARE_SIZE - SPACE), CROSS_WIDTH)

def check_winner(player):
    for row in range(BOARD_ROWS):
        if all(board[row][col] == player for col in range(BOARD_COLS)):
            draw_horizontal_winning_line(row, player)
            return True
    for col in range(BOARD_COLS):
        if all(board[row][col] == player for row in range(BOARD_ROWS)):
            draw_vertical_winning_line(col, player)
            return True
    if all(board[i][i] == player for i in range(BOARD_ROWS)):
        draw_desc_diagonal(player)
        return True
    if all(board[i][BOARD_ROWS - 1 - i] == player for i in range(BOARD_ROWS)):
        draw_asc_diagonal(player)
        return True
    return False

def check_tie():
    for row in range(BOARD_ROWS):
        for col in range(BOARD_COLS):
            if board[row][col] == " ":
                return False
    return True

def draw_horizontal_winning_line(row, player):
    posY = row * SQUARE_SIZE + SQUARE_SIZE // 2
    color = CROSS_COLOR if player == 'X' else CIRCLE_COLOR
    pygame.draw.line(screen, color, (15, posY), (WIDTH - 15, posY), LINE_WIDTH)

def draw_vertical_winning_line(col, player):
    posX = col * SQUARE_SIZE + SQUARE_SIZE // 2
    color = CROSS_COLOR if player == 'X' else CIRCLE_COLOR
    pygame.draw.line(screen, color, (posX, 15), (posX, HEIGHT - 15), LINE_WIDTH)

def draw_asc_diagonal(player):
    color = CROSS_COLOR if player == 'X' else CIRCLE_COLOR
    pygame.draw.line(screen, color, (15, HEIGHT - 15), (WIDTH - 15, 15), LINE_WIDTH)

def draw_desc_diagonal(player):
    color = CROSS_COLOR if player == 'X' else CIRCLE_COLOR
    pygame.draw.line(screen, color, (15, 15), (WIDTH - 15, HEIGHT - 15), LINE_WIDTH)

def restart():
    game_over = False
    screen.fill(BG_COLOR)
    draw_lines()
    for row in range(BOARD_ROWS):
        for col in range(BOARD_COLS):
            board[row][col] = " "

def draw_main_menu():
    screen.fill(BG_COLOR)
    two_player_button = pygame.Rect(WIDTH // 4, HEIGHT // 3, WIDTH // 2, 60)
    one_player_button = pygame.Rect(WIDTH // 4, HEIGHT // 2, WIDTH // 2, 60)
    pygame.draw.rect(screen, BUTTON_COLOR, two_player_button)
    pygame.draw.rect(screen, BUTTON_COLOR, one_player_button)

    two_player_text = button_font.render("2 Players", True, BUTTON_TEXT_COLOR)
    one_player_text = button_font.render("1 Player", True, BUTTON_TEXT_COLOR)
    screen.blit(two_player_text, (two_player_button.x + 50, two_player_button.y + 10))
    screen.blit(one_player_text, (one_player_button.x + 50, one_player_button.y + 10))

    return two_player_button, one_player_button

def draw_one_player_menu():
    screen.fill(BG_COLOR)
    play_first_button = pygame.Rect(WIDTH // 4, HEIGHT // 3, WIDTH // 2, 60)
    play_second_button = pygame.Rect(WIDTH // 4, HEIGHT // 2, WIDTH // 2, 60)
    pygame.draw.rect(screen, BUTTON_COLOR, play_first_button)
    pygame.draw.rect(screen, BUTTON_COLOR, play_second_button)

    play_first_text = button_font.render("Play First", True, BUTTON_TEXT_COLOR)
    play_second_text = button_font.render("Play Second", True, BUTTON_TEXT_COLOR)
    screen.blit(play_first_text, (play_first_button.x + 50, play_first_button.y + 10))
    screen.blit(play_second_text, (play_second_button.x + 50, play_second_button.y + 10))

    return play_first_button, play_second_button

def draw_end_menu():
    menu_button = pygame.Rect(WIDTH // 4, HEIGHT // 3, WIDTH // 2, 60)
    restart_button = pygame.Rect(WIDTH // 4, HEIGHT // 2, WIDTH // 2, 60)
    pygame.draw.rect(screen, BUTTON_COLOR, menu_button)
    pygame.draw.rect(screen, BUTTON_COLOR, restart_button)

    menu_text = button_font.render("Main Menu", True, BUTTON_TEXT_COLOR)
    restart_text = button_font.render("Restart", True, BUTTON_TEXT_COLOR)
    screen.blit(menu_text, (menu_button.x + 50, menu_button.y + 10))
    screen.blit(restart_text, (restart_button.x + 50, restart_button.y + 10))

    return menu_button, restart_button

def ai_move():
    empty_cells = [(row, col) for row in range(BOARD_ROWS) for col in range(BOARD_COLS) if board[row][col] == " "]
    if empty_cells:
        row, col = random.choice(empty_cells)
        board[row][col] = 'O'

player = 'X'
game_over = False
in_main_menu = True
in_game = False
in_one_player_menu = False
one_player_mode = False
player_starts = False

draw_lines()

# Main loop
while True:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()
        if in_main_menu:
            game_over = False
            two_player_button, one_player_button = draw_main_menu()
            pygame.display.update()
            if event.type == pygame.MOUSEBUTTONDOWN:
                if two_player_button.collidepoint(event.pos):
                    in_main_menu = False
                    in_game = True
                    one_player_mode = False
                    restart()
                elif one_player_button.collidepoint(event.pos):
                    in_main_menu = False
                    in_one_player_menu = True
        elif in_one_player_menu:
            play_first_button, play_second_button = draw_one_player_menu()
            pygame.display.update()
            if event.type == pygame.MOUSEBUTTONDOWN:
                if play_first_button.collidepoint(event.pos):
                    in_one_player_menu = False
                    in_game = True
                    one_player_mode = True
                    player_starts = True
                    restart()
                elif play_second_button.collidepoint(event.pos):
                    in_one_player_menu = False
                    in_game = True
                    one_player_mode = True
                    player_starts = False
                    restart()
                    ai_move()
                    draw_figures()
                    player = 'X'
                    
        elif in_game:
            if event.type == pygame.MOUSEBUTTONDOWN and not game_over:
                mouseX = event.pos[0]  # X coordinate
                mouseY = event.pos[1]  # Y coordinate

                clicked_row = mouseY // SQUARE_SIZE
                clicked_col = mouseX // SQUARE_SIZE

                if board[clicked_row][clicked_col] == " ":
                    board[clicked_row][clicked_col] = player
                    game_over = check_winner(player)
                    if not game_over and check_tie():
                        game_over = True
                    player = 'O' if player == 'X' else 'X'

                draw_figures()

                if one_player_mode and not game_over and player == 'O':
                    ai_move()
                    game_over = check_winner('O')
                    if not game_over and check_tie():
                        game_over = True
                    player = 'X'
                    draw_figures()
            if game_over:
                menu_button, restart_button = draw_end_menu()
                pygame.display.update()
                if event.type == pygame.MOUSEBUTTONDOWN:
                    if menu_button.collidepoint(event.pos):
                        in_game = False
                        in_main_menu = True
                    elif restart_button.collidepoint(event.pos):
                        restart()
                        player = 'X'
                        game_over = False
                        if one_player_mode and not player_starts:
                            ai_move()

        pygame.display.update()
