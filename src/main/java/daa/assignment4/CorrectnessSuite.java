package daa.assignment4;

import daa.assignment4.dp.EditDistance;
import daa.assignment4.dp.LongestCommonSubsequence;
import java.util.Random;

public final class CorrectnessSuite {
    private static int checks;

    private CorrectnessSuite() {}

    public static String runAll() {
        checks = 0;
        longestCommonSubsequences();
        editDistances();
        return "PASS: " + checks + " checks";
    }

    private static void longestCommonSubsequences() {
        checkLcs("algorithm", "algorithm", 9);
        checkLcs("abc", "XYZ", 0);
        checkLcs("", "abc", 0);
        checkLcs("abc", "", 0);
        checkLcs("", "", 0);
        checkLcs("ABCBDAB", "BDCABA", 4);
        checkLcs("aaaa", "aa", 2);
        checkLcs("abc", "ac", 2);
        checkLcs("a", "a", 1);
        checkLcs("A", "a", 0);
        checkLcs("a b!", "ab!", 3);
        checkLcs("\u043a\u043e\u0442", "\u043a\u0438\u0442", 2);
        expectThrows(NullPointerException.class,
                () -> LongestCommonSubsequence.run(null, "abc"));
        expectThrows(NullPointerException.class,
                () -> LongestCommonSubsequence.run("abc", null));

        Random random = new Random(42);
        for (int trial = 0; trial < 200; trial++) {
            String first = randomString(random, random.nextInt(9));
            String second = randomString(random, random.nextInt(9));
            int expected = exhaustiveLcs(first, second);
            checkLcs(first, second, expected);
            checkLcs(second, first, expected);
        }
    }

    private static void checkLcs(String first, String second, int expected) {
        var result = LongestCommonSubsequence.run(first, second);
        require(result.length() == expected, "LCS length matches expected value");
        require(result.cellsEvaluated() == (long) first.length() * second.length(),
                "LCS evaluates every non-base cell exactly once");
    }

    /** Independent reference: enumerate subsequences rather than fill a DP table. */
    private static int exhaustiveLcs(String first, String second) {
        int best = 0;
        for (int mask = 0; mask < (1 << first.length()); mask++) {
            int next = 0;
            int matched = 0;
            boolean found = true;
            for (int index = 0; index < first.length(); index++) {
                if ((mask & (1 << index)) == 0) {
                    continue;
                }
                while (next < second.length()
                        && second.charAt(next) != first.charAt(index)) {
                    next++;
                }
                if (next == second.length()) {
                    found = false;
                    break;
                }
                next++;
                matched++;
            }
            if (found) {
                best = Math.max(best, matched);
            }
        }
        return best;
    }

    private static void editDistances() {
        checkEditDistance("algorithm", "algorithm", 0);
        checkEditDistance("", "abc", 3);
        checkEditDistance("abc", "", 3);
        checkEditDistance("", "", 0);
        checkEditDistance("cat", "cut", 1);
        checkEditDistance("cat", "cart", 1);
        checkEditDistance("cart", "cat", 1);
        checkEditDistance("kitten", "sitting", 3);
        checkEditDistance("abc", "XYZ", 3);
        checkEditDistance("aaaa", "aa", 2);
        checkEditDistance("ab", "ba", 2);
        checkEditDistance("A", "a", 1);
        checkEditDistance("a b!", "ab!", 1);
        checkEditDistance("\u043a\u043e\u0442", "\u043a\u0438\u0442", 1);
        expectThrows(NullPointerException.class, () -> EditDistance.run(null, "abc"));
        expectThrows(NullPointerException.class, () -> EditDistance.run("abc", null));

        Random random = new Random(42);
        for (int trial = 0; trial < 200; trial++) {
            String first = randomString(random, random.nextInt(6));
            String second = randomString(random, random.nextInt(6));
            int expected = recursiveEditDistance(first, second, first.length(), second.length());
            checkEditDistance(first, second, expected);
            checkEditDistance(second, first, expected);
        }
    }

    private static void checkEditDistance(String first, String second, int expected) {
        var result = EditDistance.run(first, second);
        require(result.distance() == expected, "Edit distance matches expected value");
        require(result.cellsEvaluated() == (long) first.length() * second.length(),
                "Edit distance evaluates every non-base cell exactly once");
    }

    /** Exhaustively explore edit choices on short prefixes without a DP table. */
    private static int recursiveEditDistance(String first, String second, int row, int column) {
        if (row == 0) {
            return column;
        }
        if (column == 0) {
            return row;
        }
        if (first.charAt(row - 1) == second.charAt(column - 1)) {
            return recursiveEditDistance(first, second, row - 1, column - 1);
        }
        return 1 + Math.min(recursiveEditDistance(first, second, row - 1, column - 1),
                Math.min(recursiveEditDistance(first, second, row - 1, column),
                        recursiveEditDistance(first, second, row, column - 1)));
    }

    private static String randomString(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            text.append((char) ('a' + random.nextInt(3)));
        }
        return text.toString();
    }

    private static void require(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectThrows(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) {
                checks++;
                return;
            }
            throw new AssertionError("Expected " + type.getSimpleName(), error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
