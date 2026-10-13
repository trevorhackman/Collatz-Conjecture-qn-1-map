# Cycles through 1 for the qn+1 map: no cycles with three odd steps, and constraints on longer cycles

Trevor Hackman

**Abstract.** For odd q ≥ 3, consider the map n ↦ (qn + 1)/2^v on odd integers, where 2^v is the largest power of 2 dividing qn + 1. We prove that 1 never lies on a cycle with exactly three distinct odd numbers, settling the three-step case of a conjecture of Santos that 1 is periodic only for q = 5 and for Mersenne numbers q = 2^e − 1. The proof shows that q and every element of such a cycle other than 1, divided by suitable powers of 2, are extremely close approximations to 2^(1/3) or 4^(1/3), and plays two of these near misses against an effective irrationality measure of Bauer and Bennett. Their exponent, about 1.6149, lies just below the golden ratio, which is exactly the threshold the argument requires. The method also shows that for every fixed number ℓ ≥ 2 of odd steps only finitely many q can have 1 on a cycle with exactly ℓ distinct odd numbers (ineffectively), reduces an effective answer for each ℓ to restricted approximation exponents below the golden ratio, and settles four odd steps when the total number of halvings is ≡ 2 (mod 4).

## 1. Introduction

Fix an odd integer q ≥ 3. On the odd positive integers define

```latex
C(n) = \frac{qn + 1}{2^{v}}, \qquad \text{where } 2^{v} \text{ is the largest power of 2 dividing } qn + 1.
```

The orbit of n is the sequence n, C(n), C(C(n)), …. When q = 3, C is the odd-to-odd form of the Collatz map, and the Collatz conjecture, posed by Lothar Collatz in 1937 and still open, asserts that every orbit eventually reaches 1. For q = 3 the number 1 is fixed, since 3 · 1 + 1 = 4. For larger q, heuristic arguments suggest that most orbits grow without bound and that cycles are rare, although no orbit has been proved to diverge.

We say that 1 is periodic if its orbit returns to 1, and ask for which q this happens. We classify the answers by the length ℓ of the cycle through 1, that is, the number of distinct odd numbers on it.

For ℓ = 1 the cycle is 1 → 1, which happens exactly when q + 1 is a power of 2, that is q = 2^e − 1 ([Santos](https://arxiv.org/abs/2005.00346), Proposition 15). For ℓ = 2, the only q is 5, with 1 → 3 → 1 ([Santos](https://arxiv.org/abs/2005.00346), Proposition 16; a short proof also follows from Beukers's theorem on x² + D = 2^n, stated as Theorem 1.8 in [Bauer and Bennett (2002)](https://personal.math.ubc.ca/~bennett/BaB.pdf)). Santos conjectured that these are the only cases: 1 is periodic only for q = 5 and q = 2^e − 1.

**Theorem 1.** No odd q ≥ 3 has 1 on a cycle of C of length 3.

So the conjecture holds for every cycle through 1 of length at most 3. For longer cycles we prove an exact criterion for an effective answer, and finiteness.

**Theorem 2 (golden-ratio criterion, informal).** Fix ℓ ≥ 2. If the numbers 2^(r/ℓ), 0 < r < ℓ, have an effective approximation exponent below the golden ratio by fractions with denominator 2^j or ℓ · 2^j, then any q with 1 on a cycle of C of length ℓ is below a computable bound, so Santos's conjecture for cycles of length ℓ reduces to a finite computation. The precise statement is Theorem 2′ in Section 6.

**Theorem 3.** For each ℓ ≥ 2, only finitely many odd q have 1 on a cycle of C of length ℓ.

Theorem 3 is ineffective: it rests on Ridout's theorem and gives no bound on q. (A finiteness result is called effective if its proof gives an explicit bound on any exception, so that a finite computation can find them all, and ineffective if it gives no such bound.) Theorem 1 is effective and is completed by computation.

**Status.** These results have not been refereed. Appendix C records the independent checks that were made, including a correction to a constant in an intermediate step of the Bauer–Bennett argument that our proof uses.

(The full paper continues; this is a truncated upload for the repo. See the PDF for the complete typeset version.)
