package edu.khai.chursin.rabbitmq.entity;

import java.util.Arrays;

public class Game {

    private static final String LINE = "\n----------------------------------\n";

    private String[][] cells;

    public Game() {
        this.cells = new String[3][3];
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j] == null) {
                    sb.append("\t[").append(i).append(";").append(j).append("]");
                } else {
                    sb.append(cells[i][j]);
                }
                sb.append("\t").append("|");
            }
            sb.setLength(sb.length() - 1);
            sb.append(LINE);
        }
        System.out.println(sb);
    }

    public boolean isFinished(String str) {

        String s = "\t\t" + str;

        String[][] cells = this.getCells();

        for (String[] cell : cells) {
            if (s.equals(cell[0]) && s.equals(cell[1]) && s.equals(cell[2])) {
                return true;
            }
        }

        for (int i = 0; i < cells.length; i++) {
            if (s.equals(cells[0][i]) && s.equals(cells[1][i]) && s.equals(cells[2][i])) {
                return true;
            }
        }
        return s.equals(cells[0][0]) && s.equals(cells[1][1]) && s.equals(cells[2][2])
                || s.equals(cells[0][2]) && s.equals(cells[1][1]) && s.equals(cells[2][0]);
    }

    public boolean checkDraw() {
        boolean isDraw = true;
        for (String[] cell : cells) {
            for (String c : cell) {
                if (c == null) {
                    isDraw = false;
                }
            }
        }
        return isDraw;
    }

    public void finish(boolean isWinner) {
        if (isWinner) {
            System.out.println("YOU WON!");
        } else {
            System.out.println("YOU LOSE!");
        }
    }

    public void finish() {
        System.out.println(".....DRAW.....");
    }

    public boolean isCellFree(int[] id) {
        return this.getCells()[id[0]][id[1]] == null;
    }

    public String[][] getCells() {
        return cells;
    }

    public void setCells(String[][] cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(cells);
    }
}
