---
name: statement-proof
description: >
  Write or revise a theorem, lemma, proposition, or corollary and its proof.
  The statement gives the conclusions. An immediate consequence may be
  another claim in the same statement. A real argument belongs in the proof.
  Use when writing or revising a theorem, lemma, proposition, corollary, or
  proof, or when a statement contains part of its own argument. Name each
  formula in the sentence that uses it. Do not rename a defined term. Also
  /statement-proof.
---

# Statement then proof

The statement says what is proved. The proof is the argument.

## Statement

- The conclusions, as claims.
- An immediate consequence of one conclusion may be another claim in the same statement. Lemma 3 is the pattern: \(q^3 < 2^h < (q+1)^3\), and in particular \(3 \nmid h\) and \(q = \lfloor 2^{h/3}\rfloor\). "In particular" is the usual wording. "Hence" for that same one-line fact is normal.
- Every conclusion a later line cites.

## Proof

- Every step that is not immediate: an intermediate inequality, a rearrangement, a multiplication, a hypothesis, or an earlier result.
- The proof reaches the last claim, including an immediate one. One sentence is enough for that claim.

## Name the object

Using vague terms is not okay. A sentence that refers to a formula must contain that formula, or a symbol defined in that sentence. Do not write "the difference", "the bracket", "the expression", "the term", "the first", or "this" to point at an earlier formula.

Do not rename a defined term. Synonyms are bad. This is a maths paper. Use the term as defined, or its defined symbol. "2-adic valuation" is not "valuation", and it is not "2-adic structure".

Not: "with the bracket odd." Write: "\(x^2 - 2^{t-k_1}s\) is odd."

Not: "Thus the difference has valuation \(k_1\)." Write: "Thus the 2-adic valuation of \(2^{k_2} y\) is \(k_1\)."

## Do not

- Put a real argument in the statement. "Hence \(q < 2x^2\), that is \(x > \sqrt{q/2}\)" uses \(q + 1 = 2^{k_1} x\) and is a proof step.
