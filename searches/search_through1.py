#!/usr/bin/env python3
"""
Search for cycles through 1 of the odd-to-odd map C(n) = (q*n + 1) / 2^v.

By Lemma 10, an l-cycle through 1 has q = floor(2^{h/l}), so each h not
divisible by l gives one candidate q. We follow 1 for l steps and see if
it returns.

This is the search behind the table in Appendix C:
    l = 3: no cycle for q < 2^66666
    l = 4: no cycle for q < 2^25000
    and so on.

Usage: python3 search_through1.py [L] [H]
Defaults: L = 3, H = 66100 (the Theorem 1 range).
"""
import sys
import time


def iroot(n, k):
    x = 1 << (n.bit_length() // k + 1)
    while True:
        # Newton step for x^k - n = 0
        p = x ** (k - 1)
        y = ((k - 1) * x + n // p) // k
        if y >= x:
            break
        x = y
    while x ** k > n:
        x -= 1
    while (x + 1) ** k <= n:
        x += 1
    return x


def v2(n):
    return (n & -n).bit_length() - 1


def returns(q, L):
    a = 1
    seen = []
    for _ in range(L):
        seen.append(a)
        t = q * a + 1
        if t & 1:
            return False
        a = t >> v2(t)
    return a == 1 and len(set(seen)) == L


def search(L, H):
    found = []
    for h in range(1, H + 1):
        if h % L == 0:
            continue
        q = iroot(1 << h, L)
        if q % 2 == 0:
            q -= 1
        if q < 3:
            continue
        if returns(q, L):
            found.append((q, h))
    return found


def main():
    L = int(sys.argv[1]) if len(sys.argv) > 1 else 3
    H = int(sys.argv[2]) if len(sys.argv) > 2 else 66100
    t0 = time.time()
    found = search(L, H)
    print(f"l = {L}, h <= {H} (q < 2^{H // L}): "
          f"{len(found)} cycles through 1 in {time.time() - t0:.1f}s")
    for q, h in found:
        print(q, h)


if __name__ == "__main__":
    main()
