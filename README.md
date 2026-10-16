# Collatz-Conjecture-qn+1-map

Cycles through 1 for the qn+1 map: no cycles with three odd steps, and constraints on longer cycles.

## Contents

- `cycle-search/` — Gradle Kotlin search. From that directory, `./gradlew run --args="200 4"` searches length 4 through 200 halvings for all starting values. `./gradlew run --args="through1 2000 3"` searches length 3 through 2000 halvings along the orbit of 1. That search has no tuple enumeration, so the halving bound can be much higher. `./gradlew run --args="q 5 20"` fixes q=5 and searches every length from 2 through 20.
- `cycles-through-1-qn1.pdf` — full paper
- `cycles-through-1-qn1.tex` — editable LaTeX reconstructed from the PDF

## Paper

Author: Trevor Hackman

The paper proves that 1 never lies on a cycle with exactly three odd numbers for the qn+1 map (odd q ≥ 3), and gives finiteness and effective criteria for longer cycles via restricted Diophantine approximation and the golden-ratio threshold.

## Known cycles

- Through 1: 1→1 for Mersenne q = 2^e − 1, and 1→3→1 for q=5
- Not through 1: 13→33→83 and 17→43→27 for q=5; 27→611 and 35→99 for q=181

Every cycle of length ℓ has q = floor(2^{h/ℓ}) with ℓ not dividing h.

## What the searches rule out

Every cycle, any elements. Finished runs of `cycle-search`.

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

Cycles through 1 only. Length 2 is not a search bound: Beukers's theorem leaves only q=5. Length 3 is the digit scan in Appendix D. Lengths 4 through 15 are every admissible h ≤ 100000.

| ℓ | No cycle through 1 for |
|---|---|
| 3 | q < 2^1071999953 |
| 4 | q < 2^25000 |
| 5 | q < 2^20000 |
| 6 | q < 2^16666 |
| 7 | q < 2^14285 |
| 8 | q < 2^12500 |
| 9 | q < 2^11111 |
| 10 | q < 2^10000 |
| 11 | q < 2^9090 |
| 12 | q < 2^8333 |
| 13 | q < 2^7692 |
| 14 | q < 2^7142 |
| 15 | q < 2^6666 |

Fixed q. Every length from 2 up, only the values of h with that q.

| q | lengths searched | Cycles |
|---|---|---|
| 5 | 2 through 20 | 1→3; 13→33→83 and 17→43→27 |
| 7, 9, 11, 13, 15, 17, 19, 21, 23, 25 | 2 through 16 | none |

A length whose window q^ℓ < 2^h < (q+1)^ℓ contains no integer h has no cycle of that length. Length 10 and above, for a general q, is limited by the through-1 table. For these odd q it is also limited by the fixed-q rows.
