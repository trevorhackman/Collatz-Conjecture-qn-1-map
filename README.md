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
| `--digits cbrt2 [--places N]`                | Binary places of 2^{1/3} and 2^{2/3}         | 1072000000 places                                                       |
| `--digits sqrt2 [--places N]`                | Binary places of √2                          | 1072000000 places                                                       |

`--length` on a table selects that published row, and `--max-h` replaces its limit. `--tables any --length 6` is length 6 through h ≤ 1400. `--tables any --length 6 --max-h 100` stops at h ≤ 100. `--min-length` starts a constant-q search at that length: `--q 1093 --min-length 201 --max-length 250` searches lengths 201 through 250. `--digits cbrt2 --places 10000` and `--digits sqrt2 --places 10000` scan fewer places.

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
| \(\theta\) | theta | On a cycle it is often the root \(2^{r/\ell}\), and \(\theta_i = 2^{ir/\ell}\). In two estimates it is also a constant positive real used as an exponent. |
| \(\Theta\) | capital theta | The set of real numbers \(2^{t/\ell}\) with \(1 \le t \le \ell(\ell-1)\) and with \(\ell\) not dividing \(t\). |
| \(\kappa\) | kappa | The parameter \(\kappa(7)\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here it equals 4. |
| \(\lambda\) | lambda | The real numbers \(\lambda_1\) and \(\lambda_2\) from Bauer and Bennett (2002). Here \(\lambda_2\) is the constant real 1.617. |
| \(\Lambda\) | capital lambda | A linear form in logarithms in Bauer and Bennett (2018). Beukers's lemma is that one choice of this form is nonzero. In Paper II the same letter is also a logarithm used to bound \(q\). |
| \(\mu\) | mu | A restricted irrationality exponent for a length \(\ell \ge 2\): \(\lvert 2^{r/\ell} - p/(\ell x \cdot 2^j)\rvert > c\,(\ell x \cdot 2^j)^{-\mu}\) for every integer \(r\) with \(1 \le r \le \ell-1\), every odd positive integer \(x\), every integer \(j \ge 0\), and every integer \(p\), with an explicit constant real number \(c > 0\). The labels \(\mu_2\) and \(\mu_3\) are the exponents used at lengths 2 and 3. |
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

Fixed odd q. For each q in the table, the search runs the lengths in that row. The only values of h are those with q = floor(2^{h/ℓ}). At q = 5, lengths 2 through 20, the cycles found are 1→3, 13→33→83, and 17→43→27. For each odd q from 7 through 25, lengths 2 through 16, the search finds no cycle. Each search took under 1 s.

| q | lengths searched | Cycles |
|---|---|---|
| 5 | 2 through 20 | 1→3; 13→33→83 and 17→43→27 |
| 7 | 2 through 16 | none |
| 9 | 2 through 16 | none |
| 11 | 2 through 16 | none |
| 13 | 2 through 16 | none |
| 15 | 2 through 16 | none |
| 17 | 2 through 16 | none |
| 19 | 2 through 16 | none |
| 21 | 2 through 16 | none |
| 23 | 2 through 16 | none |
| 25 | 2 through 16 | none |
