#!/usr/bin/env python3
"""
Search for l-cycles of C(n) = (q*n + 1) / 2^v, for l = 2..Lmax,
both through 1 and with a bounded smallest element.

For each h not divisible by l, q = floor(2^{h/l}) is the only candidate.
We follow every odd start up to X for l steps.

Usage: python3 search_lengths.py [H] [X] [Lmax]
Defaults: H = 1000, X = 10000, Lmax = 10.
"""
import sys
import time


def iroot(n, k):
    x = 1 << (n.bit_length() // k + 1)
    while True:
        p = x ** (k - 1)
        if p == 0:
            break
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


def search(L, H, X):
    found = set()
    for h in range(1, H + 1):
        if h % L == 0:
            continue
        q = iroot(1 << h, L)
        if q % 2 == 0:
            q -= 1
        if q < 3:
            continue
        x = 1
        while x <= X:
            a = x
            seen = []
            ok = True
            for _ in range(L):
                if a in seen:
                    ok = False
                    break
                seen.append(a)
                t = q * a + 1
                if t & 1:
                    ok = False
                    break
                a = t >> v2(t)
            if ok and a == x:
                found.add((q, tuple(sorted(seen))))
            x += 2
    return found


def main():
    H = int(sys.argv[1]) if len(sys.argv) > 1 else 1000
    X = int(sys.argv[2]) if len(sys.argv) > 2 else 10000
    Lmax = int(sys.argv[3]) if len(sys.argv) > 3 else 10
    for L in range(2, Lmax + 1):
        t0 = time.time()
        found = search(L, H, X)
        print(f"l={L} h<={H} X={X}: {len(found)} cycles in {time.time() - t0:.1f}s")
        for q, cyc in sorted(found):
            print("   ", q, cyc)


if __name__ == "__main__":
    main()
