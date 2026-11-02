package daa.assignment4.experiment;

import daa.assignment4.dp.EditDistance;
import daa.assignment4.dp.LongestCommonSubsequence;
import daa.assignment4.lcp.Kasai;
import daa.assignment4.matching.Kmp;
import daa.assignment4.matching.MatchResult;
import daa.assignment4.matching.Naive;
import daa.assignment4.matching.RabinKarp;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Benchmark {
    private static final int WARMUPS = 3;
    private static final int REPETITIONS = 5;
    private static volatile int sink;

    private record Observation(long preprocessingComparisons, long operations, String outcome) {}
    private record Row(String algorithm, String workload, int size, int patternLength,
            double meanNanos, Observation observation) {}

    private Benchmark() {}

    public static void run(Path csvPath) throws IOException {
        Random random = new Random(42);
        List<Row> rows = new ArrayList<>();
        for (int size : new int[] {100, 500, 1000}) {
            String first = randomString(random, size);
            String second = randomString(random, size);
            rows.add(measure("LCS", "random", size, 0,
                    () -> LongestCommonSubsequence.run(first, second),
                    result -> new Observation(0, result.cellsEvaluated(), Integer.toString(result.length()))));
            rows.add(measure("Edit Distance", "random", size, 0,
                    () -> EditDistance.run(first, second),
                    result -> new Observation(0, result.cellsEvaluated(), Integer.toString(result.distance()))));
        }
        for (int size : new int[] {10000, 50000, 100000}) {
            String text = randomString(random, size);
            String present = text.substring(size / 2, size / 2 + 16);
            String absent;
            do {
                absent = randomString(random, 16);
            } while (text.contains(absent));
            for (String workload : List.of("present", "absent")) {
                String pattern = workload.equals("present") ? present : absent;
                MatchResult baseline = Naive.run(text, pattern);
                if (!baseline.positions().equals(Kmp.run(text, pattern).positions())
                        || !baseline.positions().equals(RabinKarp.run(text, pattern).positions())
                        || (workload.equals("present") && baseline.positions().isEmpty())
                        || (workload.equals("absent") && !baseline.positions().isEmpty())) {
                    throw new IllegalStateException("Matching results or workload disagree");
                }
                rows.add(measure("Naive", workload, size, pattern.length(),
                        () -> Naive.run(text, pattern), Benchmark::observe));
                rows.add(measure("KMP", workload, size, pattern.length(),
                        () -> Kmp.run(text, pattern), Benchmark::observe));
                rows.add(measure("Rabin-Karp", workload, size, pattern.length(),
                        () -> RabinKarp.run(text, pattern), Benchmark::observe));
            }
        }
        for (int size : new int[] {1000, 5000, 10000}) {
            String text = randomString(random, size);
            int[] suffixArray = suffixArray(text);
            rows.add(measure("Kasai", "random", size, 0,
                    () -> Kasai.run(text, suffixArray), result -> new Observation(
                            0, result.characterComparisons(), Integer.toString(result.lengths().hashCode()))));
        }
        Path parent = csvPath.toAbsolutePath().getParent();
        Path table = parent.resolve("tables/summary.md");
        Files.createDirectories(parent);
        Files.createDirectories(table.getParent());
        StringBuilder csv = new StringBuilder("algorithm,workload,size,pattern_length,mean_ns,preprocessing_comparisons,operations,outcome\n");
        StringBuilder summary = new StringBuilder("# Benchmark results\n\n"
                + "Five-run mean; operations are DP cells or search/LCP character comparisons.\n\n"
                + "| Algorithm | Workload | Size | Pattern length | Mean (ms) | Preprocessing comparisons | Operations | Outcome |\n"
                + "| --- | --- | ---: | ---: | ---: | ---: | ---: | --- |\n");
        for (Row row : rows) {
            Observation o = row.observation();
            csv.append(String.format(Locale.ROOT, "%s,%s,%d,%d,%.1f,%d,%d,%s%n",
                    row.algorithm(), row.workload(), row.size(), row.patternLength(), row.meanNanos(),
                    o.preprocessingComparisons(), o.operations(), o.outcome()));
            summary.append(String.format(Locale.ROOT, "| %s | %s | %d | %d | %.4f | %d | %d | %s |%n",
                    row.algorithm(), row.workload(), row.size(), row.patternLength(), row.meanNanos() / 1_000_000,
                    o.preprocessingComparisons(), o.operations(), o.outcome()));
        }
        Files.writeString(csvPath, csv, StandardCharsets.UTF_8);
        Files.writeString(table, summary, StandardCharsets.UTF_8);
        System.out.println("Wrote " + rows.size() + " benchmark rows to " + csvPath);
        System.out.println("Wrote summary tables to " + table);
    }

    private static <T> Row measure(String algorithm, String workload, int size, int patternLength,
            Supplier<T> action, Function<T, Observation> observe) {
        for (int run = 0; run < WARMUPS; run++) {
            sink = action.get().hashCode();
        }
        long total = 0;
        Observation expected = null;
        for (int run = 0; run < REPETITIONS; run++) {
            long start = System.nanoTime();
            T result = action.get();
            long elapsed = System.nanoTime() - start;
            Observation actual = observe.apply(result);
            if (expected != null && !expected.equals(actual)) {
                throw new IllegalStateException("Non-reproducible result for " + algorithm);
            }
            expected = actual;
            sink = result.hashCode();
            total += elapsed;
        }
        return new Row(algorithm, workload, size, patternLength, (double) total / REPETITIONS, expected);
    }

    private static Observation observe(MatchResult result) {
        return new Observation(result.preprocessingComparisons(), result.searchComparisons(),
                Integer.toString(result.positions().size()));
    }

    private static String randomString(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            text.append((char) ('a' + random.nextInt(26)));
        }
        return text.toString();
    }

    /** Simple construction outside the timed region; suitable for these input sizes. */
    private static int[] suffixArray(String text) {
        Integer[] indices = new Integer[text.length()];
        for (int index = 0; index < indices.length; index++) {
            indices[index] = index;
        }
        Arrays.sort(indices, Comparator.comparing(text::substring));
        return Arrays.stream(indices).mapToInt(Integer::intValue).toArray();
    }
}
