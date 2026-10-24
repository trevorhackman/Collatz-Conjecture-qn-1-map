---
name: statement-proof
description: >
  Write or revise a theorem, lemma, proposition, or corollary and its proof.
  The order is the statement, then the proof, then optional remarks.
  The statement gives the conclusions. An immediate consequence may be
  another claim in the same statement. A real argument belongs in the proof.
  A remark that points at a later section, a special case, or how the result
  is used comes after the proof, not in the statement.
  Use when writing or revising a theorem, lemma, proposition, corollary, or
  proof, or when a statement contains part of its own argument or a remark.
  Name each formula in the sentence that uses it. Do not use a word this paper
  does not define, such as empty or emptiness. Do not use a term the
  definitions section has not already defined, such as reduced length.
  Do not rename a defined
  term. Define a symbol before later lines use it. Do not mix setup and
  results in one comma-separated list. Set the type of every new variable.
  Constant takes a type: a constant real number, a constant integer, or a
  constant rational. Constant alone is not a type. This applies in a
  definition as well as in a statement. Also /statement-proof.
---

# Statement, then proof, then remarks

The order is the statement, then the proof, then optional remarks.

The statement says what is proved. The proof is the argument. A remark says how the result is used, which section computes a special case, or what the result becomes for a particular length. A remark is not a claim of the statement and not a step of the proof.

When the proof is in a later section, the statement may be followed by one sentence saying where the proof is. The remarks still come after the proof.

A cited theorem with no proof in this paper may be followed by its remarks at once.

## Statement

- The conclusions, as claims.
- An immediate consequence of one conclusion may be another claim in the same statement. Lemma 3 is the pattern: \(q^3 < 2^h < (q+1)^3\), and in particular \(3 \nmid h\) and \(q = \lfloor 2^{h/3}\rfloor\). "In particular" is the usual wording. "Hence" for that same one-line fact is normal.
- Every conclusion a later line cites.
- Setup and results are not items of one comma-separated list. A hypothesis or a range is its own sentence. The next sentence is the claims. Two results may share a sentence. Setup may not.
- Do not restate notation already defined earlier in the paper. The setup sentence names the hypothesis only.
- A term in the statement has already been defined in the definitions section. A name coined for this argument is not yet a term. Do not write it in the statement, and do not define it in the sentence before the statement in order to use it there. Write the formula.

Not: "For a cycle of length \(\ell\) through \(1\) with \(\ell \ge 2\), \(k_\ell \ge k_1 + 3\), and \(x_{\ell-1} \equiv 1 \pmod{2^{k_1}}\)." The bound \(\ell \ge 2\) is setup. The other two items are results.

Write: "Let \(\ell \ge 2\). On a cycle of length \(\ell\) through \(1\), \(k_\ell \ge k_1 + 3\) and \(x_{\ell-1} \equiv 1 \pmod{2^{k_1}}\)."

Not: "Only finitely many cycles have reduced length at most \(V\)." The definitions section does not define reduced length. A topic heading later in an appendix does not define it either.

Write: "Fix an integer \(V \ge 2\). Only finitely many cycles satisfy \(\ell/\gcd(h,\ell) \le V\)."

## Proof

- Every step that is not immediate: an intermediate inequality, a rearrangement, a multiplication, a hypothesis, or an earlier result.
- The proof reaches the last claim, including an immediate one. One sentence is enough for that claim.

## Name the object

This is a maths paper not an english essay or email. Use a word for a mathematical fact only when this paper defines that word. Otherwise write the fact.

Not: "Theorem 1 is the resulting emptiness." Write: "Theorem 1 records that no such cycle exists."

Not: "The product is empty." When \(\ell = 1\), no index \(i\) satisfies \(1 \le i \le \ell - 1\). Write: "That product has no factors and equals \(1\)."

A sentence that refers to a formula must contain that formula, or a symbol defined in that sentence. Do not write "the difference", "the bracket", "the expression", "the term", "the first", or "this" to point at an earlier formula.

Do not rename a defined term. Synonyms are bad. This is a maths paper. Use the term as defined, or its defined symbol. "2-adic valuation" is not "valuation", and it is not "2-adic structure".

Not: "with the bracket odd." Write: "\(x^2 - 2^{t-k_1}s\) is odd."

Not: "Thus the difference has valuation \(k_1\)." Write: "Thus the 2-adic valuation of \(2^{k_2} y\) is \(k_1\)."

## Define a symbol first

A symbol that later lines use is defined before it appears. "\(y = x^2 - w\) with \(w = (x-1)/2^{k_1}\)" uses \(w\) and only then says what it is. Write "Let \(w = (x-1)/2^{k_1}\). Then \(y = x^2 - w\)."

"With \(w = \ldots\)" is for a label used in that one formula and nowhere later.

A new variable has a type in the sentence that introduces it: an integer, a rational, a real number, a positive integer, a constant real number, a constant integer, or a constant rational. An inequality is not a type. Constant alone is not a type. Explicit is not a type. \(\theta > 0\) does not say whether \(\theta\) is an integer, a rational, or a real number. This applies in a definition as well as in a statement.

A real number may vary with the cycle or with \(q\). A constant real number is chosen once and does not. Those are different types. If the same value is used for every cycle, the type is a constant real number.

Not: "let \(\theta > 0\) and \(c > 0\) be real numbers."

Write: "Suppose there are constant real numbers \(\theta > 0\) and \(c > 0\) such that, on every cycle of length \(\ell\) through \(1\), every element other than \(1\) is at least \(c \cdot q^\theta\)."

Not: "there are explicit constants \(c > 0\) and \(d_0\) such that \(\lvert\theta - p/d\rvert > c \cdot d^{-\mu}\)."

Write: "there exist an explicit constant real number \(c > 0\) and an explicit constant positive integer \(d_0\) such that, for every integer \(p\) and every positive integer \(d \in \mathcal{D}\) with \(d \ge d_0\), \(\lvert\theta - p/d\rvert > c \cdot d^{-\mu}\)." Here \(c\) is a constant real number, and \(d_0\) is a constant positive integer. Explicit means the numbers can be computed. Constant means neither depends on \(p\) or on \(d\).

## Remarks

After the proof. Not inside the statement, and not between the statement and the proof.

Not: ending a theorem with "The integer \(Q_2(x)\) of Section 7 is this maximum at \(\ell = 2\)." That sentence identifies a later special case. It follows the proof.

## Do not

- Use a word this paper does not define. "Emptiness" is not a term. "No such cycle exists" is the fact. "The product is empty" is not a term. "That product has no factors and equals \(1\)" is the fact.
- Use a term the definitions section has not already defined. "Reduced length" is not a term. The fact is \(\ell/\gcd(h,\ell)\). A topic heading above the statement does not define a term for the statement.
- Put a real argument in the statement. "Hence \(q < 2x^2\), that is \(x > \sqrt{q/2}\)" uses \(q + 1 = 2^{k_1} x\) and is a proof step.
- Put a remark in the statement, or between the statement and the proof.
- Mix setup and results in one comma-separated list.
- Introduce a new variable without its type. An inequality such as \(\theta > 0\) is not a type. Constant alone is not a type: say a constant real number, a constant integer, or a constant rational. Explicit is not a type. A real number that may vary is not a constant real number.

## Numbering

Two counters, in order of appearance.

Conjectures and theorems share one counter. Lemmas, propositions, and corollaries share the other.

The first counter does not restart in an appendix, and a conjecture or theorem does not take an appendix letter.

Each appendix section has its own letter, in order. The first section is A, the next is B, and so on. Inside that section the second counter starts at 1 and carries that letter. Lemma B.1, Lemma B.2, Corollary B.3, and Proposition B.4 are one appendix. The next appendix starts at Lemma C.1.

The letter is the appendix's letter. A proposition in Appendix B is Proposition B.1.

Not: Proposition A.4 for the first proposition of Appendix B, continuing the count from Appendix A.

A theorem of this paper gets the next theorem number. A quoted theorem keeps the other author's name and year and is not numbered here.
