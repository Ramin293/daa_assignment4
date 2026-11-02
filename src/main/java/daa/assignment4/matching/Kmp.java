package daa.assignment4.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Kmp {
    private Kmp() {}

    public static MatchResult run(String text, String pattern) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(pattern, "pattern");
        List<Integer> positions = new ArrayList<>();
        if (pattern.isEmpty()) {
            for (int index = 0; index <= text.length(); index++) {
                positions.add(index);
            }
            return new MatchResult(positions, 0, 0);
        }
        int[] lps = new int[pattern.length()];
        long preprocessing = 0;
        int prefix = 0;
        int index = 1;
        while (index < pattern.length()) {
            preprocessing++;
            if (pattern.charAt(index) == pattern.charAt(prefix)) {
                lps[index++] = ++prefix;
            } else if (prefix > 0) {
                prefix = lps[prefix - 1];
            } else {
                lps[index++] = 0;
            }
        }
        long comparisons = 0;
        int matched = 0;
        index = 0;
        while (index < text.length()) {
            comparisons++;
            if (text.charAt(index) == pattern.charAt(matched)) {
                index++;
                matched++;
                if (matched == pattern.length()) {
                    positions.add(index - matched);
                    matched = lps[matched - 1];
                }
            } else if (matched > 0) {
                matched = lps[matched - 1];
            } else {
                index++;
            }
        }
        return new MatchResult(positions, preprocessing, comparisons);
    }
}
