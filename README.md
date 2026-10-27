# Collatz-Conjecture-qn+1-map

Two papers on cycles of the qn+1 map, and the searches behind their tables.

## Contents

- `cycles-through-1-qn1.tex`, built PDF `out/cycles-through-1-qn1.pdf` — Paper I. No cycles of length 3, and constraints on longer cycles through 1.
- `fixed-start-qn1.tex`, built PDF `out/fixed-start-qn1.pdf` — Paper II. Finiteness for each length, and an explicit bound on q when a constant odd element lies on a cycle of length 2 or 3.
- `cycle-search/` — Gradle Kotlin project. Run the commands below from that directory.

## Commands

Each line is `./search …` from `cycle-search/`. An omitted option takes the default in the last column. The `--tables` commands run every published row.

| Command                                      | Search                                       | Default                                                                 |
| -------------------------------------------- | -------------------------------------------- | ----------------------------------------------------------------------- |
| `--length N --max-h N`                       | One length, every odd starting value         | `--length 6 --max-h 1500`                                               |
| `--through-1 --length N --max-h N`           | One length, orbit of 1 only                  | `--length 3 --max-h 66100`                                              |
| `--q N [--min-length N] --max-length N`      | One odd q, from the minimum length through N | `--q 5 --max-length 20`, minimum 2                                      |
| `--tables any [--length N] [--max-h N]`      | The any-cycle table                          | every published row                                                     |
| `--tables through1 [--length N] [--max-h N]` | The through-1 table                          | every published row                                                     |
| `--tables q [--q N] [--max-length N]`        | The constant-q table                            | q = 5 through length 20, then odd q from 7 through 25 through length 16 |
| `--bounds q3star [--x N]`                    | Powers of two above Q_{3,\*}(x)                | odd x from 1 through 35                                                 |
| `--bounds q30 [--x N]`                       | Powers of two above Q_{3,0}(x)               | odd x from 1 through 35                                                 |
| `--bounds q2star [--x N]`                    | Powers of two above Q_{2,\*}(x)                | odd x from 1 through 35                                                 |
| `--bounds q2 [--x N]`                        | Q_2(x)                                       | odd x from 1 through 2043                                               |
| `--digits cbrt2 [--places N]`                | Binary places of 2^{1/3} and 2^{2/3}         | 1072000000 places                                                       |
| `--digits sqrt2 [--places N]`                | Binary places of √2                          | 1072000000 places                                                       |

`--length` on a table selects that published row, and `--max-h` replaces its limit. `--tables any --length 6` is length 6 through h ≤ 1400. `--tables any --length 6 --max-h 100` stops at h ≤ 100. `--min-length` starts a constant-q search at that length: `--q 1093 --min-length 201 --max-length 250` searches lengths 201 through 250. `--bounds q2 --x 1` prints Q_2(1). `--digits cbrt2 --places 10000` and `--digits sqrt2 --places 10000` scan fewer places.

`--through-1` follows the orbit of 1 and does not enumerate halving tuples, so its halving bound can sit far above an any-starting-value search. With no arguments it stops at h ≤ 66100. Theorem 1 needs only h < 66003. The printed through-1 table is `--tables through1`, and its length-3 row runs through h ≤ 200000.

## Machine

The runs below were done one at a time on a MacBook Pro with an Apple M4 Pro, 14 cores (10 performance and 4 efficiency), and 48 GB of memory, running macOS 26.6.2 and JetBrains Runtime 25. The time is the search itself.

## Papers

Author: Trevor Hackman

Paper I proves that 1 never lies on a cycle with exactly three odd numbers, for odd q ≥ 3. For every such q at least 2^22001, a lower bound and an upper bound cannot hold at the same time. Every smaller q has h < 66003. The length-3 search through h ≤ 200000 rules those out, and the digit scan rules out q < 2^1071999953. For each longer length, only finitely many q have 1 on a cycle, and that finiteness gives no bound. When the roots of 2 for that length have a restricted irrationality exponent below the golden ratio, the same comparison produces a bound that a finite search can finish.

Paper II proves that each length at least 2 has only finitely many odd q with a cycle of that length, again with no bound. If a constant odd positive integer x lies on a cycle of length 2 or 3, then q lies below an explicit bound depending only on x. For x = 1 and length 3 that bound is q < 2^17990, which the search in Paper I already covers.

## Non-English letters

\(\ell\) is the script form of l. The second column is the English name. A subscript or a prime only labels that same letter, so \(\mu_2\) is mu and \(\theta_i\) is theta. Phi is printed in the open form \(\varphi\), and epsilon in the form \(\varepsilon\). Mu is said mew, phi is said fie, theta is said thay-tuh, eta is said ay-tuh, rho is said row, tau rhymes with cow, lambda is said lam-duh, kappa is said kap-uh, chi is said kye, xi is said ksigh, and omega is said oh-may-guh.

| Symbol | Name | In these papers |
| --- | --- | --- |
| \(\ell\) | ell | The length of a cycle: how many distinct odd numbers lie on it. |
| \(\alpha\) | alpha | An algebraic irrational, in the statement of Ridout's theorem. In Paper II it is also the positive real \(\mu(\mu-1)/\gamma\). |
| \(\beta\) | beta | In Paper I, the root \(2^{r/\ell}\), including \(2^{1/3}\) and \(2^{2/3}\). In Paper II, the positive real \(\mu(\mu-1)\). |
| \(\gamma\) | gamma | The positive real \(1 - \mu + 1/\mu\). It is positive when \(\mu\) lies below the golden ratio. The labels \(\gamma_2\) and \(\gamma_3\) are this quantity at lengths 2 and 3. |
| \(\delta\) | delta | The fractional part of \(2^{h/\ell}\), so \(2^{h/\ell} = q + \delta\) with \(0 < \delta < 1\). |
| \(\Delta\) | capital delta | The parameters \(\Delta_0\) and \(\Delta_1\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here both equal 1. |
| \(\varepsilon\) | epsilon | A positive real. In Ridout's theorem it is the extra room in the exponent of an approximation. In Paper I, \(\varepsilon_i\) is the factor in \(x_i \, 2^{S_i} = q^i(1 + \varepsilon_i)\). |
| \(\eta\) | eta | A positive real. Ridout's theorem supplies it as a lower bound on certain distances and gives no formula for its value. |
| \(\theta\) | theta | An irrational real number, in the definition of a restricted irrationality exponent. On a cycle it is often the root \(2^{r/\ell}\), and \(\theta_i = 2^{ir/\ell}\). In two estimates it is also a constant positive real used as an exponent. |
| \(\Theta\) | capital theta | The set of real numbers \(2^{t/\ell}\) with \(1 \le t \le \ell(\ell-1)\) and with \(\ell\) not dividing \(t\). |
| \(\kappa\) | kappa | The parameter \(\kappa(7)\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here it equals 4. |
| \(\lambda\) | lambda | The real numbers \(\lambda_1\) and \(\lambda_2\) from Bauer and Bennett (2002). Here \(\lambda_2\) is the constant real 1.617. |
| \(\Lambda\) | capital lambda | A linear form in logarithms in Bauer and Bennett (2018). Beukers's lemma is that one choice of this form is nonzero. In Paper II the same letter is also a logarithm used to bound \(q\). |
| \(\mu\) | mu | A restricted irrationality exponent of an irrational \(\theta\) for a finite set \(S\) of positive integers: \(\lvert\theta - p/(s \cdot 2^j)\rvert > c \cdot 2^{-\mu j}\) for every \(s \in S\), with an explicit constant real number \(c > 0\) and an explicit constant integer \(j_0 \ge 0\). The labels \(\mu_2\) and \(\mu_3\) are the exponents used at lengths 2 and 3. |
| \(\xi\) | xi | The rational \(7/2^{15}\), a parameter in Theorem 1.5 of Bauer and Bennett (2002). |
| \(\Pi\) | capital pi | A product built from Padé approximants in Bauer and Bennett (2018). |
| \(\prod\) | capital pi | The product sign. Both papers use it to multiply a run of factors, including the product that equals \(2^h\). |
| \(\rho\) | rho | The relative error of a near miss. In Paper I, \(x_i = 2^{n_i/\ell}(1+\rho_i)\). In Paper II, \(x_i = x \, 2^{n_i/\ell}(1+\rho_i)\). |
| \(\sigma\) | sigma | The sum of the reciprocals of the odd numbers on the cycle. |
| \(\sum\) | capital sigma | The summation sign. Both papers use it to add a run of terms, including the sum of reciprocals that defines \(\sigma\). |
| \(\tau\) | tau | The input to an auxiliary function \(F\) in Bauer and Bennett (2018). |
| \(\varphi\) | phi | The golden ratio \((1 + \sqrt{5})/2\). |
| \(\chi\) | chi | The parameters \(\chi_1\), \(\chi_2\), and \(\chi_3\) in Theorem 1.5 of Bauer and Bennett (2002). |
| \(\Omega\) | capital omega | The parameters \(\Omega\) and \(\Omega_1\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here both equal \(2^{17}\). |

## Predicted mathematical strength & relevance of the results of paper I and II

Mathematical Strength ranks a general theorem above the special case it implies, and a proof that constrains every q above a search that only pushes a numerical fence. Novelty ranks how much of the conclusion was not already in Santos, Beukers, Franco, Ridout, or Bauer and Bennett.

### Mathematical Strength

1. **Paper II Theorem 3**, in the cases **Paper II Corollary 5** and **Paper II Corollary 6.** A named odd x on a cycle of length 3 forces q < Q_3(x), and on a cycle of length 2 forces q < Q_2(x).
2. **Paper I Theorem 2.** A restricted irrationality exponent below the golden ratio puts every cycle through 1 of that length under a computable bound.
3. **Paper I Theorem 1.** The number 1 lies on no cycle of length 3. This is Paper I Theorem 2 at length 3, with the finite check finished.
4. **Paper II Theorem 2.** Each length at least 2 occurs for only finitely many odd q, for a cycle through any elements. No bound.
5. **Paper II Corollary 14.** The same explicit bound on q, for a named odd element, when ℓ = 2^a and h ≡ ℓ/2 (mod ℓ), or ℓ = 3 · 2^a and h ≡ ℓ/3 or 2ℓ/3 (mod ℓ).
6. **Paper I Corollary 12**, **Paper I Corollary 13**, and **Paper I Corollary 14.** Those classes for cycles through 1. Paper I Corollary 12 shows that no such q exists.
7. **Paper I Theorem 3.** Each length at least 2 occurs for only finitely many odd q with 1 on the cycle. No bound.
8. **Paper II Proposition 12.** Every other length-2 cycle has q ≥ 2^1071999971 and every odd element greater than 2^17631663.
9. **Paper I Proposition A.1** and **Paper I Proposition A.2.** Length 1 is exactly q = 2^e − 1. Length 2 through 1 is exactly q = 5.

### Novelty

1. **Paper I Theorem 1.** No cycle of length 3 through 1, using Bauer and Bennett’s unrounded exponent below the golden ratio.
2. **Paper I Theorem 2.** The golden-ratio criterion for an arbitrary length through 1.
3. **Paper II Corollary 5**, from **Paper II Theorem 3** and **Paper II, Lemma 4 (constant odd part).** A named odd x on a length-3 cycle has q < Q_3(x). The constant-odd-part lemma is the cube-root measure for an arbitrary odd part.
4. **Paper II Proposition 12.** The length-2 lower bound. Franco already excludes every other period-2 orbit with |q| < 10^11.
5. **Paper II Corollary 6.** A named odd x on a length-2 cycle has q < Q_2(x).
6. **Paper I Corollary 12**, **Paper I Corollary 13**, and **Paper I Corollary 14.** The length-4 case through 1 with h ≡ 2 (mod 4) has no such q, and the other two classes through 1 have a computable bound.
7. **Paper II Corollary 14.** Those same classes with a named odd element, and q under an explicit bound.
8. **Paper II Theorem 2.** Finitely many q for each length, with no bound.
9. **Paper I Theorem 3.** Finitely many q for each length through 1, with no bound.
10. **Paper I Proposition A.1** and **Paper I Proposition A.2.** Length 1 is $1 \to 1$ for a Mersenne number, and length 2 through 1 is only q = 5.

## Known cycles

- Through 1: 1→1 for a Mersenne number, and 1→3→1 for q=5
- Not through 1: 13→33→83 and 17→43→27 for q=5; 27→611 and 35→99 for q=181

Length 1 is classified: C(x₀)=x₀ forces x₀(2^h−q)=1, so the only positive odd cycle is 1→1 with q=2^h−1. Every cycle of length ℓ≥2 has q = floor(2^{h/ℓ}) with ℓ not dividing h. Paper II conjectures that the cycles above are all of them.

## Tables

A length whose window q^ℓ < 2^h < (q+1)^ℓ contains no integer h has no cycle of that length.

### Any cycle

`./search --tables any`

Paper II, “Any cycle.” Odd elements that need not include 1. One row is `--length 2 --max-h 320000`. Another is `--length 3 --max-h 100000`.

Length 2 doubles the residue \(2^k\) modulo the gap. Length 3 halves the residue \(q^2 2^{-k}\) modulo the gap, which is odd, and matches it against those powers of 2. The run through \(h \le 100000\) used 5 threads. Lengths 4 through 9 store one half of the counts and walk the other.

Through \(h \le 160000\) the length-2 search took 146 s. The same search through \(h \le 320000\) took 1360 s. Length 3 through \(h \le 100000\) took 2681 s. Lengths 4 through 9 stay at a much smaller \(h\), where each takes about two or three minutes.

That search and Theorem 1.5 of Bauer and Bennett (2002), at \(\alpha = 3\) with the equation \(181^2 + 7 = 2^{15}\) and exponent \(1.617\), give that every other length-2 cycle has every odd element greater than \(2^{2559}\). For odd part \(1\) the inequality with exponent \(1.617\) holds from \(j = 2841\). Paper II records the constants.

The length-3 row and the zero runs of \(2^{1/3}\) and \(2^{2/3}\) leave only the two cycles at \(q = 5\) below a much higher line. For \(m \le 1071999972\), places \(m+1\) through \(m+28\) of either cube root are not all zero, so \(\delta \ge 2^{-28}\). For \(q \ge 5\) the smallest odd element is then below \(2^{29}\). Every odd starting value below \(2^{29}\) forces \(q < 2^{33333}\) on a length-3 cycle, and the search finds only \(13 \to 33 \to 83\) and \(17 \to 43 \to 27\). Every other length-3 cycle has \(q \ge 2^{1071999973}\) and its smallest odd element at least \(2^{29}\).

| ℓ | h searched | No other cycle for | Exceptions | Time |
|---|---|---|---|---|
| 2 | h ≤ 320000 | q < 2^160000 | q=5: 1→3; q=181: 27→611 and 35→99 | 1360 s |
| 3 | h ≤ 100000 | q < 2^33333 | q=5: 13→33→83 and 17→43→27 | 2681 s |
| 4 | h ≤ 5500 | q < 2^1375 | none | 140 s |
| 5 | h ≤ 4500 | q < 2^900 | none | 136 s |
| 6 | h ≤ 1400 | q < 2^233 | none | 147 s |
| 7 | h ≤ 950 | q < 2^135 | none | 167 s |
| 8 | h ≤ 470 | q < 2^58 | none | 179 s |
| 9 | h ≤ 320 | q < 2^35 | none | 138 s |

### Cycles through 1

`./search --tables through1`

Paper I, “Cycles through 1.” Length 2 is not a search bound: Beukers's theorem leaves only q=5. One row is `--through-1 --length 4 --max-h 100000`. The length-3 row is the search through h ≤ 200000. Theorem 1 needs only h < 66003, since q < 2^22001 forces 2^h < 2^66003.

| ℓ | h searched | No cycle through 1 for | Time |
|---|---|---|---|
| 3 | h ≤ 200000 | q < 2^66666 | 176 s |
| 4 | h ≤ 100000 | q < 2^25000 | 26 s |
| 5 | h ≤ 100000 | q < 2^20000 | 40 s |
| 6 | h ≤ 100000 | q < 2^16666 | 56 s |
| 7 | h ≤ 100000 | q < 2^14285 | 55 s |
| 8 | h ≤ 100000 | q < 2^12500 | 38 s |
| 9 | h ≤ 100000 | q < 2^11111 | 55 s |
| 10 | h ≤ 100000 | q < 2^10000 | 65 s |
| 11 | h ≤ 100000 | q < 2^9090 | 59 s |
| 12 | h ≤ 100000 | q < 2^8333 | 80 s |
| 13 | h ≤ 100000 | q < 2^7692 | 59 s |
| 14 | h ≤ 100000 | q < 2^7142 | 87 s |
| 15 | h ≤ 100000 | q < 2^6666 | 64 s |
| 16 | h ≤ 100000 | q < 2^6250 | 51 s |
| 17 | h ≤ 100000 | q < 2^5882 | 57 s |
| 18 | h ≤ 100000 | q < 2^5555 | 84 s |
| 19 | h ≤ 100000 | q < 2^5263 | 73 s |
| 20 | h ≤ 100000 | q < 2^5000 | 89 s |

### Digits of 2^{1/3} and 2^{2/3}

`./search --digits cbrt2`

Paper I, “Digits of 2^{1/3} and 2^{2/3}.” For m from 1 through 1071999999, the longest alternating run in 2^{1/3} has length 32, at m = 684454163, and in 2^{2/3} has length 31, at m = 1024436159. The longest zero run in 2^{1/3} has length 27, at m = 31318881 and m = 955995734, and in 2^{2/3} has length 26, at m = 71965347, 111843987, 307341593, 496232916, 611035374, and 1013171868. The scan took 192 s.

### Digits of √2

`./search --digits sqrt2`

Paper II, “Digits of √2.” For m from 1 through 1071999999, the longest zero run has length 29, at m = 1005210007. The scan took 143 s.

### Constant q

`./search --tables q`

Paper II, “A constant q.” Lengths run from 2 up, and the only values of h are those with that q. One q is `--q 5 --max-length 20`. The paper prints one row for each odd q from 7 through 25 because each of those searches found no cycle. Length 10 and above, for a general q, is limited by the through-1 table. For these odd q it is also limited by the rows below. The cycles at q = 181 were found by `--tables any`, at length 2, not by this search.

| q | lengths searched | Cycles | Time |
|---|---|---|---|
| 5 | 2 through 20 | 1→3; 13→33→83 and 17→43→27 | under 1 s |
| 7 | 2 through 16 | none | under 1 s |
| 9 | 2 through 16 | none | under 1 s |
| 11 | 2 through 16 | none | under 1 s |
| 13 | 2 through 16 | none | under 1 s |
| 15 | 2 through 16 | none | under 1 s |
| 17 | 2 through 16 | none | under 1 s |
| 19 | 2 through 16 | none | under 1 s |
| 21 | 2 through 16 | none | under 1 s |
| 23 | 2 through 16 | none | under 1 s |
| 25 | 2 through 16 | none | under 1 s |

### Paper II bounds

Each command prints one table, for odd x from 1 through 35. Each entry is the next power of two above the bound.

`./search --bounds q3star`

Powers of two above Q_{3,\*}(x). The table took under 1 s.

| x | | x | | x | |
|---|---|---|---|---|---|
| 1 | 2^17990 | 13 | 2^18387 | 25 | 2^18514 |
| 3 | 2^17907 | 15 | 2^18190 | 27 | 2^18304 |
| 5 | 2^18214 | 17 | 2^18439 | 29 | 2^18543 |
| 7 | 2^18273 | 19 | 2^18460 | 31 | 2^18556 |
| 9 | 2^18094 | 21 | 2^18255 | 33 | 2^18344 |
| 11 | 2^18356 | 23 | 2^18497 | 35 | 2^18580 |

`./search --bounds q30`

Powers of two above Q_{3,0}(x), the maximum over 0 ≤ j < 11000. Each x took about 2 s, and the table took 43 s.

| x | | x | | x | |
|---|---|---|---|---|---|
| 1 | 2^11003 | 13 | 2^11007 | 25 | 2^11009 |
| 3 | 2^11002 | 15 | 2^11006 | 27 | 2^11006 |
| 5 | 2^11006 | 17 | 2^11009 | 29 | 2^11012 |
| 7 | 2^11006 | 19 | 2^11009 | 31 | 2^11008 |
| 9 | 2^11006 | 21 | 2^11006 | 33 | 2^11005 |
| 11 | 2^11005 | 23 | 2^11006 | 35 | 2^11008 |

`./search --bounds q2star`

Powers of two above Q_{2,\*}(x). The table took under 1 s. The entries are 2^11 at x = 1, 2^8 for x = 3, 5, 7, 9, 11, and 2^7 for every odd x from 13 through 35.

`./search --bounds q2`

Q_2(x) for every odd x from 1 through 2043. Below the point where the allowed error is smaller than 1/(2x^2), every j is tested directly. From there through j < 200000 only continued-fraction convergents can fail, and none do. The last failure lies at j ≤ 47. The largest entry is x = 1849, below 2^70. The table took 6 s. `--bounds q2 --x 1` still checks that one x through j < 53620.

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
| 37 | 16 | 15 | 2^24 |
| 39 | 17 | 15 | 2^26 |
| 41 | 17 | 15 | 2^26 |
| 43 | 16 | 15 | 2^24 |
| 45 | 22 | 17 | 2^32 |
| 47 | 16 | 15 | 2^23 |
| 49 | 18 | 16 | 2^26 |
| 51 | 18 | 16 | 2^26 |
| 53 | 17 | 16 | 2^24 |
| 55 | 22 | 17 | 2^32 |
| 57 | 17 | 14 | 2^25 |
| 59 | 24 | 23 | 2^35 |
| 61 | 18 | 14 | 2^26 |
| 63 | 17 | 12 | 2^25 |
| 65 | 22 | 17 | 2^32 |
| 67 | 17 | 16 | 2^25 |
| 69 | 25 | 23 | 2^37 |
| 71 | 18 | 16 | 2^27 |
| 73 | 19 | 18 | 2^28 |
| 75 | 22 | 17 | 2^32 |
| 77 | 19 | 18 | 2^28 |
| 79 | 24 | 23 | 2^35 |
| 81 | 18 | 15 | 2^27 |
| 83 | 19 | 18 | 2^28 |
| 85 | 22 | 17 | 2^32 |
| 87 | 19 | 18 | 2^28 |
| 89 | 20 | 19 | 2^29 |
| 91 | 18 | 16 | 2^27 |
| 93 | 23 | 22 | 2^34 |
| 95 | 22 | 17 | 2^32 |
| 97 | 19 | 18 | 2^28 |
| 99 | 20 | 19 | 2^29 |
| 101 | 26 | 24 | 2^38 |
| 103 | 23 | 22 | 2^34 |
| 105 | 22 | 17 | 2^32 |
| 107 | 21 | 20 | 2^30 |
| 109 | 20 | 19 | 2^29 |
| 111 | 22 | 21 | 2^32 |
| 113 | 24 | 22 | 2^35 |
| 115 | 22 | 17 | 2^32 |
| 117 | 26 | 25 | 2^39 |
| 119 | 20 | 19 | 2^29 |
| 121 | 22 | 21 | 2^32 |
| 123 | 24 | 22 | 2^36 |
| 125 | 22 | 17 | 2^32 |
| 127 | 21 | 20 | 2^31 |
| 129 | 20 | 19 | 2^30 |
| 131 | 22 | 21 | 2^32 |
| 133 | 27 | 22 | 2^39 |
| 135 | 22 | 17 | 2^32 |
| 137 | 21 | 20 | 2^31 |
| 139 | 20 | 19 | 2^30 |
| 141 | 27 | 26 | 2^40 |
| 143 | 24 | 22 | 2^36 |
| 145 | 22 | 17 | 2^32 |
| 147 | 21 | 20 | 2^31 |
| 149 | 27 | 25 | 2^40 |
| 151 | 22 | 21 | 2^33 |
| 153 | 28 | 27 | 2^41 |
| 155 | 25 | 24 | 2^36 |
| 157 | 28 | 26 | 2^41 |
| 159 | 20 | 19 | 2^30 |
| 161 | 29 | 27 | 2^42 |
| 163 | 29 | 28 | 2^43 |
| 165 | 30 | 24 | 2^44 |
| 167 | 35 | 28 | 2^51 |
| 169 | 30 | 27 | 2^44 |
| 171 | 29 | 28 | 2^43 |
| 173 | 29 | 26 | 2^42 |
| 175 | 25 | 24 | 2^36 |
| 177 | 28 | 27 | 2^42 |
| 179 | 20 | 19 | 2^30 |
| 181 | 28 | 25 | 2^41 |
| 183 | 23 | 22 | 2^34 |
| 185 | 28 | 27 | 2^41 |
| 187 | 25 | 23 | 2^36 |
| 189 | 27 | 26 | 2^40 |
| 191 | 23 | 21 | 2^34 |
| 193 | 23 | 22 | 2^34 |
| 195 | 22 | 17 | 2^32 |
| 197 | 27 | 23 | 2^40 |
| 199 | 20 | 19 | 2^30 |
| 201 | 23 | 21 | 2^34 |
| 203 | 23 | 22 | 2^34 |
| 205 | 27 | 26 | 2^40 |
| 207 | 25 | 23 | 2^37 |
| 209 | 32 | 31 | 2^47 |
| 211 | 23 | 21 | 2^34 |
| 213 | 27 | 25 | 2^40 |
| 215 | 22 | 17 | 2^32 |
| 217 | 24 | 23 | 2^36 |
| 219 | 42 | 41 | 2^62 |
| 221 | 27 | 26 | 2^39 |
| 223 | 23 | 22 | 2^34 |
| 225 | 22 | 17 | 2^32 |
| 227 | 24 | 23 | 2^35 |
| 229 | 27 | 24 | 2^39 |
| 231 | 24 | 21 | 2^35 |
| 233 | 23 | 22 | 2^34 |
| 235 | 22 | 17 | 2^32 |
| 237 | 27 | 26 | 2^39 |
| 239 | 25 | 24 | 2^37 |
| 241 | 24 | 21 | 2^36 |
| 243 | 23 | 22 | 2^34 |
| 245 | 26 | 25 | 2^39 |
| 247 | 24 | 23 | 2^35 |
| 249 | 21 | 19 | 2^31 |
| 251 | 31 | 30 | 2^46 |
| 253 | 23 | 22 | 2^33 |
| 255 | 22 | 17 | 2^32 |
| 257 | 22 | 20 | 2^32 |
| 259 | 39 | 37 | 2^58 |
| 261 | 34 | 33 | 2^50 |
| 263 | 23 | 22 | 2^33 |
| 265 | 22 | 17 | 2^32 |
| 267 | 22 | 20 | 2^32 |
| 269 | 23 | 22 | 2^33 |
| 271 | 25 | 21 | 2^38 |
| 273 | 23 | 22 | 2^33 |
| 275 | 22 | 17 | 2^32 |
| 277 | 26 | 25 | 2^39 |
| 279 | 23 | 22 | 2^34 |
| 281 | 25 | 21 | 2^36 |
| 283 | 25 | 24 | 2^37 |
| 285 | 24 | 23 | 2^35 |
| 287 | 26 | 25 | 2^38 |
| 289 | 23 | 22 | 2^34 |
| 291 | 24 | 21 | 2^36 |
| 293 | 26 | 24 | 2^39 |
| 295 | 24 | 23 | 2^35 |
| 297 | 22 | 20 | 2^33 |
| 299 | 23 | 22 | 2^34 |
| 301 | 24 | 21 | 2^35 |
| 303 | 26 | 24 | 2^38 |
| 305 | 24 | 23 | 2^36 |
| 307 | 22 | 20 | 2^33 |
| 309 | 26 | 25 | 2^39 |
| 311 | 24 | 21 | 2^35 |
| 313 | 33 | 32 | 2^48 |
| 315 | 25 | 23 | 2^37 |
| 317 | 22 | 20 | 2^33 |
| 319 | 26 | 25 | 2^38 |
| 321 | 24 | 21 | 2^35 |
| 323 | 22 | 21 | 2^32 |
| 325 | 26 | 23 | 2^38 |
| 327 | 22 | 20 | 2^33 |
| 329 | 23 | 22 | 2^35 |
| 331 | 30 | 29 | 2^44 |
| 333 | 31 | 29 | 2^46 |
| 335 | 31 | 29 | 2^46 |
| 337 | 30 | 29 | 2^44 |
| 339 | 24 | 22 | 2^35 |
| 341 | 26 | 25 | 2^38 |
| 343 | 22 | 21 | 2^32 |
| 345 | 25 | 23 | 2^37 |
| 347 | 25 | 24 | 2^37 |
| 349 | 24 | 22 | 2^35 |
| 351 | 26 | 25 | 2^39 |
| 353 | 22 | 21 | 2^32 |
| 355 | 33 | 32 | 2^49 |
| 357 | 26 | 24 | 2^38 |
| 359 | 24 | 22 | 2^36 |
| 361 | 23 | 21 | 2^34 |
| 363 | 22 | 21 | 2^32 |
| 365 | 24 | 23 | 2^36 |
| 367 | 26 | 24 | 2^39 |
| 369 | 24 | 22 | 2^36 |
| 371 | 23 | 21 | 2^34 |
| 373 | 26 | 25 | 2^38 |
| 375 | 24 | 23 | 2^35 |
| 377 | 25 | 24 | 2^37 |
| 379 | 25 | 22 | 2^37 |
| 381 | 37 | 34 | 2^54 |
| 383 | 26 | 25 | 2^39 |
| 385 | 24 | 23 | 2^35 |
| 387 | 25 | 24 | 2^36 |
| 389 | 26 | 22 | 2^38 |
| 391 | 27 | 26 | 2^39 |
| 393 | 24 | 23 | 2^35 |
| 395 | 24 | 23 | 2^35 |
| 397 | 23 | 20 | 2^34 |
| 399 | 27 | 22 | 2^39 |
| 401 | 25 | 24 | 2^36 |
| 403 | 24 | 23 | 2^35 |
| 405 | 26 | 25 | 2^38 |
| 407 | 34 | 33 | 2^49 |
| 409 | 25 | 22 | 2^37 |
| 411 | 25 | 24 | 2^37 |
| 413 | 24 | 23 | 2^35 |
| 415 | 27 | 25 | 2^40 |
| 417 | 32 | 30 | 2^47 |
| 419 | 31 | 30 | 2^46 |
| 421 | 26 | 24 | 2^38 |
| 423 | 27 | 26 | 2^40 |
| 425 | 22 | 17 | 2^32 |
| 427 | 23 | 20 | 2^34 |
| 429 | 24 | 22 | 2^36 |
| 431 | 27 | 24 | 2^40 |
| 433 | 25 | 23 | 2^36 |
| 435 | 23 | 22 | 2^33 |
| 437 | 26 | 25 | 2^38 |
| 439 | 27 | 26 | 2^40 |
| 441 | 25 | 24 | 2^38 |
| 443 | 25 | 23 | 2^37 |
| 445 | 23 | 22 | 2^33 |
| 447 | 27 | 25 | 2^40 |
| 449 | 24 | 22 | 2^35 |
| 451 | 28 | 27 | 2^41 |
| 453 | 26 | 23 | 2^38 |
| 455 | 28 | 26 | 2^41 |
| 457 | 26 | 25 | 2^38 |
| 459 | 33 | 31 | 2^48 |
| 461 | 25 | 24 | 2^36 |
| 463 | 28 | 23 | 2^41 |
| 465 | 25 | 24 | 2^36 |
| 467 | 28 | 27 | 2^41 |
| 469 | 26 | 25 | 2^38 |
| 471 | 28 | 26 | 2^41 |
| 473 | 26 | 23 | 2^38 |
| 475 | 28 | 27 | 2^42 |
| 477 | 24 | 20 | 2^36 |
| 479 | 28 | 25 | 2^42 |
| 481 | 23 | 21 | 2^34 |
| 483 | 29 | 27 | 2^42 |
| 485 | 29 | 28 | 2^42 |
| 487 | 29 | 26 | 2^43 |
| 489 | 29 | 28 | 2^43 |
| 491 | 29 | 27 | 2^43 |
| 493 | 29 | 28 | 2^43 |
| 495 | 30 | 24 | 2^44 |
| 497 | 30 | 28 | 2^44 |
| 499 | 31 | 27 | 2^45 |
| 501 | 35 | 28 | 2^51 |
| 503 | 31 | 26 | 2^45 |
| 505 | 30 | 28 | 2^44 |
| 507 | 30 | 27 | 2^44 |
| 509 | 29 | 28 | 2^43 |
| 511 | 29 | 25 | 2^43 |
| 513 | 29 | 28 | 2^43 |
| 515 | 29 | 27 | 2^43 |
| 517 | 29 | 28 | 2^42 |
| 519 | 29 | 26 | 2^42 |
| 521 | 29 | 28 | 2^42 |
| 523 | 28 | 27 | 2^42 |
| 525 | 25 | 24 | 2^36 |
| 527 | 28 | 20 | 2^42 |
| 529 | 25 | 24 | 2^36 |
| 531 | 28 | 27 | 2^42 |
| 533 | 26 | 25 | 2^38 |
| 535 | 28 | 26 | 2^41 |
| 537 | 26 | 20 | 2^38 |
| 539 | 28 | 27 | 2^41 |
| 541 | 24 | 23 | 2^36 |
| 543 | 33 | 31 | 2^49 |
| 545 | 23 | 22 | 2^35 |
| 547 | 28 | 27 | 2^41 |
| 549 | 26 | 24 | 2^38 |
| 551 | 28 | 26 | 2^41 |
| 553 | 26 | 25 | 2^38 |
| 555 | 28 | 27 | 2^41 |
| 557 | 25 | 20 | 2^37 |
| 559 | 28 | 24 | 2^41 |
| 561 | 25 | 23 | 2^36 |
| 563 | 28 | 27 | 2^41 |
| 565 | 26 | 25 | 2^38 |
| 567 | 27 | 26 | 2^40 |
| 569 | 26 | 24 | 2^38 |
| 571 | 25 | 23 | 2^37 |
| 573 | 24 | 23 | 2^35 |
| 575 | 27 | 25 | 2^40 |
| 577 | 24 | 20 | 2^36 |
| 579 | 25 | 24 | 2^37 |
| 581 | 26 | 23 | 2^38 |
| 583 | 31 | 30 | 2^46 |
| 585 | 32 | 30 | 2^48 |
| 587 | 31 | 30 | 2^45 |
| 589 | 25 | 24 | 2^37 |
| 591 | 27 | 23 | 2^40 |
| 593 | 25 | 24 | 2^37 |
| 595 | 36 | 33 | 2^52 |
| 597 | 26 | 25 | 2^38 |
| 599 | 27 | 26 | 2^40 |
| 601 | 26 | 23 | 2^39 |
| 603 | 25 | 24 | 2^37 |
| 605 | 24 | 22 | 2^36 |
| 607 | 27 | 25 | 2^40 |
| 609 | 24 | 23 | 2^35 |
| 611 | 25 | 23 | 2^38 |
| 613 | 26 | 24 | 2^38 |
| 615 | 27 | 26 | 2^40 |
| 617 | 40 | 39 | 2^59 |
| 619 | 24 | 23 | 2^35 |
| 621 | 25 | 23 | 2^37 |
| 623 | 27 | 24 | 2^40 |
| 625 | 25 | 22 | 2^37 |
| 627 | 32 | 31 | 2^47 |
| 629 | 26 | 25 | 2^38 |
| 631 | 27 | 26 | 2^40 |
| 633 | 26 | 24 | 2^39 |
| 635 | 25 | 22 | 2^37 |
| 637 | 24 | 20 | 2^35 |
| 639 | 27 | 25 | 2^40 |
| 641 | 27 | 26 | 2^39 |
| 643 | 26 | 24 | 2^38 |
| 645 | 26 | 22 | 2^38 |
| 647 | 33 | 32 | 2^49 |
| 649 | 27 | 25 | 2^39 |
| 651 | 24 | 23 | 2^36 |
| 653 | 25 | 24 | 2^37 |
| 655 | 38 | 35 | 2^56 |
| 657 | 42 | 41 | 2^62 |
| 659 | 30 | 29 | 2^44 |
| 661 | 30 | 29 | 2^44 |
| 663 | 30 | 29 | 2^44 |
| 665 | 31 | 29 | 2^45 |
| 667 | 32 | 29 | 2^47 |
| 669 | 32 | 29 | 2^47 |
| 671 | 31 | 29 | 2^45 |
| 673 | 30 | 29 | 2^44 |
| 675 | 30 | 29 | 2^44 |
| 677 | 30 | 29 | 2^44 |
| 679 | 27 | 26 | 2^39 |
| 681 | 27 | 25 | 2^40 |
| 683 | 24 | 21 | 2^35 |
| 685 | 25 | 22 | 2^37 |
| 687 | 27 | 24 | 2^39 |
| 689 | 34 | 32 | 2^51 |
| 691 | 26 | 25 | 2^38 |
| 693 | 26 | 25 | 2^38 |
| 695 | 27 | 26 | 2^39 |
| 697 | 27 | 24 | 2^40 |
| 699 | 25 | 23 | 2^37 |
| 701 | 24 | 23 | 2^35 |
| 703 | 27 | 25 | 2^39 |
| 705 | 27 | 26 | 2^40 |
| 707 | 26 | 24 | 2^38 |
| 709 | 32 | 31 | 2^47 |
| 711 | 32 | 31 | 2^47 |
| 713 | 27 | 25 | 2^40 |
| 715 | 36 | 34 | 2^52 |
| 717 | 25 | 24 | 2^37 |
| 719 | 27 | 23 | 2^39 |
| 721 | 27 | 26 | 2^40 |
| 723 | 26 | 25 | 2^38 |
| 725 | 26 | 25 | 2^38 |
| 727 | 25 | 24 | 2^37 |
| 729 | 27 | 23 | 2^40 |
| 731 | 33 | 32 | 2^49 |
| 733 | 24 | 21 | 2^36 |
| 735 | 26 | 25 | 2^39 |
| 737 | 27 | 26 | 2^40 |
| 739 | 26 | 23 | 2^38 |
| 741 | 34 | 33 | 2^50 |
| 743 | 25 | 21 | 2^36 |
| 745 | 27 | 25 | 2^40 |
| 747 | 24 | 23 | 2^35 |
| 749 | 31 | 30 | 2^46 |
| 751 | 32 | 30 | 2^48 |
| 753 | 31 | 30 | 2^46 |
| 755 | 31 | 30 | 2^45 |
| 757 | 28 | 27 | 2^41 |
| 759 | 25 | 23 | 2^37 |
| 761 | 28 | 24 | 2^41 |
| 763 | 25 | 21 | 2^37 |
| 765 | 28 | 27 | 2^41 |
| 767 | 26 | 25 | 2^39 |
| 769 | 28 | 26 | 2^41 |
| 771 | 26 | 24 | 2^38 |
| 773 | 28 | 27 | 2^41 |
| 775 | 25 | 24 | 2^36 |
| 777 | 39 | 37 | 2^58 |
| 779 | 25 | 23 | 2^36 |
| 781 | 28 | 27 | 2^41 |
| 783 | 34 | 33 | 2^50 |
| 785 | 28 | 26 | 2^41 |
| 787 | 26 | 25 | 2^38 |
| 789 | 28 | 27 | 2^41 |
| 791 | 25 | 24 | 2^37 |
| 793 | 33 | 31 | 2^49 |
| 795 | 25 | 24 | 2^37 |
| 797 | 28 | 27 | 2^42 |
| 799 | 26 | 25 | 2^39 |
| 801 | 28 | 26 | 2^42 |
| 803 | 29 | 28 | 2^42 |
| 805 | 29 | 27 | 2^42 |
| 807 | 29 | 28 | 2^42 |
| 809 | 35 | 34 | 2^52 |
| 811 | 29 | 28 | 2^42 |
| 813 | 29 | 27 | 2^43 |
| 815 | 29 | 28 | 2^43 |
| 817 | 29 | 26 | 2^43 |
| 819 | 29 | 28 | 2^43 |
| 821 | 29 | 27 | 2^43 |
| 823 | 29 | 28 | 2^43 |
| 825 | 30 | 24 | 2^44 |
| 827 | 30 | 28 | 2^44 |
| 829 | 30 | 27 | 2^44 |
| 831 | 31 | 28 | 2^45 |
| 833 | 31 | 26 | 2^46 |
| 835 | 35 | 28 | 2^51 |
| 837 | 31 | 27 | 2^46 |
| 839 | 31 | 28 | 2^45 |
| 841 | 30 | 25 | 2^45 |
| 843 | 30 | 28 | 2^44 |
| 845 | 30 | 27 | 2^44 |
| 847 | 30 | 28 | 2^44 |
| 849 | 29 | 26 | 2^43 |
| 851 | 29 | 28 | 2^43 |
| 853 | 29 | 27 | 2^43 |
| 855 | 29 | 28 | 2^43 |
| 857 | 29 | 23 | 2^43 |
| 859 | 29 | 28 | 2^43 |
| 861 | 29 | 27 | 2^42 |
| 863 | 29 | 28 | 2^42 |
| 865 | 29 | 26 | 2^42 |
| 867 | 29 | 28 | 2^42 |
| 869 | 36 | 35 | 2^53 |
| 871 | 25 | 22 | 2^36 |
| 873 | 28 | 25 | 2^42 |
| 875 | 25 | 24 | 2^36 |
| 877 | 34 | 31 | 2^50 |
| 879 | 26 | 24 | 2^39 |
| 881 | 28 | 26 | 2^42 |
| 883 | 26 | 25 | 2^39 |
| 885 | 28 | 27 | 2^42 |
| 887 | 25 | 23 | 2^37 |
| 889 | 28 | 24 | 2^41 |
| 891 | 25 | 22 | 2^37 |
| 893 | 28 | 27 | 2^41 |
| 895 | 26 | 25 | 2^39 |
| 897 | 28 | 26 | 2^41 |
| 899 | 38 | 36 | 2^55 |
| 901 | 28 | 27 | 2^41 |
| 903 | 25 | 24 | 2^36 |
| 905 | 28 | 25 | 2^41 |
| 907 | 27 | 26 | 2^39 |
| 909 | 28 | 27 | 2^41 |
| 911 | 26 | 22 | 2^39 |
| 913 | 28 | 26 | 2^41 |
| 915 | 31 | 30 | 2^45 |
| 917 | 32 | 30 | 2^47 |
| 919 | 33 | 30 | 2^48 |
| 921 | 31 | 30 | 2^46 |
| 923 | 31 | 30 | 2^45 |
| 925 | 28 | 27 | 2^41 |
| 927 | 26 | 25 | 2^39 |
| 929 | 37 | 33 | 2^54 |
| 931 | 27 | 22 | 2^39 |
| 933 | 28 | 27 | 2^41 |
| 935 | 25 | 23 | 2^36 |
| 937 | 28 | 25 | 2^41 |
| 939 | 33 | 32 | 2^48 |
| 941 | 26 | 22 | 2^38 |
| 943 | 26 | 24 | 2^38 |
| 945 | 27 | 26 | 2^40 |
| 947 | 27 | 25 | 2^39 |
| 949 | 26 | 25 | 2^38 |
| 951 | 25 | 22 | 2^37 |
| 953 | 27 | 24 | 2^40 |
| 955 | 27 | 26 | 2^40 |
| 957 | 26 | 25 | 2^38 |
| 959 | 32 | 31 | 2^47 |
| 961 | 32 | 31 | 2^48 |
| 963 | 27 | 24 | 2^40 |
| 965 | 26 | 23 | 2^38 |
| 967 | 25 | 24 | 2^36 |
| 969 | 27 | 25 | 2^40 |
| 971 | 34 | 33 | 2^49 |
| 973 | 26 | 24 | 2^38 |
| 975 | 40 | 38 | 2^58 |
| 977 | 27 | 26 | 2^40 |
| 979 | 27 | 25 | 2^40 |
| 981 | 34 | 32 | 2^50 |
| 983 | 25 | 24 | 2^38 |
| 985 | 27 | 23 | 2^40 |
| 987 | 27 | 26 | 2^40 |
| 989 | 36 | 35 | 2^53 |
| 991 | 30 | 29 | 2^44 |
| 993 | 30 | 29 | 2^44 |
| 995 | 30 | 29 | 2^45 |
| 997 | 31 | 29 | 2^45 |
| 999 | 31 | 29 | 2^46 |
| 1001 | 32 | 29 | 2^47 |
| 1003 | 32 | 29 | 2^47 |
| 1005 | 31 | 29 | 2^46 |
| 1007 | 31 | 29 | 2^45 |
| 1009 | 30 | 29 | 2^45 |
| 1011 | 30 | 29 | 2^44 |
| 1013 | 30 | 29 | 2^44 |
| 1015 | 30 | 29 | 2^44 |
| 1017 | 27 | 24 | 2^40 |
| 1019 | 27 | 26 | 2^40 |
| 1021 | 26 | 25 | 2^38 |
| 1023 | 35 | 32 | 2^52 |
| 1025 | 27 | 26 | 2^40 |
| 1027 | 27 | 24 | 2^40 |
| 1029 | 26 | 19 | 2^38 |
| 1031 | 26 | 25 | 2^38 |
| 1033 | 27 | 25 | 2^40 |
| 1035 | 27 | 26 | 2^40 |
| 1037 | 26 | 24 | 2^39 |
| 1039 | 26 | 19 | 2^38 |
| 1041 | 27 | 26 | 2^40 |
| 1043 | 32 | 31 | 2^48 |
| 1045 | 32 | 31 | 2^47 |
| 1047 | 26 | 24 | 2^38 |
| 1049 | 35 | 34 | 2^52 |
| 1051 | 38 | 37 | 2^55 |
| 1053 | 26 | 25 | 2^39 |
| 1055 | 41 | 40 | 2^60 |
| 1057 | 27 | 26 | 2^40 |
| 1059 | 28 | 19 | 2^41 |
| 1061 | 26 | 24 | 2^38 |
| 1063 | 28 | 27 | 2^41 |
| 1065 | 33 | 32 | 2^49 |
| 1067 | 28 | 26 | 2^41 |
| 1069 | 26 | 19 | 2^39 |
| 1071 | 28 | 27 | 2^41 |
| 1073 | 27 | 26 | 2^40 |
| 1075 | 34 | 33 | 2^50 |
| 1077 | 26 | 25 | 2^38 |
| 1079 | 28 | 27 | 2^41 |
| 1081 | 31 | 30 | 2^45 |
| 1083 | 36 | 35 | 2^53 |
| 1085 | 33 | 30 | 2^48 |
| 1087 | 32 | 30 | 2^47 |
| 1089 | 31 | 30 | 2^46 |
| 1091 | 31 | 30 | 2^45 |
| 1093 | 25 | 23 | 2^38 |
| 1095 | 42 | 41 | 2^62 |
| 1097 | 27 | 25 | 2^39 |
| 1099 | 28 | 26 | 2^41 |
| 1101 | 26 | 24 | 2^39 |
| 1103 | 28 | 27 | 2^41 |
| 1105 | 27 | 26 | 2^39 |
| 1107 | 33 | 32 | 2^48 |
| 1109 | 25 | 19 | 2^37 |
| 1111 | 28 | 27 | 2^42 |
| 1113 | 27 | 23 | 2^39 |
| 1115 | 28 | 26 | 2^42 |
| 1117 | 34 | 33 | 2^51 |
| 1119 | 28 | 27 | 2^42 |
| 1121 | 27 | 26 | 2^39 |
| 1123 | 28 | 23 | 2^42 |
| 1125 | 29 | 28 | 2^42 |
| 1127 | 33 | 31 | 2^49 |
| 1129 | 32 | 31 | 2^47 |
| 1131 | 29 | 26 | 2^42 |
| 1133 | 29 | 28 | 2^42 |
| 1135 | 41 | 39 | 2^60 |
| 1137 | 29 | 28 | 2^43 |
| 1139 | 29 | 25 | 2^43 |
| 1141 | 29 | 28 | 2^43 |
| 1143 | 37 | 34 | 2^54 |
| 1145 | 29 | 28 | 2^43 |
| 1147 | 29 | 26 | 2^43 |
| 1149 | 33 | 32 | 2^48 |
| 1151 | 29 | 27 | 2^43 |
| 1153 | 30 | 28 | 2^44 |
| 1155 | 30 | 24 | 2^44 |
| 1157 | 30 | 28 | 2^44 |
| 1159 | 30 | 27 | 2^44 |
| 1161 | 30 | 28 | 2^45 |
| 1163 | 31 | 26 | 2^45 |
| 1165 | 31 | 28 | 2^46 |
| 1167 | 32 | 27 | 2^47 |
| 1169 | 35 | 28 | 2^51 |
| 1171 | 32 | 25 | 2^47 |
| 1173 | 39 | 36 | 2^57 |
| 1175 | 31 | 27 | 2^45 |
| 1177 | 30 | 28 | 2^45 |
| 1179 | 30 | 26 | 2^44 |
| 1181 | 30 | 28 | 2^44 |
| 1183 | 30 | 27 | 2^44 |
| 1185 | 30 | 28 | 2^44 |
| 1187 | 29 | 22 | 2^43 |
| 1189 | 29 | 28 | 2^43 |
| 1191 | 29 | 27 | 2^43 |
| 1193 | 29 | 28 | 2^43 |
| 1195 | 29 | 26 | 2^43 |
| 1197 | 29 | 28 | 2^43 |
| 1199 | 29 | 27 | 2^43 |
| 1201 | 29 | 28 | 2^43 |
| 1203 | 37 | 35 | 2^54 |
| 1205 | 29 | 28 | 2^42 |
| 1207 | 29 | 27 | 2^42 |
| 1209 | 32 | 31 | 2^47 |
| 1211 | 34 | 31 | 2^50 |
| 1213 | 32 | 31 | 2^47 |
| 1215 | 29 | 27 | 2^42 |
| 1217 | 29 | 28 | 2^42 |
| 1219 | 28 | 24 | 2^42 |
| 1221 | 34 | 33 | 2^49 |
| 1223 | 28 | 27 | 2^42 |
| 1225 | 26 | 25 | 2^39 |
| 1227 | 28 | 26 | 2^42 |
| 1229 | 27 | 24 | 2^40 |
| 1231 | 33 | 32 | 2^48 |
| 1233 | 26 | 25 | 2^38 |
| 1235 | 28 | 25 | 2^42 |
| 1237 | 35 | 34 | 2^52 |
| 1239 | 28 | 27 | 2^42 |
| 1241 | 26 | 23 | 2^39 |
| 1243 | 28 | 26 | 2^41 |
| 1245 | 27 | 25 | 2^40 |
| 1247 | 31 | 30 | 2^45 |
| 1249 | 31 | 30 | 2^46 |
| 1251 | 32 | 30 | 2^47 |
| 1253 | 33 | 30 | 2^49 |
| 1255 | 31 | 30 | 2^46 |
| 1257 | 31 | 30 | 2^46 |
| 1259 | 31 | 30 | 2^45 |
| 1261 | 27 | 23 | 2^40 |
| 1263 | 36 | 33 | 2^53 |
| 1265 | 26 | 25 | 2^38 |
| 1267 | 28 | 25 | 2^41 |
| 1269 | 27 | 26 | 2^40 |
| 1271 | 28 | 27 | 2^41 |
| 1273 | 33 | 32 | 2^49 |
| 1275 | 28 | 26 | 2^41 |
| 1277 | 27 | 25 | 2^40 |
| 1279 | 28 | 27 | 2^41 |
| 1281 | 26 | 23 | 2^38 |
| 1283 | 28 | 24 | 2^41 |
| 1285 | 27 | 26 | 2^40 |
| 1287 | 28 | 27 | 2^41 |
| 1289 | 26 | 25 | 2^39 |
| 1291 | 28 | 26 | 2^41 |
| 1293 | 32 | 31 | 2^47 |
| 1295 | 39 | 37 | 2^58 |
| 1297 | 26 | 25 | 2^38 |
| 1299 | 28 | 25 | 2^41 |
| 1301 | 27 | 26 | 2^40 |
| 1303 | 28 | 27 | 2^41 |
| 1305 | 34 | 33 | 2^50 |
| 1307 | 28 | 26 | 2^41 |
| 1309 | 27 | 25 | 2^40 |
| 1311 | 28 | 27 | 2^41 |
| 1313 | 26 | 24 | 2^38 |
| 1315 | 34 | 32 | 2^50 |
| 1317 | 30 | 29 | 2^43 |
| 1319 | 30 | 29 | 2^44 |
| 1321 | 30 | 29 | 2^44 |
| 1323 | 30 | 29 | 2^44 |
| 1325 | 30 | 29 | 2^44 |
| 1327 | 30 | 29 | 2^45 |
| 1329 | 30 | 29 | 2^45 |
| 1331 | 35 | 34 | 2^51 |
| 1333 | 31 | 29 | 2^46 |
| 1335 | 32 | 29 | 2^48 |
| 1337 | 33 | 29 | 2^48 |
| 1339 | 31 | 29 | 2^46 |
| 1341 | 31 | 29 | 2^45 |
| 1343 | 31 | 29 | 2^45 |
| 1345 | 30 | 29 | 2^45 |
| 1347 | 30 | 29 | 2^44 |
| 1349 | 30 | 29 | 2^44 |
| 1351 | 30 | 29 | 2^44 |
| 1353 | 30 | 29 | 2^44 |
| 1355 | 30 | 29 | 2^44 |
| 1357 | 36 | 32 | 2^53 |
| 1359 | 26 | 23 | 2^38 |
| 1361 | 28 | 27 | 2^41 |
| 1363 | 27 | 25 | 2^40 |
| 1365 | 28 | 26 | 2^41 |
| 1367 | 26 | 24 | 2^39 |
| 1369 | 28 | 27 | 2^41 |
| 1371 | 27 | 26 | 2^40 |
| 1373 | 28 | 25 | 2^41 |
| 1375 | 26 | 25 | 2^38 |
| 1377 | 33 | 31 | 2^48 |
| 1379 | 32 | 31 | 2^48 |
| 1381 | 28 | 26 | 2^41 |
| 1383 | 35 | 34 | 2^52 |
| 1385 | 28 | 27 | 2^41 |
| 1387 | 37 | 36 | 2^54 |
| 1389 | 28 | 23 | 2^41 |
| 1391 | 26 | 24 | 2^38 |
| 1393 | 28 | 27 | 2^41 |
| 1395 | 27 | 25 | 2^40 |
| 1397 | 28 | 26 | 2^41 |
| 1399 | 34 | 32 | 2^50 |
| 1401 | 28 | 27 | 2^41 |
| 1403 | 27 | 26 | 2^40 |
| 1405 | 28 | 25 | 2^41 |
| 1407 | 27 | 26 | 2^39 |
| 1409 | 34 | 33 | 2^50 |
| 1411 | 27 | 24 | 2^40 |
| 1413 | 31 | 30 | 2^45 |
| 1415 | 31 | 30 | 2^46 |
| 1417 | 38 | 35 | 2^56 |
| 1419 | 33 | 30 | 2^49 |
| 1421 | 32 | 30 | 2^47 |
| 1423 | 31 | 30 | 2^46 |
| 1425 | 31 | 30 | 2^45 |
| 1427 | 31 | 30 | 2^45 |
| 1429 | 28 | 26 | 2^42 |
| 1431 | 27 | 24 | 2^39 |
| 1433 | 28 | 27 | 2^42 |
| 1435 | 27 | 26 | 2^40 |
| 1437 | 28 | 25 | 2^42 |
| 1439 | 27 | 26 | 2^39 |
| 1441 | 33 | 32 | 2^49 |
| 1443 | 27 | 22 | 2^40 |
| 1445 | 28 | 26 | 2^42 |
| 1447 | 37 | 36 | 2^55 |
| 1449 | 29 | 27 | 2^42 |
| 1451 | 35 | 33 | 2^52 |
| 1453 | 29 | 22 | 2^42 |
| 1455 | 29 | 28 | 2^42 |
| 1457 | 29 | 27 | 2^42 |
| 1459 | 32 | 31 | 2^47 |
| 1461 | 34 | 31 | 2^49 |
| 1463 | 32 | 31 | 2^47 |
| 1465 | 29 | 27 | 2^43 |
| 1467 | 29 | 28 | 2^43 |
| 1469 | 29 | 25 | 2^43 |
| 1471 | 29 | 28 | 2^43 |
| 1473 | 29 | 27 | 2^43 |
| 1475 | 29 | 28 | 2^43 |
| 1477 | 37 | 34 | 2^54 |
| 1479 | 29 | 28 | 2^43 |
| 1481 | 29 | 27 | 2^43 |
| 1483 | 33 | 32 | 2^48 |
| 1485 | 30 | 24 | 2^44 |
| 1487 | 30 | 28 | 2^44 |
| 1489 | 30 | 27 | 2^44 |
| 1491 | 30 | 28 | 2^44 |
| 1493 | 41 | 38 | 2^61 |
| 1495 | 30 | 28 | 2^45 |
| 1497 | 31 | 27 | 2^45 |
| 1499 | 31 | 28 | 2^46 |
| 1501 | 32 | 25 | 2^47 |
| 1503 | 35 | 28 | 2^51 |
| 1505 | 32 | 27 | 2^47 |
| 1507 | 31 | 28 | 2^46 |
| 1509 | 31 | 26 | 2^45 |
| 1511 | 31 | 28 | 2^45 |
| 1513 | 30 | 27 | 2^45 |
| 1515 | 30 | 28 | 2^44 |
| 1517 | 30 | 23 | 2^44 |
| 1519 | 30 | 28 | 2^44 |
| 1521 | 30 | 27 | 2^44 |
| 1523 | 46 | 43 | 2^67 |
| 1525 | 33 | 32 | 2^48 |
| 1527 | 29 | 28 | 2^43 |
| 1529 | 29 | 27 | 2^43 |
| 1531 | 29 | 28 | 2^43 |
| 1533 | 42 | 41 | 2^62 |
| 1535 | 29 | 28 | 2^43 |
| 1537 | 36 | 35 | 2^53 |
| 1539 | 38 | 37 | 2^55 |
| 1541 | 29 | 26 | 2^43 |
| 1543 | 32 | 31 | 2^47 |
| 1545 | 35 | 31 | 2^51 |
| 1547 | 32 | 31 | 2^47 |
| 1549 | 29 | 24 | 2^42 |
| 1551 | 29 | 28 | 2^42 |
| 1553 | 29 | 27 | 2^42 |
| 1555 | 34 | 33 | 2^50 |
| 1557 | 29 | 26 | 2^42 |
| 1559 | 29 | 28 | 2^42 |
| 1561 | 29 | 27 | 2^42 |
| 1563 | 29 | 28 | 2^42 |
| 1565 | 33 | 32 | 2^48 |
| 1567 | 27 | 26 | 2^40 |
| 1569 | 38 | 37 | 2^56 |
| 1571 | 36 | 34 | 2^53 |
| 1573 | 41 | 40 | 2^60 |
| 1575 | 27 | 25 | 2^40 |
| 1577 | 28 | 27 | 2^42 |
| 1579 | 31 | 30 | 2^45 |
| 1581 | 31 | 30 | 2^46 |
| 1583 | 31 | 30 | 2^46 |
| 1585 | 32 | 30 | 2^47 |
| 1587 | 34 | 30 | 2^49 |
| 1589 | 32 | 30 | 2^47 |
| 1591 | 31 | 30 | 2^46 |
| 1593 | 31 | 30 | 2^45 |
| 1595 | 31 | 30 | 2^45 |
| 1597 | 35 | 33 | 2^52 |
| 1599 | 27 | 26 | 2^40 |
| 1601 | 28 | 27 | 2^41 |
| 1603 | 27 | 24 | 2^39 |
| 1605 | 28 | 26 | 2^41 |
| 1607 | 33 | 32 | 2^49 |
| 1609 | 28 | 27 | 2^41 |
| 1611 | 27 | 26 | 2^39 |
| 1613 | 28 | 24 | 2^41 |
| 1615 | 39 | 38 | 2^57 |
| 1617 | 28 | 27 | 2^41 |
| 1619 | 27 | 25 | 2^39 |
| 1621 | 28 | 26 | 2^41 |
| 1623 | 27 | 24 | 2^40 |
| 1625 | 28 | 27 | 2^41 |
| 1627 | 32 | 31 | 2^48 |
| 1629 | 33 | 31 | 2^49 |
| 1631 | 37 | 35 | 2^54 |
| 1633 | 28 | 27 | 2^41 |
| 1635 | 27 | 23 | 2^39 |
| 1637 | 28 | 26 | 2^41 |
| 1639 | 34 | 33 | 2^50 |
| 1641 | 28 | 27 | 2^41 |
| 1643 | 27 | 26 | 2^39 |
| 1645 | 28 | 23 | 2^41 |
| 1647 | 30 | 29 | 2^44 |
| 1649 | 34 | 32 | 2^50 |
| 1651 | 30 | 29 | 2^44 |
| 1653 | 40 | 39 | 2^59 |
| 1655 | 30 | 29 | 2^44 |
| 1657 | 30 | 29 | 2^44 |
| 1659 | 30 | 29 | 2^45 |
| 1661 | 37 | 36 | 2^55 |
| 1663 | 31 | 29 | 2^45 |
| 1665 | 35 | 34 | 2^51 |
| 1667 | 32 | 29 | 2^46 |
| 1669 | 33 | 29 | 2^48 |
| 1671 | 33 | 29 | 2^48 |
| 1673 | 32 | 29 | 2^47 |
| 1675 | 31 | 29 | 2^46 |
| 1677 | 31 | 29 | 2^45 |
| 1679 | 30 | 29 | 2^45 |
| 1681 | 30 | 29 | 2^45 |
| 1683 | 30 | 29 | 2^44 |
| 1685 | 30 | 29 | 2^44 |
| 1687 | 30 | 29 | 2^44 |
| 1689 | 30 | 29 | 2^44 |
| 1691 | 39 | 32 | 2^58 |
| 1693 | 30 | 29 | 2^44 |
| 1695 | 28 | 26 | 2^41 |
| 1697 | 27 | 24 | 2^39 |
| 1699 | 28 | 27 | 2^41 |
| 1701 | 27 | 26 | 2^40 |
| 1703 | 28 | 25 | 2^41 |
| 1705 | 27 | 26 | 2^39 |
| 1707 | 28 | 27 | 2^41 |
| 1709 | 32 | 31 | 2^47 |
| 1711 | 33 | 31 | 2^48 |
| 1713 | 33 | 31 | 2^48 |
| 1715 | 32 | 31 | 2^46 |
| 1717 | 35 | 34 | 2^51 |
| 1719 | 28 | 22 | 2^41 |
| 1721 | 37 | 36 | 2^55 |
| 1723 | 28 | 27 | 2^41 |
| 1725 | 27 | 25 | 2^40 |
| 1727 | 28 | 26 | 2^41 |
| 1729 | 27 | 22 | 2^39 |
| 1731 | 28 | 27 | 2^41 |
| 1733 | 34 | 32 | 2^50 |
| 1735 | 28 | 25 | 2^41 |
| 1737 | 27 | 26 | 2^39 |
| 1739 | 28 | 27 | 2^42 |
| 1741 | 27 | 24 | 2^40 |
| 1743 | 34 | 33 | 2^51 |
| 1745 | 31 | 30 | 2^45 |
| 1747 | 31 | 30 | 2^45 |
| 1749 | 31 | 30 | 2^46 |
| 1751 | 36 | 35 | 2^54 |
| 1753 | 33 | 30 | 2^49 |
| 1755 | 32 | 30 | 2^48 |
| 1757 | 31 | 30 | 2^46 |
| 1759 | 35 | 34 | 2^51 |
| 1761 | 31 | 30 | 2^45 |
| 1763 | 28 | 27 | 2^42 |
| 1765 | 27 | 26 | 2^40 |
| 1767 | 29 | 25 | 2^42 |
| 1769 | 29 | 28 | 2^42 |
| 1771 | 29 | 27 | 2^42 |
| 1773 | 29 | 28 | 2^42 |
| 1775 | 33 | 32 | 2^49 |
| 1777 | 29 | 28 | 2^42 |
| 1779 | 29 | 27 | 2^42 |
| 1781 | 29 | 28 | 2^42 |
| 1783 | 29 | 23 | 2^42 |
| 1785 | 36 | 33 | 2^52 |
| 1787 | 29 | 27 | 2^43 |
| 1789 | 29 | 28 | 2^43 |
| 1791 | 29 | 26 | 2^43 |
| 1793 | 32 | 31 | 2^47 |
| 1795 | 34 | 31 | 2^50 |
| 1797 | 32 | 31 | 2^48 |
| 1799 | 29 | 25 | 2^43 |
| 1801 | 29 | 28 | 2^43 |
| 1803 | 29 | 27 | 2^43 |
| 1805 | 29 | 28 | 2^43 |
| 1807 | 29 | 26 | 2^43 |
| 1809 | 29 | 28 | 2^43 |
| 1811 | 36 | 34 | 2^53 |
| 1813 | 39 | 37 | 2^58 |
| 1815 | 30 | 24 | 2^44 |
| 1817 | 33 | 32 | 2^49 |
| 1819 | 30 | 27 | 2^44 |
| 1821 | 30 | 28 | 2^44 |
| 1823 | 30 | 26 | 2^44 |
| 1825 | 30 | 28 | 2^45 |
| 1827 | 34 | 33 | 2^50 |
| 1829 | 31 | 28 | 2^45 |
| 1831 | 31 | 25 | 2^46 |
| 1833 | 31 | 28 | 2^46 |
| 1835 | 32 | 27 | 2^47 |
| 1837 | 35 | 28 | 2^51 |
| 1839 | 32 | 26 | 2^47 |
| 1841 | 31 | 28 | 2^46 |
| 1843 | 31 | 27 | 2^46 |
| 1845 | 36 | 35 | 2^53 |
| 1847 | 30 | 21 | 2^45 |
| 1849 | 48 | 45 | 2^70 |
| 1851 | 40 | 39 | 2^59 |
| 1853 | 30 | 28 | 2^44 |
| 1855 | 30 | 26 | 2^44 |
| 1857 | 33 | 32 | 2^48 |
| 1859 | 33 | 32 | 2^48 |
| 1861 | 30 | 28 | 2^44 |
| 1863 | 30 | 25 | 2^44 |
| 1865 | 29 | 28 | 2^43 |
| 1867 | 29 | 27 | 2^43 |
| 1869 | 29 | 28 | 2^43 |
| 1871 | 36 | 35 | 2^52 |
| 1873 | 29 | 28 | 2^43 |
| 1875 | 29 | 27 | 2^43 |
| 1877 | 32 | 31 | 2^47 |
| 1879 | 35 | 31 | 2^51 |
| 1881 | 32 | 31 | 2^47 |
| 1883 | 29 | 27 | 2^43 |
| 1885 | 29 | 28 | 2^43 |
| 1887 | 29 | 26 | 2^43 |
| 1889 | 39 | 38 | 2^57 |
| 1891 | 29 | 27 | 2^43 |
| 1893 | 29 | 28 | 2^42 |
| 1895 | 29 | 25 | 2^42 |
| 1897 | 29 | 28 | 2^42 |
| 1899 | 33 | 32 | 2^49 |
| 1901 | 33 | 32 | 2^48 |
| 1903 | 29 | 26 | 2^42 |
| 1905 | 37 | 34 | 2^54 |
| 1907 | 29 | 27 | 2^42 |
| 1909 | 29 | 28 | 2^42 |
| 1911 | 31 | 30 | 2^45 |
| 1913 | 31 | 30 | 2^45 |
| 1915 | 31 | 30 | 2^46 |
| 1917 | 32 | 30 | 2^46 |
| 1919 | 32 | 30 | 2^48 |
| 1921 | 34 | 30 | 2^50 |
| 1923 | 32 | 30 | 2^47 |
| 1925 | 31 | 30 | 2^46 |
| 1927 | 31 | 30 | 2^46 |
| 1929 | 31 | 30 | 2^45 |
| 1931 | 42 | 40 | 2^61 |
| 1933 | 27 | 24 | 2^40 |
| 1935 | 38 | 36 | 2^56 |
| 1937 | 27 | 25 | 2^40 |
| 1939 | 28 | 27 | 2^42 |
| 1941 | 33 | 32 | 2^49 |
| 1943 | 28 | 24 | 2^42 |
| 1945 | 27 | 26 | 2^40 |
| 1947 | 28 | 27 | 2^42 |
| 1949 | 27 | 25 | 2^40 |
| 1951 | 43 | 42 | 2^63 |
| 1953 | 27 | 24 | 2^41 |
| 1955 | 28 | 27 | 2^41 |
| 1957 | 35 | 34 | 2^51 |
| 1959 | 32 | 31 | 2^47 |
| 1961 | 44 | 43 | 2^64 |
| 1963 | 33 | 31 | 2^49 |
| 1965 | 38 | 35 | 2^56 |
| 1967 | 28 | 26 | 2^41 |
| 1969 | 28 | 25 | 2^41 |
| 1971 | 42 | 41 | 2^62 |
| 1973 | 35 | 33 | 2^51 |
| 1975 | 28 | 22 | 2^41 |
| 1977 | 30 | 29 | 2^44 |
| 1979 | 30 | 29 | 2^44 |
| 1981 | 30 | 29 | 2^44 |
| 1983 | 34 | 32 | 2^50 |
| 1985 | 30 | 29 | 2^44 |
| 1987 | 30 | 29 | 2^44 |
| 1989 | 30 | 29 | 2^44 |
| 1991 | 30 | 29 | 2^45 |
| 1993 | 30 | 29 | 2^45 |
| 1995 | 37 | 36 | 2^54 |
| 1997 | 31 | 29 | 2^46 |
| 1999 | 35 | 34 | 2^52 |
| 2001 | 32 | 29 | 2^47 |
| 2003 | 33 | 29 | 2^48 |
| 2005 | 33 | 29 | 2^49 |
| 2007 | 32 | 29 | 2^47 |
| 2009 | 31 | 29 | 2^46 |
| 2011 | 41 | 38 | 2^60 |
| 2013 | 31 | 29 | 2^45 |
| 2015 | 34 | 33 | 2^50 |
| 2017 | 30 | 29 | 2^45 |
| 2019 | 30 | 29 | 2^44 |
| 2021 | 30 | 29 | 2^44 |
| 2023 | 30 | 29 | 2^44 |
| 2025 | 36 | 32 | 2^54 |
| 2027 | 30 | 29 | 2^44 |
| 2029 | 30 | 29 | 2^44 |
| 2031 | 30 | 29 | 2^44 |
| 2033 | 30 | 29 | 2^44 |
| 2035 | 34 | 33 | 2^49 |
| 2037 | 28 | 27 | 2^41 |
| 2039 | 28 | 23 | 2^41 |
| 2041 | 28 | 26 | 2^41 |
| 2043 | 32 | 31 | 2^47 |
