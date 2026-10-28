---
name: one-paper
description: >
  Work in one paper only. Paper I is cycles-through-1-qn1.tex. Paper II is
  fixed-start-qn1.tex. A file in notes/ is not a paper. When the task names
  one paper, one of those files, a note, or a theorem in one of them, do not
  open the other paper. Paper I comes first. Use when editing either paper, a file in notes/,
  when the user says Paper I or Paper II, or when a proof starts borrowing
  symbols, bounds, or case splits from the other paper, or when the same
  symbol is defined differently in the two papers. Also /one-paper.
---

# One paper

Paper I is `cycles-through-1-qn1.tex`. Paper II is `fixed-start-qn1.tex`. Paper I comes first.

A task on one paper uses that file only. Do not open the other file to copy a bound or a case split. A proof produces its bound from the hypotheses of the statement it is proving.

Companion papers share a definition. The same symbol has one definition, and that definition is the same text in both papers. A second formula in the other paper is a second definition. When a definition is written or changed, open the other paper and make the two entries the same. Do not change what either theorem concludes in order to do that.

A file in `notes/` is not a paper. The two papers stay the two `.tex` files at the repository root. A task on a note uses that note. It leaves both papers, the README contents list, and `out/` as they are. The note may cite a paper. A paper does not cite a note. A PDF of a note, when compiled, goes under `notes/`.
