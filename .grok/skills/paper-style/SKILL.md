---
name: paper-style
description: >
  Style a mathematics paper in LaTeX so a new paragraph is a blank line on
  the page, not a first-line indent. Use when writing or editing a .tex paper,
  fixing paragraph spacing, theorem layout, topic headings, or PDF formatting,
  or when the user runs /paper-style.
---

# Paper style

A blank line in the source is a blank line on the page. Do not use first-line indent as the paragraph mark.

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
