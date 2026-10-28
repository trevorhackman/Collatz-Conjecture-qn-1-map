---
name: paper-style
description: >
  Style a mathematics paper in LaTeX so a new paragraph is a blank line on
  the page, not a first-line indent. In the prose, replace an undefined word
  with the mathematical fact. Use when writing or editing a .tex paper,
  fixing paragraph spacing, theorem layout, topic headings, or PDF formatting,
  or when a sentence uses a word the paper does not define, such as empty
  or emptiness, or renames a defined term, such as calling a cycle of
  length $\ell$ an odd step, or when a new symbol has no type, such as
  an explicit constant $c$, or when a definition is a usage note, such as
  making the constant in $O$ depend on $\ell$,   or when an orbit that
  grows without bound is called unbounded, or when a sentence uses
  one as a pronoun, such as one has or one sees,   or when a new symbol
  is introduced by saying A is B, or when the same symbol is used for a
  second formula, or when the user runs
  /paper-style.
---

# Paper style

A blank line in the source is a blank line on the page. Do not use first-line indent as the paragraph mark.

## Words

This is a maths paper not an english essay or email. Use a word for a mathematical fact only when this paper defines that word. Otherwise write the fact.

Not: "The product is empty." When \(\ell = 1\), no index \(i\) satisfies \(1 \le i \le \ell - 1\). Write: "That product has no factors and equals \(1\)."

Not: "Theorem 1 is the resulting emptiness." Write: "Theorem 1 records that no such cycle exists."

Do not rename a defined term. Use the term this paper defines. Every use of the term is that definition. A later sentence does not get a second version. The symbol in the definition is that formula. Do not keep the symbol and change the formula. Matching a theorem to a definition does not change what the theorem concludes.

This paper says cycle and length. A cycle of length \(\ell\) with starting value \(1\) is not "\(\ell\) odd steps", not "\(\ell\) steps", and not a "one-step cycle". Length \(1\) and length \(2\) are not "one and two odd steps". The paper titles may say "through \(1\)" and "beyond \(1\)". The body says starting value.

An orbit that grows without bound diverges. The word is diverge.

Do not use one as a pronoun. In a formula, one is the number \(1\).

Where a sentence would say "one has," write "we have." Dropping the verb leaves "Since \(A\), \(B\)," and the comma is the only separation between the two formulas.

Not: "From \(q^3 < 2^h < (q+1)^3\) one has \(q = \lfloor 2^{h/3} \rfloor\)." Write: "Since \(q^3 < 2^h < (q+1)^3\), we have \(q = \lfloor 2^{h/3} \rfloor\)."

A definition may say what a term is. "A Mersenne number is \(2^e - 1\)." "The golden ratio is \(\varphi = (1 + \sqrt{5})/2\)." "The starting value is the smallest element \(x_0\)." "The total number of halvings is \(h = k_1 + \cdots + k_\ell\)." The new symbol is what the sentence introduces.

Do not attach a new name by claiming a fact about an object already in view. That sentence reads as a proof.

Not: "The element after \(1\) is \(x = x_1\)." Write: "Write \(x\) for \(x_1\)." "Let \(x = x_1\)" is also a definition. "Write" and "let" are examples, not the only openings.

Definitions should obviously not be usage notes. A definition states what the term or symbol means and then stops. It does not record how this paper happens to use it, and it does not state a bound, a threshold, or a sentence of the form "if this holds, then ...". See the definitions-section skill.

## Types

Every new symbol has a type in the sentence that introduces it. An inequality is not a type. Constant alone is not a type. Say a constant real number, a constant integer, or a constant rational. Explicit is not a type.

Not: "explicit constants \(c > 0\) and \(j_0\)." In \(\lvert\theta - p/(s \cdot 2^j)\rvert > c \cdot 2^{-\mu j}\), \(c\) is an explicit constant real number with \(c > 0\), and \(j_0\) is an explicit constant integer with \(j_0 \ge 0\). The constant in \(O\) is a constant real number independent of the variable. "Depends only on \(\ell\)" is not part of that definition.

## Preamble

```latex
\usepackage[skip=\baselineskip]{parskip}
```

That sets `\parskip` to one line and `\parindent` to zero. The package keeps section headings from picking up a second skip.

## Paragraphs

- A blank line in the `.tex` starts a new paragraph. It must show as an empty line in the PDF.
- Do not indent the first line.
- After a display, omit the blank line if the same paragraph continues; keep it if the next sentence is a new paragraph.

## Named statements

```latex
\newenvironment{named}[1]{%
  \par\addvspace{\medskipamount}%
  \pagebreak[3]%
  \noindent{\bfseries #1}\space\ignorespaces
}{\par\addvspace{\medskipamount}}
```

`\addvspace` so two theorems in a row do not stack two skips. `\pagebreak[3]` so a statement prefers to start on the next page rather than split.

The order on the page is the statement, then the proof, then optional remarks. A remark does not go inside the statement. See the statement-proof skill.

For the contents list, see the table-of-contents skill.

## Topic headings

Run-in bold. Paragraph skip already supplies the gap, so do not add `\vspace`:

```latex
\newcommand{\topic}[1]{\par\noindent\textbf{#1}}
```

## Check the PDF

- Consecutive body paragraphs have a full blank line, no indent.
- A theorem is not split across a page break.
- `\ldots.` at the end of a sentence prints as four dots; that is the period, leave it.
- Topic entries and theorems are not double-spaced beyond one paragraph skip plus the named `\addvspace` when it is larger.
