
import java.util.*;

public class AIFinalProject {

    static Scanner input = new Scanner(System.in);
    private static GameBoard board;
    private static Person person;
    private static ComputerAI computerAI;
    static boolean endGame = false;

    static int x;

    public static void main(String[] args) {

        board = new GameBoard();
        person = new Person(board);
        computerAI = new ComputerAI(board, 6);     // search depht

        while (!endGame) {

            board.printBoard();

            outer:
            while (!board.gameOver()) {

                if (board.getLastPlayerNumber() == 2) {

                    x = inputCol();
                    switch (person.move(x, 2)) {
                        case -1:
                            board.printBoard();
                            board.changePlayerNumber();
                            break;
                        case -2:
                            continue;
                        case 1:
                            board.printBoard();
                            System.out.println("\nComputerAI win\n");
                            endGame();
                            break outer;
                        case 2:
                            board.printBoard();
                            System.out.println("\nPerson win\n");
                            endGame();
                            break outer;
                    }

                }
                if (board.getLastPlayerNumber() == 1) {

                    switch (computerAI.move(computerAI.chooseMove(1), 1)) {
                        case -1:
                            board.printBoard();
                            board.changePlayerNumber();
                            break;
                        case 1:
                            board.printBoard();
                            System.out.println("\nComputerAI win\n");
                            endGame();
                            break outer;
                        case 2:
                            board.printBoard();
                            System.out.println("\nPerson win\n");
                            endGame();
                            break outer;
                    }

                }

            }

            board.resetGameBoard();

        }

    }

    static int inputCol() {
        System.out.println("input number:");
        x = input.nextInt();
        return x;
    }

    static void endGame() {
        System.out.println("Reset game ?  ( y / n )");
        char ans = input.next().charAt(0);
        if (ans == 'y') {
            endGame = false;
        } else if (ans == 'n') {
            endGame = true;
        }
    }

}
//==========================================================================================================

class GameBoard {

    protected int[][] board;
    private int winner;             //0 means a draw if game is over
    protected int cellFilled;
    private boolean isGameOver;
    private int lastPlayerNumber;
    protected Stack<Move> movesList;

    //-----------------------------------------------------------------------------
    public GameBoard() {
        board = new int[8][8];
        winner = 0;
        cellFilled = 0;
        isGameOver = false;
        lastPlayerNumber = 2;
        movesList = new Stack<Move>();
        initializeBoard();
    }

    //-----------------------------------------------------------------------------
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = 0;
            }
        }
    }

    //-----------------------------------------------------------------------------
    public void changePlayerNumber() {
        lastPlayerNumber = 3 - lastPlayerNumber;
    }

    //-----------------------------------------------------------------------------
    public void printBoard() {
        clearConsole();
        System.out.println("|0 1 2 3 4 5 6 7|");

        char[][] copyBoard = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (board[i][j] == 0) {
                    copyBoard[i][j] = '_';
                } else if (board[i][j] == 1) {
                    copyBoard[i][j] = 'O';
                } else if (board[i][j] == 2) {
                    copyBoard[i][j] = 'X';
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            System.out.println("|" + copyBoard[i][0] + " " + copyBoard[i][1] + " "
                    + copyBoard[i][2] + " " + copyBoard[i][3] + " "
                    + copyBoard[i][4] + " " + copyBoard[i][5] + " "
                    + copyBoard[i][6] + " " + copyBoard[i][7] + "|");
        }
    }

    //-----------------------------------------------------------------------------
    public void clearConsole() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    //-----------------------------------------------------------------------------
    public int getLastPlayerNumber() {
        return lastPlayerNumber;
    }

    //-----------------------------------------------------------------------------
    public int[][] getBoard() {
        return board;
    }

    //-----------------------------------------------------------------------------
    public int getWinner() {
        return winner;
    }

    //-----------------------------------------------------------------------------
    public int getCellFilled() {
        return cellFilled;
    }

    //-----------------------------------------------------------------------------
    public boolean gameOver() {
        return isGameOver;
    }

    //-----------------------------------------------------------------------------
    public void resetGameBoard() {
        winner = 0;
        cellFilled = 0;
        isGameOver = false;
        lastPlayerNumber = 2;
        movesList.removeAllElements();
        initializeBoard();
    }

    //-----------------------------------------------------------------------------
    public void undoMove() {
        if (cellFilled > 0) {
            Move m = movesList.pop();
            board[m.getRow()][m.getCol()] = 0;
            winner = 0;
            cellFilled--;
            isGameOver = false;
            lastPlayerNumber = 3 - lastPlayerNumber;
        }
    }

    //-----------------------------------------------------------------------------
    public boolean placeNumber(int col, int playerNumber) {

        if ((col < 0) || (col > 7)) {
            System.out.println("invalid column\n\n");
            return false;
        } else {
            int row = findDepth(col);
            if (row == -1) {
                System.out.println("column is full");
                return false;
            } else {
                board[row][col] = playerNumber;
                lastPlayerNumber = playerNumber;
                movesList.push(new Move(row, col));
                cellFilled++;
                if (checkWin(playerNumber)) {
                    winner = playerNumber;
                    isGameOver = true;
                } else if (cellFilled == 42) {
                    isGameOver = true;
                }
                return true;
            }
        }
    }

    //-----------------------------------------------------------------------------
    public int findDepth(int col) {
        int depth = 0;
        while (depth < 8 && board[depth][col] == 0) {
            depth++;
        }
        --depth;
        return depth;
    }

    //-----------------------------------------------------------------------------
    private boolean checkWin(int playerNumber) {

        int row = movesList.peek().getRow();
        int col = movesList.peek().getCol();

        int groupSize = countGroupSize(row + 1, col, Direction.S, playerNumber);
        if (groupSize >= 3) {
            return true;
        }

        groupSize = countGroupSize(row, col + 1, Direction.E, playerNumber) + countGroupSize(row, col - 1, Direction.W, playerNumber);
        if (groupSize >= 3) {
            return true;
        }

        groupSize = countGroupSize(row - 1, col + 1, Direction.NE, playerNumber) + countGroupSize(row + 1, col - 1, Direction.SW, playerNumber);
        if (groupSize >= 3) {
            return true;
        }

        groupSize = countGroupSize(row - 1, col - 1, Direction.NW, playerNumber) + countGroupSize(row + 1, col + 1, Direction.SE, playerNumber);
        if (groupSize >= 3) {
            return true;
        }

        return false;
    }

    //-----------------------------------------------------------------------------
    public int countGroupSize(int row, int col, Direction dir, int playerNumber) {

        if (row < 8 && row > -1 && col < 8 && col > -1 && board[row][col] == playerNumber) {
            switch (dir) {
                case N:
                    return 1 + countGroupSize(row - 1, col, dir, playerNumber);
                case S:
                    return 1 + countGroupSize(row + 1, col, dir, playerNumber);
                case E:
                    return 1 + countGroupSize(row, col + 1, dir, playerNumber);
                case W:
                    return 1 + countGroupSize(row, col - 1, dir, playerNumber);
                case NE:
                    return 1 + countGroupSize(row - 1, col + 1, dir, playerNumber);
                case NW:
                    return 1 + countGroupSize(row - 1, col - 1, dir, playerNumber);
                case SE:
                    return 1 + countGroupSize(row + 1, col + 1, dir, playerNumber);
                case SW:
                    return 1 + countGroupSize(row + 1, col - 1, dir, playerNumber);
                default:
                    return 0;
            }

        } else {
            return 0;
        }
    }

    //-----------------------------------------------------------------------------
    public int findMax(int playerNumber) {

        int otherPlayer = 3 - playerNumber;
        int value = 0;
        int winningScore = 9999999;

        if (this.gameOver()) {
            if (this.getWinner() == playerNumber) {
                return winningScore;
            } else if (this.getWinner() == otherPlayer) {
                return -winningScore;
            } else {
                return 0;//  draw
            }
        }

        int valueMod2 = 59;
        int valueMod1 = 19;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                //For current Player
                if (board[r][c] == playerNumber) {

                    int groupSizeVertical = countGroupSize(r + 1, c, Direction.S, playerNumber) + countGroupSize(r - 1, c, Direction.N, playerNumber);
                    if (groupSizeVertical == 2) {
                        value += valueMod2;
                    } else if (groupSizeVertical == 1) {
                        value += valueMod1;
                    }

                    int groupSizeHorizontal = countGroupSize(r, c + 1, Direction.E, playerNumber) + countGroupSize(r, c - 1, Direction.W, playerNumber);
                    if (groupSizeHorizontal == 2) {
                        value += valueMod2;
                    } else if (groupSizeHorizontal == 1) {
                        value += valueMod1;
                    }

                    int groupSizeDiagonal = countGroupSize(r - 1, c + 1, Direction.NE, playerNumber) + countGroupSize(r + 1, c - 1, Direction.SW, playerNumber);
                    if (groupSizeDiagonal == 2) {
                        value += valueMod2;
                    } else if (groupSizeDiagonal == 1) {
                        value += valueMod1;
                    }

                    int groupSizeOdiagonal = countGroupSize(r - 1, c - 1, Direction.NW, playerNumber) + countGroupSize(r + 1, c + 1, Direction.SE, playerNumber);
                    if (groupSizeOdiagonal == 2) {
                        value += valueMod2;
                    } else if (groupSizeOdiagonal == 1) {
                        value += valueMod1;
                    }

                } //For Other Player
                else if (board[r][c] == otherPlayer) {

                    int groupSizeVertical = countGroupSize(r + 1, c, Direction.S, otherPlayer) + countGroupSize(r - 1, c, Direction.N, otherPlayer);
                    if (groupSizeVertical == 2) {
                        value -= valueMod2;
                    } else if (groupSizeVertical == 1) {
                        value -= valueMod1;
                    }

                    int groupSizeHorizontal = countGroupSize(r, c + 1, Direction.E, otherPlayer) + countGroupSize(r, c - 1, Direction.W, otherPlayer);
                    if (groupSizeHorizontal == 2) {
                        value -= valueMod2;
                    } else if (groupSizeHorizontal == 1) {
                        value -= valueMod1;
                    }

                    int groupSizeDiagonal = countGroupSize(r - 1, c + 1, Direction.NE, otherPlayer) + countGroupSize(r + 1, c - 1, Direction.SW, otherPlayer);
                    if (groupSizeDiagonal == 2) {
                        value -= valueMod2;
                    } else if (groupSizeDiagonal == 1) {
                        value -= valueMod1;
                    }

                    int groupSizeOdiagonal = countGroupSize(r - 1, c - 1, Direction.NW, otherPlayer) + countGroupSize(r + 1, c + 1, Direction.SE, otherPlayer);
                    if (groupSizeOdiagonal == 2) {
                        value -= valueMod2;
                    } else if (groupSizeOdiagonal == 1) {
                        value -= valueMod1;
                    }
                }
            }
        }

        return value;
    }
}
//=================================================

interface Player {

    public int move(int col, int playerNumber);
}
//=================================================

class Person implements Player {

    private GameBoard board;

    public Person(GameBoard board) {
        this.board = board;
    }

    @Override
    public int move(int col, int playerNumber) {
        if (!board.placeNumber(col, playerNumber)) {
            return -2;// move was not successful
        }
        if (board.gameOver()) {
            return board.getWinner();
        }
        return -1;// game not over
    }

}
//=================================================

class ComputerAI implements Player {

    private int searchDepth;
    private GameBoard board;

    public ComputerAI(GameBoard board, int searchDepth) {
        this.board = board;
        this.searchDepth = searchDepth;
    }

    //-----------------------------------------------------------------------------
    @Override
    public int move(int col, int playerNumber) {
        if (!board.placeNumber(col, playerNumber)) {
            throw new IllegalArgumentException(
                    "ComputerAI chose a full/invalid column");
        }
        if (board.gameOver()) {
            return board.getWinner();
        }
        return -1;// game not over
    }

    //-----------------------------------------------------------------------------
    public int chooseMove(int playerNumber) {
        int offset = 20;
        return negaMaxWithABPruning(0, playerNumber, 1, Integer.MIN_VALUE + offset, Integer.MAX_VALUE - offset).col;

        //return negaMax(0, playerNumber, 1).col;7
        
    }

    //-----------------------------------------------------------------------------
    public static class Pair {

        private int value;
        private int col;

        public Pair(int val, int col) {
            this.value = val;
            this.col = col;
        }
    }

    //-----------------------------------------------------------------------------
    private Pair negaMax(int depth, int playerNumber, int sign) {
        if (board.gameOver() || depth == searchDepth) {
            int util = sign * board.findMax(playerNumber);
            return new Pair(util, -1);// col doesn't matter
        }

        int max = Integer.MIN_VALUE;
        int col = 0;

        for (int i = 0; i < 8; i++) {
            if (board.findDepth(i) > -1) {
                if (sign == 1) {
                    board.placeNumber(i, playerNumber);
                } else {
                    board.placeNumber(i, 3 - playerNumber);
                }
                Pair p = negaMax(depth + 1, playerNumber, -sign);
                int x = -p.value;
                board.undoMove();

                if (x > max) {
                    max = x;
                    col = i;
                }
            }
        }
        return new Pair(max, col);
    }

    //-----------------------------------------------------------------------------
    private Pair negaMaxWithABPruning(int depth, int playerNumber, int sign, int alpha, int beta) {
        if (board.gameOver() || depth == searchDepth) {
            int util = sign * board.findMax(playerNumber);
            return new Pair(util, -1);// col doesn't matter 
        }

        int col = 0;
        int i = 0;

        while (i < 8 && alpha < beta) {
            if (board.findDepth(i) > -1) {
                if (sign == 1) {
                    board.placeNumber(i, playerNumber);
                } else {
                    board.placeNumber(i, 3 - playerNumber);
                }
                Pair p = negaMaxWithABPruning(depth + 1, playerNumber, -sign, -beta, -alpha);
                int x = -p.value;
                board.undoMove();

                if (x > alpha) {
                    alpha = x;
                    col = i;
                }
            }
            i++;
        }
        return new Pair(alpha, col);
    }

}
//=================================================

class Move {

    private int row;
    private int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
//=================================================

enum Direction {
    N, S, E, W, NE, NW, SE, SW
}
