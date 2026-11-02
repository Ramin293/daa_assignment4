package daa.assignment4.dp;

import java.util.Objects;

/** Table-based Levenshtein distance with unit insertion, deletion, and substitution costs. */
public final class EditDistance {
    private EditDistance() {}

    public static EditDistanceResult run(String first, String second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        int[][] distances = new int[first.length() + 1][second.length() + 1];
        for (int row = 0; row <= first.length(); row++) {
            distances[row][0] = row;
        }
        for (int column = 0; column <= second.length(); column++) {
            distances[0][column] = column;
        }
        long cellsEvaluated = 0;
        for (int row = 1; row <= first.length(); row++) {
            for (int column = 1; column <= second.length(); column++) {
                int cost = first.charAt(row - 1) == second.charAt(column - 1) ? 0 : 1;
                distances[row][column] = Math.min(
                        distances[row - 1][column - 1] + cost,
                        Math.min(distances[row - 1][column] + 1,
                                distances[row][column - 1] + 1));
                cellsEvaluated++;
            }
        }
        return new EditDistanceResult(
                distances[first.length()][second.length()], cellsEvaluated);
    }
}
