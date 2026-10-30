---
name: search-records
description: >
  Keep README tables, the computations in the notes, and cycle-search results tied to
  a Kotlin search that can be rerun. Use when editing the README, a
  computations section, a bound table, a search time, or cycle-search Kotlin,
  or when a README paragraph explains how a search works, or when a sentence
  says the search looks at every starting value, or when the same scan fact
  is written twice. Also /search-records.
---

# Search records

A number in the README or in a computations section is a record of a command in `cycle-search`. The command is Kotlin, invoked as `./search` from that directory.

## What may be printed

- Run the search. The time in the README is the time that run prints for the search itself, not Gradle startup and not a guess.
- A search time appears in the README only. The paper states what the search found. It does not say how long the search took.
- One quantity gets one table. Do not leave a short table with a time on every row next to the full table. In the README, say how long the whole table took.
- A table that is too long for the paper goes in the README. The paper keeps the statement. It does not duplicate the long table.
- Do not fill a README table from a one-off Python script, an unsaved session, or any computation the repo cannot rerun.
- A number in `notes/` that did not come from `./search` stays in the note. It is not a README row.
- Do not put a search bound into a Kotlin constant or a default flag. The q, the length, the number of odds followed, and the number of places are arguments of the command. If the command omits one, the program fails. The README records the command that was run, numbers included.

## How to explain a search

Every computational search section opens with one sentence that says what the search is. A cycle search begins: This is a search that finds any or proves there's no cycles, and then names the quantifiers for that search. Constant \(q\) uses: for a fixed \(q\), for any starting value, and length up to the search bounds. Any-cycle uses: for any starting value, for one fixed length, and for \(h\) up to the bound. Through 1 uses: through 1, for one fixed length, and for \(h\) up to the bound. A digit scan begins: This is a scan of the binary places, and names the root and the run it reports. Do not paraphrase a section's opening into a different shape. Do not begin "For one fixed odd \(q\)". The mechanism comes after that sentence. A quantifier belongs in that sentence. It is not three words stuck on the end of a later sentence.

The search does not look at starting values. Listing them is impossible. It searches halving counts. A cycle of length \(\ell\) with that \(q\) has \(q^\ell < 2^h < (q+1)^\ell\), so the only \(h\) tried are the integers in that range. The counts determine the starting value: it is the numerator \(N\) divided by the gap \(2^h - q^\ell\). When that range of \(h\) contains no integer, that length has no cycle.

Not: "This search looks at every odd starting value." Not: "the search finds no cycle for any odd starting value" tacked onto the results sentence after the opening failed to say it.

A digit scan is one paragraph, in the shape of the \(\sqrt{2}\) entry: the range of \(m\), the longest run, the place, the time. Name each root once. Do not write the alternating runs for both roots and then write the zero runs for both roots again. The range of \(m\) appears once.
