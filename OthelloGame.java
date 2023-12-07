import java.util.Scanner;

public class OthelloGame {

    private static final int BOARD_SIZE = 8;
    private static char[][] board = new char[BOARD_SIZE][BOARD_SIZE];

    private static final char EMPTY = '-';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';

    private static char currentPlayer = PLAYER_X;

    public static void main(String[] args) {
        initializeBoard();
        printBoard();

        while (true) {
            playTurn();
            printBoard();

            if (isGameOver()) {
                break;
            }

            switchPlayer();
        }

        announceWinner();
    }

    private static void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }

        // 初期配置
        board[3][3] = PLAYER_X;
        board[3][4] = PLAYER_O;
        board[4][3] = PLAYER_O;
        board[4][4] = PLAYER_X;
    }

    private static void printBoard() {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void playTurn() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Player " + currentPlayer + "'s turn.");
        System.out.print("Enter row (0-7): ");
        int row = scanner.nextInt();

        System.out.print("Enter column (0-7): ");
        int col = scanner.nextInt();

        if (isValidMove(row, col)) {
            board[row][col] = currentPlayer;
            flipTiles(row, col);
        } else {
            System.out.println("Invalid move. Try again.");
            playTurn();
        }
    }

    private static boolean isValidMove(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col] != EMPTY) {
            return false;
        }

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE &&
                        board[newRow][newCol] == getOpponent()) {
                    while (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE &&
                            board[newRow][newCol] == getOpponent()) {
                        newRow += i;
                        newCol += j;
                    }

                    if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE &&
                            board[newRow][newCol] == currentPlayer) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void flipTiles(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE &&
                        board[newRow][newCol] == getOpponent()) {
                    while (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE &&
                            board[newRow][newCol] == getOpponent()) {
                        newRow += i;
                        newCol += j;
                    }

                    if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE &&
                            board[newRow][newCol] == currentPlayer) {
                        while (newRow != row || newCol != col) {
                            newRow -= i;
                            newCol -= j;
                            board[newRow][newCol] = currentPlayer;
                        }
                    }
                }
            }
        }
    }

    private static boolean isGameOver() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY && isValidMove(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void switchPlayer() {
        currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    private static char getOpponent() {
        return (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    private static void announceWinner() {
        int countX = 0;
        int countO = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == PLAYER_X) {
                    countX++;
                } else if (board[i][j] == PLAYER_O) {
                    countO++;
                }
            }
        }

        if (countX > countO) {
            System.out.println("Player X wins!");
        } else if (countO > countX) {
            System.out.println("Player O wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
