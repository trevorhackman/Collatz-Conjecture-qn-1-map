---
name: definitions-section
description: >
  Write or revise a definitions and notation section in a mathematics paper.
  A definition says what a term means and nothing else: no proofs, no later
  lemmas or theorems, and no notation that has not been introduced yet.
  Use when the user asks for a definitions section, a notation section, or
  to define a term, or when a definition mentions lemmas, proofs, or symbols
  defined later. Also /definitions-section.
---

# Definitions section

Each entry defines one term a reader needs before the proofs. Write it so someone who stops at this section can use the term.

## An entry contains

- The term, the objects it applies to, and what the term is.
- Only symbols already defined in this section or earlier.
- A plain description when a symbol is a piece of an earlier one, such as a fractional part.

## An entry does not contain

- A proof, a derivation, or an existence claim.
- A lemma, theorem, proposition, corollary, or section from later in the paper.
- The numerical bounds. Those belong in the statements that prove them.
- Notation whose meaning appears later in the same section. \(O(\cdot)\) before the conventions paragraph is one case. Say the bound in words, or move the convention above the first use.

## Shape

One term per block. A blank line in the source is not a visible gap when the paragraph skip is zero, so put an explicit vertical space between terms.

State each quantity in its own sentence. Do not start in words, interrupt the sentence with a display, and continue with "where".

When the user corrects the meaning, use their words. For this paper, \(\delta\) is the fractional part of \(2^{h/\ell}\), and the window is a lower bound and an upper bound on \(\delta\):

> On a cycle of length \(\ell\) through 1 with \(h\) halvings in all, write \(2^{h/\ell} = q + \delta\), so \(\delta\) is the fractional part of \(2^{h/\ell}\). The window is a lower bound and an upper bound on \(\delta\).
