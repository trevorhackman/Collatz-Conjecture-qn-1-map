---
name: definitions-section
description: >
  Write or revise a definitions and notation section in a mathematics paper.
  A definition says what a term means and nothing else: no proofs, no later
  lemmas or theorems, and no notation that has not been introduced yet.
  Use when the user asks for a definitions section, a notation section, or
  to define a term, or when a definition mentions lemmas, proofs, or symbols
  defined later, or when prose uses a word this section does not define,
  such as empty or emptiness, or when a new symbol has no type, such as
  an explicit constant \(c\). Set the type of every new symbol. Constant
  takes a type: a constant real number, a constant integer, or a constant
  rational. Also /definitions-section.
---

# Definitions section

Each entry defines one term a reader needs before the proofs. Write it so someone who stops at this section can use the term.

## An entry contains

- The term, the objects it applies to, and what the term is.
- Only symbols already defined in this section or earlier.
- A plain description when a symbol is a piece of an earlier one, such as a fractional part.

## An entry does not contain

- An English word standing in for a fact. This is a maths paper not an english essay or email. Define the word in this section, or write the fact and do not use the word. "Emptiness" is not a term. "No such cycle exists" is the fact.
- A proof, a derivation, or an existence claim.
- A lemma, theorem, proposition, corollary, or section from later in the paper.
- The numerical bounds. Those belong in the statements that prove them.
- Notation whose meaning appears later in the same section. \(O(\cdot)\) before the conventions paragraph is one case. Say the bound in words, or move the convention above the first use.

## Shape

One term per block. A blank line in the source is not a visible gap when the paragraph skip is zero, so put an explicit vertical space between terms.

State each quantity in its own sentence. Do not start in words, interrupt the sentence with a display, and continue with "where".

## Type every new symbol

A new symbol has a type in the sentence that introduces it. An inequality is not a type. Constant alone is not a type. Say a constant real number, a constant integer, or a constant rational. Explicit is not a type.

Not: "there are explicit constants \(c > 0\) and \(d_0\) such that \(\lvert\theta - p/d\rvert > c \cdot d^{-\mu}\)."

Write: "there exist an explicit constant real number \(c > 0\) and an explicit constant positive integer \(d_0\) such that, for every integer \(p\) and every positive integer \(d \in \mathcal{D}\) with \(d \ge d_0\), \(\lvert\theta - p/d\rvert > c \cdot d^{-\mu}\)." Here \(c\) is a constant real number, and \(d_0\) is a constant positive integer.

When the user corrects the meaning, use their words. For this paper, \(\delta\) is the fractional part of \(2^{h/\ell}\), and the window is a lower bound and an upper bound on \(\delta\):

> On a cycle of length \(\ell\) through 1 with \(h\) halvings in all, write \(2^{h/\ell} = q + \delta\), so \(\delta\) is the fractional part of \(2^{h/\ell}\). The window is a lower bound and an upper bound on \(\delta\).
