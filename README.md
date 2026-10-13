# Collatz-Conjecture-qn+1-map

Cycles through 1 for the qn+1 map: no cycles with three odd steps, and constraints on longer cycles.

## Contents

- `searches/` — Python scripts for searching cycles of the odd-to-odd map C(n) = (qn+1)/2^v
  - `search_length2.py` — modular search for length-2 cycles (q < 2^10000 by default)
  - `search_length3.py` — window-pruned search for any 3-cycle with a bounded smallest element
  - `search_through1.py` — cycles through 1 for a given length
  - `search_lengths.py` — general search for lengths 2–10
- `cycles-through-1-qn1.pdf` — full paper

## Paper

Author: Trevor Hackman

The paper proves that 1 never lies on a cycle with exactly three odd numbers for the qn+1 map (odd q ≥ 3), and gives finiteness and effective criteria for longer cycles via restricted Diophantine approximation and the golden-ratio threshold.

## Known cycles

- Through 1: 1→1 for Mersenne q, and 1→3→1 for q=5
- Not through 1: 13→33→83 and 17→43→27 for q=5; 27→611 and 35→99 for q=181
