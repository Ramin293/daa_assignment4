package daa.assignment4.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Naive {
    private Naive() {}

    public static MatchResult run(String text, String pattern) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(pattern, "pattern");
        List<Integer> positions = new ArrayList<>();
        long comparisons = 0;
        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int index = 0;
            while (index < pattern.length()) {
                comparisons++;
                if (text.charAt(start + index) != pattern.charAt(index)) {
                    break;
                }
                index++;
            }
            if (index == pattern.length()) {
                positions.add(start);
            }
        }
        return new MatchResult(positions, 0, comparisons);
    }
}
