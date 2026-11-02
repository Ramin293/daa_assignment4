package daa.assignment4;

import daa.assignment4.dp.EditDistance;
import daa.assignment4.dp.LongestCommonSubsequence;
import daa.assignment4.lcp.Kasai;
import daa.assignment4.matching.Kmp;
import daa.assignment4.matching.Naive;
import daa.assignment4.matching.RabinKarp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public final class CorrectnessSuite {
    private static int checks;

    private CorrectnessSuite() {}

    public static String runAll() {
        checks = 0;
        longestCommonSubsequences();
        editDistances();
        stringMatching();
        longestCommonPrefixes();
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

    private static void stringMatching() {
        checkMatching("abcabc", "abc", List.of(0, 3));
        checkMatching("xxabcxx", "abc", List.of(2));
        checkMatching("xxabc", "abc", List.of(2));
        checkMatching("aaaaa", "aa", List.of(0, 1, 2, 3));
        checkMatching("abc", "z", List.of());
        checkMatching("ab", "abc", List.of());
        checkMatching("abc", "", List.of(0, 1, 2, 3));
        checkMatching("", "", List.of(0));
        checkMatching("", "a", List.of());
        checkMatching("ababababac", "ababac", List.of(4));
        expectThrows(NullPointerException.class, () -> Naive.run(null, "a"));
        expectThrows(NullPointerException.class, () -> Naive.run("a", null));
        expectThrows(NullPointerException.class, () -> Kmp.run(null, "a"));
        expectThrows(NullPointerException.class, () -> Kmp.run("a", null));
        expectThrows(NullPointerException.class, () -> RabinKarp.run(null, "a"));
        expectThrows(NullPointerException.class, () -> RabinKarp.run("a", null));
        // Base 256 causes these distinct two-character strings to have the same hash.
        checkMatching("\u0001\u0000", "\u0000\u0100", List.of());
        require(RabinKarp.run("\u0001\u0000", "\u0000\u0100").searchComparisons() > 0,
                "A hash collision is verified character by character");
        checkMatching("\u0000\u0100\u0001\u0000\u0000\u0100", "\u0000\u0100", List.of(0, 4));
        Random random = new Random(42);
        for (int trial = 0; trial < 500; trial++) {
            String text = randomString(random, random.nextInt(35));
            String pattern = randomString(random, random.nextInt(10));
            List<Integer> expected = new ArrayList<>();
            for (int start = 0; start <= text.length() - pattern.length(); start++) {
                if (text.startsWith(pattern, start)) {
                    expected.add(start);
                }
            }
            checkMatching(text, pattern, expected);
        }
        expectThrows(UnsupportedOperationException.class,
                () -> Kmp.run("aaa", "a").positions().clear());
    }

    private static void checkMatching(String text, String pattern, List<Integer> expected) {
        require(Naive.run(text, pattern).positions().equals(expected), "Naive occurrences match");
        require(Kmp.run(text, pattern).positions().equals(expected), "KMP occurrences match");
        require(RabinKarp.run(text, pattern).positions().equals(expected), "Rabin-Karp occurrences match");
    }

    private static void longestCommonPrefixes() {
        require(Kasai.run("banana", new int[] {5, 3, 1, 0, 4, 2}).lengths()
                .equals(List.of(0, 1, 3, 0, 0, 2)), "Known banana suffixes and LCP");
        require(Kasai.run("", new int[] {}).lengths().isEmpty(), "Empty text has empty LCP");
        require(Kasai.run("a", new int[] {0}).lengths().equals(List.of(0)), "Single suffix");
        require(Kasai.run("aaaa", new int[] {3, 2, 1, 0}).lengths()
                .equals(List.of(0, 1, 2, 3)), "Repeated suffix prefixes");
        expectThrows(NullPointerException.class, () -> Kasai.run(null, new int[] {}));
        expectThrows(NullPointerException.class, () -> Kasai.run("a", null));
        expectThrows(IllegalArgumentException.class, () -> Kasai.run("ab", new int[] {0}));
        expectThrows(IllegalArgumentException.class, () -> Kasai.run("ab", new int[] {0, 0}));
        expectThrows(IllegalArgumentException.class, () -> Kasai.run("ab", new int[] {-1, 1}));
        expectThrows(IllegalArgumentException.class, () -> Kasai.run("ab", new int[] {0, 2}));
        expectThrows(IllegalArgumentException.class, () -> Kasai.run("ab", new int[] {1, 0}));
        expectThrows(IllegalArgumentException.class, () -> Kasai.run("aaa", new int[] {0, 1, 2}));
        expectThrows(UnsupportedOperationException.class,
                () -> Kasai.run("a", new int[] {0}).lengths().clear());
        Random random = new Random(42);
        for (int trial = 0; trial < 500; trial++) {
            String text = randomString(random, random.nextInt(30));
            Integer[] starts = new Integer[text.length()];
            for (int index = 0; index < starts.length; index++) {
                starts[index] = index;
            }
            Arrays.sort(starts, Comparator.comparing(text::substring));
            int[] suffixArray = Arrays.stream(starts).mapToInt(Integer::intValue).toArray();
            List<Integer> expected = new ArrayList<>();
            for (int index = 0; index < suffixArray.length; index++) {
                int common = 0;
                if (index > 0) {
                    int left = suffixArray[index - 1];
                    int right = suffixArray[index];
                    while (left + common < text.length() && right + common < text.length()
                            && text.charAt(left + common) == text.charAt(right + common)) {
                        common++;
                    }
                }
                expected.add(common);
            }
            require(Kasai.run(text, suffixArray).lengths().equals(expected),
                    "Kasai matches direct adjacent-suffix comparisons");
        }
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
