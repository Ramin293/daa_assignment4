# Assignment 4: Text Analysis with Dynamic Programming and String Algorithms

## Project overview

This Java project studies longest common subsequences (LCS), Levenshtein edit distance, Naive string matching, Knuth-Morris-Pratt (KMP), Rabin-Karp, and Kasai's longest common prefix (LCP) array algorithm.

The first development milestone implements table-based LCS, operation counting, a demonstration, and a correctness suite. The remaining algorithms and experimental report will be added in the commits listed below. This README is the report; no separate PDF report is required.

## How to run

Use Java JDK 17 or newer. From the project folder on Windows:

```powershell
.\run.cmd demo
.\run.cmd test
.\run.cmd all
```

The launcher compiles the Java sources automatically. `all` runs the tests and demonstration. Double-clicking `run.cmd` runs the demonstration and waits for a key before closing. No external Java dependencies are required.

Alternatively, compile and run directly from PowerShell:

```powershell
$classes = "target/classes"
New-Item -ItemType Directory -Force $classes | Out-Null
$sources = Get-ChildItem src/main/java -Filter *.java -Recurse | ForEach-Object FullName
javac -encoding UTF-8 --release 17 -d $classes $sources
java -cp $classes daa.assignment4.Main --test
java -cp $classes daa.assignment4.Main --demo
```

## Implementation

`LongestCommonSubsequence.run` returns an immutable `LcsResult` containing the LCS length and the number of evaluated non-base table cells. Inputs are case-sensitive Java strings, compared as UTF-16 code units without normalization. Null inputs are rejected. Empty strings are accepted. The algorithm returns a length; it does not reconstruct a subsequence.

For prefixes of lengths `i` and `j`, let `D[i][j]` be their LCS length. The base cases are `D[0][j] = D[i][0] = 0`. When the final characters agree, `D[i][j] = D[i-1][j-1] + 1`; otherwise, `D[i][j] = max(D[i-1][j], D[i][j-1])`.

An empty prefix has no nonempty subsequence. When the final characters match, an optimal common subsequence can use that character after an optimal solution for the shorter prefixes. Removing its last character leaves a common subsequence of those prefixes, so a longer answer is impossible. When the final characters differ, a common subsequence must omit at least one of them, giving the two smaller subproblems. Filling the table row by row computes every dependency before it is used.

## Complexity analysis

Here `m` and `n` are the two input lengths. Auxiliary space excludes the input strings and constant-size result.

| Algorithm | Main operation or recurrence | Best-case time | Worst-case time | Auxiliary space | Justification |
| --- | --- | --- | --- | --- | --- |
| LCS | Equal: diagonal + 1; unequal: max of top and left | O((m + 1)(n + 1)) | O((m + 1)(n + 1)) | O((m + 1)(n + 1)) | Allocate and initialize the full table; evaluate all `mn` non-base cells regardless of content. |

For nonempty strings these bounds simplify to `O(mn)`. The explicit factors also account for table allocation when either input is empty. The operation counter records exactly `mn` cells, excluding base-case initialization. Bounds for the remaining algorithms will accompany their implementations. Theoretical bounds are separate from measured execution times.

## Experimental setup

Planned workloads follow the assignment: LCS and Edit Distance use two strings of length 100, 500, and 1,000; matching uses text lengths 10,000, 50,000, and 100,000; Kasai uses lengths 1,000, 5,000, and 10,000. Random inputs will use fixed seed `42` and the lowercase English alphabet `abcdefghijklmnopqrstuvwxyz`.

Algorithms solving the same task will receive identical inputs. Matching comparisons will include present and absent patterns. Each measured experiment will run five times using `System.nanoTime()`, reporting the arithmetic mean and relevant operation counts. Input generation, suffix-array preparation, console output, and file operations will be outside timed regions.

## Results

No timing experiments have been run in this milestone. Raw CSV, result tables, and the two required plots will be added after all algorithms are implemented.

## Comparison and discussion

LCS performs the same number of table-cell updates for a given pair of lengths, even when strings are identical or have no shared characters. Its full table makes the recurrence explicit but requires quadratic space for equal input lengths. Experimental comparisons are pending.

## Testing

`CorrectnessSuite` checks identical strings, completely different strings, either or both inputs empty, a known LCS of length four, repeated characters, non-contiguous subsequences, single characters, case sensitivity, whitespace, punctuation, Cyrillic characters, and null rejection.

An independent exhaustive reference enumerates subsequences on 200 reproducible pairs of small strings over `abc`. Both input orders are checked against the reference. Every length check also verifies the `mn` cell count. The suite prints `PASS: 826 checks` on success.

## Conclusion

The first milestone provides a runnable, verified LCS foundation. The final conclusion will compare all six algorithms after the remaining implementations and experiments are complete.

## Development plan

1. Initialize the Java project, table-based LCS, correctness suite, launchers, and report structure.
2. Implement Levenshtein edit distance with recurrence explanation and tests.
3. Implement Naive and KMP matching, LPS preprocessing, and shared occurrence tests.
4. Implement Rabin-Karp rolling hashes, collision verification, and three-way matching checks.
5. Implement Kasai's LCP array from a supplied suffix array, with manually verified examples.
6. Add reproducible benchmarks, operation counts, raw CSV, and result tables for every required size.
7. Add plots and complete the complexity table, experimental discussion, and final report.

## AI acknowledgement

AI assisted with the code, documentation, and development plan. No experimental timing values have been generated or claimed in this milestone.
