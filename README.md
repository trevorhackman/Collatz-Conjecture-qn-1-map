# Collatz-Conjecture-qn+1-map

The paper on cycles of the qn+1 map, the notes, and the searches behind the tables.

## Contents

- `paper/cycles-through-1-qn1.tex`, PDF `paper/cycles-through-1-qn1.pdf` — the paper. No cycles of length 3 through 1, finiteness for each length, and constraints on longer cycles through 1.
- `notes/fixed-start-qn1.tex` — notes on cycles beyond 1. An explicit bound on q for a constant odd starting value of a cycle of length 2 or 3.
- `notes/divergence-qn1.tex` — notes on divergent orbits.
- `cycle-search/` — Gradle Kotlin project. Run the commands below from that directory.

## Commands

Each line is `./search …` from `cycle-search/`. An omitted option takes the default in the last column.

| Command                                      | Search                                       | Default                                                                 |
| -------------------------------------------- | -------------------------------------------- | ----------------------------------------------------------------------- |
| `--length N --max-h N`                       | One length, \(h\) up to the bound            | both required                                                           |
| `--through-1 --length N --max-h N`           | One length, starting value 1, \(h\) up to the bound | both required                                                    |
| `--q N --max-length N --checked-odds N`      | One odd q, lengths from the minimum through the maximum | all three required; `--min-length` starts at 2                  |
| `--digits cbrt2 --places N`                  | Binary places of 2^{1/3} and 2^{2/3}         | `--places` required                                                     |
| `--digits sqrt2 --places N`                  | Binary places of √2                          | `--places` required                                                     |

`--length` fixes the length, and `--max-h` is the halving bound. `--length 6 --max-h 1400` is length 6 through h ≤ 1400. `--length 6 --max-h 100` stops at h ≤ 100. `--min-length` starts a constant-q search at that length: `--q 1093 --min-length 201 --max-length 250` searches lengths 201 through 250. `--digits cbrt2 --places 10000` and `--digits sqrt2 --places 10000` scan fewer places.

`--through-1` follows the orbit of 1 and does not enumerate halving tuples, so its halving bound can sit far above a search with the length fixed and the starting value free. `--through-1` alone uses length 3 and stops at h ≤ 66100. Theorem 1 needs only h < 66003. The length-3 row in the table below is `--through-1 --length 3 --max-h 200000`.

## Machine

The runs below were done one at a time on a MacBook Pro with an Apple M4 Pro, 14 cores (10 performance and 4 efficiency), and 48 GB of memory, running macOS 26.6.2 and JetBrains Runtime 25. The time is the search itself.

## Papers

Author: Trevor Hackman

The paper proves that no odd q ≥ 3 has a cycle of length 3 with starting value 1. For every such q at least 2^22001, a lower bound and an upper bound cannot hold at the same time. Every smaller q has h < 66003. The length-3 search through h ≤ 200000 rules those out, and the digit scan rules out q < 2^1071999953. For each length at least 2, only finitely many odd q have a cycle of that length, and that finiteness gives no bound. The case of starting value 1 uses only the S-unit form of Ridout's theorem. When a length has a restricted irrationality exponent below the golden ratio, the same comparison produces a bound that a finite search can finish for cycles through 1.

The notes in `notes/fixed-start-qn1.tex` record that if a constant odd positive integer x is the starting value of a cycle of length 2 or 3, then q lies below an explicit bound depending only on x. For x = 1 and length 3 that bound is q < 2^17990, which the search in the paper already covers.

## Non-English letters

\(\ell\) is the script form of l. The second column is the English name. A subscript or a prime only labels that same letter, so \(\mu_2\) is mu and \(\theta_i\) is theta. Phi is printed in the open form \(\varphi\), and epsilon in the form \(\varepsilon\). Mu is said mew, phi is said fie, theta is said thay-tuh, eta is said ay-tuh, rho is said row, tau rhymes with cow, lambda is said lam-duh, kappa is said kap-uh, chi is said kye, xi is said ksigh, and omega is said oh-may-guh.

| Symbol | Name | Where it is used |
| --- | --- | --- |
| \(\ell\) | ell | The length of a cycle: how many distinct odd numbers lie on it. |
| \(\alpha\) | alpha | An algebraic irrational, in the statement of Ridout's theorem. In the notes it is also the parameter \(\alpha = 3\) in Theorem 1.5 of Bauer and Bennett (2002). |
| \(\beta\) | beta | In the paper, the root \(2^{r/\ell}\), including \(2^{1/3}\) and \(2^{2/3}\). |
| \(\gamma\) | gamma | The positive real \(1 - \mu + 1/\mu\). It is positive when \(\mu\) lies below the golden ratio. The labels \(\gamma_2\) and \(\gamma_3\) are this quantity at lengths 2 and 3. |
| \(\delta\) | delta | The fractional part of \(2^{h/\ell}\), so \(2^{h/\ell} = q + \delta\) with \(0 < \delta < 1\). |
| \(\Delta\) | capital delta | The parameters \(\Delta_0\) and \(\Delta_1\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here both equal 1. |
| \(\varepsilon\) | epsilon | A positive real. In Ridout's theorem it is the extra room in the exponent of an approximation. \(\varepsilon_i\) is the factor in the product for \(x_i\): in the paper, \(x_i \, 2^{S_i} = q^i(1 + \varepsilon_i)\), and in the notes, \(x_i \, 2^{S_i} = x \, q^i(1 + \varepsilon_i)\). |
| \(\eta\) | eta | A positive real. Ridout's theorem supplies it as a lower bound on certain distances and gives no formula for its value. |
| \(\theta\) | theta | On a cycle it is the root \(2^{r/\ell}\), and \(\theta_i = 2^{ir/\ell}\). In an earlier argument in the paper it is also a constant positive real used as an exponent. |
| \(\Theta\) | capital theta | The set of real numbers \(2^{t/\ell}\) with \(1 \le t \le \ell(\ell-1)\) and with \(\ell\) not dividing \(t\). |
| \(\kappa\) | kappa | The parameter \(\kappa(7)\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here it equals 4. |
| \(\lambda\) | lambda | The real numbers \(\lambda_1\) and \(\lambda_2\) from Bauer and Bennett (2002). Here \(\lambda_2\) is the constant real 1.617. |
| \(\Lambda\) | capital lambda | A linear form in Bauer and Bennett (2018). Both papers use it there. Beukers's lemma is that one choice of this form is nonzero. |
| \(\mu\) | mu | A restricted irrationality exponent for a length \(\ell \ge 2\). For each odd positive integer \(x\) there is a positive real number \(c(x)\) and a nonnegative integer \(j_0(x)\) such that, for every integer \(r\) with \(1 \le r \le \ell-1\), every integer \(p\), and every integer \(j \ge j_0(x)\), \(\lvert 2^{r/\ell} - p/(\ell x \cdot 2^j)\rvert > c(x) \cdot 2^{-\mu j}\). The labels \(\mu_2\) and \(\mu_3\) are the exponents used at lengths 2 and 3. |
| \(\xi\) | xi | The rational \(7/2^{15}\), a parameter in Theorem 1.5 of Bauer and Bennett (2002). |
| \(\Pi\) | capital pi | A product built from Padé approximants in Bauer and Bennett (2018). |
| \(\prod\) | capital pi | The product sign. Both papers use it to multiply a run of factors, including the product that equals \(2^h\). |
| \(\rho\) | rho | The relative error of a near miss. In the paper, \(x_i = 2^{n_i/\ell}(1+\rho_i)\). In the notes, \(x_i = x \, 2^{n_i/\ell}(1+\rho_i)\). |
| \(\sigma\) | sigma | A sum of reciprocals of odd elements. In the paper the sum runs over the elements other than 1. In the notes the sum runs over every element. |
| \(\sum\) | capital sigma | The summation sign. Both papers use it to add a run of terms, including the sum of reciprocals that defines \(\sigma\). |
| \(\tau\) | tau | The input to an auxiliary function \(F\) in Bauer and Bennett (2018). |
| \(\varphi\) | phi | The golden ratio \((1 + \sqrt{5})/2\). |
| \(\chi\) | chi | The parameters \(\chi_1\), \(\chi_2\), and \(\chi_3\) in Theorem 1.5 of Bauer and Bennett (2002). |
| \(\Omega\) | capital omega | The parameters \(\Omega\) and \(\Omega_1\) in Theorem 1.5 of Bauer and Bennett (2002). In the application here both equal \(2^{17}\). |

## Predicted mathematical strength and relevance

Mathematical Strength ranks a general theorem above the special case it implies, and a proof that constrains every q above a search that only pushes a numerical fence. Novelty ranks how much of the conclusion was not already in Santos, Beukers, Franco, Ridout, or Bauer and Bennett.

### Mathematical Strength

1. **Notes, Theorem 3**, in the cases **Notes, Corollary 5** and **Notes, Corollary 6.** A named odd starting value \(x\) of a cycle of length 3 forces \(q < Q_3(x)\), and of a cycle of length 2 forces \(q < Q_2(x)\).
2. **Paper I Theorem 2.** A restricted irrationality exponent below the golden ratio puts every cycle through 1 of that length under a computable bound.
3. **Paper I Theorem 1.** The number 1 lies on no cycle of length 3. This is Paper I Theorem 2 at length 3, with the finite check finished.
4. **Paper I Theorem 4.** Each length at least 2 occurs for only finitely many odd q, for a cycle through any elements. No bound.
5. **Notes, Corollary 14.** The same explicit bound on q, for a named odd starting value, when ℓ = 2^a and h ≡ ℓ/2 (mod ℓ), or ℓ = 3 · 2^a and h ≡ ℓ/3 or 2ℓ/3 (mod ℓ).
6. **Paper I Corollary 12**, **Paper I Corollary 13**, and **Paper I Corollary 14.** Those classes for cycles through 1. Paper I Corollary 12 shows that no such q exists.
7. **Paper I Theorem 3.** Each length at least 2 occurs for only finitely many odd q with 1 on the cycle. No bound.
8. **Notes, Proposition 12.** Every other length-2 cycle has q ≥ 2^1071999971 and every odd element greater than 2^17631663.
9. **Paper I Proposition A.1** and **Paper I Proposition A.2.** Length 1 is exactly q = 2^e − 1. Length 2 through 1 is exactly q = 5.

### Novelty

1. **Paper I Theorem 1.** No cycle of length 3 through 1, using Bauer and Bennett’s unrounded exponent below the golden ratio.
2. **Paper I Theorem 2.** The golden-ratio criterion for an arbitrary length through 1.
3. **Notes, Corollary 5**, from **Notes, Theorem 3** and **Notes, Lemma 4 (constant odd part).** A named odd starting value \(x\) of a length-3 cycle has \(q < Q_3(x)\). The constant-odd-part lemma is the cube-root measure for an arbitrary odd part.
4. **Notes, Proposition 12.** The length-2 lower bound. Franco already excludes every other period-2 orbit with |q| < 10^11.
5. **Notes, Corollary 6.** A named odd starting value \(x\) of a length-2 cycle has \(q < Q_2(x)\).
6. **Paper I Corollary 12**, **Paper I Corollary 13**, and **Paper I Corollary 14.** The length-4 case through 1 with h ≡ 2 (mod 4) has no such q, and the other two classes through 1 have a computable bound.
7. **Notes, Corollary 14.** Those same classes with a named odd starting value, and q under an explicit bound.
8. **Paper I Theorem 4.** Finitely many q for each length, with no bound.
9. **Paper I Theorem 3.** Finitely many q for each length through 1, with no bound.
10. **Paper I Proposition A.1** and **Paper I Proposition A.2.** Length 1 is $1 \to 1$ for a Mersenne number, and length 2 through 1 is only q = 5.

## Known cycles

- Through 1: 1→1 for a Mersenne number, and 1→3→1 for q=5
- Not through 1: 13→33→83 and 17→43→27 for q=5; 27→611 and 35→99 for q=181

Length 1 is classified: C(x₀)=x₀ forces x₀(2^h−q)=1, so the only positive odd cycle is 1→1 with q=2^h−1. Every cycle of length ℓ≥2 has q = floor(2^{h/ℓ}) with ℓ not dividing h. The notes conjecture that the cycles above are all of them.

## Tables

A length whose window q^ℓ < 2^h < (q+1)^ℓ contains no integer h has no cycle of that length.

### Length fixed

`./search --length 2 --max-h 320000`

This is a search that finds any or proves there's no cycles for any starting value, for one fixed length, and for \(h\) up to the bound. It searches halving counts. One row is `--length 2 --max-h 320000`. Another is `--length 3 --max-h 100000`.

Length 2 doubles the residue \(2^k\) modulo the gap. Length 3 halves the residue \(q^2 2^{-k}\) modulo the gap, which is odd, and matches it against those powers of 2. The run through \(h \le 100000\) used 5 threads. Lengths 4 through 9 store one half of the counts and walk the other.

Through \(h \le 160000\) the length-2 search took 146 s. The same search through \(h \le 320000\) took 1360 s. Length 3 through \(h \le 100000\) took 2681 s. Lengths 4 through 9 stay at a much smaller \(h\), where each takes about two or three minutes.

The length-2 search is the range \(m < 160000\) in the notes. Together with Theorem 1.5 of Bauer and Bennett (2002), at \(\alpha = 3\) with the equation \(181^2 + 7 = 2^{15}\) and exponent \(1.617\), it gives that every other length-2 cycle has every odd element greater than \(2^{2559}\). The digit scan of \(\sqrt{2}\) then gives Notes, Proposition 12: every other length-2 cycle has \(q \ge 2^{1071999971}\) and every odd element greater than \(2^{17631663}\).

The length-3 search through \(h \le 100000\) finds only \(13 \to 33 \to 83\) and \(17 \to 43 \to 27\), both at \(q = 5\). Every other length-3 cycle has \(q \ge 2^{33333}\). The notes' computations table records the shorter search through \(h \le 7000\), which is \(q < 2^{2333}\).

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

### Length fixed, starting value 1

`./search --through-1 --length 3 --max-h 200000`

This is a search that finds any or proves there's no cycles through 1, for one fixed length, and for \(h\) up to the bound. It follows the orbit of 1. It does not enumerate halving counts. Length 2 is not a search bound: Beukers's theorem leaves only q=5. One row is `--through-1 --length 4 --max-h 100000`. The length-3 row is the search through h ≤ 200000. Theorem 1 needs only h < 66003, since q < 2^22001 forces 2^h < 2^66003.

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

### Digits of \(2^{1/3}\) and \(2^{2/3}\)

`./search --digits cbrt2`

This is a scan of the binary places of \(2^{1/3}\) and \(2^{2/3}\). It reports the longest alternating run and the longest zero run.

Paper I, “Digits of \(2^{1/3}\) and \(2^{2/3}\).” For m from 1 through 1071999999, \(2^{1/3}\) has longest alternating run 32 at m = 684454163 and longest zero run 27 at m = 31318881 and m = 955995734. \(2^{2/3}\) has longest alternating run 31 at m = 1024436159 and longest zero run 26 at m = 71965347, 111843987, 307341593, 496232916, 611035374, and 1013171868. The scan took 193 s.

### Digits of √2

`./search --digits sqrt2`

This is a scan of the binary places of √2. It reports the longest zero run.

The notes, “Digits of √2.” For m from 1 through 1071999999, the longest zero run has length 29, at m = 1005210007. The scan took 143 s.

### q fixed

`./search --q 5 --max-length 100`

This is a search that finds any or proves there's no cycles for a fixed \(q\), for any starting value, and length up to the search bounds. It searches halving counts, not starting values. The first 100 odd positive integers, 1 through 199, are followed. Every other cycle has distinct elements at least 201, so \(2^h\) is at most \((q + 1/201)(q + 1/203)\cdots\) with \(\ell\) factors. Those counts determine the starting value: it is the numerator \(N\) divided by the gap \(2^h - q^\ell\). When that product leaves no integer \(h\), that length has no cycle except one found on a checked orbit. One \(q\) is `--q 5 --max-length 100 --checked-odds 100`.

Odd \(q\) from 5 through 181 were checked for the lengths listed below.

| q | lengths searched | Cycles | Time |
|---|---|---|---|
| 5 | 2 through 100 | 1→3; 13→33→83 and 17→43→27 | 446.138 s |
| 7 | 2 through 100 | none | 2.861 s |
| 9 | 2 through 100 | none | 15.329 s |
| 11 | 2 through 100 | none | 0.936 s |
| 13 | 2 through 100 | none | 0.003 s |
| 15 | 2 through 100 | none | 0.391 s |
| 17 | 2 through 100 | none | 0.212 s |
| 19 | 2 through 100 | none | 0.003 s |
| 21 | 2 through 100 | none | 0.071 s |
| 23 | 2 through 100 | none | 0.016 s |
| 25 | 2 through 100 | none | 0.029 s |
| 27 | 2 through 100 | none | 0.021 s |
| 29 | 2 through 100 | none | 0.003 s |
| 31 | 2 through 100 | none | 0.004 s |
| 33 | 2 through 100 | none | 0.118 s |
| 35 | 2 through 100 | none | 0.088 s |
| 37 | 2 through 100 | none | 0.003 s |
| 39 | 2 through 100 | none | 0.003 s |
| 41 | 2 through 100 | none | 0.003 s |
| 43 | 2 through 100 | none | 0.003 s |
| 45 | 2 through 100 | none | 0.003 s |
| 47 | 2 through 100 | none | 0.003 s |
| 49 | 2 through 100 | none | 0.003 s |
| 51 | 2 through 100 | none | 0.003 s |
| 53 | 2 through 100 | none | 0.003 s |
| 55 | 2 through 100 | none | 0.003 s |
| 57 | 2 through 100 | none | 0.003 s |
| 59 | 2 through 100 | none | 0.003 s |
| 61 | 2 through 100 | none | 0.003 s |
| 63 | 2 through 100 | none | 0.004 s |
| 65 | 2 through 100 | none | 0.003 s |
| 67 | 2 through 100 | none | 0.003 s |
| 69 | 2 through 100 | none | 0.003 s |
| 71 | 2 through 100 | none | 0.003 s |
| 73 | 2 through 100 | none | 0.102 s |
| 75 | 2 through 100 | none | 0.003 s |
| 77 | 2 through 100 | none | 0.003 s |
| 79 | 2 through 100 | none | 0.109 s |
| 81 | 2 through 100 | none | 0.003 s |
| 83 | 2 through 100 | none | 0.003 s |
| 85 | 2 through 100 | none | 0.003 s |
| 87 | 2 through 100 | none | 0.003 s |
| 89 | 2 through 100 | none | 0.003 s |
| 91 | 2 through 100 | none | 0.004 s |
| 93 | 2 through 100 | none | 0.003 s |
| 95 | 2 through 100 | none | 0.177 s |
| 97 | 2 through 100 | none | 0.003 s |
| 99 | 2 through 100 | none | 0.003 s |
| 101 | 2 through 100 | none | 0.118 s |
| 103 | 2 through 100 | none | 0.004 s |
| 105 | 2 through 100 | none | 0.569 s |
| 107 | 2 through 100 | none | 0.003 s |
| 109 | 2 through 100 | none | 0.003 s |
| 111 | 2 through 100 | none | 0.004 s |
| 113 | 2 through 100 | none | 0.004 s |
| 115 | 2 through 100 | none | 0.003 s |
| 117 | 2 through 100 | none | 0.048 s |
| 119 | 2 through 100 | none | 0.004 s |
| 121 | 2 through 100 | none | 0.004 s |
| 123 | 2 through 100 | none | 0.163 s |
| 125 | 2 through 100 | none | 0.004 s |
| 127 | 2 through 100 | none | 0.004 s |
| 129 | 2 through 100 | none | 0.173 s |
| 131 | 2 through 100 | none | 0.003 s |
| 133 | 2 through 100 | none | 0.003 s |
| 135 | 2 through 100 | none | 0.003 s |
| 137 | 2 through 100 | none | 0.044 s |
| 139 | 2 through 100 | none | 0.004 s |
| 141 | 2 through 100 | none | 0.003 s |
| 143 | 2 through 100 | none | 0.004 s |
| 145 | 2 through 100 | none | 0.005 s |
| 147 | 2 through 100 | none | 0.003 s |
| 149 | 2 through 100 | none | 0.116 s |
| 151 | 2 through 100 | none | 0.004 s |
| 153 | 2 through 100 | none | 0.004 s |
| 155 | 2 through 100 | none | 0.004 s |
| 157 | 2 through 100 | none | 0.003 s |
| 159 | 2 through 100 | none | 0.003 s |
| 161 | 2 through 100 | none | 0.003 s |
| 163 | 2 through 100 | none | 0.004 s |
| 165 | 2 through 100 | none | 0.003 s |
| 167 | 2 through 100 | none | 0.176 s |
| 169 | 2 through 100 | none | 0.004 s |
| 171 | 2 through 100 | none | 0.004 s |
| 173 | 2 through 100 | none | 0.004 s |
| 175 | 2 through 100 | none | 0.165 s |
| 177 | 2 through 100 | none | 0.004 s |
| 179 | 2 through 100 | none | 0.003 s |
| 181 | 2 through 100 | 27→611 and 35→99 | 0.004 s |
