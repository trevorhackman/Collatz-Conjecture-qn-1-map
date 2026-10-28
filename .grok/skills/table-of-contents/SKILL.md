---
name: table-of-contents
description: >
  Write or fix the table of contents of a mathematics paper. Sections, and
  the paper's own conjectures, theorems, and propositions. The entry for a
  repeated statement is the statement followed by its proof, not the
  announcement in the introduction. A theorem stated only in the
  introduction, with the proof in a later section, is the wrong entry.
  Use when adding or editing a table of
  contents, \tableofcontents, a contents entry for a theorem, or when the
  user says the contents list is wrong. Also /table-of-contents.
---

# Table of contents

The list is the sections, plus the paper's own conjectures, theorems, and propositions.

Not in the list: a lemma, a corollary, an outline, a theorem quoted from someone else (Ridout, Bauer and Bennett), or a proposition in an appendix. Appendix sections stay.

## Which occurrence

The introduction may announce a theorem. When the proof is in a later section, state the theorem again immediately before that proof, with the same heading. The contents entry is that later statement. The announcement in the introduction is not an entry.

Not: Theorem 1 stated only in the introduction, and the proof in a later section with no second statement. The contents link then opens the introduction. The statement immediately before the proof is the entry.

A statement that occurs once, and whose proof follows it, is that one place. A conjecture has no proof; its single statement is the entry.

One heading, one entry. When the same heading occurs twice, keep the last occurrence. Record the count in the aux file. Write the contents line only when this occurrence's number equals the total from the previous run. The first run after the change does not yet know the total.

## How the line looks

Contents entries are `\small`. Set `\parskip` to `0pt` for the contents list. This paper's paragraph skip is one blank line; left on, every contents line is separated by a blank line.

Section lines stay the class's section lines: bold, no dot leaders.

A statement is not a subsection. This is the broken line:

```latex
\addcontentsline{toc}{subsection}{Theorem 2}
```

With no `\numberline`, the name sits in the number column and dot leaders run across the rest of the line.

Give statements their own contents level. Indent the name under the section title. Put the page number at the right. No dot leaders. `\setcounter{tocdepth}{2}` so that level is printed.

`\phantomsection` immediately before the contents write, at that statement, so the link opens the statement.

## After editing

Compile until the log no longer says that labels may have changed. A longer contents list moves the later pages, and the page numbers in the list are from the previous run.
