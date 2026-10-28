---
name: references
description: >
  Cite an external result by name and year. Use when writing or editing a
  citation, a conjecture or theorem attributed to someone else, a named
  heading for an outside result, or a bibliography entry in a mathematics
  paper. Also /references.
---

# References

An external result is cited by the author's name and the year. The year is in parentheses after the name.

## Form

- Bauer and Bennett (2018)
- Ridout (1958)
- Santos (2020)
- Crandall (1978)
- Franco and Pomerance (1995)
- Two papers by the same authors: Bauer and Bennett (2002, 2018). Steiner (1977, 1981).

A result number belongs in the same citation.

- Proposition 15 of Santos (2020)
- Theorem 3 of Bauer and Bennett (2018)
- Bauer and Bennett's Corollary 1.6 (2002)

A displayed statement of someone else's result uses that form as its heading.

```latex
\begin{named}{Ridout's theorem (1958).}
\begin{named}{Conjecture 1 (Santos (2020)).}
```

A conjecture this paper states is numbered, and the attribution keeps the name and the year. Santos's conjecture is Conjecture 1.

The year is the year in the bibliography. A preprint still has one. Santos's preprint is arXiv:2005.00346, so the year is 2020, and the bibliography entry carries 2020.

## Not this

- Conjecture (Santos). The parentheses are for the year, not the name. A number does not drop the year: Conjecture 1 (Santos (2020)).
- (Santos, Proposition 15). The name has no year.
- Bauer and Bennett 2002. The year is in parentheses.
- A `\cite` in place of the year. The sentence still says the name and the year. `\cite` may follow them. It does not replace them.

## This paper

A statement proved here is Theorem 1, Lemma 3, Corollary 4. No author and no year.

The other paper in the pair is the companion paper, with `\cite{hackman-i}` or `\cite{hackman-ii}`. It has no publication year.
