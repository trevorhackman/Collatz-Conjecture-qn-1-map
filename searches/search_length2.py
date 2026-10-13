#!/usr/bin/env python3
"""
Search for 2-cycles of the odd-to-odd map C(n) = (q*n + 1) / 2^v,
for odd q >= 3.

A 2-cycle x -> y -> x forces h = k1 + k2 odd, q = floor(2^{h/2}), and
    x = (q + 2^{k1}) / D,   y = (q + 2^{k2}) / D,
where D = 2^h - q^2. So each odd h gives one candidate q and a single
modular condition 2^{k1} ≡ -q (mod D).

Known cycles (all found by this search):
    q = 5:   1 -> 3
    q = 181: 27 -> 611,  35 -> 99

Usage: python3 search_length2.py [H]
Default H = 20000 covers q < 2^{10000}.
"""
import sys
import time


def isqrt(n):
    x = 1 << (n.bit_length() // 2 + 1)
    while True:
        y = (x + n // x) // 2
        if y >= x:
            break
        x = y
    while x * x > n:
        x -= 1
    return x


def v2(n):
    return (n & -n).bit_length() - 1


def search(H):
    found = set()
    for h in range(1, H + 1, 2):          # h odd
        N = 1 << h
        q = isqrt(N)
        if q % 2 == 0:
            q -= 1
        if q < 3:
            continue
        D = N - q * q
        if D <= 0:
            continue
        target = (-q) % D
        pow2 = 2 % D
        for k1 in range(1, h):
            if pow2 == target and (q + (1 << k1)) % D == 0:
                x = (q + (1 << k1)) // D
                if x >= 1 and x % 2 == 1:
                    t = q * x + 1
                    if v2(t) == k1:
                        y = t >> k1
                        if y >= 1 and y % 2 == 1 and y != x:
                            t2 = q * y + 1
                            k2 = v2(t2)
                            if k1 + k2 == h and (t2 >> k2) == x:
                                found.add((q, tuple(sorted((x, y)))))
            pow2 = (pow2 * 2) % D
    return found


def main():
    H = int(sys.argv[1]) if len(sys.argv) > 1 else 20000
    t0 = time.time()
    found = search(H)
    print(f"h <= {H} (q < 2^{H // 2}): {len(found)} cycles in {time.time() - t0:.1f}s")
    for q, xy in sorted(found):
        print(q, xy)


if __name__ == "__main__":
    main()
