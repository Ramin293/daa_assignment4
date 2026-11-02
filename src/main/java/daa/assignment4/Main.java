package daa.assignment4;

import daa.assignment4.dp.LongestCommonSubsequence;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        String command = args.length == 0 ? "--help" : args[0];
        if (args.length > 1) {
            throw new IllegalArgumentException("Expected at most one command");
        }
        switch (command) {
            case "--test" -> System.out.println(CorrectnessSuite.runAll());
            case "--demo" -> demo();
            case "--help" -> System.out.println("Usage: java -cp target/classes "
                    + "daa.assignment4.Main --test | --demo | --help");
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
    }
}
