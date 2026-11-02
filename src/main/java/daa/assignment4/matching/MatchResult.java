package daa.assignment4.matching;

import java.util.List;

/** Occurrence positions and character comparisons in preprocessing and search. */
public record MatchResult(List<Integer> positions, long preprocessingComparisons, long searchComparisons) {
    public MatchResult {
        positions = List.copyOf(positions);
    }
}
