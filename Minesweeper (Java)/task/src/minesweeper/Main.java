package minesweeper;

import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();
    static final int ROWS = 9;
    static final int COLS = 9;
    static char[][] field = new char[ROWS][COLS];
    static int correctGuesses, incorrectGuesses;
    static final char ZERO = 48; // char ASCII value for '0'
    static int numOfMines;
    static List<int[]> mineLocations = new ArrayList<>();
    static Map<int[], Character> threatMap = new HashMap<>();
    static boolean isBeginning = true, dead = false;

    public static void main(String[] args) {
        System.out.print("How many mines do you want on the field? ");
        numOfMines = scanner.nextInt();
        scanner.nextLine();
        populateField(numOfMines);
        metalDetector();
        printField();
        do {
            minesSwept();
        } while (!dead && correctGuesses < numOfMines && incorrectGuesses == 0);
        System.out.println(dead ? "You stepped on a mine and failed!" : "Congratulations! You found all the mines!");
    }

    public static void minesSwept() {
        correctGuesses = incorrectGuesses = 0;
        System.out.print("Set/unset mines marks or claim a cell as free: ");
        String[] input = scanner.nextLine().strip().split("\\s+");
        int y = Integer.parseInt(input[0]) - 1;
        int x = Integer.parseInt(input[1]) - 1;
        String mode = input[2].toLowerCase();
        switch (mode) {
            case "mine" -> mine(x, y);
            case "free" -> free(x, y);
        }
        printField();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (field[row][col] == '*' && mineLocations.contains(new int[]{col, row})) correctGuesses++;
                else if (field[row][col] == '*' && !mineLocations.contains(new int[]{col, row})) incorrectGuesses++;
            }
        }
    }

    public static void revealAll() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (isMine(row, col)) field[row][col] = 'X';
            }
        }
    }

    public static boolean isMine(int row, int col) {
        int[] targetArray = {row, col};
        for (int[] coordinates : mineLocations) {
            if (Arrays.equals(coordinates, targetArray)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasThreat(int[] coords) {
        for (int[] key : threatMap.keySet()) {
            if (Arrays.equals(key, coords)) {
                return true;
            }
        }
        return false;
    }

    public static Character getThreat(int[] coords) {
        for (int[] key : threatMap.keySet()) {
            if (Arrays.equals(key, coords)) {
                return threatMap.get(key);
            }
        }
        return null;
    }

    public static void free(int row, int col) {
        if (field[row][col] == '.') {
            int[] coords = {row, col};
            if (isMine(row, col) && isBeginning) {
                populateField(numOfMines);
                metalDetector();
                free(row, col);
            } else if (isMine(row, col) && !isBeginning) {
                revealAll();
                dead = true;
            } else if (hasThreat(coords)) {
                field[row][col] = getThreat(coords);
            } else {
                if (row > 0 && !isMine(row - 1, col)) { // top
                    field[row][col] = '/';
                    free(row - 1, col);
                } if (col > 0 && !isMine(row,col - 1)) { // left
                    field[row][col] = '/';
                    free(row,col - 1);
                } if (row < ROWS - 1 && !isMine(row + 1, col)) { // bottom
                    field[row][col] = '/';
                    free(row + 1, col);
                } if (col < COLS - 1 && !isMine(row,col + 1)) { // right
                    field[row][col] = '/';
                    free(row,col + 1);
                } if (row > 0 &&col> 0 && !isMine(row - 1,col - 1)) { // top left
                    field[row][col] = '/';
                    free(row - 1,col - 1);
                } if (row > 0 && col < COLS - 1 && !isMine(row - 1,col + 1)) { // top right
                    field[row][col] = '/';
                    free(row - 1,col + 1);
                } if (row < ROWS - 1 && col > 0 && !isMine(row + 1,col - 1)) { // bottom left
                    field[row][col] = '/';
                    free(row + 1,col - 1);
                } if (row < ROWS - 1 && col < COLS - 1 && !isMine(row + 1,col + 1)) { // bottom right
                    field[row][col] = '/';
                    free(row + 1,col + 1);
                }
            }
            isBeginning = false;
        }
    }

    public static void mine(int x, int y) {
        if (field[x][y] >= 48 && field[x][y] <= 56) System.out.println("There is a number here!");
        else if (field[x][y] == '*') field[x][y] = '.';
        else field[x][y] = '*';
    }

    public static void metalDetector() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (field[row][col] == '.') {
                    int[] coords = {row, col};
                    char threat = ZERO;
                    if (row > 0 && isMine(row - 1, col)) threatMap.put(coords, ++threat); // top
                    if (col > 0 && isMine(row, col - 1)) threatMap.put(coords, ++threat); // left
                    if (row < ROWS - 1 && isMine(row + 1, col)) threatMap.put(coords, ++threat); // bottom
                    if (col < COLS - 1 && isMine(row, col + 1)) threatMap.put(coords, ++threat); // right
                    if (row > 0 && col > 0 && isMine(row - 1, col - 1)) threatMap.put(coords, ++threat); // top left
                    if (row > 0 && col < COLS - 1 && isMine(row - 1, col + 1)) threatMap.put(coords, ++threat); // top right
                    if (row < ROWS - 1 && col > 0 && isMine(row + 1, col - 1)) threatMap.put(coords, ++threat); // btm left
                    if (row < ROWS - 1 && col < COLS - 1 && isMine(row + 1, col + 1)) threatMap.put(coords, ++threat); // btm right
                }
            }
        }
    }

    public static void populateField(int numOfMines) {
        mineLocations.clear();
        threatMap.clear();
        int mineCount = 0;
        for (char[] chars : field) {
            Arrays.fill(chars, '.');
        }
        while (mineCount < numOfMines) {
            int randomX = random.nextInt(9);
            int randomY = random.nextInt(9);
            if (field[randomX][randomY] == 'X') {
                continue;
            } else {
                mineLocations.add(new int[]{randomX, randomY});
            }
            mineCount++;
        }
    }

    public static void printField() {
        System.out.println("\n |123456789|\n-|---------|");
        for (int row = 0; row < ROWS; row++) {
            System.out.print((row + 1) + "|");
            for (int col = 0; col <= COLS; col++) {
                if (col == 9) System.out.println("|");
                else System.out.print(field[row][col]);
            }
        }
        System.out.println("-|---------|");
    }
}