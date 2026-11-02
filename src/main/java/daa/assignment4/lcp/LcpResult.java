package daa.assignment4.lcp;

import java.util.List;

/** Adjacent-suffix LCP lengths and character comparisons. */
public record LcpResult(List<Integer> lengths, long characterComparisons) {
    public LcpResult {
        lengths = List.copyOf(lengths);
    }
}
