---
name: definitions-section
description: >
  Write or revise a definitions and notation section in a mathematics paper.
  A definition says what a term means and nothing else: no proofs, no later
  lemmas or theorems, no notation that has not been introduced yet, and no
  usage note about how this paper happens to use the term.
  Use when the user asks for a definitions section, a notation section, or
  to define a term, or when a definition mentions lemmas, proofs, or symbols
  defined later, or when a definition is a usage note, such as making the
  constant in \(O\) depend on \(\ell\), or when prose uses a word this
  section does not define, such as empty or emptiness, or when a new symbol
  has no type, such as an explicit constant \(c\), or when the same symbol is
  used for a second formula. Set the type of every
  new symbol. Constant takes a type: a constant real number, a constant
  integer, or a constant rational. Also
  /definitions-section.
---

# Definitions section

Each entry defines one term a reader needs before the proofs. Write it so someone who stops at this section can use the term. Every later use of that term is this definition. A theorem, a remark, or the other paper does not get a second version.

A definition is one definition. The term means that entry. The symbol in the entry means that entry. Later text uses that meaning, or it does not use the symbol for another formula. Do not add a factor, a denominator, a quantifier, or a hypothesis and keep the symbol.

When a theorem and a definition give the same symbol two formulas, one of them is wrong. Change the definition, or change the theorem so that it uses the definition. Do not change what the theorem concludes. The proved bound, the cases, and the objects it rules out stay as they are.

## An entry contains

- The term, the objects it applies to, and what the term is.
- Only symbols already defined in this section or earlier.
- A plain description when a symbol is a piece of an earlier one, such as a fractional part.

## An entry does not contain

- A usage note. Definitions should obviously not be usage notes. A definition states what the term or symbol means in mathematics. It does not record how this paper happens to use it. Not: "An implied constant in \(O\) depends only on \(\ell\)." That sentence is about one estimate. It is not what \(O\) means. Big \(O\), the Bachmann--Landau symbol, means \(|f(t)| \le C\,|g(t)|\) for a constant real number \(C > 0\) independent of the variable \(t\). The definition states that name. A fact about one estimate stays next to that estimate.
- An English word standing in for a fact. This is a maths paper not an english essay or email. Define the word in this section, or write the fact and do not use the word. "Emptiness" is not a term. "No such cycle exists" is the fact.
- A proof, a derivation, or an existence claim.
- A lemma, theorem, proposition, corollary, or section from later in the paper.
- The numerical bounds. Those belong in the statements that prove them.
- A standard symbol parked in a conventions paragraph. Define it by its mathematical meaning, in its own entry, before the first use.

## Shape

One term per block. Paragraph skip is already one line. A blank line between topic entries is the gap. Do not add `\vspace`.

State each quantity in its own sentence. Do not start in words, interrupt the sentence with a display, and continue with "where".

## Type every new symbol

A new symbol has a type in the sentence that introduces it. An inequality is not a type. Constant alone is not a type. Say a constant real number, a constant integer, or a constant rational. Explicit is not a type.

Not: "there are explicit constants \(c > 0\) and \(j_0\)."

A constant has a type. Say a constant real number or a constant integer. There exists one, and there exists the other. Do not write "there exist" for the pair.

When the user corrects the meaning, use their words.
