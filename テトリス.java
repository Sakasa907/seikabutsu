import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TetrisFX extends Application {

    private static final int TILE_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final Color EMPTY_COLOR = Color.BLACK;

    private List<Rectangle> board;
    private Rectangle[][] grid;
    private char[][] currentBlock;
    private int currentBlockX, currentBlockY;
    private boolean canHold;
    private char[][] heldBlock;
    private int score;
    private int level;
    private Timeline timeline;
    private Label scoreLabel;
    private Label levelLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        board = new ArrayList<>();
        grid = new Rectangle[BOARD_HEIGHT][BOARD_WIDTH];
        currentBlock = Tetrominoes.getRandomTetromino();
        canHold = true;
        heldBlock = null;
        score = 0;
        level = 1;

        initializeBoard();
        createBoard();
        createLabels();
        createTimeline();

        Scene scene = new Scene(createGamePane(), BOARD_WIDTH * TILE_SIZE, BOARD_HEIGHT * TILE_SIZE + 50);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.setOnMouseClicked(event -> handleMouseClick(event));
        primaryStage.setTitle("TetrisFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGamePane() {
        GridPane gamePane = new GridPane();
        gamePane.setAlignment(Pos.CENTER);

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                gamePane.add(grid[i][j], j, i);
            }
        }

        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(scoreLabel, levelLabel);
        gamePane.add(infoBox, 0, BOARD_HEIGHT);

        return gamePane;
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE, EMPTY_COLOR);
                grid[i][j] = tile;
                board.add(tile);
            }
        }
    }

    private void createBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                grid[i][j].setFill(EMPTY_COLOR);
            }
        }
    }

    private void createLabels() {
        scoreLabel = new Label("Score: 0");
        levelLabel = new Label("Level: 1");
    }

    private void createTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> moveBlockDown()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleKeyPress(KeyCode code) {
        switch (code) {
            case LEFT:
                moveBlockLeft();
                break;
            case RIGHT:
                moveBlockRight();
                break;
            case DOWN:
                moveBlockDown();
                break;
            case UP:
                rotateBlock();
                break;
            case SPACE:
                hardDrop();
                break;
            case H:
                holdBlock();
                break;
        }
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            rotateBlock();
        } else if (event.isSecondaryButtonDown()) {
            hardDrop();
        }
    }

    private void moveBlockLeft() {
        if (isValidMove(currentBlockX - 1, currentBlockY, currentBlock)) {
            eraseBlock();
            currentBlockX--;
            drawBlock();
        }
    }

    private void moveBlockRight() {
        if (isValidMove(currentBlockX + 1, currentBlockY, currentBlock)) {
            eraseBlock();
            currentBlockX++;
            drawBlock();
        }
    }

    private void moveBlockDown() {
        if (isValidMove(currentBlockX, currentBlockY + 1, currentBlock)) {
            eraseBlock();
            currentBlockY++;
            drawBlock();
        } else {
            // Block reached the bottom, check for line clear or game over
            checkLineClear();
            currentBlock = Tetrominoes.getRandomTetromino();
            canHold = true;
            currentBlockX = BOARD_WIDTH / 2 - currentBlock[0].length / 2;
            currentBlockY = 0;

            if (!isValidMove(currentBlockX, currentBlockY, currentBlock)) {
                // Game over
                timeline.stop();
                System.out.println("Game Over! Score: " + score);
            }
        }
    }

    private void rotateBlock() {
        char[][] rotatedBlock = Tetrominoes.rotateTetromino(currentBlock);
        if (isValidMove(currentBlockX, currentBlockY, rotatedBlock)) {
            eraseBlock();
            currentBlock = rotatedBlock;
            drawBlock();
        }
    }

    private void hardDrop() {
        while (isValidMove(currentBlockX, currentBlockY + 1, currentBlock)) {
            moveBlockDown();
        }
    }

    private void holdBlock() {
        if (canHold) {
            if (heldBlock == null) {
                heldBlock = currentBlock;
                currentBlock = Tetrominoes.getRandomTetromino();
                canHold = false;
            } else {
                char[][] temp = heldBlock;
                heldBlock = currentBlock;
                currentBlock = temp;
                currentBlockX = BOARD_WIDTH / 2 - currentBlock[0].length / 2;
                currentBlockY = 0;
                canHold = false;
            }
            eraseBlock();
            drawBlock();
        }
    }

    private boolean isValidMove(int x, int y, char[][] tetromino) {
        if (x < 0 || x + tetromino[0].length > BOARD_WIDTH || y + tetromino.length > BOARD_HEIGHT) {
            return false;
        }

        for (int i = 0; i < tetromino.length; i++) {
            for (int j = 0; j < tetromino[i].length; j++) {
                if (tetromino[i][j] == 'X' && grid[y + i][x + j].getFill() != EMPTY_COLOR) {
                    return false;
                }
            }
        }

        return true;
    }

    private void drawBlock() {
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[i].length; j++) {
                if (currentBlock[i][j] == 'X') {
                    grid[currentBlockY + i][currentBlockX + j].setFill(Color.BLUE);
                }
            }
        }
    }

    private void eraseBlock() {
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[i].length; j++) {
                if (currentBlock[i][j] == 'X') {
                    grid[currentBlockY + i][currentBlockX + j].setFill(EMPTY_COLOR);
                }
            }
        }
    }

    private void checkLineClear() {
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineClear = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (grid[i][j].getFill() == EMPTY_COLOR) {
                    lineClear = false;
                    break;
                }
            }
            if (lineClear) {
                clearLine(i);
                moveLinesDown(i);
                i++;  // Check the same line again after moving down
                increaseScore();
            }
        }
    }

    private void clearLine(int row) {
        for (int j = 0; j < BOARD_WIDTH; j++) {
            grid[row][j].setFill(EMPTY_COLOR);
        }
    }

    private void moveLinesDown(int startRow) {
        for (int i = startRow; i > 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                grid[i][j].setFill(grid[i - 1][j].getFill());
            }
        }
    }

    private void increaseScore() {
        score += 100;
        scoreLabel.setText("Score: " + score);

        // Increase level every 500 points
        if (score % 500 == 0) {
            level++;
            levelLabel.setText("Level: " + level);
            // You can adjust the game speed based on the level
            timeline.setRate(1.0 + 0.1 * level);
        }
    }
}

class Tetrominoes {
    private static final char[][][] shapes = {
            {
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', ' ', ' '}
            },
            {
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', 'X', ' '}
            },
            {
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', ' ', ' '},
                    {' ', 'X', 'X', ' '}
            },
            {
                    {'X', 'X', ' '},
                    {'X', 'X', ' '}
            },
            {
                    {' ', 'X', 'X'},
                    {'X', 'X', ' '}
            },
            {
                    {'X', 'X', ' '},
                    {' ', 'X', 'X'}
            },
            {
                    {' ', 'X', 'X'},
                    {'X', 'X', ' '}
            }
    };

    static char[][] getRandomTetromino() {
        Random random = new Random();
        return shapes[random.nextInt(shapes.length)];
    }

    static char[][] rotateTetromino(char[][] tetromino) {
        int rows = tetromino.length;
        int cols = tetromino[0].length;
        char[][] rotated = new char[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - i - 1] = tetromino[i][j];
            }
        }

        return rotated;
    }
}
