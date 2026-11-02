package daa.assignment4.dp;

import java.util.Objects;

/** Table-based LCS over Java UTF-16 code units. */
public final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {}

    public static LcsResult run(String first, String second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        int[][] lengths = new int[first.length() + 1][second.length() + 1];
        long cellsEvaluated = 0;
        for (int row = 1; row <= first.length(); row++) {
            for (int column = 1; column <= second.length(); column++) {
                if (first.charAt(row - 1) == second.charAt(column - 1)) {
                    lengths[row][column] = lengths[row - 1][column - 1] + 1;
                } else {
                    lengths[row][column] = Math.max(
                            lengths[row - 1][column], lengths[row][column - 1]);
                }
                cellsEvaluated++;
            }
        }
        return new LcsResult(lengths[first.length()][second.length()], cellsEvaluated);
    }
}
