package daa.assignment4.dp;

/** Levenshtein distance and the number of non-base DP cells evaluated. */
public record EditDistanceResult(int distance, long cellsEvaluated) {}
