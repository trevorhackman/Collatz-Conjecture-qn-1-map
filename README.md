# Collatz-Conjecture-qn+1-map

Two papers on cycles of the qn+1 map, and the searches behind their tables.

## Contents

- `cycles-through-1-qn1.tex`, built PDF `out/cycles-through-1-qn1.pdf` — Paper I. No cycle through 1 with three odd steps, and constraints on longer cycles through 1.
- `fixed-start-qn1.tex`, built PDF `out/fixed-start-qn1.pdf` — Paper II. Finiteness for each length, and an explicit bound on q when a fixed odd element lies on a cycle of length 2 or 3.
- `cycle-search/` — Gradle Kotlin project. Run the commands below from that directory.

## Commands

Each line is `./search …` from `cycle-search/`. An omitted option takes the default in the last column. The `--tables` commands run every published row.

| Command | Search | Default |
|---|---|---|
| `--length N --max-h N` | One length, every odd starting value | `--length 6 --max-h 1500` |
| `--through-1 --length N --max-h N` | One length, orbit of 1 only | `--length 3 --max-h 66100` |
| `--q N --max-length N` | One odd q, lengths 2 through N | `--q 5 --max-length 20` |
| `--tables any [--length N] [--max-h N]` | The any-cycle table | every published row |
| `--tables through1 [--length N] [--max-h N]` | The through-1 table | every published row |
| `--tables q [--q N] [--max-length N]` | The fixed-q table | q = 5 through length 20, then odd q from 7 through 25 through length 16 |
| `--bounds [--x N]` | The four Paper II bound tables | odd x from 1 through 35 |
| `--digits [--places N]` | Binary places of 2^{1/3} and 2^{2/3} | 1072000000 places |

`--length` on a table selects that published row, and `--max-h` replaces its limit. `--tables any --length 6` is length 6 through h ≤ 1800. `--tables any --length 6 --max-h 100` stops at h ≤ 100. `--bounds --x 1` prints the four bounds for x = 1. `--digits --places 10000` scans fewer places.

`--through-1` follows the orbit of 1 and does not enumerate halving tuples, so its halving bound can sit far above an any-starting-value search. The default `--through-1` is the length-3 check h ≤ 66100 cited in the proof of Theorem 1. The printed through-1 table is the longer run, `--tables through1`.

## Machine

The runs below were done one at a time on a MacBook Pro with an Apple M4 Pro, 14 cores (10 performance and 4 efficiency), and 48 GB of memory, running macOS 26.6.2 and JetBrains Runtime 25. The time is the search itself.

## Papers

Author: Trevor Hackman

Paper I proves that 1 never lies on a cycle with exactly three odd numbers, for odd q ≥ 3. For every such q at least 2^22001, a lower bound and an upper bound cannot hold at the same time, and every smaller q is ruled out by checking h < 66100. For each longer length, only finitely many q have 1 on a cycle, and that finiteness gives no bound. When the roots of 2 for that length have an approximation exponent below the golden ratio, the same comparison produces a bound that a finite search can finish.

Paper II proves that each length at least 2 has only finitely many odd q with a cycle of that length, again with no bound. If a fixed odd positive integer x lies on a cycle of length 2 or 3, then q lies below an explicit bound depending only on x. For x = 1 and length 3 that bound is q < 2^17990, which the search in Paper I already covers.

## Known cycles

- Through 1: 1→1 for Mersenne q = 2^e − 1, and 1→3→1 for q=5
- Not through 1: 13→33→83 and 17→43→27 for q=5; 27→611 and 35→99 for q=181

Length 1 is classified: C(x₀)=x₀ forces x₀(2^h−q)=1, so the only positive odd cycle is 1→1 with q=2^h−1. Every cycle of length ℓ≥2 has q = floor(2^{h/ℓ}) with ℓ not dividing h. Paper II conjectures that the cycles above are all of them.

## Tables

A length whose window q^ℓ < 2^h < (q+1)^ℓ contains no integer h has no cycle of that length.

### Any cycle

`./search --tables any`

Paper I, “Any cycle.” Odd elements that need not include 1. One row is `--length 2 --max-h 8000`.

| ℓ | h searched | No other cycle for | Exceptions | Time |
|---|---|---|---|---|
| 2 | h ≤ 8000 | q < 2^4000 | q=5: 1→3; q=181: 27→611 and 35→99 | 90 s |
| 3 | h ≤ 4000 | q < 2^1333 | q=5: 13→33→83 and 17→43→27 | 25 s |
| 4 | h ≤ 3000 | q < 2^750 | none | 5 s |
| 5 | h ≤ 2000 | q < 2^400 | none | 6 s |
| 6 | h ≤ 1800 | q < 2^300 | none | 503 s |
| 7 | h ≤ 700 | q < 2^100 | none | 47 s |
| 8 | h ≤ 560 | q < 2^70 | none | 687 s |
| 9 | h ≤ 270 | q < 2^30 | none | 125 s |

### Cycles through 1

`./search --tables through1`

Paper I, “Cycles through 1.” Length 2 is not a search bound: Beukers's theorem leaves only q=5. One row is `--through-1 --length 4 --max-h 100000`. The length-3 proof uses the shorter check `--through-1`, h ≤ 66100, since q ≥ 2^22001 is already impossible.

| ℓ | h searched | No cycle through 1 for | Time |
|---|---|---|---|
| 3 | h ≤ 200000 | q < 2^66666 | 1184 s |
| 4 | h ≤ 100000 | q < 2^25000 | 246 s |
| 5 | h ≤ 100000 | q < 2^20000 | 247 s |
| 6 | h ≤ 100000 | q < 2^16666 | 306 s |
| 7 | h ≤ 100000 | q < 2^14285 | 283 s |
| 8 | h ≤ 100000 | q < 2^12500 | 330 s |
| 9 | h ≤ 100000 | q < 2^11111 | 279 s |
| 10 | h ≤ 100000 | q < 2^10000 | 397 s |
| 11 | h ≤ 100000 | q < 2^9090 | 355 s |
| 12 | h ≤ 100000 | q < 2^8333 | 432 s |
| 13 | h ≤ 100000 | q < 2^7692 | 337 s |
| 14 | h ≤ 100000 | q < 2^7142 | 423 s |
| 15 | h ≤ 100000 | q < 2^6666 | 378 s |

### Digits of 2^{1/3} and 2^{2/3}

`./search --digits`

Paper I, “Digits of 2^{1/3} and 2^{2/3}.” Both roots are scanned. With no alternating block of length 48 from m = 100 through m = 1071999952, that rules out every 3-odd cycle through 1 with q < 2^1071999953. Every m < 100 has h = 3m+r < 66100 and is covered by `--through-1`. The scan took 170 s.

### Fixed q

`./search --tables q`

Paper I, “A fixed q.” Lengths run from 2 up, and the only values of h are those with that q. One q is `--q 5 --max-length 20`. The paper prints one row for q = 7, 9, …, 25 because each of those searches found no cycle. Length 10 and above, for a general q, is limited by the through-1 table. For these odd q it is also limited by the rows below. The cycles at q = 181 were found by `--tables any`, at length 2, not by this search.

| q | lengths searched | Cycles | Time |
|---|---|---|---|
| 5 | 2 through 20 | 1→3; 13→33→83 and 17→43→27 | 793 s |
| 7 | 2 through 16 | none | 7 s |
| 9 | 2 through 16 | none | 19 s |
| 11 | 2 through 16 | none | 24 s |
| 13 | 2 through 16 | none | 25 s |
| 15 | 2 through 16 | none | 42 s |
| 17 | 2 through 16 | none | 39 s |
| 19 | 2 through 16 | none | 119 s |
| 21 | 2 through 16 | none | 89 s |
| 23 | 2 through 16 | none | 128 s |
| 25 | 2 through 16 | none | 172 s |

### Paper II bounds

`./search --bounds`

Paper II. One command prints all four tables, for odd x from 1 through 35. Each entry is the next power of two above the bound. The run took 68 s.

Powers of two above Q_{3,\*}(x):

| x | | x | | x | |
|---|---|---|---|---|---|
| 1 | 2^17990 | 13 | 2^18387 | 25 | 2^18514 |
| 3 | 2^17907 | 15 | 2^18190 | 27 | 2^18304 |
| 5 | 2^18214 | 17 | 2^18439 | 29 | 2^18543 |
| 7 | 2^18273 | 19 | 2^18460 | 31 | 2^18556 |
| 9 | 2^18094 | 21 | 2^18255 | 33 | 2^18344 |
| 11 | 2^18356 | 23 | 2^18497 | 35 | 2^18580 |

Powers of two above Q_{3,0}(x), the maximum over 0 ≤ j < 11000:

| x | | x | | x | |
|---|---|---|---|---|---|
| 1 | 2^11003 | 13 | 2^11007 | 25 | 2^11009 |
| 3 | 2^11002 | 15 | 2^11006 | 27 | 2^11006 |
| 5 | 2^11006 | 17 | 2^11009 | 29 | 2^11012 |
| 7 | 2^11006 | 19 | 2^11009 | 31 | 2^11008 |
| 9 | 2^11006 | 21 | 2^11006 | 33 | 2^11005 |
| 11 | 2^11005 | 23 | 2^11006 | 35 | 2^11008 |

Powers of two above Q_{2,\*}(x): 2^11 at x = 1, 2^8 for x = 3, 5, 7, 9, 11, and 2^7 for every odd x from 13 through 35.

Q_2(x). The last failure of the square-root inequality for these x lies at j ≤ 21, and a check through j < 53620 finds no later failure.

| x | j_0(x) | winning j | Q_2(x) below |
|---|---|---|---|
| 1 | 9 | 7 | 2^15 |
| 3 | 12 | 11 | 2^18 |
| 5 | 22 | 17 | 2^33 |
| 7 | 12 | 10 | 2^18 |
| 9 | 12 | 11 | 2^18 |
| 11 | 13 | 12 | 2^19 |
| 13 | 14 | 9 | 2^21 |
| 15 | 22 | 17 | 2^32 |
| 17 | 15 | 13 | 2^23 |
| 19 | 17 | 14 | 2^25 |
| 21 | 17 | 12 | 2^25 |
| 23 | 16 | 14 | 2^23 |
| 25 | 22 | 17 | 2^32 |
| 27 | 15 | 14 | 2^22 |
| 29 | 17 | 16 | 2^25 |
| 31 | 17 | 16 | 2^25 |
| 33 | 15 | 13 | 2^22 |
| 35 | 22 | 17 | 2^32 |
