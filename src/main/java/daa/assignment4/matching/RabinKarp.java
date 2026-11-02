package daa.assignment4.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Rolling polynomial hash with character-by-character collision verification. */
public final class RabinKarp {
    private static final long BASE = 256;
    private static final long MODULUS = 1_000_000_007;

    private RabinKarp() {}

    public static MatchResult run(String text, String pattern) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(pattern, "pattern");
        List<Integer> positions = new ArrayList<>();
        int length = pattern.length();
        if (length == 0) {
            for (int index = 0; index <= text.length(); index++) {
                positions.add(index);
            }
            return new MatchResult(positions, 0, 0);
        }
        if (length > text.length()) {
            return new MatchResult(positions, 0, 0);
        }
        long power = 1;
        long patternHash = 0;
        long windowHash = 0;
        for (int index = 0; index < length; index++) {
            patternHash = (patternHash * BASE + pattern.charAt(index)) % MODULUS;
            windowHash = (windowHash * BASE + text.charAt(index)) % MODULUS;
            if (index > 0) {
                power = power * BASE % MODULUS;
            }
        }
        long comparisons = 0;
        for (int start = 0; start <= text.length() - length; start++) {
            if (windowHash == patternHash) {
                int index = 0;
                while (index < length) {
                    comparisons++;
                    if (text.charAt(start + index) != pattern.charAt(index)) {
                        break;
                    }
                    index++;
                }
                if (index == length) {
                    positions.add(start);
                }
            }
            if (start < text.length() - length) {
                long outgoing = text.charAt(start) * power % MODULUS;
                windowHash = (windowHash - outgoing + MODULUS) % MODULUS;
                windowHash = (windowHash * BASE + text.charAt(start + length)) % MODULUS;
            }
        }
        return new MatchResult(positions, 0, comparisons);
    }
}
