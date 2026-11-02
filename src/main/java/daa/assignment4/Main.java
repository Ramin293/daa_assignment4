package daa.assignment4;

import daa.assignment4.lcp.Kasai;
import daa.assignment4.experiment.Benchmark;
import java.nio.file.Path;
import daa.assignment4.matching.Kmp;
import daa.assignment4.matching.Naive;
import daa.assignment4.matching.RabinKarp;
import daa.assignment4.dp.EditDistance;
import daa.assignment4.dp.LongestCommonSubsequence;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        String command = args.length == 0 ? "--help" : args[0];
        if (args.length > 2 || (args.length > 1 && !command.equals("--benchmark"))) {
            throw new IllegalArgumentException("Unexpected arguments");
        }
        switch (command) {
            case "--test" -> System.out.println(CorrectnessSuite.runAll());
            case "--demo" -> demo();
            case "--benchmark" -> Benchmark.run(Path.of(
                    args.length > 1 ? args[1] : "results/results.csv"));
            case "--help" -> System.out.println("Usage: java -cp target/classes "
                    + "daa.assignment4.Main --test | --demo | --benchmark [output.csv] | --help");
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static void demo() {
        String first = "ABCBDAB";
        String second = "BDCABA";
        var result = LongestCommonSubsequence.run(first, second);
        System.out.println("First string: " + first);
        System.out.println("Second string: " + second);
        System.out.println("LCS length: " + result.length()
                + " | cells evaluated=" + result.cellsEvaluated());

        System.out.println("Naive matches: " + Naive.run("abababa", "aba").positions());
        System.out.println("KMP matches: " + Kmp.run("abababa", "aba").positions());
        System.out.println("Rabin-Karp matches: " + RabinKarp.run("abababa", "aba").positions());

        int[] suffixArray = {5, 3, 1, 0, 4, 2};
        System.out.println("banana LCP: " + Kasai.run("banana", suffixArray).lengths());

        String source = "kitten";
        String target = "sitting";
        var edits = EditDistance.run(source, target);
        System.out.println("Edit distance " + source + " -> " + target + ": "
                + edits.distance() + " | cells evaluated=" + edits.cellsEvaluated());
    }
}
