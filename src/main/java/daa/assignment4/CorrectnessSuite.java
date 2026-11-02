package daa.assignment4;

import daa.assignment4.dp.LongestCommonSubsequence;
import java.util.Random;

public final class CorrectnessSuite {
    private static int checks;

    private CorrectnessSuite() {}

    public static String runAll() {
        checks = 0;
        longestCommonSubsequences();
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
