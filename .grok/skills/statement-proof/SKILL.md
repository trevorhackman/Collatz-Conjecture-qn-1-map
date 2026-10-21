---
name: statement-proof
description: >
  Write or revise a theorem, lemma, proposition, or corollary and its proof.
  The statement gives the conclusions. An immediate consequence may be
  another claim in the same statement. A real argument belongs in the proof.
  Use when writing or revising a theorem, lemma, proposition, corollary, or
  proof, or when a statement contains part of its own argument. Name each
  formula in the sentence that uses it. Do not rename a defined term. Define
  a symbol before later lines use it. Do not mix setup and results in one
  comma-separated list. Set the type of every new variable. Also /statement-proof.
---

# Statement then proof

The statement says what is proved. The proof is the argument.

## Statement

- The conclusions, as claims.
- An immediate consequence of one conclusion may be another claim in the same statement. Lemma 3 is the pattern: \(q^3 < 2^h < (q+1)^3\), and in particular \(3 \nmid h\) and \(q = \lfloor 2^{h/3}\rfloor\). "In particular" is the usual wording. "Hence" for that same one-line fact is normal.
- Every conclusion a later line cites.
- Setup and results are not items of one comma-separated list. A hypothesis or a range is its own sentence. The next sentence is the claims. Two results may share a sentence. Setup may not.
- Do not restate notation already defined earlier in the paper. The setup sentence names the hypothesis only.

Not: "For a cycle of length \(\ell\) through \(1\) with \(\ell \ge 2\), \(k_\ell \ge k_1 + 3\), and \(x_{\ell-1} \equiv 1 \pmod{2^{k_1}}\)." The bound \(\ell \ge 2\) is setup. The other two items are results.

Write: "Let \(\ell \ge 2\). On a cycle of length \(\ell\) through \(1\), \(k_\ell \ge k_1 + 3\) and \(x_{\ell-1} \equiv 1 \pmod{2^{k_1}}\)."

## Proof

- Every step that is not immediate: an intermediate inequality, a rearrangement, a multiplication, a hypothesis, or an earlier result.
- The proof reaches the last claim, including an immediate one. One sentence is enough for that claim.

## Name the object

Using vague terms is not okay. A sentence that refers to a formula must contain that formula, or a symbol defined in that sentence. Do not write "the difference", "the bracket", "the expression", "the term", "the first", or "this" to point at an earlier formula.

Do not rename a defined term. Synonyms are bad. This is a maths paper. Use the term as defined, or its defined symbol. "2-adic valuation" is not "valuation", and it is not "2-adic structure".

Not: "with the bracket odd." Write: "\(x^2 - 2^{t-k_1}s\) is odd."

Not: "Thus the difference has valuation \(k_1\)." Write: "Thus the 2-adic valuation of \(2^{k_2} y\) is \(k_1\)."

## Define a symbol first

A symbol that later lines use is defined before it appears. "\(y = x^2 - w\) with \(w = (x-1)/2^{k_1}\)" uses \(w\) and only then says what it is. Write "Let \(w = (x-1)/2^{k_1}\). Then \(y = x^2 - w\)."

"With \(w = \ldots\)" is for a label used in that one formula and nowhere later.

A new variable has a type in the sentence that introduces it: an integer, a rational, a real number, a positive integer, a fixed real number. An inequality is not a type. \(\theta > 0\) does not say whether \(\theta\) is an integer, a rational, or a real number.

A real number may vary with the cycle or with \(q\). A fixed real number is chosen once and does not. Those are different types. If the same value is used for every cycle, the type is a fixed real number.

Not: "let \(\theta > 0\) and \(c > 0\) be real numbers."

Write: "Suppose there are fixed real numbers \(\theta > 0\) and \(c > 0\) such that, on every cycle of length \(\ell\) through \(1\), every element other than \(1\) is at least \(c \cdot q^\theta\)."

## Do not

- Put a real argument in the statement. "Hence \(q < 2x^2\), that is \(x > \sqrt{q/2}\)" uses \(q + 1 = 2^{k_1} x\) and is a proof step.
- Mix setup and results in one comma-separated list.
- Introduce a new variable without its type. An inequality such as \(\theta > 0\) is not a type. A real number that may vary is not a fixed real number.
