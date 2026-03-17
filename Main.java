import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] command = new String[]{""};
        String[] players = new String[2];

        while (!command[0].equals("exit")) {
            System.out.print("Input command: ");
            command = scanner.nextLine().trim().split(" ");

            if (command[0].equals("start")) {
                if (command.length != 3) {
                    System.out.println("Bad parameters!");
                    continue;
                }

                System.arraycopy(command, 1, players, 0, 2);

                play(players);
            }
        }
    }

    private static void play(String[] players) {
        Grid grid = new Grid();
        int turn = 0;

        while (!grid.isWinner(grid.grid, 'X') && !grid.isWinner(grid.grid, 'O') && grid.moves != 9) {
            switch (players[turn % 2]) {
                case "user" -> grid.doPlayerMove();
                case "easy" -> grid.doEasyBotMove();
                case "medium" -> grid.doMediumBotMove(turn % 2 == 1 ? 'O' : 'X');
                case "hard" -> grid.doHardBotMove(turn % 2 == 1 ? 'O' : 'X');
                case "default" -> System.out.println("error");
            }

            grid.printGrid();
            turn++;
        }

        if (grid.isWinner(grid.grid,'X') || grid.isWinner(grid.grid,'O')) {
            System.out.printf("%c wins\n", grid.isWinner(grid.grid,'X') ? 'X' : 'O');
        } else {
            System.out.println("Draw");
        }
    }
}

class Grid {
    int moves = 0;
    char[] grid = new char[9];
    final Random RANDOM = new Random();
    final Scanner SCANNER = new Scanner(System.in);

    public Grid() {
        for (int i = 0; i < 9; i++) {
            grid[i] = ' ';
        }
        printGrid();
    }

    protected void place(int pos) {
        grid[pos] = moves % 2 == 0 ? 'X' : 'O';
        moves++;
    }


    public void printGrid() {
        System.out.println("---------");

        for (int row = 0; row < 3; row++) {
            System.out.print("|");

            for (int column = 0; column < 3; column++) {
                System.out.printf(" %c", grid[row * 3 + column]);
            }

            System.out.println(" |");
        }

        System.out.println("---------");
    }

    private int parseInput(String userInput) {
        String[] tokens = userInput.split(" ");
        int[] pos = new int[2];

        for (int i = 0; i < tokens.length; i++) {
            try {
                pos[i] = Integer.parseInt(tokens[i]) - 1;
            } catch (Exception e) {
                System.out.println("You should enter numbers!");
                return -1;
            }
            if (pos[i] < 0 || pos[i] > 2) {
                System.out.println("Coordinates should be from 1 to 3!");
                return -1;
            }
        }

        if (grid[pos[1] + 3 * pos[0]] == 'X' || grid[pos[1] + 3 * pos[0]] == 'O') {
            System.out.println("This cell is occupied! Choose another one!");
            return -1;
        }

        return pos[1] + 3 * pos[0];
    }

    private int getWinningMove(char symbol) {
        for (int i = 0; i < 9; i++) {
            if (grid[i] == ' ') {
                grid[i] = symbol;
                if (isWinner(grid, symbol)) {
                    grid[i] = ' ';
                    return i;
                } else {
                    grid[i] = ' ';
                }
            }
        }

        return -1;
    }

    private int getMinimaxPos(char[] board, char botChar, char playingChar, int moves) {
        if (moves == 0) {
            return 4;
        }
        if (isWinner(board, botChar)) {
            return 1;
        } else if (isWinner(board, botChar == 'X' ? 'O' : 'X')) {
            return -1;
        } else if (moves == 9) {
            return 0;
        }

        int[][] options = new int[9 - moves][2];
        int n = 0;

        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = playingChar;
                options[n][0] = i;
                options[n][1] = getMinimaxPos(board, botChar, playingChar == 'X' ? 'O' : 'X', moves + 1);
                board[i] = ' ';
                n++;
            }
        }

        int score;
        if (this.moves == moves) {
            score = -2;
            int pos = -1;
            for (int[] option: options) {
                if (option[1] > score) {
                    score = option[1];
                    pos = option[0];
                }
            }
            return pos;
        }


        if (playingChar == botChar) {
            score = -2;
            for (int[] option: options) {
                if (option[1] > score) {
                    score = option[1];
                }
            }
        } else {
            score = 2;
            for (int[] option: options) {
                if (option[1] < score) {
                    score = option[1];
                }
            }
        }

        return score;
    }

    protected boolean isWinner(char[] grid, char playerSymbol) {
        for (int i = 0; i < 3; i++) {
            if (grid[i * 3] == playerSymbol && grid[1 + i * 3] == playerSymbol && grid[2 + i * 3] == playerSymbol) {
                return true;
            } else if (grid[i] == playerSymbol && grid[i + 3] == playerSymbol && grid[i + 6] == playerSymbol) {
                return true;
            }
        }
        return grid[4] == playerSymbol && ((grid[2] == playerSymbol && grid[6] == playerSymbol) || (grid[0] == playerSymbol && grid[8] == playerSymbol));
    }

    private int getRandomPos() {
        int pos;

        do {
            pos = RANDOM.nextInt(9);
        } while (grid[pos] == 'X' || grid[pos] == 'O');

        return pos;
    }

    protected void doHardBotMove(char botChar) {
        System.out.println("Making move level \"hard\"");
        int pos = getMinimaxPos(grid, botChar, botChar, moves);

        place(pos);
    }

    protected void doMediumBotMove(char botChar) {
        System.out.println("Making move level \"medium\"");

        int botPos = getWinningMove(botChar);
        if (botPos != -1) {
            place(botPos);
            return;
        }

        int enemyPos = getWinningMove(botChar == 'X' ? 'O' : 'X');
        if (enemyPos != -1) {
            place(enemyPos);
            return;
        }

        place(getRandomPos());
    }

    protected void doEasyBotMove() {
        System.out.println("Making move level \"easy\"");
        place(getRandomPos());
    }

    protected void doPlayerMove() {
        int position = -1;

        System.out.println("Enter the coordinates:");

        while (position == -1) {
            position = parseInput(SCANNER.nextLine().trim());
        }

        place(position);
    }
}