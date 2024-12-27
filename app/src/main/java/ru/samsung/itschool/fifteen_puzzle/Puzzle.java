package ru.samsung.itschool.fifteen_puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Puzzle {
    private final int[][] board;
    private final int size;
    private int emptyRow;
    private int emptyCol;

    public Puzzle(int size) {
        this.size = size;
        board = new int[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        List<Integer> tiles = new ArrayList<>();
        for (int i = 0; i < size * size - 1; i++) {
            tiles.add(i + 1);
        }
        tiles.add(0);

        do {
            Collections.shuffle(tiles);
        } while (!isSolvable(tiles, generateGoalState(), size));

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = tiles.get(i * size + j);
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }
    }

    private List<Integer> generateGoalState() {
        List<Integer> goal = new ArrayList<>();
        for (int i = 0; i < size * size - 1; i++) {
            goal.add(i + 1);
        }
        goal.add(0);
        return goal;
    }

    private int getInversions(List<Integer> list) {
        int inversions = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            int n = list.get(i);
            if (n <= 1) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                int m = list.get(j);
                if (m > 0 && n > m) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    private boolean isSolvable(List<Integer> start, List<Integer> goal, int width) {
        int startInversions = getInversions(start);
        int goalInversions = getInversions(goal);

        if (width % 2 == 0) {
            int goalZeroRow = goal.indexOf(0) / width;
            int startZeroRow = start.indexOf(0) / width;
            if (goalInversions % 2 == 0) {
                return startInversions % 2 == (goalZeroRow + startZeroRow) % 2;
            } else {
                return startInversions % 2 != (goalZeroRow + startZeroRow) % 2;
            }
        } else {
            return startInversions % 2 == goalInversions % 2;
        }
    }

    public boolean moveTile(int row, int col) {
        if (canMove(row, col)) {
            board[emptyRow][emptyCol] = board[row][col];
            board[row][col] = 0;
            emptyRow = row;
            emptyCol = col;
            return true;
        }
        return false;
    }

    private boolean canMove(int row, int col) {
        return (Math.abs(emptyRow - row) == 1 && emptyCol == col) ||
                (Math.abs(emptyCol - col) == 1 && emptyRow == row);
    }

    public boolean isSolved() {
        int expected = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == size - 1 && j == size - 1) {
                    return board[i][j] == 0;
                }
                if (board[i][j] != expected++) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return board;
    }
}
