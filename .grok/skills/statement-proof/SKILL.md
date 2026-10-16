---
name: statement-proof
description: >
  Write or revise a theorem, lemma, proposition, or corollary and its proof.
  The statement gives the conclusions. An immediate consequence may be
  another claim in the same statement. A real argument belongs in the proof.
  Use when writing or revising a theorem, lemma, proposition, corollary, or
  proof, or when a statement contains part of its own argument. Also
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

## Do not

- Put a real argument in the statement. "Hence \(q < 2x^2\), that is \(x > \sqrt{q/2}\)" uses \(q + 1 = 2^{k_1} x\) and is a proof step.
