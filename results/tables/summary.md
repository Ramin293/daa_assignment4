# Benchmark results

Five-run mean; operations are DP cells or search/LCP character comparisons.

| Algorithm | Workload | Size | Pattern length | Mean (ms) | Preprocessing comparisons | Operations | Outcome |
| --- | --- | ---: | ---: | ---: | ---: | ---: | --- |
| LCS | random | 100 | 0 | 0.2628 | 0 | 10000 | 28 |
| Edit Distance | random | 100 | 0 | 0.3563 | 0 | 10000 | 93 |
| LCS | random | 500 | 0 | 0.7104 | 0 | 250000 | 159 |
| Edit Distance | random | 500 | 0 | 0.7282 | 0 | 250000 | 439 |
| LCS | random | 1000 | 0 | 2.2042 | 0 | 1000000 | 315 |
| Edit Distance | random | 1000 | 0 | 3.7467 | 0 | 1000000 | 889 |
| Naive | present | 10000 | 16 | 0.2513 | 0 | 10388 | 1 |
| KMP | present | 10000 | 16 | 0.2015 | 15 | 10371 | 1 |
| Rabin-Karp | present | 10000 | 16 | 0.3331 | 0 | 16 | 1 |
| Naive | absent | 10000 | 16 | 0.1040 | 0 | 10404 | 0 |
| KMP | absent | 10000 | 16 | 0.0779 | 17 | 10398 | 0 |
| Rabin-Karp | absent | 10000 | 16 | 0.1403 | 0 | 0 | 0 |
| Naive | present | 50000 | 16 | 0.1916 | 0 | 52043 | 1 |
| KMP | present | 50000 | 16 | 0.1239 | 16 | 51964 | 1 |
| Rabin-Karp | present | 50000 | 16 | 0.5349 | 0 | 16 | 1 |
| Naive | absent | 50000 | 16 | 0.1180 | 0 | 51991 | 0 |
| KMP | absent | 50000 | 16 | 0.1298 | 15 | 51925 | 0 |
| Rabin-Karp | absent | 50000 | 16 | 0.4114 | 0 | 0 | 0 |
| Naive | present | 100000 | 16 | 0.2317 | 0 | 103906 | 1 |
| KMP | present | 100000 | 16 | 0.1262 | 15 | 103747 | 1 |
| Rabin-Karp | present | 100000 | 16 | 0.6570 | 0 | 16 | 1 |
| Naive | absent | 100000 | 16 | 0.2294 | 0 | 103940 | 0 |
| KMP | absent | 100000 | 16 | 0.1386 | 15 | 103819 | 0 |
| Rabin-Karp | absent | 100000 | 16 | 0.6564 | 0 | 0 | 0 |
| Kasai | random | 1000 | 0 | 0.1166 | 0 | 1972 | -1456963061 |
| Kasai | random | 5000 | 0 | 0.4416 | 0 | 9970 | 1254230736 |
| Kasai | random | 10000 | 0 | 0.3657 | 0 | 19970 | 2083112495 |
