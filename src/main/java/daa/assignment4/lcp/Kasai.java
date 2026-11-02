package daa.assignment4.lcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Builds LCP values from a supplied lexicographically sorted suffix array. */
public final class Kasai {
    private Kasai() {}

    public static LcpResult run(String text, int[] suffixArray) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(suffixArray, "suffixArray");
        int length = text.length();
        if (suffixArray.length != length) {
            throw new IllegalArgumentException("Suffix array length must match text length");
        }
        int[] rank = new int[length];
        boolean[] seen = new boolean[length];
        for (int index = 0; index < length; index++) {
            int start = suffixArray[index];
            if (start < 0 || start >= length || seen[start]) {
                throw new IllegalArgumentException("Suffix array must be a permutation of text indices");
            }
            seen[start] = true;
            rank[start] = index;
        }
        // Adjacent suffixes are sorted iff their first units and subsequent suffix ranks are ordered.
        for (int index = 1; index < length; index++) {
            int left = suffixArray[index - 1];
            int right = suffixArray[index];
            int leftTail = left + 1 == length ? -1 : rank[left + 1];
            int rightTail = right + 1 == length ? -1 : rank[right + 1];
            if (text.charAt(left) > text.charAt(right)
                    || (text.charAt(left) == text.charAt(right) && leftTail >= rightTail)) {
                throw new IllegalArgumentException("Suffix array must be lexicographically sorted");
            }
        }
        int[] lcp = new int[length];
        int common = 0;
        long comparisons = 0;
        for (int start = 0; start < length; start++) {
            int position = rank[start];
            if (position == 0) {
                common = 0;
                continue;
            }
            int previous = suffixArray[position - 1];
            while (common < length - start && common < length - previous) {
                comparisons++;
                if (text.charAt(start + common) != text.charAt(previous + common)) {
                    break;
                }
                common++;
            }
            lcp[position] = common;
            if (common > 0) {
                common--;
            }
        }
        List<Integer> values = new ArrayList<>(length);
        for (int value : lcp) {
            values.add(value);
        }
        return new LcpResult(values, comparisons);
    }
}
