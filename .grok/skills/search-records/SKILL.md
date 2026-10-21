---
name: search-records
description: >
  Keep README tables, Paper II computations, and cycle-search results tied to
  a Kotlin search that can be rerun. Use when editing the README, a
  computations section, a bound table, Q_2, a search time, or
  cycle-search Kotlin, or when a result would otherwise come from a one-off
  script. Also /search-records.
---

# Search records

A number in the README or in a computations section is a record of a command in `cycle-search`. The command is Kotlin, invoked as `./search` from that directory.

## What may be printed

- Run the search. The time in the README is the time that run prints for the search itself, not Gradle startup and not a guess.
- One quantity gets one table. Do not leave a short table with a time on every row next to the full table. Say how long the whole table took.
- A table that is too long for the paper goes in the README. The paper keeps the statement. It does not duplicate the long table.
- Do not fill a README table from a one-off Python script, an unsaved session, or any computation the repo cannot rerun.

## Q_2

`./search --bounds q2` prints Q_2(x) for every odd x from 1 through 2043. Below the point where the allowed error is smaller than 1/(2x^2), every j is tested directly. From there through j < 200000 only continued-fraction convergents can fail.

`./search --bounds q2 --x N` checks that one odd x through j < 53620.
