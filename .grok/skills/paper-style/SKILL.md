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
  an explicit constant $c$, or when the user runs /paper-style.
---

# Paper style

A blank line in the source is a blank line on the page. Do not use first-line indent as the paragraph mark.

## Words

This is a maths paper not an english essay or email. Use a word for a mathematical fact only when this paper defines that word. Otherwise write the fact.

Not: "The product is empty." When \(\ell = 1\), no index \(i\) satisfies \(1 \le i \le \ell - 1\). Write: "That product has no factors and equals \(1\)."

Not: "Theorem 1 is the resulting emptiness." Write: "Theorem 1 records that no such cycle exists."

Do not rename a defined term. Use the term this paper defines.

This paper says cycle and length. A cycle of length \(\ell\) through \(1\) is not "\(\ell\) odd steps", not "\(\ell\) steps", and not a "one-step cycle". Length \(1\) and length \(2\) are not "one and two odd steps".

## Types

Every new symbol has a type in the sentence that introduces it. An inequality is not a type. Constant alone is not a type. Say a constant real number, a constant integer, or a constant rational. Explicit is not a type.

Not: "explicit constants \(c > 0\) and \(d_0\)." In \(\lvert\theta - p/d\rvert > c \cdot d^{-\mu}\), \(c\) is an explicit constant real number with \(c > 0\), and \(d_0\) is an explicit constant positive integer.

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
