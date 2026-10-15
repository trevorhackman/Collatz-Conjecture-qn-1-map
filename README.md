# Collatz-Conjecture-qn+1-map

Cycles through 1 for the qn+1 map: no cycles with three odd steps, and constraints on longer cycles.

## Contents

- `cycle-search/` — Gradle Kotlin search. From that directory, `./gradlew run --args="200 4"` searches every cycle (maximum halvings, then maximum length). `./gradlew run --args="through1 2000 3"` follows only the orbit of 1 for one length, and a further number sets the last length. That mode has no tuple enumeration, so the halving bound can be much higher.
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
| 4 | h ≤ 400 | q < 2^100 | none |
| 5 | h ≤ 120 | q < 2^24 | none |

Cycles through 1 only. Length 2 is not a search bound: Beukers's theorem leaves only q=5. Length 3 is the digit scan in Appendix C; lengths 4–8 are the Appendix C searches; lengths 9–15 are `cycle-search` for every admissible h ≤ 100000.

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

Nothing above limits length 6 or more, except the through-1 rows.
