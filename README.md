# Collatz-Conjecture-qn+1-map

Two papers on cycles of the qn+1 map, and the searches behind their tables.

## Contents

- `cycles-through-1-qn1.tex`, built PDF `out/cycles-through-1-qn1.pdf` — Paper I. No cycle through 1 with three odd steps, and constraints on longer cycles through 1.
- `fixed-start-qn1.tex`, built PDF `out/fixed-start-qn1.pdf` — Paper II. Finiteness for each length, and an explicit bound on q when a fixed odd element lies on a cycle of length 2 or 3.
- `cycle-search/` — Gradle Kotlin project. Run the commands below from that directory.

`./gradlew run --args="200 4"` searches length 4 through 200 halvings for all starting values. `./gradlew run --args="through1 2000 3"` searches length 3 through 2000 halvings along the orbit of 1. That search has no tuple enumeration, so the halving bound can be much higher. `./gradlew run --args="q 5 20"` fixes q=5 and searches every length from 2 through 20.

`./gradlew run --args="bounds"` prints the Paper II powers of two for odd x from 1 through 35: Q_{3,*}, Q_{3,0}, Q_{2,*}, and Q_2. `bounds 1` prints one row. `./gradlew run --args="tables i through1"`, `tables i any`, and `tables i q` rerun the three Paper I computation tables. A length, or a q, alone reruns one published row. A second number overrides that row's limit. `./gradlew run --args="digits"` scans the first 1072000000 binary places of 2^{1/3} and 2^{2/3} for an alternating block of length 48. `digits 10000` scans fewer places.

## Papers

Author: Trevor Hackman

Paper I proves that 1 never lies on a cycle with exactly three odd numbers, for odd q ≥ 3. For every such q at least 2^22001, a lower bound and an upper bound cannot hold at the same time, and every smaller q is ruled out by checking h < 66100. For each longer length, only finitely many q have 1 on a cycle, and that finiteness gives no bound. When the roots of 2 for that length have an approximation exponent below the golden ratio, the same comparison produces a bound that a finite search can finish.

Paper II proves that each length at least 2 has only finitely many odd q with a cycle of that length, again with no bound. If a fixed odd positive integer x lies on a cycle of length 2 or 3, then q lies below an explicit bound depending only on x. For x = 1 and length 3 that bound is q < 2^17990, which the search in Paper I already covers.

## Known cycles

- Through 1: 1→1 for Mersenne q = 2^e − 1, and 1→3→1 for q=5
- Not through 1: 13→33→83 and 17→43→27 for q=5; 27→611 and 35→99 for q=181

Length 1 is classified: C(x₀)=x₀ forces x₀(2^h−q)=1, so the only positive odd cycle is 1→1 with q=2^h−1. Every cycle of length ℓ≥2 has q = floor(2^{h/ℓ}) with ℓ not dividing h. Paper II conjectures that the cycles above are all of them.

## What the searches rule out

Every cycle, any elements. Finished runs of `cycle-search`. `tables i any` reruns these rows.

| ℓ | h searched | No other cycle for | Exceptions |
|---|---|---|---|
| 2 | h ≤ 8000 | q < 2^4000 | q=5: 1→3; q=181: 27→611 and 35→99 |
| 3 | h ≤ 4000 | q < 2^1333 | q=5: 13→33→83 and 17→43→27 |
| 4 | h ≤ 3000 | q < 2^750 | none |
| 5 | h ≤ 2000 | q < 2^400 | none |
| 6 | h ≤ 1800 | q < 2^300 | none |
| 7 | h ≤ 700 | q < 2^100 | none |
| 8 | h ≤ 560 | q < 2^70 | none |
| 9 | h ≤ 270 | q < 2^30 | none |

Cycles through 1. Length 2 is not a search bound: Beukers's theorem leaves only q=5. Lengths 3 through 15 are every admissible h up to the limit below. `tables i through1` reruns these rows. The length-3 proof of Paper I uses the shorter check h < 66100, since q ≥ 2^22001 is already impossible.

| ℓ | h searched | No cycle through 1 for |
|---|---|---|
| 3 | h ≤ 200000 | q < 2^66666 |
| 4 | h ≤ 100000 | q < 2^25000 |
| 5 | h ≤ 100000 | q < 2^20000 |
| 6 | h ≤ 100000 | q < 2^16666 |
| 7 | h ≤ 100000 | q < 2^14285 |
| 8 | h ≤ 100000 | q < 2^12500 |
| 9 | h ≤ 100000 | q < 2^11111 |
| 10 | h ≤ 100000 | q < 2^10000 |
| 11 | h ≤ 100000 | q < 2^9090 |
| 12 | h ≤ 100000 | q < 2^8333 |
| 13 | h ≤ 100000 | q < 2^7692 |
| 14 | h ≤ 100000 | q < 2^7142 |
| 15 | h ≤ 100000 | q < 2^6666 |

`digits` scans 2^{1/3} and 2^{2/3}. With no alternating block of length 48 from m = 100 through m = 1071999952, that rules out every 3-odd cycle through 1 with q < 2^1071999953. Every m < 100 has h = 3m+r < 66100 and is covered by the length-3 search.

Fixed q. Every length from 2 up, only the values of h with that q. `tables i q` reruns these rows.

| q | lengths searched | Cycles |
|---|---|---|
| 5 | 2 through 20 | 1→3; 13→33→83 and 17→43→27 |
| 7, 9, 11, 13, 15, 17, 19, 21, 23, 25 | 2 through 16 | none |

A length whose window q^ℓ < 2^h < (q+1)^ℓ contains no integer h has no cycle of that length. Length 10 and above, for a general q, is limited by the through-1 table. For these odd q it is also limited by the fixed-q rows.
